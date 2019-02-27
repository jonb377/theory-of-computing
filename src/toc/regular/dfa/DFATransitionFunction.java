package toc.regular.dfa;

import toc.regular.TransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class DFATransitionFunction extends TransitionFunction<Integer> {

    /**
     * Parse the transition function from the given string.
     * Format: (1 a 0)(1 b 2)...
     * will map delta(1, a) = 0, delta(1, b) = 2, etc.
     * Parentheses are optional.
     *
     * Any values not specified will map to the trap state by default.
     *
     * @param s A string of the specified format.
     * @return A new transition function representing the string.
     */
    public static DFATransitionFunction fromString(String s) {
        s = s.replaceAll("[\\(\\),]", " ");
        Scanner in = new Scanner(s);
        Map<Integer, Map<Character, Integer>> map = new HashMap<>();
        int maxState = -1;
        while (in.hasNext()) {
            int state = in.nextInt();
            char transition = in.next().charAt(0);
            int result = in.nextInt();

            if (!map.containsKey(state)) {
                if (state > maxState) maxState = state;
                map.put(state, new HashMap<>());
            }
            map.get(state).put(transition, result);
        }
        Map<Character, Integer>[] arr = new Map[maxState];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = map.getOrDefault(i, new HashMap<>());
        }
        return createTotalTransitionFunction(arr, arr.length > 0 ? arr[0].keySet() : Collections.EMPTY_SET);
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
    public static DFATransitionFunction createTotalTransitionFunction(List<List<Integer>> transition, Map<Character, Integer> map) {
        // Verify this is a valid transition function.
        for (int q = 0; q < transition.size(); q++) {
            for (char a = 0; a < transition.get(q).size(); a++) {
                if (map.keySet().contains(a) && (transition.get(q).get(a) == null || transition.get(q).get(a) < 0 || transition.get(q).get(a) > transition.size())) {
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
     * The transition must be an unmodifiable list of unmodifiable lists.
     * @param transition
     * @param map
     */
    private DFATransitionFunction(List<List<Integer>> transition, Map<Character, Integer> map) {
        super(transition, map);
    }

    @Override
    public Integer of(int state, char c) {
        if (!map.containsKey(c)) {
            throw new RuntimeException("Character not in alphabet.");
        }
        Integer result = transitions.get(state).get(map.get(c));
        return result == null ? 0 : result;
    }

    /**
     * Create a new transition function with only the specified states.
     * @param states The states to keep from the transition function.
     * @return A new DFATransitionFunction with a subset of the original states.
     */
    public DFATransitionFunction keepStates(Set<Integer> states) {
        System.out.println("Keeping states " + states  + " out of " + numStates());
        int[] offsets = new int[transitions.size()];
        int currOffset = 0;
        for (int i = 0; i < offsets.length; i++) {
            if (!states.contains(i)) {
                offsets[i] = i;
                currOffset ++;
            } else {
                offsets[i] = currOffset;
            }
        }
//        System.out.println("Offsets: " + Arrays.toString(offsets));
        List<List<Integer>> newTransition = new ArrayList<>();
        for (int i = 0; i < transitions.size(); i++) {
            if (!states.contains(i)) continue;
            ArrayList<Integer> curr = new ArrayList<>();
            for (int j = 0; j < map.size(); j++) {
                curr.add(transitions.get(i).get(j) - offsets[transitions.get(i).get(j)]);
            }
            newTransition.add(Collections.unmodifiableList(curr));
//            System.out.println(Arrays.toString(newTransition[currTransition]));
        }
        return new DFATransitionFunction(Collections.unmodifiableList(newTransition), map);
    }
}
