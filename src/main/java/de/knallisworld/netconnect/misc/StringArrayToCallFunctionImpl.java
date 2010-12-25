package de.knallisworld.netconnect.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

import de.knallisworld.netconnect.model.Call;
import de.knallisworld.netconnect.model.PhoneNumber;

public class StringArrayToCallFunctionImpl implements Function<String[], Call> {

	private final DateFormat	dateFormat	     = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

	private final Pattern	 timeDurationPattern	= Pattern.compile("(\\d+):(\\d+):(\\d+)");

	@Override
	public Call apply(final String[] arg0) {
		Call call = null;

		if (arg0 != null && arg0.length == 5) {

			Call.CallType type = null;
			if ("icon_voip_call_err_h15".equals(arg0[0])) {
				type = Call.CallType.ERR;
			}
			if ("icon_voip_call_in_h15".equals(arg0[0])) {
				type = Call.CallType.IN;
			}
			if ("icon_voip_call_out_h15".equals(arg0[0])) {
				type = Call.CallType.OUT;
			}

			Date startDate = null;
			Date endDate = null;
			try {
				startDate = dateFormat.parse(arg0[1]);
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				final Matcher matcher = timeDurationPattern.matcher(arg0[2]);
				if (matcher.matches() && matcher.groupCount() == 3) {
					calendar.add(Calendar.HOUR, Integer.parseInt(matcher.group(1)));
					calendar.add(Calendar.MINUTE, Integer.parseInt(matcher.group(2)));
					calendar.add(Calendar.SECOND, Integer.parseInt(matcher.group(3)));
					endDate = calendar.getTime();
				}
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final PhoneNumber targetPhoneNumber = new PhoneNumber(arg0[3]);
			final PhoneNumber sourcePhoneNumber = new PhoneNumber(arg0[4]);

			call = new Call(type, startDate, endDate, sourcePhoneNumber, targetPhoneNumber);
		}

		return call;
	}

}
