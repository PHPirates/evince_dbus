package nl.deltadak.evincedbus;

import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.interfaces.CallbackHandler;

/**
 *
 */
public class CallBackExample implements CallbackHandler {

    @Override
    public void handle(java.lang.Object r) {
        System.out.println("Handling callback: " + r);
    }

    @Override
    public void handleError(DBusExecutionException e) {
        System.out.println("Handling error:");
        e.printStackTrace();
    }
}