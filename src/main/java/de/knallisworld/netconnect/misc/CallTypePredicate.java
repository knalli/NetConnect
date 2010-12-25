package de.knallisworld.netconnect.misc;

import com.google.common.base.Predicate;

import de.knallisworld.netconnect.model.Call;
import de.knallisworld.netconnect.model.Call.CallType;

public class CallTypePredicate implements Predicate<Call> {

	private final CallType	type;

	public CallTypePredicate(final CallType type) {
		this.type = type;
	}

	@Override
	public boolean apply(final Call input) {
		return type.equals(input.getType());
	}

}
