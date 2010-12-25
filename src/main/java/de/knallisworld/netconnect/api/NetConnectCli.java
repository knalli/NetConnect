package de.knallisworld.netconnect.api;

import java.io.Writer;
import java.util.Collection;
import java.util.Date;

import de.knallisworld.netconnect.model.Call;
import de.knallisworld.netconnect.model.IPAddress;
import de.knallisworld.netconnect.model.PhoneNumber;

public interface NetConnectCli {

	void printHelp(Writer writer);

	void printHeader(Writer writer);

	Date getUptime();

	IPAddress getExternalIPAddress();

	void startPPPoE();

	void stopPPPoE();

	Collection<Call> getLatestPhoneCalls();

	Collection<Call> getLatestIncomingPhoneCalls();

	Collection<Call> getLatestOutgoingPhoneCalls();

	PhoneNumber reverseSearch(PhoneNumber phoneNumber);
}
