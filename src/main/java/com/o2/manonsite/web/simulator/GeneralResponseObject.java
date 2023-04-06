package com.o2.manonsite.web.simulator;

public class GeneralResponseObject {
	private String status;
	private String Message;
	private Object ResponseData;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	
	
	public Object getResponseData() {
		return ResponseData;
	}
	public void setResponseData(Object responseData) {
		ResponseData = responseData;
	}
	@Override
	public String toString() {
		return "GeneralResponseObject [status=" + status + ", Message=" + Message + "]";
	}
	
	
	
}