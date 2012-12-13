/*
 * KFileTest.cpp
 *
 *  Created on: Nov 14, 2012
 *      Author: root
 */

#include <string>
#include <iostream>

#include "UnitTest.h" //Must be included before tested class
#include "Ktrace.h"

using namespace std;

#ifdef TEST_FILE

int main(int argc, char **argv) {

	INIT_TESTS("Trimmer single token tests");
	TEST_CASE("trimmer") {
		KFile f;
		if (!f.prepareTrimmer(""))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(2);
		if (f.skipToken != 0)
			TEST_FAIL(3);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer_") {
		KFile f;
		if (!f.prepareTrimmer(" "))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(2);
		if (f.skipToken != 0)
			TEST_FAIL(3);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer#") {
		KFile f;
		if (!f.prepareTrimmer("##;"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(2);
		if (f.skipToken == 0)
			TEST_FAIL(3);
		f.finalizeTrimmer();
	} TEST_END;
	TEST_CASE("trimmer&") {
		KFile f;
		if (!f.prepareTrimmer("&s;"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 1)
			TEST_FAIL(2);
		if (f.tokens[DIV].tokenList[0] != 's')
			TEST_FAIL(3);
		if (f.skipToken != 0)
			TEST_FAIL(4);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer+") {
		KFile f;
		if (!f.prepareTrimmer("+s;"))
			TEST_FAIL(1);
		if (f.tokens[BEGIN].tokenList[0] != 's')
			TEST_FAIL(2);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 1)
			TEST_FAIL(3);
		if (f.skipToken != 0)
			TEST_FAIL(4);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer|") {
		KFile f;
		if (!f.prepareTrimmer("|s;"))
			TEST_FAIL(1);
		if (f.tokens[REP].tokenList[0] != 's')
			TEST_FAIL(2);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 1)
			TEST_FAIL(3);
		if (f.skipToken != 0)
			TEST_FAIL(4);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer-") {
		KFile f;
		if (!f.prepareTrimmer("-s;"))
			TEST_FAIL(1);
		if (f.tokens[END].tokenList[0] != 's')
			TEST_FAIL(2);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 1)
			TEST_FAIL(3);
		if (f.skipToken != 0)
			TEST_FAIL(4);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer=") {
		KFile f;
		if (!f.prepareTrimmer("=s;"))
			TEST_FAIL(1);
		if (f.tokens[END].tokenList[0] != 's' || f.tokens[BEGIN].tokenList[0] != 's')
			TEST_FAIL(2);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 2)
			TEST_FAIL(3);
		if (f.skipToken != 0)
			TEST_FAIL(4);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer:") {
		KFile f;
		if (!f.prepareTrimmer(":s;"))
			TEST_FAIL(1);
		if (f.tokens[MID].tokenList[0] != 's')
			TEST_FAIL(2);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 1)
			TEST_FAIL(3);
		if (f.skipToken != 0)
			TEST_FAIL(4);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer~") {
		KFile f;
		if (!f.prepareTrimmer("~s;"))
			TEST_FAIL(1);
		if (f.tokens[END].tokenList[0] != 's' || f.tokens[MID].tokenList[0] != 's' ||
				f.tokens[BEGIN].tokenList[0] != 's')
			TEST_FAIL(2);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 3)
			TEST_FAIL(3);
		if (f.skipToken != 0)
			TEST_FAIL(4);
		f.finalizeTrimmer();
	} TEST_END;
	RUN_TESTS;

	INIT_TESTS("Trimmer fails tests");
	TEST_CASE("trimmer_error") {
		KFile f;
		if (f.prepareTrimmer("+a"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(2);
		if (f.skipToken != 0)
			TEST_FAIL(3);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer_error2") {
		KFile f;
		if (f.prepareTrimmer("+"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(2);
		if (f.skipToken != 0)
			TEST_FAIL(3);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer_error3") {
		KFile f;
		if (f.prepareTrimmer("c"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(2);
		if (f.skipToken != 0)
			TEST_FAIL(3);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer_error4") {
		KFile f;
		if (f.prepareTrimmer("=;"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(5);
		if (f.skipToken != 0)
			TEST_FAIL(8);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer_error5") {
		KFile f;
		if (f.prepareTrimmer("+a|;"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(5);
		if (f.skipToken != 0)
			TEST_FAIL(8);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer_error6") {
		KFile f;
		if (f.prepareTrimmer(";;"))
			TEST_FAIL(1);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res)
			TEST_FAIL(2);
		if (f.skipToken != 0)
			TEST_FAIL(3);
		f.finalizeTrimmer();
	} TEST_END;
	RUN_TESTS;

	INIT_TESTS("Trimmer long tests");
	TEST_CASE("trimmer1") {
		KFile f;
		if (!f.prepareTrimmer(":c; =c;"))
			TEST_FAIL(1);
		if (f.tokens[BEGIN].tokenList[0] != 'c')
			TEST_FAIL(2);
		if (f.tokens[END].tokenList[0] != 'c')
			TEST_FAIL(3);
		if (f.tokens[MID].tokenList[0] != 'c')
			TEST_FAIL(4);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 3)
			TEST_FAIL(5);
		if (f.skipToken != 0)
			TEST_FAIL(6);
		f.finalizeTrimmer();
	} TEST_END;

	TEST_CASE("trimmer2") {
		KFile f;
		if (!f.prepareTrimmer("+c; -a; =t; :h; ~w; #g; &d; |p;"))
			TEST_FAIL(1);
		if (	f.tokens[BEGIN].tokenList[0] != 'c' ||
				f.tokens[BEGIN].tokenList[1] != 't'  ||
				f.tokens[BEGIN].tokenList[2] != 'w')
			TEST_FAIL(2);
		if (	f.tokens[END].tokenList[0] != 'a' ||
				f.tokens[END].tokenList[1] != 't' ||
				f.tokens[END].tokenList[2] != 'w')
			TEST_FAIL(3);
		if (	f.tokens[MID].tokenList[0] != 'h' ||
				f.tokens[MID].tokenList[1] != 'w')
			TEST_FAIL(4);
		if (f.tokens[REP].tokenList[0] != 'p')
			TEST_FAIL(5);
		if (f.tokens[DIV].tokenList[0] != 'd')
			TEST_FAIL(6);
		int res = 0;
		for (int i = 0; i < TOKEN_TYPES; ++i)
			res += f.tokens[i].tokSize;
		if (res != 10)
			TEST_FAIL(7);
		if (f.skipToken != 'g')
			TEST_FAIL(8);
		f.finalizeTrimmer();
	} TEST_END;
	RUN_TESTS;

	const char * fileName = "/home/pkopka/Downloads/test.out";
	INIT_TESTS("Parser tests");
	TEST_CASE("parser1") {
		{
			KFile testFile(fileName, "c");
			assert(fileExists(fileName));
			assert(testFile.setPermission("r+a"));
			assert(testFile.writeStr("# tracer:\n"));
		}
		{
			KFile testFile(fileName, "r");
			if (!testFile.prepareTrimmer("##;"))
				TEST_FAIL(1);
			vector<string> dest;
			int lines = 0;
			while (testFile.readLine(&dest)) {
				++lines;
			}
			if (lines != 1)
				TEST_FAIL(2);
			if (dest.size() != 0)
				TEST_FAIL(3);
			testFile.finalizeTrimmer();
		}
	} TEST_END;

	TEST_CASE("parser2") {
		{
			KFile testFile(fileName, "c");
			assert(fileExists(fileName));
			assert(testFile.setPermission("r+a"));
			assert(testFile.writeStr("parser-1234 dupa-dupa\n a a\n tmp-1 -s w- ---\n"));
		}
		{
			KFile testFile(fileName, "r");
			if (!testFile.prepareTrimmer("&-;"))
				TEST_FAIL(1);
			if(testFile.tokens[DIV].tokenList[0] != '-')
				TEST_FAIL(2);
			vector<string> dest;
			int lines = 0;
			while (testFile.readLine(&dest)) {
				++lines;
			}
			if (lines != 3)
				TEST_FAIL(3);
			int i = -1;
			if(		dest.size() != 12 ||
					dest[++i].compare("parser") != 0 ||
					dest[++i].compare("1234") != 0 ||
					dest[++i].compare("dupa") != 0 ||
					dest[++i].compare("dupa") != 0 ||
					dest[++i].compare("a") != 0 ||
					dest[++i].compare("a") != 0 ||
					dest[++i].compare("tmp") != 0 ||
					dest[++i].compare("1") != 0 ||
					dest[++i].compare("-s") != 0 ||
					dest[++i].compare("w-") != 0 ||
					dest[++i].compare("-") != 0 ||
					dest[++i].compare("-") != 0)
				TEST_FAIL(4);
			testFile.finalizeTrimmer();
		}
	} TEST_END;

	TEST_CASE("parser3") {
		{
			KFile testFile(fileName, "c");
			assert(fileExists(fileName));
			assert(testFile.setPermission("r+a"));
			assert(testFile.writeStr(":testx dupa- *axa\n a* *a*\n xtmp-1 -=-j:\n"));
		}
		{
			KFile testFile(fileName, "r");
			if (!testFile.prepareTrimmer("+:; --; =*; ~x; :m;"))
				TEST_FAIL(1);
			if(		testFile.tokens[BEGIN].tokenList[0] != ':' ||
					testFile.tokens[END].tokenList[0] != '-' ||
					testFile.tokens[BEGIN].tokenList[1] != '*' ||
					testFile.tokens[END].tokenList[1] != '*' ||
					testFile.tokens[BEGIN].tokenList[2] != 'x' ||
					testFile.tokens[END].tokenList[2] != 'x' ||
					testFile.tokens[MID].tokenList[0] != 'x' ||
					testFile.tokens[MID].tokenList[1] != 'm')
				TEST_FAIL(2);
			vector<string> dest;
			int lines = 0;
			while (testFile.readLine(&dest)) {
				++lines;
			}
			if (lines != 3)
				TEST_FAIL(3);
			int i = -1;
			if(		dest.size() != 7 ||
					dest[++i].compare("test") != 0 ||
					dest[++i].compare("dupa") != 0 ||
					dest[++i].compare("aa") != 0 ||
					dest[++i].compare("a") != 0 ||
					dest[++i].compare("a") != 0 ||
					dest[++i].compare("tp-1") != 0 ||
					dest[++i].compare("-=-j:") != 0)
				TEST_FAIL(4);
			testFile.finalizeTrimmer();
		}
	} TEST_END;
	TEST_CASE("parser3") {
		{
			KFile testFile(fileName, "c");
			assert(fileExists(fileName));
			assert(testFile.setPermission("r+a"));
			assert(testFile.writeStr("23(55):\n"));
		}
		{
			KFile testFile(fileName, "r");
			if (!testFile.prepareTrimmer("&(; :); -:;"))
				TEST_FAIL(1);
			if(		testFile.tokens[DIV].tokenList[0] != '(' ||
					testFile.tokens[MID].tokenList[0] != ')' ||
					testFile.tokens[END].tokenList[0] != ':')
				TEST_FAIL(2);
			vector<string> dest;
			int lines = 0;
			while (testFile.readLine(&dest)) {
				++lines;
			}
			if (lines != 1)
				TEST_FAIL(3);
			int i = -1;
			if(		dest.size() != 2 ||
					dest[++i].compare("23") != 0 ||
					dest[++i].compare("55") != 0)
				TEST_FAIL(4);
			testFile.finalizeTrimmer();
		}
	} TEST_END;

	TEST_CASE("parser4") {
		{
			KFile testFile(fileName, "c");
			assert(fileExists(fileName));
			assert(testFile.setPermission("r+a"));
			assert(testFile.writeStr(" part1=part2 \n"));
		}
		{
			KFile testFile(fileName, "r");
			if (!testFile.prepareTrimmer("|=;"))
				TEST_FAIL(1);
			vector<string> dest;
			int lines = 0;
			while (testFile.readLine(&dest)) {
				++lines;
			}
			if (lines != 1)
				TEST_FAIL(2);
			if (	dest.size() != 1 ||
					dest[0] != "part2")
				TEST_FAIL(3);
			testFile.finalizeTrimmer();
		}
	} TEST_END;
	RUN_TESTS;
}

#endif //TEST_FILE
