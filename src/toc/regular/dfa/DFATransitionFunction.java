package toc.regular.dfa;

import toc.regular.TransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class DFATransitionFunction extends TransitionFunction<Integer> {

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
    public static DFATransitionFunction fromString(String s) {
        throw new RuntimeException("I'm Lazy :D");
    }

    /**
     * Creates a total transition function defined over the alphabet.
     * @param transition The transition function defined over the character's ascii values.
     *                   i.e., transition[0]['a'] is the transition from state 0 when parsing an 'a'.
     * @param Σ The alphabet set.
     * @return A transition function.
     */
    public static DFATransitionFunction createTotalTransitionFunction(Map<Character, Integer>[] transition, Set<Character> Σ) {
        // Verify this is a valid transition function.
        for (int q = 0; q < transition.length; q++) {
            if (transition[q].size() != Σ.size()) {
                throw new RuntimeException("Busted Transition Function");
            }
            for (char a : transition[q].keySet()) {
                if (!Σ.contains(a) || transition[q].get(a) == null || transition[q].get(a) < 0 || transition[q].get(a) > transition.length) {
                    throw new RuntimeException("Busted Transition Function");
                }
            }
        }
        return new DFATransitionFunction(transition, Σ);
    }

    /**
     * Creates a total transition function defined over the alphabet.
     * @param transition The transition function defined over the characters' mapping.
     *                   i.e., transition[0][map.get('a')] is the transition from state 0 when parsing an 'a'.
     * @return A transition function.
     */
    public static DFATransitionFunction createTotalTransitionFunction(Integer[][] transition, Map<Character, Integer> map) {
        // Verify this is a valid transition function.
        for (int q = 0; q < transition.length; q++) {
            for (char a = 0; a < transition[q].length; a++) {
                if (map.keySet().contains(a) && (transition[q][a] == null || transition[q][a] < 0 || transition[q][a] > transition.length)) {
                    throw new RuntimeException("Busted Transition Function");
                }
            }
        }
        return new DFATransitionFunction(transition, map);
    }

    /**
     * Create a new toc.regular.dfa.DFATransitionFunction from the given mapping.
     * @param transition Transition function. transition[q][a] is delta(q, a).
     *                   q = 0 is the trap state; transition[0] must be {0, 0, ..., 0}
     */
    private DFATransitionFunction(Map<Character, Integer>[] transition, Set<Character> Σ) {
        super(transition, Σ);
    }

    /**
     * Create a new toc.regular.dfa.DFATransitionFunction, given the transition and map.
     * @param transition
     * @param map
     */
    private DFATransitionFunction(Integer[][] transition, Map<Character, Integer> map) {
        super(transition, map);
    }

    @Override
    public Integer of(int state, char c) {
        if (!map.containsKey(c)) {
            throw new RuntimeException("Character not in alphabet.");
        }
        Integer result = (Integer) transitions[state][map.get(c)];
        return result == null ? 0 : result;
    }


    /*
        TODO: Finish conversion to non-ascii alphabet.

     */

    public DFATransitionFunction keepStates(Set<Integer> states) {
        System.out.println("Keeping states " + states  + " out of " + numStates());
        int newSize = states.size();
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
        Integer[][] newTransition = new Integer[newSize][map.size()];
        int currTransition = 0;
        for (int i = 0; i < transitions.length; i++) {
            if (!states.contains(i)) continue;
            for (int j = 0; j < map.size(); j++) {
                newTransition[currTransition][j] = (Integer) transitions[i][j] - offsets[(int) transitions[i][j]];
            }
            System.out.println(Arrays.toString(newTransition[currTransition]));
            currTransition++;
        }
        return new DFATransitionFunction(newTransition, map);
    }
}
