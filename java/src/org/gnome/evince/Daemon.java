package org.gnome.evince;

import org.freedesktop.dbus.interfaces.DBusInterface;

/**
 * This interface can be used to call methods on the dbus.
 */
@SuppressWarnings("NewMethodNamingConvention")
public interface Daemon extends DBusInterface {
    /**
     * Find a certain document.
     *
     * @param uri Path to a pdf file, prepended with file://
     * @param spawn Probably whether to spawn Evince or not.
     * @return The name owner of the evince process for the given document URI. Source: https://mail.gnome.org/archives/commits-list/2010-July/msg02054.html
     */
    String FindDocument(String uri, Boolean spawn);
}
