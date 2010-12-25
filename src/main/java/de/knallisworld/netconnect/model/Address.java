package de.knallisworld.netconnect.model;

public class Address {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (city == null ? 0 : city.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (plz == null ? 0 : plz.hashCode());
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
		final Address other = (Address) obj;
		if (city == null) {
			if (other.city != null) {
				return false;
			}
		} else if (!city.equals(other.city)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (plz == null) {
			if (other.plz != null) {
				return false;
			}
		} else if (!plz.equals(other.plz)) {
			return false;
		}
		return true;
	}

	public Address(final String name, final String plz, final String city) {
		this.name = name;
		this.plz = plz;
		this.city = city;
	}

	private final String	name;

	private final String	plz;

	private final String	city;

	public String getName() {
		return name;
	}

	public String getPlz() {
		return plz;
	}

	public String getCity() {
		return city;
	}

	@Override
	public String toString() {
		return "Address [name=" + name + ", plz=" + plz + ", city=" + city + "]";
	}

}
