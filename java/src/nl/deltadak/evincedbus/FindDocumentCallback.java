package nl.deltadak.evincedbus;

import org.freedesktop.SyncSourceSignalHandler;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.CallbackHandler;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.UInt32;
import org.gnome.evince.Window;

/**
 * todo
 */
public class FindDocumentCallback implements CallbackHandler {

    /**
     * Given the process owner of a document, register to the SyncSource signal with a callback which will be called when backward search command is executed in Evince.
     *
     * NOTE This is a failed attempt, the exception was not solved.
     *
     * @param processOwner Process owner.
     */
    @Override
    public void handle(java.lang.Object processOwner) {
        System.out.println("Handling FindDocument callback of owner: " + processOwner);
        try {
            // Get the connection
            DBusConnection connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);

            // Get the Window object, similar as with the Daemon object, using name, path and destination class
            Window window = connection.getRemoteObject((String)processOwner, "/org/gnome/evince/Window/0", Window.class);

            //     window.connect_to_signal("SyncSource", on_sync_source)
            System.out.println("Connecting to the SyncSource signal...");
            // todo Exception while running signal handler DBusException: Error deserializing message: number of parameters didn't match receiving signature
            DBusSigHandler<Window.SyncSource> handler = new DBusSigHandler<Window.SyncSource>() {
                @Override
                public void handle(Window.SyncSource signal) {
                    System.out.println("Handling call of signal " + signal + " from file " + signal.inputFile);

                }

                public void handle(String inputFile, Struct sourceLink, UInt32 timestamp) {

                }
            };

            // Above exception happens with any of these:
            connection.addSigHandler(Window.SyncSource.class, window, handler);
//            connection.addSigHandler(Window.SyncSource.class, handler);
//            connection.addSigHandler(Window.SyncSource.class, (String)processOwner, handler);

        } catch (DBusException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleError(DBusExecutionException e) {
        System.out.println("Handling error:");
        e.printStackTrace();
    }
}