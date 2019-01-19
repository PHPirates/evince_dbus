package nl.deltadak.evincedbus;

import org.freedesktop.dbus.DBusAsyncReply;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.interfaces.CallbackHandler;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.connections.BusAddress;
import org.freedesktop.DBus;
import org.freedesktop.dbus.interfaces.Introspectable;

/**
 * Provides forward search for Evince: sync Evince with the source.
 */
public class ForwardSearch {

    String evinceDaemonPath = "/org/gnome/evince/Daemon";
    String evinceDaemonName = "org.gnome.evince.Daemon";
    String evinceDaemonInterface = "org.gnome.evince.Daemon";

    String evincePath = "/org/gnome/evince/Evince";
    String evinceInterface = "org.gnome.evince.Application";

    String evinceWindowPath = "/org/gnome/evince/Window/0";
    String evinceWindowInterface = "org.gnome.evince.Window";

    void forwardSearch() {

        String pdfFile = "main.pdf";
        String texFile = "main.tex";
        int lineNumber = 5;


        DBusConnection connection = null;
        try {
            // Initialize a session bus
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);

            // Get object using bus (deamon) name and object (daemon) path
            // https://dbus.freedesktop.org/doc/dbus-tutorial.html
            // Applications can send messages to this bus name, object, and interface to execute method calls.
            // We are using D-Bus with a message bus daemon
            // https://dbus.freedesktop.org/doc/dbus-java/dbus-java/dbus-javase2.html#x9-130002
            // This gets a reference to the “/Test” object on the process with the name “foo.bar.Test” . The object implements the Introspectable interface, and calls may be made to methods in that interface as if it was a local object.
            Introspectable interfaceDaemonObject = connection.getRemoteObject(evinceDaemonName, evinceDaemonPath, Introspectable.class);
            // Now we will get xml data with all available methods, especially those under the org.gnome.evince.Daemon are of interest for us.
            String data = interfaceDaemonObject.Introspect();
            System.out.println(data);

            // Now we have the evince daemon object, we want to call a method on it.
            // We want to do this asynchronously so we don't have to wait for a reply.
            // Execute FindDocument via daemon interface
            CallbackHandler<Object> callback = new CallBackExample();
            connection.callWithCallback(interfaceDaemonObject, "FindDocument", callback, pdfFile, true);



            Introspectable interfaceWindowObject = connection.getRemoteObject(evinceWindowInterface, evinceWindowPath, Introspectable.class);

            connection.callWithCallback(interfaceWindowObject, "SyncView", callback, texFile, lineNumber, 0, evinceWindowInterface);


        } catch (DBusException e) {
            System.out.println("Caught DBusException: ");
            e.printStackTrace();
        } catch (DBusExecutionException e) {
            System.out.println("Caught DBusExecutionException: " + e.getMessage());
            e.printStackTrace();
        }


    }

    /**
     *
     * @param args args
     */
    public static void main(String[] args) {
        new ForwardSearch().forwardSearch();
    }

//    conn.addSigHandler(TestSignalInterface.TestSignal.class,
//            new SignalHandler());
//    This sets up a signal handler for the given signal type. SignalHandler.handle will be called in a new thread with an instance of TestSignalInterface.TestSignal when that signal is recieved.
}
