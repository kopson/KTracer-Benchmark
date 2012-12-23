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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.Workspace;

/**
 * File utilities class
 * 
 * @author kopson
 */
public class KFile extends File {

	// Logger instance
	private static final Logger LOG = Logger.getLogger(KFile.class.getName());
		
	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = -6806813507430129712L;

	/**
	 * The constructor overwrites standard File constructor
	 * 
	 * @param pathname
	 *            File path
	 */
	public KFile(String pathname) {
		super(pathname);
	}

	/**
	 * Write project property to file in format: <br>
	 * [name]=[value] <br>
	 * If property already exists - overwrites its value.
	 * 
	 * @param property
	 *            Property name
	 * @param value
	 *            Property value
	 * @return Returns true if property was written successfully
	 */
	public boolean writeProperty(Project.Properties property, String value) {
		try {
			if (readProperty(property) == null) {
				BufferedWriter out = new BufferedWriter(new FileWriter(this,
						true));
				out.write(property.name());
				out.write("=");
				out.write(value);
				out.write("\n");
				out.close();
				return true;
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
						if (words.length == 2
								&& words[0].equals(property.name())) {
							lines.add(property.name() + "=" + value + "\n");
						} else {
							lines.add(line);
						}
					}
					in.close();

					BufferedWriter out = new BufferedWriter(new FileWriter(
							this, false));
					for (String l : lines) {
						out.write(l + "\n");
					}
					out.close();
					return true;
				} catch (FileNotFoundException e) {
					KWindow.displayError(e.getMessage());
					LOG.log(Level.SEVERE, e.getMessage());
				}
			}
		} catch (IOException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		}
		return false;
	}

	/**
	 * Return property from file
	 * 
	 * @param property
	 *            Property name
	 * @return Property value or NULL if not found
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
			in.close();
		} catch (FileNotFoundException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		}

		return null;
	}

	/**
	 * Save text to file
	 * 
	 * @param text Text to save
	 */
	public void setText(String text) {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(this, true));
			out.write(text);
			out.close();
		} catch (IOException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Append text to file
	 * 
	 * @param text Text to save
	 */
	public void appendText(String text) {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(this, true));
			out.append(text);
			out.close();
		} catch (IOException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}
	
	/**
	 * Get text from file
	 * 
	 * @return Returns string containing file data
	 */
	public String getText() {
		InputStream fis;
		BufferedReader in;
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			fis = new FileInputStream(this.getAbsolutePath());
			in = new BufferedReader(new InputStreamReader(fis,
					Charset.forName("UTF-8")));
			while ((line = in.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			in.close();
		} catch (FileNotFoundException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		}

		return stringBuilder.toString();
	}

	/**
	 * Remove directory and all its content
	 * 
	 * @param path
	 *            Directory path
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
	 * Check if toCheck string is a valid path to create a directory
	 * 
	 * @param toCheck
	 *            Path to check
	 * @return Returns true if:<br>
	 *         - parent directory exists<br>
	 *         - destination directory name is a valid file name
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

	/**
	 * Check if workspace is empty
	 * 
	 * @param path
	 *            New workspace path
	 * @return Returns true if new workspace is different from old workspace and
	 *         old workspace is not empty
	 */
	public static boolean checkWorkspaceNotEmpty(String path) {
		File oldWorkspace = new File(Workspace.getInstance().getPath());
		// TODO: Return true only if workspace contains valid projects
		try {
			if (oldWorkspace.isDirectory() && oldWorkspace.list().length > 0
					&& !oldWorkspace.getCanonicalPath().equals(path)) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		}
		return false;
	}

	public void clear() {
		try {
			FileOutputStream writer = new FileOutputStream(this);
			writer.write((new String()).getBytes());
			writer.close();
		} catch (IOException e) {
			KWindow.displayError(e.getMessage());
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}
}
