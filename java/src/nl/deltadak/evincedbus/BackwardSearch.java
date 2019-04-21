package nl.deltadak.evincedbus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Backward search.
 */
public class BackwardSearch {

    /**
     * Start listening for backward search calls on the dbus directly, not registering signals using dbus-java.
     * <p>
     * By running the command {@code dbus-monitor --session} on the command line, you can view all information going through the D-Bus. This is a lot of information, but it also includes all information when a backward search signal is emitted by Evince.
     * <p>
     * As explained in backward_search.sh, it is also possible to listen specifically for one object path, but then you do not get all the contents of the signal.
     */
    public void startListening() {
        try {

            String[] commands = {"dbus-monitor", "--session"};
            Process process = Runtime.getRuntime().exec(commands);
            InputStream stdin = process.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(stdin);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                // Check if a SyncSource signal appeared from Evince
                if (line.contains("interface=org.gnome.evince.Window; member=SyncSource")) {
                    // Read the contents of the signal, using the output which looks like the following.
                    // Note that these values are also described in the
                    /*
                    signal time=1555856761.368235 sender=:1.47 -> destination=(null destination) serial=38 path=/org/gnome/evince/Window/0; interface=org.gnome.evince.Window; member=SyncSource
                    string "file:///path/to/main.tex"
                    struct {
                        int32 9
                        int32 -1
                    }
                    uint32 2250697
                    */
                    // Get the value between quotes
                    String filenameLine = bufferedReader.readLine();
                    String filename = filenameLine.substring(filenameLine.indexOf("\"") + 1, filenameLine.lastIndexOf("\""));
                    filename = filename.replaceFirst("file://", "");

                    // Pass over the "struct {" line
                    bufferedReader.readLine();

                    // Get the location represented by the struct
                    String lineNumberLine = bufferedReader.readLine();
                    String lineNumberString = lineNumberLine.substring(lineNumberLine.indexOf("int32") + 6, lineNumberLine.indexOf("int32") + 7);
                    // todo error handling
                    int lineNumber = Integer.parseInt(lineNumberString);

                    // The second value seems to be unused, so we can sync IntelliJ now
                    syncSource(filename, lineNumber);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sync IntelliJ (location is hardcoded) on the given source file and line number.
     *
     * @param filePath   Full to a file.
     * @param lineNumber Line number in the file.
     */
    public void syncSource(String filePath, int lineNumber) {
        // Command to execute for backward search, where %f is the tex file to load and %l is the line number
        String command = "/home/thomas/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/191.6707.61/bin/idea.sh --line ";
        command += lineNumber + " " + filePath;

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start backward search.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        new BackwardSearch().startListening();

//        new BackwardSearch().syncSource("/home/thomas/GitRepos/evince_dbus/main.tex",  9);
    }
}
