package de.knallisworld.netconnect.api.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.knallisworld.netconnect.Main;
import de.knallisworld.netconnect.api.NetConnectCli;
import de.knallisworld.netconnect.misc.CallTypePredicate;
import de.knallisworld.netconnect.misc.SourceCallPhoneNumberPredicate;
import de.knallisworld.netconnect.misc.StringArrayToCallFunctionImpl;
import de.knallisworld.netconnect.misc.TargetCallPhoneNumberPredicate;
import de.knallisworld.netconnect.model.Call;
import de.knallisworld.netconnect.model.Call.CallType;
import de.knallisworld.netconnect.model.IPAddress;
import de.knallisworld.netconnect.model.PhoneNumber;

public class DefaultNetConnectCliImpl implements NetConnectCli {

	private final Logger	                    logger	                        = Logger.getLogger(getClass()
	                                                                                    .getCanonicalName());

	private static final String	                HOSTNAME	                    = "netconnect.box";

	private static final String	                OVERVIEW_INDEX	                = "/web.cgi?controller=Overview&action=IndexOverview&id=0";
	private static final String	                INTERNET_INDEX	                = "/web.cgi?controller=Internet&action=Index";
	private static final String	                INTERNET_START_PPPOE	        = "/web.cgi?controller=Internet&action=StartPppoe&id=0";
	private static final String	                INTERNET_STOP_PPPOE	            = "/web.cgi?controller=Internet&action=StopPppoe&id=0";
	private static final String	                TELEPHONE_LAST_CALLS	        = "/web.cgi?controller=Telephony&action=IndexLastCalls";
	private static final String	                TELEPHONE_LAST_CALLS_TABLE	    = "/web.cgi?controller=Telephony&action=IndexLastCallsTable";

	protected static final String	            ONLINE_TIME_PATTERN_EXP	        = ".*idPOnlineTime',(\\d+),(\\d+),(\\d+),\"Stunden\".*";
	protected static final String	            EXTERNAL_IP_ADDR_PATTERN_EXP	= ".*<label for=\"\">IP-Adresse:</label>\n</td>\n<td>\n(\\d+\\.\\d+\\.\\d+\\.\\d+)\n</td>.*";
	protected static final String	            TELEPHONE_CALL_ITEM_PATTERN_EXP	= "<tr.*><td class=\"cCallDirectionImage\">\n<img src=\"images/(.*).gif\" vspace=\"0\" hspace=\"0\" /></td>\n<td class=\".*\">\n(.*)</td>\n<td class=\".*\">\n(.*)</td>\n<td class=\".*\">\n(.*)</td>\n<td class=\".*\">\n(.*)</td>\n</tr>";

	private boolean	                            session	                        = false;

	private final HttpClient	                httpClient;
	private final HttpHost	                    httpTarget;
	private final ResponseHandler<String>	    httpResponseHandler;

	private final StringArrayToCallFunctionImpl	stringArrayToCallFunction	    = new StringArrayToCallFunctionImpl();

	protected static final Pattern	            ONLINE_TIME_PATTERN	            = Pattern.compile(
	                                                                                    ONLINE_TIME_PATTERN_EXP,
	                                                                                    Pattern.DOTALL);
	protected static final Pattern	            EXTERNAL_IP_ADDR_PATTERN	    = Pattern.compile(
	                                                                                    EXTERNAL_IP_ADDR_PATTERN_EXP,
	                                                                                    Pattern.DOTALL);
	protected static final Pattern	            TELEPHONE_CALL_ITEM_PATTERN	    = Pattern
	                                                                                    .compile(
	                                                                                            TELEPHONE_CALL_ITEM_PATTERN_EXP,
	                                                                                            Pattern.DOTALL
	                                                                                                    & Pattern.MULTILINE);
	protected static Splitter	                COMMA_SPLITTER	                = Splitter.on(",").trimResults();

	private final GoYellowInverseSearchImpl	    invserseSearch	                = new GoYellowInverseSearchImpl();

	public DefaultNetConnectCliImpl() {
		httpTarget = new HttpHost(HOSTNAME, 80, "http");

		final DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, false));
		httpClient = client;

		httpResponseHandler = new BasicResponseHandler();
	}

	public boolean loadSession() {
		logger.info("Loading session...");

		if (session) {
			return true;
		}

		try {
			getSession();
			session = true;
			return true;
		} catch (final Exception e) {
			logger.log(Level.WARNING, "Session failed.", e);
		}

		return false;
	}

	private void getSession() throws ClientProtocolException, IOException {
		internalLoad(OVERVIEW_INDEX);
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

	@Override
	public void printHelp(final Writer writer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printHeader(final Writer writer) {
		final String title = Main.RESOURCES.getString("title");
		final String version = Main.RESOURCES.getString("version");
		write(writer, String.format(title, version) + "\n");
	}

	private void write(final Writer writer, final String string) {
		try {
			writer.write(string);
		} catch (final IOException e) {
			logger.log(Level.WARNING, "Failure.", e);
		}
	}

	@Override
	public Date getUptime() {
		logger.info("Perform getUptime...");

		Preconditions.checkState(loadSession(), "No valid session.");

		final String response = loadAndReturnResponse(INTERNET_INDEX);
		final Matcher matcher = ONLINE_TIME_PATTERN.matcher(response);

		if (matcher.matches() && matcher.groupCount() == 3) {
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, -Integer.parseInt(matcher.group(1)));
			calendar.add(Calendar.MINUTE, -Integer.parseInt(matcher.group(2)));
			calendar.add(Calendar.SECOND, -Integer.parseInt(matcher.group(3)));
			return calendar.getTime();
		}

		return null;
	}

	@Override
	public IPAddress getExternalIPAddress() {
		logger.info("Perform getExternalIPAddress...");

		Preconditions.checkState(loadSession(), "No valid session.");

		final String response = loadAndReturnResponse(INTERNET_INDEX);
		final Matcher matcher = EXTERNAL_IP_ADDR_PATTERN.matcher(response);

		if (matcher.matches() && matcher.groupCount() == 1) {
			return new IPAddress(matcher.group(1));
		}

		return null;
	}

	@Override
	public void startPPPoE() {
		logger.info("Perform startPPPoE...");

		Preconditions.checkState(loadSession(), "No valid session.");

		load(INTERNET_START_PPPOE);
	}

	@Override
	public void stopPPPoE() {
		logger.info("Perform stopPPPoE...");

		Preconditions.checkState(loadSession(), "No valid session.");

		load(INTERNET_STOP_PPPOE);
	}

	@Override
	public Collection<Call> getLatestIncomingPhoneCalls() {

		logger.info("Perform getLatestIncomingPhoneCalls...");
		Preconditions.checkState(loadSession(), "No valid session.");

		final Predicate<Call> typePredicate = getCallTypePredicate(CallType.ERR, CallType.IN);
		final TargetCallPhoneNumberPredicate numbersPredicate = new TargetCallPhoneNumberPredicate(
		        getHousePhoneNumbers());

		return Collections2.filter(getLatestPhoneCalls(), Predicates.and(typePredicate, numbersPredicate));
	}

	private static Collection<PhoneNumber> getHousePhoneNumbers() {
		final Collection<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
		for (final String number : COMMA_SPLITTER.split(Main.RESOURCES.getString("houseNumbers"))) {
			phoneNumbers.add(new PhoneNumber(number));
		}
		return phoneNumbers;
	}

	private static Predicate<Call> getCallTypePredicate(final CallType... types) {
		final CallTypePredicate[] predicates = new CallTypePredicate[types.length];
		for (int i = 0; i < types.length; i++) {
			predicates[i] = new CallTypePredicate(types[i]);
		}
		return Predicates.or(predicates);
	}

	@Override
	public Collection<Call> getLatestPhoneCalls() {

		logger.info("Perform getLatestPhoneCalls...");
		Preconditions.checkState(loadSession(), "No valid session.");

		final String response = loadAndReturnResponse(TELEPHONE_LAST_CALLS_TABLE, TELEPHONE_LAST_CALLS);
		final Matcher matcher = TELEPHONE_CALL_ITEM_PATTERN.matcher(response);

		final List<String[]> results = new ArrayList<String[]>();
		int index = 0;
		while (matcher.find(index)) {
			final int max = matcher.groupCount();
			final String[] result = new String[max];
			for (int i = 1; i <= max; i++) {
				result[i - 1] = matcher.group(i);
			}
			results.add(result);
			index = matcher.end();
		}

		return Lists.transform(results, stringArrayToCallFunction);
	}

	@Override
	public Collection<Call> getLatestOutgoingPhoneCalls() {

		logger.info("Perform getLatestOutgoingPhoneCalls...");
		Preconditions.checkState(loadSession(), "No valid session.");

		final Predicate<Call> typePredicate = getCallTypePredicate(CallType.ERR, CallType.OUT);
		final SourceCallPhoneNumberPredicate numbersPredicate = new SourceCallPhoneNumberPredicate(
		        getHousePhoneNumbers());

		return Collections2.filter(getLatestPhoneCalls(), Predicates.and(typePredicate, numbersPredicate));
	}

	@Override
	public PhoneNumber reverseSearch(final PhoneNumber phoneNumber) {
		return invserseSearch.search(phoneNumber);
	}

}
