package org.gnome.evince;


import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.UInt32;

/**
 * This interface can be used to call methods on the dbus.
 */
@SuppressWarnings("NewMethodNamingConvention")
public interface Window extends DBusInterface {

    /**
     * Highlight a certain line in a pdf file.
     * I have not been able to find any documentation about this method.
     *
     * Method signature as per the D-Bus API export at https://github.com/GNOME/evince/blob/master/shell/ev-gdbus.xml
     *
     * @param sourceFile Path to a source LaTeX file.
     * @param sourcePoint A pair of a line number (int) and something else which apparently can just be 1 (also int).
     * @param timestamp Seems to work when this is set to 1.
     */
    void SyncView(String sourceFile, Struct sourcePoint, UInt32 timestamp);
}
