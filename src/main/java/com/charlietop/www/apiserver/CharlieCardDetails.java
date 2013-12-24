package com.charlietop.www.apiserver;

public class CharlieCardDetails {
	private double storedValue;
	private String transaction;
	private String timestamp;

	public CharlieCardDetails(double storedValue, String transaction,
			String timestamp) {
		this.storedValue = storedValue;
		this.transaction = transaction;
		this.timestamp = timestamp;
	}

	public double getStoredValue() {
		return this.storedValue;
	}

	public void setStoredValue(double storedValue) {
		this.storedValue = storedValue;
	}

	public String getTransaction() {
		return this.transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String transaction) {
		this.transaction = transaction;
	}
}
