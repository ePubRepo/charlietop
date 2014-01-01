package com.charlietop.www.apiserver;

import java.util.Arrays;
import java.util.Calendar;

public class CreditCard {

	private String[] VALID_STATES = { "AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DC", "DE", "FL",
			"GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI", "MN",
			"MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR",
			"PA", "RI", "SC", "SD", "TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY" };

	// If more card types are added, make sure to update getCardTypeAsString()
	public enum CardType {
		DINERS, VISA, AMERICAN_EXPRESS, MASTERCARD, DISCOVER, JCB
	}

	private String billingName;
	private String billingAddressOne;
	private String billingAddressTwo;
	private String billingCity;
	private String billingState;
	private String billingZip;
	private CardType cardType;
	private String cardNumber;
	private int expirationMonth;
	private int expirationYear;
	private String securityCode;

	public String getBillingName() {
		return billingName;
	}

	public void setBillingName(String billingName) throws IllegalArgumentException {
		this.checkLength(billingName, 3, 50);
		this.billingName = billingName;
	}

	public String getBillingAddressOne() {
		return billingAddressOne;
	}

	public void setBillingAddressOne(String billingAddressOne) {
		this.checkLength(billingAddressOne, 4, 20);
		this.billingAddressOne = billingAddressOne;
	}

	public String getBillingAddressTwo() {
		return billingAddressTwo;
	}

	public void setBillingAddressTwo(String billingAddressTwo) {
		this.checkLength(billingAddressTwo, 0, 20);
		this.billingAddressTwo = billingAddressTwo;
	}

	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.checkLength(billingCity, 1, 20);
		this.billingCity = billingCity;
	}

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		// TODO: Make this case insensitive
		if (!Arrays.asList(VALID_STATES).contains(billingState)) {
			throw new IllegalArgumentException();
		}
		this.billingState = billingState;
	}

	public String getBillingZip() {
		return billingZip;
	}

	// (comment: ZIP : mandatory; min. length = 5; max. length = 5)
	public void setBillingZip(String billingZip) {
		this.checkLength(billingZip, 5, 5);
		this.billingZip = billingZip;
	}

	public CardType getCardType() {
		return this.cardType;
	}

	public String getCardTypeAsString() {
		switch (this.cardType) {
		case DINERS:
			return "Diners";
		case VISA:
			return "Visa";
		case AMERICAN_EXPRESS:
			return "American Express";
		case MASTERCARD:
			return "Mastercard";
		case DISCOVER:
			return "Discover";
		case JCB:
			return "JCB";
		default:
			return "";
		}
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.checkLength(cardNumber, 13, 21);
		this.cardNumber = cardNumber;
	}

	public int getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(int expirationMonth) {
		this.checkValue(expirationMonth, 1, 12);
		this.expirationMonth = expirationMonth;
	}

	public int getExpirationYear() {
		return expirationYear;
	}

	// Four digit expiration year
	public void setExpirationYear(int expirationYear) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		this.checkValue(expirationYear, year, year + 10);
		this.expirationYear = expirationYear;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.checkLength(securityCode, 3, 5);
		this.securityCode = securityCode;
	}

	private void checkLength(String input, int minLength, int maxLength)
			throws IllegalArgumentException {
		if (input.length() < minLength || input.length() > maxLength) {
			throw new IllegalArgumentException();
		}
	}

	private void checkValue(int input, int minValue, int maxValue) throws IllegalArgumentException {
		if (input < minValue || input > maxValue) {
			throw new IllegalArgumentException();
		}
	}
}
