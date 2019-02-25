package nl.deltadak.evincedbus;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.interfaces.CallbackHandler;
import org.gnome.evince.Window;

/**
 * todo
 */
public class FindDocumentCallback implements CallbackHandler {

    /**
     * Given the process owner of a document, register to the SyncSource signal with a callback which will be called when backward search command is executed in Evince.
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
            connection.addSigHandler();

//            connection.addSigHandler(
//                    Window.DocumentLoaded.class,
//                    new OnDocLoadedSignalHandler<Window.DocumentLoaded>());

//            connection.addSigHandler(
//                    OnDocLoadedSignalInterface.OnDocLoadedSignal.class,
//                    daemon
//                    );

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