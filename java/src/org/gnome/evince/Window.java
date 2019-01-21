package org.gnome.evince;


import org.freedesktop.dbus.interfaces.DBusInterface;

/**
 * This interface can be used to call methods on the dbus.
 */
@SuppressWarnings("NewMethodNamingConvention")
public interface Window extends DBusInterface {

    void SyncView(String sourceFile, int sourcePoint, )
}
