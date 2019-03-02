package org.gnome.evince;


import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.UInt32;

/**
 * This interface can be used to call methods on the dbus.
 */
@SuppressWarnings("NewMethodNamingConvention")
public interface Window extends DBusInterface {

    // Declare a method on the DBus
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

    public class SyncSource extends DBusSignal {
        public SyncSource(String objectpath, String inputFile, Struct sourceLink, UInt32 timestamp) throws DBusException {
            super(objectpath, inputFile, sourceLink, timestamp);
            System.out.println("SyncSource constructor");
        }

        // Declaration of the signal, see https://dbus.freedesktop.org/doc/dbus-java/dbus-java/dbus-javase4.html#x17-170004
        // todo why are the output parameters of the signal input parameters here?
        public void SyncSource(String path, String inputFile, Struct sourceLink, UInt32 timestamp) {
            System.out.println("SyncSource handler");
        }
    }

    @SuppressWarnings("JavaDoc") // todo
    public class DocumentLoaded extends DBusSignal {
//        public DocumentLoaded(String source, String path, String iface, String member, String sig, Object... args) throws DBusException {
//            super(source, path, iface, member, sig, args);
//        }

        public DocumentLoaded(String uri) throws DBusException {
            super(uri);
            System.out.println("Handling callback! uri: " + uri);
        }
    }
}
