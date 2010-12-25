package de.knallisworld.netconnect.misc;

import java.util.Collection;

import de.knallisworld.netconnect.model.Call;
import de.knallisworld.netconnect.model.PhoneNumber;

public class TargetCallPhoneNumberPredicate extends CallPhoneNumberPredicate {

	public TargetCallPhoneNumberPredicate(final Collection<PhoneNumber> phoneNumbers) {
		super(phoneNumbers);
	}

	public TargetCallPhoneNumberPredicate(final PhoneNumber... phoneNumbers) {
		super(phoneNumbers);
	}

	@Override
	public boolean apply(final Call input) {
		return phoneNumbers.contains(input.getTargetPhoneNumber());
	}

}
