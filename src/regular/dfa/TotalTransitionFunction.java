package regular.dfa;

import java.util.Arrays;

/**
 * @author Jon Bolin
 */
public class TotalTransitionFunction {

    /**
     * Parse the transition function from the given string.
     * Format: 1: a0 b2 c1, 2: b2
     * will map delta(1, a) = 0, delta(1, b) = 2, etc.
     *
     * Any values not specified will map to the trap state by default.
     *
     * @param s A string of the specified format.
     * @return A new transition function representing the string.
     */
    public static TotalTransitionFunction fromString(String s) {
        throw new RuntimeException("I'm Lazy :D");
    }

    /**
     * Verifies that the transition function is valid.
     *
     * A valid transition function satisfies:
     *    1. transition[0] = {0, 0, ..., 0}, i.e. 0 is the trap state
     *    2. 0 <= transition[q][a] < transition.length
     *
     * @param transition The transition function to be tested.
     * @return true if valid, false otherwise.
     */
    private static boolean verifyTransitionFunction(int[][] transition) {
        int numStates = transition.length;
        if (Arrays.stream(transition[0]).anyMatch((x) -> x != 0)) {
            return false;
        }
        return Arrays.stream(transition).allMatch((x) -> Arrays.stream(x).allMatch((i) -> i >= 0 && i < numStates));
    }

    int[][] transitions;

    /**
     * Create a new regular.dfa.TotalTransitionFunction from the given mapping.
     * @param transition Transition function. transition[q][a] is delta(q, a).
     *                   q = 0 is the trap state; transition[0] must be {0, 0, ..., 0}
     */
    public TotalTransitionFunction(int[][] transition) {
        if (!verifyTransitionFunction(transition)) {
            throw new RuntimeException("Invalid transition function!");
        }
        transitions = transition;
    }

    /**
     * @return The number of states this transition function is defined for.
     */
    public int numStates() {
        return transitions.length;
    }

    /**
     * Defines the transition function delta for a finite acceptor.
     * @param c
     * @param state
     * @return
     */
    int of(int state, char c) {
        return transitions[state][(int) c];
    }
}
