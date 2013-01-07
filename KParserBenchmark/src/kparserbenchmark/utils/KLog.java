/*******************************************************************************
 Copyright (c) 2012 kopson kopson.piko@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 *******************************************************************************/

package kparserbenchmark.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Logger utilities class
 * 
 * @author kopson
 */
public class KLog {

	/**
	 * Log to file?
	 */
	private static boolean mLogFile;

	/**
	 * Log file name
	 */
	private static final String mLogFileName = "SystemOut";

	/**
	 * Log redirection class
	 * 
	 */
	private class FilteredOutputStream extends FilterOutputStream {

		/**
		 * The constructor
		 * 
		 * @param aStream
		 *            Output stream
		 */
		public FilteredOutputStream(OutputStream aStream) {
			super(aStream);
		}

		@Override
		public void write(byte b[]) throws IOException {
			String aString = new String(b);
			if (mLogFile) {
				FileWriter aWriter = new FileWriter(mLogFileName + ".log", true);
				aWriter.write(aString);
				aWriter.close();
			} else {
				System.out.println(aString);
			}

		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			String aString = new String(b, off, len);
			if (mLogFile) {
				FileWriter aWriter = new FileWriter(mLogFileName + ".log", true);
				aWriter.write(aString);
				aWriter.close();
			} else {
				System.out.println(aString);
			}
		}
	}

	/**
	 * Redirect standard output
	 */
	public void redirectStdOut() {
		mLogFile = true;
		PrintStream outputStream = new PrintStream(new FilteredOutputStream(
				new ByteArrayOutputStream()));
		System.setOut(outputStream);
	}

	/**
	 * Merry Christmas!
	 * 
	 * @param height
	 * @param width
	 */
	public static void printXmassTree(int height, int width) {
		int w = 0;
		int v = 0;
		int z = 1;
		int l = 0;
		int o = 3;
		int p = 5;
		int g = 0;
		int d = 5;
		if (height <= 0 || width <= 0)
			return;
		if (width % 2 != 0)
			++width;
		for (int i = 0; i < height; ++i) {
			++l;
			if (l % (p * z - g) == 0) {
				l -= o++;
				++z;
				g *= 2;
			}
			w = v = 0;
			for (int k = height; k > l; --k) {
				System.out.print(' ');
				++v;
			}
			for (int j = 0; j < 2 * l - 1; ++j) {
				System.out.print('*');
				++w;
			}
			System.out.println();
		}
		for (int u = 0; u < d; ++u) {
			for (int m = 0; m < w + v; ++m) {
				if (m < (w - width) / 2 + v || m > (w - width) / 2 + width + v) {
					System.out.print(' ');
				} else {
					System.out.print('#');
				}
			}
			System.out.println();
		}
	}
}
