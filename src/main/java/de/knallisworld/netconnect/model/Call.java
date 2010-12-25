package de.knallisworld.netconnect.model;

import java.util.Date;

public class Call {

	public static enum CallType {
		IN, OUT, ERR
	}

	private final CallType	      type;

	private final Date	      startDate;

	private final Date	      endDate;

	private final PhoneNumber	sourcePhoneNumber;

	private final PhoneNumber	targetPhoneNumber;

	public Call(final CallType type, final Date startDate, final Date endDate, final PhoneNumber sourcePhoneNumber,
	        final PhoneNumber targetPhoneNumber) {
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sourcePhoneNumber = sourcePhoneNumber;
		this.targetPhoneNumber = targetPhoneNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (endDate == null ? 0 : endDate.hashCode());
		result = prime * result + (sourcePhoneNumber == null ? 0 : sourcePhoneNumber.hashCode());
		result = prime * result + (startDate == null ? 0 : startDate.hashCode());
		result = prime * result + (targetPhoneNumber == null ? 0 : targetPhoneNumber.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
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
		final Call other = (Call) obj;
		if (endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!endDate.equals(other.endDate)) {
			return false;
		}
		if (sourcePhoneNumber == null) {
			if (other.sourcePhoneNumber != null) {
				return false;
			}
		} else if (!sourcePhoneNumber.equals(other.sourcePhoneNumber)) {
			return false;
		}
		if (startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!startDate.equals(other.startDate)) {
			return false;
		}
		if (targetPhoneNumber == null) {
			if (other.targetPhoneNumber != null) {
				return false;
			}
		} else if (!targetPhoneNumber.equals(other.targetPhoneNumber)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public CallType getType() {
		return type;
	}

	public long getDurationInSeconds() {
		if (startDate != null && endDate != null) {
			return endDate.getTime() - startDate.getTime();
		}
		return 0;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public PhoneNumber getSourcePhoneNumber() {
		return sourcePhoneNumber;
	}

	public PhoneNumber getTargetPhoneNumber() {
		return targetPhoneNumber;
	}

	@Override
	public String toString() {
		return "Call [type=" + type + ", startDate=" + startDate + ", endDate=" + endDate + ", durationInSeconds="
		        + getDurationInSeconds() + ", sourcePhoneNumber=" + sourcePhoneNumber + ", targetPhoneNumber="
		        + targetPhoneNumber + "]";
	}

}
