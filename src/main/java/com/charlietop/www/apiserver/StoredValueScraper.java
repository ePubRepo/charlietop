package com.charlietop.www.apiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StoredValueScraper {

	private Map<String, String> cookies = new HashMap<String, String>();

	private static String URL_DOMAIN = "https://charliecard.mbta.com";

	private static String STARTING_URL = URL_DOMAIN
			+ "/CharlieCardWebProgram/pages/charlieCardCenter.jsf";

	public List<String> addStoredValue(CharlieCardNum charlieCardNum, int amount,
			CreditCard creditCard) throws IOException, IllegalArgumentException {
		List<String> links = new ArrayList<String>();

		// PAGE 1: HOME PAGE
		Connection.Response homePage = this.createConnection(STARTING_URL).execute();
		this.cookies = homePage.cookies();

		Map<String, String> reloadPageFormData = this.scrapeFormDataOnHomePage(homePage.parse());
		String reloadPageUrl = this.scrapeFormAction(homePage.parse(), "menu_form");

		links.add("reloadPageFormData: " + reloadPageFormData.toString());
		links.add("reloadPageUrl: " + reloadPageUrl);
		links.add("this.cookies: " + this.cookies.toString());

		// PAGE 2: RELOAD CHARLIECARD
		Connection.Response reloadPage = this.createPostConnection(reloadPageUrl,
				reloadPageFormData);

		Map<String, String> enterCharlieCardPageFormData = this
				.scrapeFormDataOnReloadPage(reloadPage.parse());
		String enterCharlieCardPageUrl = this.scrapeFormAction(reloadPage.parse(), "main_form");

		links.add("enterCharlieCardPageFormData: " + enterCharlieCardPageFormData.toString());
		links.add("enterCharlieCardPageUrl: " + enterCharlieCardPageUrl);

		// PAGE 3: ADD STORED VALUE (ENTER CHARLIECARD NUMBER)
		Connection.Response enterCharlieCardPage = this.createPostConnection(
				enterCharlieCardPageUrl, enterCharlieCardPageFormData);

		Map<String, String> selectAmountPageFormData = this
				.scrapeFormDataOnEnterCharlieCardPage(enterCharlieCardPage.parse());
		this.addCharlieCardNumberToFormData(selectAmountPageFormData, enterCharlieCardPage.parse()
				.getElementById("main_form"), charlieCardNum, links);
		String selectAmountPageUrl = this.scrapeFormAction(enterCharlieCardPage.parse(),
				"main_form");

		links.add("selectAmountPageFormData: " + selectAmountPageFormData);
		links.add("selectAmountPageUrl: " + selectAmountPageUrl);

		// PAGE 4: SELECT AMOUNT (OF STORED VALUE) PAGE
		Connection.Response selectAmountPage = this.createPostConnection(selectAmountPageUrl,
				selectAmountPageFormData);

		Map<String, String> confirmAmountPageFormData = this
				.scrapeFormDataOnSelectAmountPage(selectAmountPage.parse());
		this.addAmountToFormData(confirmAmountPageFormData, selectAmountPage.parse()
				.getElementById("main_form"), amount, links);
		String confirmAmountPageUrl = this.scrapeFormAction(selectAmountPage.parse(), "main_form");

		links.add("confirmAmountPageFormData: " + confirmAmountPageFormData);
		links.add("confirmAmountPageUrl: " + confirmAmountPageUrl);

		// PAGE 5: CONFIRM AMOUNT (OF STORED VALUE) PAGE
		Connection.Response confirmAmountPage = this.createPostConnection(confirmAmountPageUrl,
				confirmAmountPageFormData);

		Map<String, String> creditCardPageFormData = this
				.scrapeFormDataOnConfirmAmountPage(confirmAmountPage.parse());
		this.addAmountToFormData(creditCardPageFormData,
				confirmAmountPage.parse().getElementById("main_form"), amount, links);
		String creditCardPageUrl = this.scrapeFormAction(confirmAmountPage.parse(), "main_form");

		links.add("confirmAmountPageFormData: " + confirmAmountPageFormData);
		links.add("confirmAmountPageUrl: " + confirmAmountPageUrl);

		// PAGE 6: ENTER CREDIT CARD INFO PAGE
		Connection.Response creditCardPage = this.createPostConnection(creditCardPageUrl,
				creditCardPageFormData);

		Map<String, String> confirmationPageFormData = this.scrapeFormDataOnCreditCardPage(
				creditCardPage.parse(), creditCard);
		String confirmationPageUrl = this.scrapeFormAction(creditCardPage.parse(), "main_form");

		links.add("confirmationPageFormData: " + confirmationPageFormData);
		links.add("confirmationPageUrl: " + confirmationPageUrl);
		
		// PAGE 7: CONFIRMATION PAGE
		Connection.Response confirmationPage = this.createPostConnection(confirmationPageUrl,
				confirmationPageFormData);

		for (Element link : confirmationPage.parse().select("a[href]")) {
			links.add(String.format("<%s>  (%s)", link.attr("abs:href"), link.text()));
		}

		return links;
	}

	private Element getFirstElementWhereTextContains(Elements els, String textSubstring) {
		for (Element el : els) {
			if (el.text().contains(textSubstring)) {
				return el;
			}
		}
		return null;
	}

	// Return the form items to be submitted to the Add Stored Value link
	private Map<String, String> scrapeFormDataOnHomePage(Document doc) {
		Map<String, String> formData = new HashMap<String, String>();

		this.addHiddenInputFieldsToFormData(doc.getElementById("menu_form"), formData);
		Element menuLink = this.getFirstElementWhereTextContains(
				doc.select("#menu ul>li>a[id^=menu_form:nav:]"), "Reload");
		if (menuLink == null) {
			// TODO: Throw exception that indicates page parsing problem
			// TODO: Log the problem parsing the page. Unable to find Reload
			// link on Home Page
		} else {
			formData.put("menu_form:_link_hidden_", menuLink.attr("id"));
		}

		return formData;
	}

	private Map<String, String> scrapeFormDataOnReloadPage(Document doc) {
		Map<String, String> formData = new HashMap<String, String>();

		this.addHiddenInputFieldsToFormData(doc.getElementById("main_form"), formData);
		Element menuLink = this.getFirstElementWhereTextContains(
				doc.select("#main_form a[id^=main_form:]"), "Stored Value");
		if (menuLink == null) {
			// TODO: Throw exception that indicates page parsing problem
			// TODO: Log the problem parsing the page. Unable to find Stored
			// Value link on Reload Page
		} else {
			formData.put("main_form:_link_hidden_", menuLink.attr("id"));
		}

		return formData;
	}

	private Map<String, String> scrapeFormDataOnEnterCharlieCardPage(Document doc) {
		Map<String, String> formData = new HashMap<String, String>();

		this.addHiddenInputFieldsToFormData(doc.getElementById("main_form"), formData);

		Element submitBtn = doc.select("#main_form input[type=submit][value*=Continue]").first();
		if (submitBtn == null) {
			// TODO Throw an exception and log an error indicating a problem
			// parsing the page
		} else {
			formData.put(submitBtn.attr("name"), submitBtn.attr("value"));
		}

		return formData;
	}

	private Map<String, String> scrapeFormDataOnCreditCardPage(Document doc, CreditCard creditCard) {
		Map<String, String> formData = new HashMap<String, String>();

		Element formElm = doc.getElementById("main_form");
		this.addHiddenInputFieldsToFormData(formElm, formData);
		this.addInputFieldsToFormData(formElm, formData, "input[type=text]");

		try {
			Element submitBtn = formElm.select("input[type=submit][value*=Submit]").first();
			formData.put(submitBtn.attr("name"), submitBtn.attr("value"));

			Element checkboxElm = formElm.select("input[type=checkbox]").first();
			formData.put(checkboxElm.attr("name"), checkboxElm.attr("value"));

			Element fullNameElm = formElm.select("input[type=text][name*=FullName]").first();
			formData.put(fullNameElm.attr("name"), creditCard.getBillingName());

			// Note that "Adress1" really is contained in the input's name
			// attribute
			Element address1Elm = formElm.select("input[type=text][name*=Adress1]").first();
			formData.put(address1Elm.attr("name"), creditCard.getBillingAddressOne());

			// Note that "Adress2" really is contained in the input's name
			// attribute
			Element address2Elm = formElm.select("input[type=text][name*=Adress2]").first();
			formData.put(address2Elm.attr("name"), creditCard.getBillingAddressOne());

			Element cityElm = formElm.select("input[type=text][name*=City]").first();
			formData.put(cityElm.attr("name"), creditCard.getBillingAddressOne());

			Element stateElm = formElm.select("select[name*=State]").first();
			formData.put(stateElm.attr("name"), creditCard.getBillingState());

			Element zipElm = formElm.select("select[name*=ZIP]").first();
			formData.put(zipElm.attr("name"), creditCard.getBillingZip());

			Element selectElm = formElm.select("select[name*=cardType]").first();
			Element optionElm = this.getFirstElementWhereTextContains(selectElm.select("option"),
					creditCard.getCardTypeAsString());
			formData.put(selectElm.attr("name"), optionElm.attr("value"));

			Element creditCardNumElm = formElm.select("select[name*=CreditCardNo]").first();
			formData.put(creditCardNumElm.attr("name"), creditCard.getCardNumber());

			Element securityCodeElm = formElm.select("select[name*=SecurityCode]").first();
			formData.put(securityCodeElm.attr("name"), creditCard.getSecurityCode());

			Element termsElm = formElm.select("select[name*=AgreeQuestion]").first();
			formData.put(termsElm.attr("name"), "true");
		} catch (NullPointerException e) {
			// TODO: Log this exception, including the state of formData so that
			// I can reconstruction where page parsing failed--i.e., which
			// element failed to match the page
		}

		return formData;
	}

	private void addCharlieCardNumberToFormData(Map<String, String> formData, Element formElm,
			CharlieCardNum charlieCardNum, List<String> output) {
		Elements textFields = formElm.select("input[type=text]");
		output.add("textFields: " + textFields.toString());
		for (Element textField : textFields) {
			if (textField.attr("name").contains("Prefix")) {
				output.add("textField: name: " + textField.attr("name") + " prefix: "
						+ charlieCardNum.getPrefix());
				formData.put(textField.attr("name"), String.valueOf(charlieCardNum.getPrefix()));
			} else if (textField.attr("name").contains("Number")) {
				output.add("textField: name: " + textField.attr("name") + " number: "
						+ charlieCardNum.getNumber());
				formData.put(textField.attr("name"), String.valueOf(charlieCardNum.getNumber()));
			}
		}
	}

	private Map<String, String> scrapeFormDataOnSelectAmountPage(Document doc) {
		Map<String, String> formData = new HashMap<String, String>();

		Element formElm = doc.getElementById("main_form");

		this.addHiddenInputFieldsToFormData(formElm, formData);
		this.addInputFieldsToFormData(formElm, formData, "input[type=text]");

		// This represents the fact that we are not selecting a monthly pass
		// This corresponds to a <select> element and thus won't be handled when
		// adding hidden input fields or text input fields
		formData.put("main_form:productType", "0");
		return formData;
	}

	private Map<String, String> scrapeFormDataOnConfirmAmountPage(Document doc) {
		Map<String, String> formData = this.scrapeFormDataOnSelectAmountPage(doc);

		Element submitBtn = doc.select("#main_form input[type=submit][value*=Continue]").first();
		if (submitBtn == null) {
			// TODO: Throw an exception and log an error because page parsing
			// didn't work as expected
		} else {
			formData.put(submitBtn.attr("name"), submitBtn.attr("value"));
		}

		return formData;
	}

	private void addAmountToFormData(Map<String, String> formData, Element formElm, int amount,
			List<String> output) {
		List<String> validAmounts = new ArrayList<String>();

		Elements optionElms = formElm.select("option");
		for (Element optionElm : optionElms) {
			if (optionElm.text().contains("$ " + amount + ".00")) {
				formData.put("main_form:svAmount", optionElm.attr("value"));
				return;
			}

			Pattern p = Pattern.compile("US\\$ (\\d+)\\.00");
			Matcher m = p.matcher(optionElm.text());
			if (m.find()) {
				validAmounts.add("$" + m.group(1));
			}
		}

		// TODO: Log an error
		throw new IllegalArgumentException("Amount to add must be one of: "
				+ validAmounts.toString());
	}

	private String scrapeFormAction(Document doc, String formId) {
		Element el = doc.getElementById(formId);
		if (el == null) {
			// TODO: throw new ParsingException
		}
		return URL_DOMAIN + el.attr("action");
	}

	private void addInputFieldsToFormData(Element form, Map<String, String> formData,
			String cssSelector) {
		Elements hiddenInputs = form.select("input" + cssSelector);
		for (Element hiddenInput : hiddenInputs) {
			if (hiddenInput.attr("name").length() > 0) {
				formData.put(hiddenInput.attr("name"), hiddenInput.attr("value"));
			}
		}
	}

	private void addHiddenInputFieldsToFormData(Element form, Map<String, String> formData) {
		this.addInputFieldsToFormData(form, formData, "[type=hidden]");
	}

	private Connection createConnection(String url) {
		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.68 Safari/537.36";
		String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
		String acceptEncoding = "gzip,deflate,sdch";
		String acceptLanguage = "en-US,en;q=0.8";

		return Jsoup.connect(url).cookies(this.cookies).userAgent(userAgent)
				.header("Accept", accept).header("Accept-Encoding", acceptEncoding)
				.header("Accept-Language", acceptLanguage).method(Connection.Method.GET);
	}

	private Connection.Response createPostConnection(String url, Map<String, String> data)
			throws IOException {
		return this.createConnection(url).method(Connection.Method.POST).data(data).execute();
	}

	private String stackTraceToString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
