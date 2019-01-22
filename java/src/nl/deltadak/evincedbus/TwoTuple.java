package nl.deltadak.evincedbus;

import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.annotations.Position;

/**
 * This is our struct to be used for sending linenumber information via D-Bus. See https://dbus.freedesktop.org/doc/dbus-java/dbus-java/dbus-javase7.html
 *
 * @param <A> First element type.
 * @param <B> Second element type.
 */
public final class TwoTuple extends Struct {
    // As per the documentation of Tuple, we have to specify the order using annotations

    /** First element. */
    @Position(0)
    public final int a;

    /** Second element. */
    @Position(1)
    public final int b;

    /**
     * Create a tuple of two values.
     *
     * @param a First element.
     * @param b Second element.
     */
    public TwoTuple(int a, int b) {
        this.a = a;
        this.b = b;
    }
}
