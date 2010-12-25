package de.knallisworld.netconnect.growl;

import java.util.Collection;

import de.knallisworld.netconnect.model.NotificationType;

public interface GrowlNotification {
	void notify(Collection<String> hosts, NotificationType type, String title, String message);

	void notify(String host, NotificationType type, String title, String message);
}
