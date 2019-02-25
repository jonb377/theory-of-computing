package toc.regular;

import java.util.*;

/**
 * @author Jon Bolin
 */
public abstract class TransitionFunction<T> {

    public Map<Character, Integer> map;
    protected Object[][] transitions;

    public TransitionFunction(T[][] transition, Set<Character> Σ) {
        // Create the map: Σ -> {0, 1, ..., |Σ| - 1}
        Map<Character, Integer> map = new HashMap<>();
        List<Character> alphaList = new ArrayList<>(Σ);
        for (int i = 0; i < alphaList.size(); i++) map.put(alphaList.get(i), i);

        this.map = Collections.unmodifiableMap(map);

        // Create the new transition function
        this.transitions = new Object[transition.length][Σ.size()];
        for (int i = 0; i < transitions.length; i++) {
            for (int j = 0; j < Σ.size(); j++) {
                this.transitions[i][j] = transition[i][alphaList.get(j)];
            }
        }
    }

    protected  TransitionFunction(T[][] transition, Map<Character, Integer> map) {
        this.map = map;
        this.transitions = transition;
    }

    /**
     * @return The number of states this transition function is defined for.
     */
    public int numStates() {
        return transitions.length;
    }

    /**
     * Determines the next state given the current state and transition.
     * @param state The current state
     * @param c The current character.
     * @return Either a new state or set of states
     */
    public abstract T of(int state, char c);


}
