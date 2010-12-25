package de.knallisworld.netconnect.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.knallisworld.netconnect.Main;

public class GoYellowInverseSearchImplTest {

	private Document readFile(final String filename) {
		Document doc = null;
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			docBuilderFactory.setValidating(false);
			docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			final InputStream is = Main.class.getClassLoader().getResourceAsStream("fragments/goyellow.html");
			doc = docBuilder.parse(is);
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

	@Test
	public void testSearchResultProcess() throws IOException, URISyntaxException, XPathExpressionException {
		final Document document = readFile("fragments/goyellow.html");

		final XPathFactory xpathFactory = XPathFactory.newInstance();
		final XPath xpath = xpathFactory.newXPath();

		final String QRY_ITEMS = "//div[@class=\"listEntry basic buggybox vcard\"]";
		final String QRY_NAME = "div[@class=\"head\"]/h3/a/span/text()";
		final String QRY_POST = "div[@class=\"content buggybox\"]/div[@class=\"col info\"]/p[@class=\"address adr\"]/span[@class=\"postcode postal-code\"]/text()";
		final String QRY_CITY = "div[@class=\"content buggybox\"]/div[@class=\"col info\"]/p[@class=\"address adr\"]/span[@class=\"city locality\"]/text()";

		final NodeList entries = (NodeList) xpath.evaluate(QRY_ITEMS, document, XPathConstants.NODESET);
		for (int i = 0; i < entries.getLength(); i++) {
			final Node entry = entries.item(i);
			final String name = (String) xpath.evaluate(QRY_NAME, entry, XPathConstants.STRING);
			final String postCode = (String) xpath.evaluate(QRY_POST, entry, XPathConstants.STRING);
			final String city = (String) xpath.evaluate(QRY_CITY, entry, XPathConstants.STRING);

			System.out.printf("name=%s, postCode=%s, city=%s%n", name, postCode, city);
		}

		final Pattern pattern = Pattern.compile(GoYellowInverseSearchImpl.RESULT_ITEM_EXP, Pattern.DOTALL
		        & Pattern.MULTILINE);

	}
}
