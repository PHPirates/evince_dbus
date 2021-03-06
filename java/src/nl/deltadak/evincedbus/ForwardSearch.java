package nl.deltadak.evincedbus;

import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.Tuple;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.interfaces.CallbackHandler;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Introspectable;
import org.freedesktop.dbus.types.UInt32;
import org.gnome.evince.Daemon;
import org.gnome.evince.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.System.exit;

/**
 * Provides forward search for Evince: sync Evince with the source.
 */
@SuppressWarnings({"CloneDetection", "LawOfDemeter"})
public class ForwardSearch {

    String evinceDaemonPath = "/org/gnome/evince/Daemon";
    String evinceDaemonName = "org.gnome.evince.Daemon";
    String evinceDaemonInterface = "org.gnome.evince.Daemon";

    String evincePath = "/org/gnome/evince/Evince";
    String evinceInterface = "org.gnome.evince.Application";

    String evinceWindowPath = "/org/gnome/evince/Window/0";
    String evinceWindowInterface = "org.gnome.evince.Window";

    void forwardSearch() {

        String workDir = System.getProperty("user.dir");
        String pdfFile = workDir + "/main.pdf";
        String texFile = workDir + "/main.tex";
        int lineNumber = 5;


        DBusConnection connection;
        try {
            // Initialize a session bus
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);

            // Get object using bus (deamon) name and object (daemon) path
            // https://dbus.freedesktop.org/doc/dbus-tutorial.html
            // Applications can send messages to this bus name, object, and interface to execute method calls.
            // We are using D-Bus with a message bus daemon
            // https://dbus.freedesktop.org/doc/dbus-java/dbus-java/dbus-javase2.html#x9-130002
            // This gets a reference to the “/Test” object on the process with the name “foo.bar.Test” . The object implements the Introspectable interface, and calls may be made to methods in that interface as if it was a local object.
            Introspectable introspectableDaemon = connection.getRemoteObject(evinceDaemonName, evinceDaemonPath, Introspectable.class);
            // Now we will get xml data with all available methods, especially those under the org.gnome.evince.Daemon are of interest for us.
//            System.out.println(introspectableDaemon.Introspect());

            // Now the same, but to get the right object (for executing FindDocument)
            // Daemon is the interface we have declared ourselves, see the org.gnome.evince package
            Daemon daemon = connection.getRemoteObject("org.gnome.evince.Daemon", "/org/gnome/evince/Daemon", Daemon.class);
            // We will find the process owner in order to use it for SyncView later on as busname
            String processOwner = daemon.FindDocument("file://" + pdfFile, true);
            // Will be something like :1.155
            System.out.println("Owner: " + processOwner);


            // todo call methods asynchronously
            // Now we have the evince daemon object, we want to call a method on it.
            // We want to do this asynchronously so we don't have to wait for a reply.
            // Execute FindDocument via daemon interface
//            CallbackHandler<Object> callback = new FindDocumentCallback();
//            connection.callWithCallback(introspectable, "FindDocument", callback, pdfFile, true);

            // Introspect the Window object to see method signatures
            Introspectable introspectableWindow = connection.getRemoteObject(processOwner, "/org/gnome/evince/Window/0", Introspectable.class);
//            System.out.println(introspectableWindow.Introspect());

            String command = "gdbus call --session --dest " + processOwner + " --object-path /org/gnome/evince/Window/0 --method org.gnome.evince.Window.SyncView " + texFile + " '(" + lineNumber + ", 1)' 0";
            Runtime.getRuntime().exec(new String[] { "bash", "-c", command});


            // For the record, failed attempt at executing SyncView via the Java D-Bus bindings:

            // Result: NoReply exception. But we checked:
            // dbusName is right name for introspection, the org.gnome.evince.Window doesn't help here and fails with introspection
            // the object path is right for introspectino, other path without 0 doesn't work
            // Interface name org.gnome.evince.Window is provided by the Window.class
            // Struct is like example in docs
            // Parameter types correspond exactly to xml, translated according to table in docs
            // But: changing them to something insensible results in the same error
            Window window = connection.getRemoteObject(processOwner, "/org/gnome/evince/Window/0", Window.class);

            // We have created our own tuple TwoTuple in order to pass a tuple/struct to SyncView
            Struct lineTuple = new TwoTuple(lineNumber, 1);
            window.SyncView(texFile, lineTuple, new UInt32(0));

        } catch (DBusException e) {
            System.out.println("Caught DBusException: ");
            e.printStackTrace();
        } catch (DBusExecutionException e) {
            System.out.println("Caught DBusExecutionException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Caught exception when executing SyncView via terminal: " + e.getMessage());
            e.printStackTrace();
        }

        exit(0);
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

