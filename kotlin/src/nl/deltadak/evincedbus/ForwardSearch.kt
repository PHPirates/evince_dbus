package nl.deltadak.evincedbus

import org.freedesktop.dbus.connections.impl.DBusConnection
import org.gnome.evince.Daemon

/**
 * Provides forward search for Evince: sync Evince with the source.
 */
class ForwardSearch {

    /**
     * Object path of the Evince daemon. Together with the object name, this allows us to find the
     * D-Bus object which allows us to execute the FindDocument function, which is exported on the D-Bus
     * by Evince.
     */
    val evinceDaemonPath = "/org/gnome/evince/Daemon"

    /**
     * Object name of the Evince daemon.
     */
    val evinceDaemonName = "org.gnome.evince.Daemon"

    /**
     * Execute forward search with Evince.
     */
    fun forwardSearch() {

        // Some predefined variables for this example.
        val workDir = System.getProperty("user.dir")
        val pdfFile = "$workDir/main.pdf"
        val texFile = "$workDir/main.tex"
        val lineNumber = 5

        // Initialize a session bus
        val connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION)

        // Get the Daemon object using its bus name and object path
        val daemon = connection.getRemoteObject(evinceDaemonName, evinceDaemonPath, Daemon::class.java)

        // Call the method on the D-Bus by using the function we defined in the Daemon interface
        val documentProcessOwner = daemon.FindDocument("file://$pdfFile", true)

        // Theoretically we should use the Java D-Bus bindings as well to call SyncView, but that did
        // not succeed with a NoReply exception, so we will execute a command via the shell.
        val command = "gdbus call --session --dest $documentProcessOwner --object-path /org/gnome/evince/Window/0 --method org.gnome.evince.Window.SyncView $texFile '($lineNumber, 1)' 0"
        Runtime.getRuntime().exec(arrayOf("bash", "-c", command))
    }

}

/**
 * Execute the forward search example.
 */
fun main() {
    ForwardSearch().forwardSearch()
}