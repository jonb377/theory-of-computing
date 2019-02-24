package regular.nfa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Jon Bolin
 */
public class TransitionFunction {

    private static final Set<Integer> TRAP = new HashSet<>(Arrays.asList(0));

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
    private static boolean verifyTransitionFunction(Set<Integer>[][] transition) {
        int numStates = transition.length;
        if (Arrays.stream(transition[0]).anyMatch(Objects::nonNull)) {
            return false;
        }
        return Arrays.stream(transition).allMatch(
                (x)-> Arrays.stream(x).allMatch(
                        (s) -> s == null || s.stream().allMatch(
                                (i) -> i >= 0 && i < numStates)));
    }

    Set<Integer>[][] transitions;

    /**
     * Create a new regular.dfa.TotalTransitionFunction from the given mapping.
     * @param transition Transition function. transition[q][a] is delta(q, a).
     *                   q = 0 is the trap state; transition[0] must be {0, 0, ..., 0}
     */
    public TransitionFunction(Set<Integer>[][] transition) {
        if (!verifyTransitionFunction(transition)) {
            throw new RuntimeException("Invalid transition function!");
        }
        transitions = transition;
    }



    Set<Integer> of(int state, char a) {
        Set<Integer> result = transitions[state][a];
        return result == null ? TRAP : result;
    }

}
