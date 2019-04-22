package nl.deltadak.evincedbus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Provides backward search for Evince: sync IntelliJ with Evince.
 */
class BackwardSearch {

    /**
     * Start listening for backward search calls on the D-Bus.
     */
    fun startListening() {
        try {
            // Listen on the session D-Bus to catch SyncSource signals emitted by Evince
            val commands = arrayOf("dbus-monitor", "--session")
            val process = Runtime.getRuntime().exec(commands)
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String? = bufferedReader.readLine()

            while (line != null) {
                // Check if a SyncSource signal appeared from Evince and if so, read the contents
                if (line!!.contains("interface=org.gnome.evince.Window; member=SyncSource")) {
                    // Get the value between quotes
                    val filenameLine = bufferedReader.readLine()
                    var filename = filenameLine.substring(filenameLine.indexOf("\"") + 1, filenameLine.lastIndexOf("\""))
                    filename = filename.replaceFirst("file://".toRegex(), "")

                    // Pass over the "struct {" line
                    bufferedReader.readLine()

                    // Get the location represented by the struct
                    val lineNumberLine = bufferedReader.readLine()
                    val lineNumberString = lineNumberLine.substring(lineNumberLine.indexOf("int32") + 6, lineNumberLine.indexOf("int32") + 7)
                    val lineNumber = Integer.parseInt(lineNumberString)

                    // Sync the IDE
                    syncSource(filename, lineNumber)
                }

                line = bufferedReader.readLine();
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Sync IntelliJ (location is hardcoded) on the given source file and line number.
     *
     * @param filePath   Full to a file.
     * @param lineNumber Line number in the file.
     */
    fun syncSource(filePath: String, lineNumber: Int) {
        // Command to execute for backward search, where %f is the tex file to load and %l is the line number
        val command = "/home/thomas/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/191.6707.61/bin/idea.sh --line $lineNumber $filePath"

        try {
            Runtime.getRuntime().exec(command)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

/**
 * Execute the backward search example.
 */
fun main() {
    // Run in a coroutine so the main thread can continue
    GlobalScope.launch {
        BackwardSearch().startListening()
    }

    println("Continuing...")
    // Assume the program runs on indefinitely (so the program doesn't finish, which would kill the listening coroutine)
    runBlocking {
        while (true) {}
    }
}