package org.freedesktop;

import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;

@SuppressWarnings("ALL") // todo
public class SyncSourceSignalHandler<SyncSource> implements DBusSigHandler {
    @Override
    public void handle(DBusSignal signal) {
        System.out.println("Handling call of signal " + signal);
    }
}
