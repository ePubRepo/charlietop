package com.charlietop.www.apiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.InternalServerErrorException;

import javax.inject.Named;

@Api(name = "apiserver", version = "v1")
public class Endpoints {
	/**
	 * External API to add stored value to a CharlieCard, charging it to the
	 * specified credit card.
	 * 
	 * @param charlieCardNumStr
	 *            CharlieCard number as a string, including the dash to separate
	 *            the prefix from the rest of the number.
	 * @param billingName
	 *            Name exactly as it appears on the credit card
	 * @param billingAddressOne
	 *            First line of the billing address of the credit card
	 * @param billingAddressTwo
	 *            Second line of the billing address of the credit card
	 *            (optional)
	 * @param billingCity
	 *            City of the billing address of the credit card
	 * @param billingZip
	 *            Zip code of the billing address of the credit card
	 * @param creditCardNumber
	 *            Credit card number (no spaces or dashes)
	 * @param expirationMonth
	 *            Expiration month of the credit card
	 * @param expirationYear
	 *            Expiration year of the credit card (four digit number)
	 * @param securityCode
	 *            Security code of the credit card (optional; usually on the
	 *            back of the card)
	 * @param amount
	 *            USD amount of stored value to add to the CharlieCard
	 * @return Eventually this will return void; for now returns debugging info
	 * @throws BadRequestException
	 *             Indicates the request contained incorrect/invalid data (e.g.,
	 *             improperly formatted CharlieCard number, invalid amount of
	 *             stored value to add, etc.). The exception's getMessage()
	 *             contains a error message safe to display to end users.
	 * @throws InternalServerErrorException
	 *             Something went wrong when communicating with the MBTA
	 *             servers, and it's possible retrying won't fix the problem.
	 */
	@ApiMethod(name = "addstoredvalue")
	public List<String> addStoredValue(@Named("charlieCardNum") String charlieCardNumStr,
			@Named("ccBillingName") String billingName,
			@Named("ccBillingAddressOne") String billingAddressOne,
			@Nullable @Named("ccBillingAddressTwo") String billingAddressTwo,
			@Named("ccBillingCity") String billingCity, @Named("ccBillingZip") String billingZip,
			@Named("ccNumber") String creditCardNumber,
			@Named("ccExpirationMonth") int expirationMonth,
			@Named("ccExpirationYear") int expirationYear,
			@Named("ccSecurityCode") String securityCode, @Named("amount") int amount)
			throws BadRequestException, InternalServerErrorException {

		// TODO: Create a sub-class of BadRequestException. That subclass
		// should:
		// - Indicate the computer-readable name of the problematic field (e.g.,
		// charlieCardNum)
		// - Handle more than argument validation error at once (e.g., both
		// credit card number and CharlieCard number failures)
		// - Have a version number so I can change the format of the error
		// without all the clients failing

		CharlieCardNum charlieCardNum;
		CreditCard creditCard;

		try {
			charlieCardNum = new CharlieCardNum(charlieCardNumStr);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("CharlieCard must be only numbers and one dash (-)");
		}
		
		try {
			creditCard = new CreditCard();
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("CharlieCard must be only numbers and one dash (-)");
		}

		this.validateCreditCardArguments(billingName, billingAddressOne, billingAddressTwo,
				billingCity, billingZip, creditCardNumber, expirationMonth, expirationYear,
				securityCode);

		try {
			StoredValueScraper scraper = new StoredValueScraper();
			return scraper.addStoredValue(charlieCardNum, amount, creditCard);
		} catch (IOException e) {
			// TODO: Log the details of the exception thrown by the scraper
			// TODO: Change this so I don't actually print the stack trace;
			// that's only for debugging
			throw new InternalServerErrorException(this.stackTraceToString(e));
		} catch (Exception e) {
			throw new InternalServerErrorException(this.stackTraceToString(e));
		}
	}

	@ApiMethod(name = "getcarddetails")
	public CharlieCardDetails getCharlieCardDetails(
			@Named("charlieCardNum") String charlieCardNumStr) throws BadRequestException,
			InternalServerErrorException {

		try {
			CharlieCardNum charlieCardNum = new CharlieCardNum(charlieCardNumStr);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("CharlieCard must be only numbers and one dash (-)");
		}

		// TODO: Any time I throw an InternalServerErrorException, log as much
		// as possible about what happened

		return new CharlieCardDetails(14.00, "Haymarket", "December 20, 2013 07:42:24 PM");
	}

	private void validateCreditCardArguments(String billingName, String billingAddressOne,
			String billingAddressTwo, String billingCity, String billingZip,
			String creditCardNumber, int expirationMonth, int expirationYear, String securityCode)
			throws BadRequestException {

		// TODO: Insert a lot more argument validation
	}

	private String stackTraceToString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
