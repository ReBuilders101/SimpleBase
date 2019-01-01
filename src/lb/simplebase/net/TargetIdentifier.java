package lb.simplebase.net;

import java.util.HashMap;

/**
 * All objects implementing this interface can be used to identify a network target.
 * Targets are compared by instance, so the instances of all network targets should be available somewhere
 * in the program.<br>
 * Because {@link TargetIdentifier} is used as a key in {@link HashMap}s, all implementatioons should
 * implement the {@link #hashCode()} method.
 *  */
public interface TargetIdentifier {
}
