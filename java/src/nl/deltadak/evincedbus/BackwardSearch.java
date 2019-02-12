package nl.deltadak.evincedbus;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.gnome.evince.Daemon;
import org.gnome.evince.Window;
import org.gnome.gtk.Gtk;

/**
 * Backward search.
 */
public class BackwardSearch {

    /**
     * Start backward search on main.pdf.
     */
    void backwardSearch() {
        String workDir = System.getProperty("user.dir");
        String pdfFile = workDir + "/main.pdf";
        String texFile = workDir + "/main.tex";

        DBusConnection connection;

        try {
            // Initialize a session bus
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);

            Daemon daemon = connection.getRemoteObject("org.gnome.evince.Daemon", "/org/gnome/evince/Daemon", Daemon.class);

            // https://dbus.freedesktop.org/doc/dbus-java/api/org/freedesktop/dbus/DBusConnection.html#addSigHandler(java.lang.Class,%20java.lang.String,%20org.freedesktop.dbus.DBusInterface,%20org.freedesktop.dbus.DBusSigHandler)
            // org.gnome.evince.Window.DocumentLoaded is the signal to watch for
            // We also give a handler to be called when a signal is received
            connection.addSigHandler(
//                    OnDocLoadedSignalInterface.OnDocLoadedSignal.class,
                    Window.DocumentLoaded.class,
//                    daemon,
                    new OnDocLoadedSignalHandler<Window.DocumentLoaded>());

            // Find the process owner
            // todo reply handler
            String processOwner = daemon.FindDocument("file://" + pdfFile, true);
            System.out.println("Owner: " + processOwner);

            connection.callWithCallback(daemon, "FindDocument", new CallBackExample(), "file://" + pdfFile, true);



        } catch (DBusException e) {
            e.printStackTrace();
        }

    }

    /**
     * Start backward search (not blocking execution).
     *
     * @param args args
     */
    public static void main(String[] args) {
        // Has to be called before any other gnome classes are used
        Gtk.init(args);

        new BackwardSearch().backwardSearch();

        // http://java-gnome.sourceforge.net/doc/api/4.1/org/gnome/gtk/Gtk.html
        // "This method blocks, ie, it does not return until the GTK main loop is terminated."
        // Suggests that the following call starts the main loop.
        // See the top of that file for an example "typical program", which first calls init() and main() at the very end.
        Gtk.main();

    }
}
