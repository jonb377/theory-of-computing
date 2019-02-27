package toc.regular;

import java.util.*;
import java.util.function.Function;

/**
 * @author Jon Bolin
 */
public abstract class TransitionFunction<T> {

    public interface UnmodifiableFunction<S> extends Function<S, S> { }

    public static Map<Character, Integer> createMap(Set<Character> Σ) {
        Map<Character, Integer> map = new HashMap<>();
        List<Character> alphaList = new ArrayList<>(Σ);
        Collections.sort(alphaList);
        for (int i = 0; i < alphaList.size(); i++) map.put(alphaList.get(i), i);
        return Collections.unmodifiableMap(map);
    }

    public Map<Character, Integer> map;
    public List<List<T>> transitions;

    public TransitionFunction(Map<Character, T>[] transition, Set<Character> Σ) {
        this(transition, Σ, (t) -> t);
    }

    public TransitionFunction(Map<Character, T>[] transition, Set<Character> Σ, UnmodifiableFunction<T> f) {
        // Create the map: Σ -> {0, 1, ..., |Σ| - 1}
        this.map = TransitionFunction.createMap(Σ);

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

    protected TransitionFunction(List<List<T>> transition, Map<Character, Integer> map) {
        this.map = map;
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
