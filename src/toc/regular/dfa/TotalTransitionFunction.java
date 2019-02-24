package toc.regular.dfa;

import java.util.*;
import java.util.stream.IntStream;

import static toc.Settings.ALPHABET_SIZE;

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
     * Create a new toc.regular.dfa.TotalTransitionFunction from the given mapping.
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

    public TotalTransitionFunction keepStates(Set<Integer> states) {
        System.out.println("Keeping states " + states  + " out of " + numStates());
        states.add(0);
        int newSize = states.size();    // One additional for the trap state
        int[] offsets = new int[transitions.length];
        int currOffset = 0;
        for (int i = 0; i < offsets.length; i++) {
            if (!states.contains(i)) {
                offsets[i] = i;
                currOffset ++;
            } else {
                offsets[i] = currOffset;
            }
        }
        System.out.println("Offsets: " + Arrays.toString(offsets));
        int[][] newTransition = new int[newSize][ALPHABET_SIZE];
        int currTransition = 0;
        for (int i = 0; i < transitions.length; i++) {
            if (!states.contains(i)) continue;
            for (int j = 0; j < ALPHABET_SIZE; j++) {
                newTransition[currTransition][j] = transitions[i][j] - offsets[transitions[i][j]];
            }
            currTransition++;
        }
        return new TotalTransitionFunction(newTransition);
    }
}
