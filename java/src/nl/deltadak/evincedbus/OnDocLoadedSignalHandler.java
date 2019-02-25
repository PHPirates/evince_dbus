package nl.deltadak.evincedbus;

import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;

@SuppressWarnings("JavaDoc") // todo
public class OnDocLoadedSignalHandler<OnDocLoadedSignal> implements DBusSigHandler {
    @Override
    public void handle(DBusSignal signal) {
        System.out.println("Handling call of signal " + signal);
    }

    public void handle(String uri, Object... args) {
        System.out.println("Handling call of uri " + uri);
    }
}
