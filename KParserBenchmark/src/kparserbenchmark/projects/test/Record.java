package kparserbenchmark.projects.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Record {

	private int logId;
	private String logName;
	private String logType;
	private long timestamp;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/**
	 * The constructor
	 */
	public Record() {
	}

	/**
	 * The constructor
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
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

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
	
	 @Override
	  public String toString() {
	    return logId + ": " + logName + " " + logType + " " + timestamp;
	  }
}
