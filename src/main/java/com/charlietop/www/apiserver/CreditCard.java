package com.charlietop.www.apiserver;

public class CreditCard {

	private String billingName;
	private String billingAddressOne;
	private String billingAddressTwo;
	private String billingCity;
	private String billingState;
	private int billingZip;
	private double cardNumber;
	private int expirationMonth;
	private int expirationYear;
	private String securityCode;

	public String getBillingName() {
		return billingName;
	}

	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	public String getBillingAddressOne() {
		return billingAddressOne;
	}

	public void setBillingAddressOne(String billingAddressOne) {
		this.billingAddressOne = billingAddressOne;
	}

	public String getBillingAddressTwo() {
		return billingAddressTwo;
	}

	public void setBillingAddressTwo(String billingAddressTwo) {
		this.billingAddressTwo = billingAddressTwo;
	}

	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
	}

	public int getBillingZip() {
		return billingZip;
	}

	public void setBillingZip(int billingZip) {
		this.billingZip = billingZip;
	}

	public double getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(double cardNumber) {
		this.cardNumber = cardNumber;
	}

	public int getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(int expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public int getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(int expirationYear) {
		this.expirationYear = expirationYear;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
}
