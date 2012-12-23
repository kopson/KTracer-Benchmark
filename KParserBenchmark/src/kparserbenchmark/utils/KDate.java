package kparserbenchmark.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KDate {

	private static final String mDateFormatText = "[yyyy/MM/dd HH:mm:ss]: ";
	
	private static final DateFormat mDateFormat = new SimpleDateFormat(mDateFormatText);
	
	public static String now() {
		return mDateFormat.format(new Date());
	}
	
	public static int getDateLen() {
		return mDateFormatText.length();
	}
}
