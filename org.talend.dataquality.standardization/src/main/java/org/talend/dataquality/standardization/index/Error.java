package org.talend.dataquality.standardization.index;

/**
 * record the error information
 * @author tychu
 *
 */
public class Error {
	private boolean status = true;
	private String message = "";
	
	public void set(boolean status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public void reset() {
		this.status = true;
		this.message = "";
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
}
