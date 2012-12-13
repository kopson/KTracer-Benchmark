/*
 * FileUtils.h
 *
 *  Created on: Oct 15, 2012
 *      Author: pkopka
 */

#ifndef FILEUTILS_H_
#define FILEUTILS_H_

#include <string>
#include <vector>

#define READ_BUFFER 	4096
#define WORD_BUFFER 	512
#define TOKEN_TYPES 	5

enum tokenState {
	BEGIN = 0,
	END = 1,
	MID = 2,
	REP = 3,
	DIV = 4,
	SKIP = 5,
	SEP = 6,
	TOK = 7,
};

bool fileExists(const char* fileName);
bool clearFile(const char* fileName);
bool removeFile(const char* fileName);

typedef struct Token {
	char tokenList[WORD_BUFFER];
	int tokSize;
} Token;

struct KFile {

public:
	KFile(const char* fileName = "", const char* flag = "c");
	~KFile();
	const char* getFileName();
	bool openFile(const char* fileName, const char* flag);
	bool writeStr(const char* str);
	std::string readProperty(const char* str);
	int readFile();
	bool readLine(std::vector<std::string> *dest);
	bool prepareTrimmer(std::string trimmer);
	void finalizeTrimmer();
	int copyFile(const char* destFile);
	bool clearFile();
	bool compressFile(const char* destFile);
	bool deleteFile();
	bool setPermission(const char *perm);
	bool rewindFile();

private:
	int fileDescriptor;
	const char* fileName;
	int accessFlag;
	ssize_t fileSize;

	char readBuff[READ_BUFFER];
	int readOffset, readSize;
	int getNextState(int *tokState, int *currState, int nextState, char sign = 0);
	void parseBegin(char *wordBuff, int *wordOffset);
	void parseEnd(char *wordBuff, int *wordOffset);
	bool parseMid(char *wordBuff, int *wordOffset);
	Token tokens[TOKEN_TYPES];
	char skipToken;
};

#endif /* FILEUTILS_H_ */
