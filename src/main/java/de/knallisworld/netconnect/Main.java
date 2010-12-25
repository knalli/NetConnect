package de.knallisworld.netconnect;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.knallisworld.netconnect.api.NetConnectCli;
import de.knallisworld.netconnect.api.impl.DefaultNetConnectCliImpl;
import de.knallisworld.netconnect.model.Call;
import de.knallisworld.netconnect.model.PhoneNumber;

public class Main {

	public static final ResourceBundle	RESOURCES	= ResourceBundle.getBundle("bundles/messages", Locale.getDefault());

	public static void main(final String[] args) throws SecurityException, IOException {

		LogManager.getLogManager().readConfiguration(
		        Main.class.getClassLoader().getResourceAsStream("logging.properties"));
		Logger.getLogger(Main.class.getCanonicalName()).info("Starting...");

		final Options options = new Options();

		options.addOption("help", false, "display help");

		options.addOption("ipaddr", false, "display current external ip address");
		options.addOption("uptime", false, "display current uptime");
		options.addOption("latestIncomingCalls", false, "get the latest incoming calls");
		options.addOption("latestOutgoingCalls", false, "get the latest outgoing calls");
		options.addOption("inverseSearch", true, "search the given phone number for address");

		try {
			final CommandLineParser parser = new PosixParser();
			final CommandLine command = parser.parse(options, args);

			final NetConnectCli api = new DefaultNetConnectCliImpl();
			final PrintWriter writer = new PrintWriter(System.out);

			handleOptions(api, writer, command, options);

			if (command.getOptions().length == 0) {
				new HelpFormatter().printHelp(RESOURCES.getString("app"), options);
			}

			writer.flush();
		} catch (final ParseException e) {
			System.out.println(e.getLocalizedMessage());
			new HelpFormatter().printHelp(RESOURCES.getString("app"), options);
		}
	}

	private static void handleOptions(final NetConnectCli api, final PrintWriter writer, final CommandLine command,
	        final Options options) {

		api.printHeader(writer);

		if (command.hasOption("help")) {
			new HelpFormatter().printHelp(RESOURCES.getString("app"), options);
		}

		if (command.hasOption("ipaddr")) {
			System.out.println(api.getExternalIPAddress().toString());
		}

		if (command.hasOption("uptime")) {
			System.out.println(api.getUptime().toString());
		}

		if (command.hasOption("latestIncomingCalls")) {
			printCalls(writer, api.getLatestIncomingPhoneCalls());
		}

		if (command.hasOption("latestOutgoingCalls")) {
			printCalls(writer, api.getLatestOutgoingPhoneCalls());
		}

		if (command.hasOption("inverseSearch")) {
			final String number = command.getOptionValue("inverseSearch");
			final PhoneNumber phoneNumber = api.reverseSearch(new PhoneNumber(number));
			if (phoneNumber.getAddress() != null) {
				writer.write(phoneNumber.getAddress().toString());
			} else {
				writer.write("Unknown.");
			}
		}
	}

	private static void printCalls(final Writer writer, final Collection<Call> calls) {
		final String format = "%5s | %16s | %10s | %20s -> %20s%n";

		try {
			writer.write(String.format("%d calls.%n", calls.size()));
			writer.write(String.format(format, "Type", "Date", "Duration", "Source", "Target"));
		} catch (final IOException e1) {
			e1.printStackTrace();
		}

		for (final Call call : calls) {
			try {
				writer.write(String.format(format, call.getType(), call.getStartDate(), call.getDurationInSeconds(),
				        call.getSourcePhoneNumber(), call.getTargetPhoneNumber()));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

}
