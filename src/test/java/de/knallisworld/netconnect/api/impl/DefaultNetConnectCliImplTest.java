package de.knallisworld.netconnect.api.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.io.Files;

import de.knallisworld.netconnect.Main;

public class DefaultNetConnectCliImplTest {
	@Test
	public void testUptimeExpression() {
		final String text = "<script>\nnew autoTimer('idPOnlineTime',4,38,44,\"Stunden\");/* ]]> */</script>\n<b>";
		final Pattern pattern = Pattern.compile(DefaultNetConnectCliImpl.ONLINE_TIME_PATTERN_EXP, Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(text);

		Assert.assertEquals(true, matcher.matches());
		Assert.assertEquals(3, matcher.groupCount());

		Assert.assertEquals("4", matcher.group(1));
		Assert.assertEquals("38", matcher.group(2));
		Assert.assertEquals("44", matcher.group(3));
	}

	@Test
	public void testExternalIpAddrExpression() {
		final String text = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n<tr>\n<td class=\"c180\">\n<label for=\"\">IP-Adresse:</label>\n</td>\n<td>\n78.34.159.24\n</td>\n</tr>\n<tr>";
		final Pattern pattern = Pattern.compile(DefaultNetConnectCliImpl.EXTERNAL_IP_ADDR_PATTERN_EXP, Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(text);

		Assert.assertEquals(true, matcher.matches());
		Assert.assertEquals(1, matcher.groupCount());

		Assert.assertEquals("78.34.159.24", matcher.group(1));
	}

	@Test
	public void testCallsExpression() throws IOException, URISyntaxException {
		final String text = Files.toString(new File(Main.class.getClassLoader().getResource("fragments/calls.html")
		        .toURI()), Charset.forName("ISO-8859-1"));
		final Pattern pattern = Pattern.compile(DefaultNetConnectCliImpl.TELEPHONE_CALL_ITEM_PATTERN_EXP,
		        Pattern.DOTALL & Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);

		int index = 0;
		int rows = 0;
		while (matcher.find(index)) {
			final int max = matcher.groupCount();
			Assert.assertEquals(5, max);
			for (int i = 1; i <= max; i++) {
				final String value = matcher.group(i);
				System.out.printf("%02d: %s%n", i, value);
			}
			index = matcher.end();
			rows++;
		}
		Assert.assertEquals("Last index does not match.", 15178, index);
		Assert.assertEquals("Should be 50 rows.", 50, rows);
	}
}
