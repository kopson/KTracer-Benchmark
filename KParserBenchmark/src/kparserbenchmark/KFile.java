package kparserbenchmark;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kparserbenchmark.projectexplorer.Project;

/**
 * File utilities class
 * 
 * @author kopson
 */
public class KFile extends File {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -6806813507430129712L;

	/**
	 * The constructor
	 * 
	 * @param pathname
	 */
	public KFile(String pathname) {
		super(pathname);
	}

	/**
	 * Write property to file
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	public boolean writeProperty(Project.Properties property, String propertyValue) {
		try {
			if (readProperty(property) == null) {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(this, true));
					out.write(property.name());
					out.write("=");
					out.write(propertyValue);
					out.write("\n");
					out.close();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				InputStream fis;
				BufferedReader in;
				Vector<String> lines = new Vector<String>();
				String line;

				try {
					fis = new FileInputStream(this.getAbsolutePath());
					in = new BufferedReader(new InputStreamReader(fis,
							Charset.forName("UTF-8")));

					while ((line = in.readLine()) != null) {
						String[] words = line.split("=");
						if (words.length == 2 && words[0].equals(property.name())) {
							lines.add(property.name() + "=" + propertyValue + "\n");
						} else {
							lines.add(line);
						}
					}
					in.close();

					BufferedWriter out = new BufferedWriter(new FileWriter(this, false));
					for (String l : lines) {
						out.write(l + "\n");
					}
					out.close();
					return true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Return property from file
	 * 
	 * @param propertyName
	 * @return propertyValue or NULL if not found
	 */
	public String readProperty(Project.Properties property) {
		InputStream fis;
		BufferedReader in;
		String line;

		try {
			fis = new FileInputStream(this.getAbsolutePath());
			in = new BufferedReader(new InputStreamReader(fis,
					Charset.forName("UTF-8")));
			while ((line = in.readLine()) != null) {
				String[] words = line.split("=");
				if (words.length == 2 && words[0].equals(property.name())) {
					in.close();
					return words[1];
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Removes directory and all its content
	 * 
	 * @param oldWorkspace
	 * @throws FileNotFoundException
	 */
	public static void removeDirectoryRecursive(String path)
			throws FileNotFoundException {
		File f = new File(path);
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				removeDirectoryRecursive(c.getAbsolutePath());
			}
			f.delete();
		} else if (!f.delete()) {
			throw new FileNotFoundException("Failed to delete file: "
					+ f.getName());
		}
	}

	/**
	 * Check if toCheck string is a valid path:
	 * 
	 * - parent directory exists - destination directory name is valid
	 * 
	 * @param toCheck
	 * @return
	 */
	public static boolean isPathVaild(String toCheck) {
		String retrName = toCheck;

		// path must be at least "/"
		if (toCheck.length() < 2) {
			if (toCheck.length() == 0) {
				return false;
			} else if (retrName.charAt(0) == File.separatorChar) {
				return true;
			} else {
				return false;
			}
		}

		if (retrName.charAt(retrName.length() - 1) == File.separatorChar) {
			retrName = retrName.substring(0, retrName.length() - 1);
			toCheck = toCheck.substring(0, toCheck.length() - 1);
		}

		int idx = retrName.lastIndexOf(File.separatorChar);
		if (idx > 0) {
			retrName = retrName.substring(0, idx);
		}

		Pattern pattern = Pattern.compile(".*\\W+.*");
		String lastName = toCheck.substring(idx + 1, toCheck.length());
		Matcher matcher = pattern.matcher(lastName);
		if (matcher.find()) {
			return false;
		}
		if (!(new File(retrName)).isDirectory()) {
			return false;
		}
		return true;
	}
}
