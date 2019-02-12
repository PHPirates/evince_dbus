package nl.deltadak.evincedbus;

import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

@SuppressWarnings("JavaDoc") // todo
public interface OnDocLoadedSignalInterface extends DBusInterface {
    public class OnDocLoadedSignal extends DBusSignal {

        public OnDocLoadedSignal(String source, String path, String iface, String member, String sig, Object... args) throws DBusException {
            super(source, path, iface, member, sig, args);
        }
    }
}
