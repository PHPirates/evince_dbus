package org.gnome.evince;

import org.freedesktop.dbus.interfaces.DBusInterface;

/**
 * Like Introspectable but then for the Evince Daemon object?
 */
public interface Daemon extends DBusInterface {
    /**
     *
     * @param uri todo
     * @param spawn todo
     * @return todo
     */
    public String FindDocument(String uri, Boolean spawn);
}
