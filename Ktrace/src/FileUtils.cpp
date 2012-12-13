/*
 * FileUtils.cpp
 *
 *  Created on: Oct 15, 2012
 *      Author: pkopka
 */

#include <assert.h>
#include <errno.h>
#include <fcntl.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/sendfile.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <zlib.h>

#include "FileUtils.h"
#include "Ktrace.h"

using std::string;
using std::vector;

/* Check whether a file exists */
bool fileExists(const char* fileName) {
	return access(fileName, F_OK) != -1;
}

bool clearFile(const char* fileName) {
	int traceFD = creat(fileName, 0);
	if (traceFD == -1) {
		fprintf(stderr, "error truncating %s: %s (%d)\n", fileName,
				strerror(errno), errno);
		return false;
	}

	close(traceFD);
	return true;
}

bool removeFile(const char* fileName) {
	if(remove(fileName) != 0) {
		kprint(E, 0, "\n\tWhile removing file %s", fileName);
		return false;
	}

	return true;
}

/* Create new KFile object */
KFile::KFile(const char* fileName, const char* flag) :
		fileDescriptor(-1), fileName(""), accessFlag(0), fileSize(0), readOffset(0),
		readSize(0) {

	memset(readBuff, 0, READ_BUFFER);
	finalizeTrimmer();

	if (strcmp(fileName, "") == 0) {
		return;
	}

	assert(openFile(fileName, flag));
}

/* Close KFile object */
KFile::~KFile() {
	if (fileDescriptor > 0) {
		if (close(fileDescriptor) != 0) {
			kprint(E, 0, "\n\tWhile closing file %s", fileName);
		}
		fileDescriptor = -1;
		kdebug("Closed file : %s\n", fileName);
	}
}

/* Write a string to a file, returning true if the write was successful */
bool KFile::writeStr(const char* str) {
	if (fileDescriptor == -1) {
		kprint(E, NOT_OPEN, "\n\tWhile writing to %s", fileName);
		return false;
	}

	bool ok = true;
	ssize_t len = strlen(str);
	if (write(fileDescriptor, str, len) != len) {
		kprint(E, NOT_OPEN, "\n\tWhile writing to %s", fileName);
		ok = false;
	}

	return ok;
}

/* Open file. Depending on flag type:
 * "w"		write only
 * "r"		read only
 * "c"		create
 * "r+w"	write and read
 */
bool KFile::openFile(const char* fileName, const char* flag) {
	if(fileDescriptor != -1) {
		kprint(E, ALREADY_OPEN, "\n\tWhile opening %s", fileName);
		return false;
	}

	if (strcmp(flag, "w") == 0) {
		fileDescriptor = open(fileName, O_WRONLY);
		accessFlag = O_WRONLY;
	} else if (strcmp(flag, "r") == 0) {
		fileDescriptor = open(fileName, O_RDONLY);
		accessFlag = O_RDONLY;
	} else if (strcmp(flag, "c") == 0) {
		fileDescriptor = creat(fileName, O_RDWR);
		accessFlag = O_RDWR;
	} else if (strcmp(flag, "r+w") == 0) {
		fileDescriptor = open(fileName, O_RDWR);
		accessFlag = O_RDWR;
	} else {
		assert(0);
	}

	if (fileDescriptor == -1) {
		kprint(E, 0, "\n\tWhile opening file: %s \t flag: %s", fileName, flag);
		return false;
	}

	this->fileName = fileName;

	struct stat filestatus;
	if (stat(fileName, &filestatus) != 0) {
		kprint(E, 0, "\n\tWhile checking file size %s", fileName);
		return false;
	}
	fileSize = filestatus.st_size;

	kdebug("Opened file : %s\n", fileName);
	return true;
}

bool KFile::setPermission(const char *perm) {
	int flag = 0;

	if (strcmp(perm, "r+a") == 0) {
		flag = S_IRUSR|S_IRGRP|S_IROTH;
	}

	if (flag != 0) {
		if (chmod(fileName, flag) != 0) {
			kprint(E, 0, "\n\tWhile setting permissions %s", fileName);
			return false;
		}
		return true;
	}
	return false;
}

bool KFile::deleteFile() {
	if (unlink(fileName) != 0) {
		kprint(E, 0, "\n\tWhile deleting %s", fileName);
		return false;
	}
	return true;
}

/* Copy data to dest file */
int KFile::copyFile(const char* destFile) {
	if (fileDescriptor == -1) {
		kprint(E, NOT_OPEN, "\n\tWhile reading %s", fileName);
		return -1;
	}

	KFile destF;

	if (strcmp(destFile, "") == 0) {
		destF.fileDescriptor = STDOUT_FILENO;
	} else {
		if (!destF.openFile(destFile, "c")) {
			return -1;
		}
		if(chmod(destFile, 775) == -1) {
			kprint(E, 0, "\n\tWhile changing permissions %s", destFile);
			return -1;
		}
	}

	ssize_t sent = 0;
	while ((sent = sendfile(destF.fileDescriptor, fileDescriptor, NULL, 64*1024*1024)) > 0);
	if (sent == -1) {
		kprint(E, 0, "\n\tWhile copying %s to %s", fileName, destFile);
	}

	return 0;
}

string KFile::readProperty(const char* str) {
	vector<string> dest;
	string ret;

	if (!prepareTrimmer("&=;")) {
		kprint(E, PARSE_ERROR, "\n\tWhile reading property from %s", fileName);
		return NULL;
	}

	while (readLine(&dest)) {
		if (dest.size() < 2) {
			kprint(E, PARSE_ERROR, "\n\tWhile checking property from %s", fileName);
			break;
		}
		if (dest[0].compare(str) == 0) {
			ret = dest[1];
			break;
		}
	}

	finalizeTrimmer();
	return ret;
}

/* Print file to standard output */
int KFile::readFile() {
	return copyFile("");
}

/* Clear all data from file */
bool KFile::clearFile() {
	if (fileDescriptor == -1) {
		kprint(E, NOT_OPEN, "\n\tWhile clearing file %s", fileName);
		return false;
	}

	if(close(fileDescriptor) != 0) {
		kprint(E, 0, "\n\tWhile closing file %s", fileName);
		return false;
	}

	if((fileDescriptor = creat(fileName, 0)) == -1) {
		kprint(E, 0, "\n\tWhile creating empty file %s", fileName);
		return false;
	}

	return true;
}

bool KFile::compressFile(const char* destFile) {
	z_stream zs;
	uint8_t *in, *out;
	int result, flush;

	KFile dest(destFile, "r+w");

	bzero(&zs, sizeof(zs));
	result = deflateInit(&zs, Z_DEFAULT_COMPRESSION);
	if (result != Z_OK) {
		kprint(E, result, "\n\tWhile initializing zlib %s", fileName);
		return false;
	}

	const size_t bufSize = 64*1024;
	in = (uint8_t*)malloc(bufSize);
	out = (uint8_t*)malloc(bufSize);
	flush = Z_NO_FLUSH;

	zs.next_out = out;
	zs.avail_out = bufSize;
	do {
		if (zs.avail_in == 0) {
			// More input is needed.
			result = read(fileDescriptor, in, bufSize);
			if (result < 0) {
				kprint(E, 0, "\n\tWhile reading file %s", fileName);
				result = Z_STREAM_END;
				break;
			} else if (result == 0) {
				flush = Z_FINISH;
			} else {
				zs.next_in = in;
				zs.avail_in = result;
			}
		}
		if (zs.avail_out == 0) {
			// Need to write the output.
			result = write(dest.fileDescriptor, out, bufSize);
			if ((size_t)result < bufSize) {
				kprint(E, 0, "\n\tWhile writing deflated file %s", fileName);
				result = Z_STREAM_END; // skip deflate error message
				zs.avail_out = bufSize; // skip the final write
				break;
			}
			zs.next_out = out;
			zs.avail_out = bufSize;
		}
	} while ((result = deflate(&zs, flush)) == Z_OK);

	if (result != Z_STREAM_END) {
		kprint(E, 0, "%s", zs.msg);
	}

	if (zs.avail_out < bufSize) {
		size_t bytes = bufSize - zs.avail_out;
		result = write(dest.fileDescriptor, out, bytes);
		if ((size_t)result < bytes) {
			kprint(E, 0, "\n\tWhile writing deflated file %s", fileName);
		}
	}

	result = deflateEnd(&zs);
	if (result != Z_OK) {
		kprint(E, result, "\n\tWhile cleaning up zlib %s", fileName);
	}

	free(in);
	free(out);

	return true;
}

/////////////////////////// Trimmer ///////////////////////////////////

int KFile::getNextState(int *tokState, int *currState, int nextState, char sign) {

	switch(nextState) {
		case BEGIN:
		case END:
		case MID:
		case REP:
		case DIV: {
			if (*currState == TOK || *tokState == TOK)
				return -1;
			*currState = nextState;
			if(tokens[*currState].tokSize == WORD_BUFFER - 1)
				return -1;
			int size = tokens[*currState].tokSize++;
			tokens[*currState].tokenList[size] = sign;
			return *currState;
		} case SEP: {
			if (*tokState == SEP)
				return -1;
			*currState = nextState;
			*tokState = nextState;
			return *currState;
		} case TOK: {
			if (*currState == SEP || *tokState == TOK)
				return -1;
			*tokState = TOK;
			return *currState;
		} case SKIP: {
			*currState = nextState;
			skipToken = sign;
			return *currState;
		}
	}
	return -1;
}

bool KFile::prepareTrimmer(string trimmer) {
	if (trimmer.compare("") == 0)
		return true;

	int trimState = SEP; 	//BEGIN - trim from begin state
							//END 	- trim from end state
							//MID 	- trim from middle state
							//REP 	- replace sign state
							//DIV	- divide sign state
							//SKIP 	- skip line state
							//SEP 	- separator state
							//TOK 	- token state
	int tokState = SEP;
	int trimSize = trimmer.length();
	bool failure = false;
	int found = 0;
	for(int j = 0; j < trimSize; ++j) {
		if(trimmer[j] == ' ' || trimmer[j] == '\t' || trimmer[j] == '\n')
			continue;

		if((trimmer[j] == '+' || trimmer[j] == '=' || trimmer[j] == '~') && found < 2) {
			if(j == trimSize - 1)
				failure = true;
			if(getNextState(&tokState, &trimState, BEGIN, trimmer[j + 1]) < 0)
				failure = true;
			found = 1;
		}

		if((trimmer[j] == '-' || trimmer[j] == '=' || trimmer[j] == '~') && found < 2) {
			if(j == trimSize - 1)
				failure = true;
			if(getNextState(&tokState, &trimState, END, trimmer[j + 1]) < 0)
				failure = true;
			found = 1;
		}

		if ((trimmer[j] == ':' || trimmer[j] == '~') && found < 2) {
			if(j == trimSize - 1)
				failure = true;
			if (getNextState(&tokState, &trimState, MID, trimmer[j + 1]) < 0)
				failure = true;
			found = 1;
		}

		if (trimmer[j] == '|' && trimState == SEP) {
			if(j == trimSize - 1)
				failure = true;
			if (getNextState(&tokState, &trimState, REP, trimmer[j + 1]) < 0)
				failure = true;
			found = 2;
		} else if (trimmer[j] == '&' && trimState == SEP) {
			if(j == trimSize - 1)
				failure = true;
			if (getNextState(&tokState, &trimState, DIV, trimmer[j + 1]) < 0)
				failure = true;
			found = 2;
		} else if (trimmer[j] == '#' && trimState == SEP) {
			if (skipToken != 0 || j == trimSize - 1)
				failure = true;
			if (getNextState(&tokState, &trimState, SKIP, trimmer[j + 1]) < 0)
				failure = true;
			found = 2;
		} else if (trimmer[j] == ';') {
			if (getNextState(&tokState, &trimState, SEP) < 0)
				failure = true;
			found = 0;
		} else if (found == 3 || found == 2) {
			if (getNextState(&tokState, &trimState, TOK) < 0)
				failure = true;
		} else if (found == 1) {
			found = 3;
		} else {
			failure = true;
		}

		if (failure) {
			finalizeTrimmer();
			kprint(E, SYNTAX_ERROR, "\n\tWhile reading trimmer for %s", fileName);
			return false;
		}
	}

	if (trimState != SEP) {
		finalizeTrimmer();
		kprint(E, SYNTAX_ERROR, "\n\tWhile reading trimmer for %s", fileName);
		return false;
	}

	return true;
}

void KFile::finalizeTrimmer() {
	for (int i = 0; i < TOKEN_TYPES; ++i) {
		memset(tokens[i].tokenList, 0, WORD_BUFFER);
		tokens[i].tokSize = 0;
	}
	skipToken = 0;
}

///////////////////////// Trimmer end /////////////////////////////////

bool KFile::rewindFile() {
	int position = lseek(fileDescriptor, 0L, SEEK_SET);
	if(position == -1L) {
		kprint(E, 0, "\n\tWhile rewind: '%s'", fileName);
		return false;
	}
	return true;
}

////////////////////////// Parser //////////////////////////////////

void KFile::parseBegin(char *wordBuff, int *wordOffset) {
	for(int i = 0; i < tokens[BEGIN].tokSize; ++i) {
		if (wordBuff[0] == tokens[BEGIN].tokenList[i]) {
			(*wordOffset) = 0;
			return;
		}
	}
}

bool KFile::parseMid(char *wordBuff, int *wordOffset) {
	for(int i = 0; i < tokens[MID].tokSize; ++i) {
		if (wordBuff[(*wordOffset) - 1] == tokens[MID].tokenList[i]) {
			--(*wordOffset);
			return false;
		}
	}
	for(int i = 0; i < tokens[REP].tokSize; ++i) {
		if (wordBuff[(*wordOffset) - 1] == tokens[REP].tokenList[i]) {
			(*wordOffset) = 0;
			return false;
		}
	}
	for(int i = 0; i < tokens[DIV].tokSize; ++i) {
		if (wordBuff[(*wordOffset) - 1] == tokens[DIV].tokenList[i]) {
			wordBuff[--(*wordOffset)] = '\0';
			return true;
		}
	}
	return false;
}

void KFile::parseEnd(char *wordBuff, int *wordOffset) {
	for(int i = 0; i < tokens[END].tokSize; ++i) {
		if (wordBuff[(*wordOffset) - 1] == tokens[END].tokenList[i]) {
			wordBuff[--(*wordOffset)] = '\0';
			return;
		}
	}
	wordBuff[(*wordOffset)] = '\0';
}

/* Read line from file into dest buffer, removes signs listed in trimmer. Syntax is:
 *
 * + removes next char if occurs on the beginning of the word
 * - removes next char if occurs on the end of the word
 * = removes next char if occurs on the beginning or end of the word
 * : removes next char if occurs in the middle of the word
 * ~ removes next char if occurs anywhere
 * ; separates tokens
 * | skip word before next char
 * & divide word into two words separated with next char
 * # skips all lines starting from next char
 * ' ', '\t', '\n' are ignored
 *
 * Example trimmer:
 *
 * &(; :); -:
 * input: "23(55):" output: "23 55"
 *
 */
bool KFile::readLine(vector<string> *dest) {
	if(dest == NULL)
		return false;

	if(readOffset < readSize) {
		if(readBuff[readOffset] == skipToken) {
			do {} while(readBuff[readOffset++] != '\n');
			return true;
		}

		char wordBuff[WORD_BUFFER];
		int wordOffset = 0;
		memset(wordBuff, 0, WORD_BUFFER);

		bool stopParse = false;
		bool inWord = false;

		for (int i = readOffset; i < readSize; ++i) {
			if (readBuff[i] == ' ')
				continue;
			else
				wordBuff[wordOffset++] = readBuff[i];

			if (inWord) {
				if(readBuff[i + 1] != ' ' && readBuff[i + 1] != '\n') {
					if (parseMid(wordBuff, &wordOffset)) {
						kdebug("Token1: \"%s\"\n", wordBuff);
						dest->push_back(wordBuff);
						wordOffset = 0;
					}
				}
			} else {
				inWord = true;
				parseBegin(wordBuff, &wordOffset);
			}

			if (readBuff[i + 1] == ' ') {
				if (!inWord) {
					++i;
					continue;
				}
				inWord = false;
				parseEnd(wordBuff, &wordOffset);
				if (wordOffset) {
					kdebug("Token2: \"%s\"\n", wordBuff);
					dest->push_back(wordBuff);
					wordOffset = 0;
				}
			} else if (readBuff[i + 1] == '\n' && inWord) {
				inWord = false;
				stopParse = true;
				parseEnd(wordBuff, &wordOffset);
				if (wordOffset) {
					kdebug("Token3: \"%s\"\n", wordBuff);
					dest->push_back(wordBuff);
					wordOffset = 0;
				}
			} else if (readBuff[i] == '\n') {
				stopParse = true;
			}

			if (stopParse) {
				readOffset = i + 2;
				return true;
			}
		}
	} else {
		readSize = read(fileDescriptor, readBuff, sizeof(readBuff));
		readOffset = 0;
		if(readSize < 0) {
			kprint(E, 0, "\n\tWhile reading line from %s", fileName);
			return false;
		} else if (readSize == 0)
			return false;
		else {
			readLine(dest);
		}
	}

	return true;
}

///////////////////////// Parser end /////////////////////////////////

const char* KFile::getFileName() {
	return fileName;
}
