package de.knallisworld.netconnect.api.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.knallisworld.netconnect.model.Address;
import de.knallisworld.netconnect.model.PhoneNumber;

public class GoYellowInverseSearchImpl {

	private final Logger	           logger	       = Logger.getLogger(getClass().getCanonicalName());

	protected static final String	   HOSTNAME	       = "www.goyellow.de";

	protected static final String	   SEARCH	       = "/inverssuche/?TEL=";

	private final HttpHost	           httpTarget;

	private final DefaultHttpClient	   httpClient;

	private final BasicResponseHandler	httpResponseHandler;

	protected static final String	   RESULT_ITEM_EXP	= "<div xmlns=\"\" style=\".*\" class=\"listEntry basic buggybox vcard\">\n<div class=\"head\">\n<div class=\"branchCategory\">\n.*\n</div>\n<h3>\n<a onClick=\"return goUtils.appendInv.*;\" title=\".*\" href=\"/upgrade.*\">\n<span class=\"normal fn (.*)\">(.*)</span>.*\n<div class=\"col info\">\n<p class=\"address adr\">.*<span class=\"city locality\">(.)</span>.*<span class=\"geo\">";

	protected static final String	   QRY_ITEMS	   = "//div[@class=\"listEntry basic buggybox vcard\"]";
	protected static final String	   QRY_NAME	       = "div[@class=\"head\"]/h3/a/span/text()";
	protected static final String	   QRY_POST	       = "div[@class=\"content buggybox\"]/div[@class=\"col info\"]/p[@class=\"address adr\"]/span[@class=\"postcode postal-code\"]/text()";
	protected static final String	   QRY_CITY	       = "div[@class=\"content buggybox\"]/div[@class=\"col info\"]/p[@class=\"address adr\"]/span[@class=\"city locality\"]/text()";

	private final XPath	               xpath;

	public GoYellowInverseSearchImpl() {
		httpTarget = new HttpHost(HOSTNAME, 80, "http");

		final DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, false));
		httpClient = client;

		httpResponseHandler = new BasicResponseHandler();

		final XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();
	}

	public PhoneNumber search(final PhoneNumber phoneNumber) {
		PhoneNumber result = null;

		final String response = loadAndReturnResponse(SEARCH + phoneNumber.getValue());
		final List<Address> addresses = transformResults(response);

		if (addresses.isEmpty()) {
			result = phoneNumber;
		} else {
			result = new PhoneNumber(phoneNumber.getValue(), addresses.get(0));
		}

		return result;
	}

	private Document buildXml(final String content) {
		Document doc = null;
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			docBuilderFactory.setValidating(false);
			docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new InputSource(new StringReader(content)));
		} catch (final ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	private List<Address> transformResults(final String response) {
		final List<Address> addresses = new ArrayList<Address>();

		final Document document = buildXml(response);
		try {
			final NodeList entries = (NodeList) xpath.evaluate(QRY_ITEMS, document, XPathConstants.NODESET);
			for (int i = 0; i < entries.getLength(); i++) {
				final Node entry = entries.item(i);
				String name;
				String postCode;
				String city;
				try {
					name = (String) xpath.evaluate(QRY_NAME, entry, XPathConstants.STRING);
					postCode = (String) xpath.evaluate(QRY_POST, entry, XPathConstants.STRING);
					city = (String) xpath.evaluate(QRY_CITY, entry, XPathConstants.STRING);
					addresses.add(new Address(name, postCode, city));
				} catch (final Exception e) {
					e.printStackTrace();
				}

			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return addresses;
	}

	private String internalLoad(final String url) throws IOException, ClientProtocolException {
		return internalLoad(url, null);
	}

	private String internalLoad(final String url, final String referer) throws IOException, ClientProtocolException {
		final HttpGet httpRequest = new HttpGet(url);
		if (referer != null) {
			httpRequest.addHeader("Referer", "http://" + HOSTNAME + referer);
		}
		return httpClient.execute(httpTarget, httpRequest, httpResponseHandler);
	}

	private String loadAndReturnResponse(final String url) {
		return loadAndReturnResponse(url, null);
	}

	private String loadAndReturnResponse(final String url, final String referer) {
		try {
			return internalLoad(url, referer);
		} catch (final Exception e) {
			logger.log(Level.WARNING, "Loading failed.", e);
			return null;
		}
	}

	private void load(final String url) {
		try {
			internalLoad(url);
		} catch (final Exception e) {
			logger.log(Level.WARNING, "Loading failed.", e);
		}
	}

}
