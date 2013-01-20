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

package kparserbenchmark.projects.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Table record template class
 * 
 * @author Kopson
 */
public class Record {

	/** Log id */
	private int logId;
	/** Human readable record event */
	private String logName;
	/** Record type */
	private String logType;
	/** timestamp */
	private long timestamp;

	/**
	 * Property change support allows other items to listen for table viewer
	 * events
	 */
	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * The constructor
	 */
	public Record() {
		propertyChangeSupport = null;
	}

	/**
	 * The default constructor
	 * 
	 * @param id
	 * @param name
	 * @param type
	 * @param ts
	 */
	public Record(int id, String name, String type, long ts) {
		logId = id;
		logName = name;
		logType = type;
		timestamp = ts;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Add property change listener
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove property change listener
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/********** Getters/Setters **********/
	public int getLogId() {
		return logId;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		propertyChangeSupport.firePropertyChange("logName", this.logName,
				this.logName = logName);
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		propertyChangeSupport.firePropertyChange("logType", this.logType,
				this.logType = logType);
	}

	public long getTimestamp() {
		return timestamp;
	}
	/*************************************/

	@Override
	public String toString() {
		return logId + ": " + logName + " " + logType + " " + timestamp;
	}
}
