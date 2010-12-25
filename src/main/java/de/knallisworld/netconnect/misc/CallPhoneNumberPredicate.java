package de.knallisworld.netconnect.misc;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import de.knallisworld.netconnect.model.Call;
import de.knallisworld.netconnect.model.PhoneNumber;

abstract public class CallPhoneNumberPredicate implements Predicate<Call> {
	protected List<PhoneNumber>	phoneNumbers;

	public CallPhoneNumberPredicate(final PhoneNumber... phoneNumbers) {
		this.phoneNumbers = Lists.newArrayList(phoneNumbers);
	}

	public CallPhoneNumberPredicate(final Collection<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = Lists.newArrayList(phoneNumbers);
	}
}
