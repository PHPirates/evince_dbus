package nl.deltadak.evincedbus;

import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.annotations.Position;

/**
 * This is our tuple to be used for sending linenumber information via D-Bus.
 * @param <A> First element type.
 * @param <B> Second element type.
 */
public class TwoTuple<A, B> extends Struct {
    // As per the documentation of Tuple, we have to specify the order using annotations

    /** First element. */
    @Position(0)
    public A a;

    /** Second element. */
    @Position(1)
    public B b;

    /**
     * Create a tuple of two values.
     *
     * @param a First element.
     * @param b Second element.
     */
    public TwoTuple(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
