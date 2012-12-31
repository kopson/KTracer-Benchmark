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

import java.io.IOException;

/**
 * Throw when trying to create file/directory with name that already exist in selected
 * destination path
 * 
 * @author kopson
 */
public class DuplicatedPathException extends IOException {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 7848403718333550300L;
	
	/**
	 * The constructor
	 * 
	 * @param path Duplicated path
	 */
	public DuplicatedPathException(String path) {
		super("Path: " + path + " already exists");	
	}

}
