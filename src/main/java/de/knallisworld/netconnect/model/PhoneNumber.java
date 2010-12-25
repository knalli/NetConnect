package de.knallisworld.netconnect.model;

public class PhoneNumber {

	private final String	value;

	private final Address	address;

	public PhoneNumber(final String value) {
		this(value, null);
	}

	public PhoneNumber(final String value, final Address address) {
		if ("Withheld".equals(value)) {
			this.value = "***";
		} else {
			this.value = value;
		}
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (value == null ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PhoneNumber other = (PhoneNumber) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
