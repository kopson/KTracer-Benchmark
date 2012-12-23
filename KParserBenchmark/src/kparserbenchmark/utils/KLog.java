package kparserbenchmark.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class KLog {

	private static boolean mLogFile;

	private static final String mLogFileName = "SystemOut";
	
	private class FilteredOutputStream extends FilterOutputStream {

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
	
	public void redirectStdOut() {
		mLogFile = true;
		PrintStream outputStream = new PrintStream(new FilteredOutputStream(new ByteArrayOutputStream()));
		System.setOut(outputStream);
	}
}
