package toc.regular;

import toc.TransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public abstract class RegularTransitionFunction<T> extends TransitionFunction {

    public List<List<T>> transitions;

    public RegularTransitionFunction(Map<Character, T>[] transition, Set<Character> Σ) {
        this(transition, Σ, (t) -> t);
    }

    public RegularTransitionFunction(Map<Character, T>[] transition, Set<Character> Σ, UnmodifiableFunction<T> f) {
        super(Σ);

        ArrayList<Character> alphaList = new ArrayList<>(Σ);
        Collections.sort(alphaList);

        // Create the new transition function
        ArrayList<List<T>> modifiable = new ArrayList<>(transition.length);
        for (int i = 0; i < transition.length; i++) {
            ArrayList<T> tmp = new ArrayList<>(Σ.size());
            for (int j = 0; j < Σ.size(); j++) {
                tmp.add(f.apply(transition[i].getOrDefault(alphaList.get(j), null)));
            }
            modifiable.add(Collections.unmodifiableList(tmp));
        }
        this.transitions = Collections.unmodifiableList(modifiable);
    }

    protected RegularTransitionFunction(List<List<T>> transition, Map<Character, Integer> map) {
        super(map);
        this.transitions = transition;
    }

    /**
     * @return The number of states this transition function is defined for.
     */
    public int numStates() {
        return transitions.size();
    }

    /**
     * Determines the next state given the current state and transition.
     * @param state The current state
     * @param c The current character.
     * @return Either a new state or set of states
     */
    public abstract T of(int state, char c);


}
