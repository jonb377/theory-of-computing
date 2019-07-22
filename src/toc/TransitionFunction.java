package toc;

import java.util.*;
import java.util.function.Function;

/**
 * @author Jon Bolin
 */
public abstract class TransitionFunction {
    public interface UnmodifiableFunction<S> extends Function<S, S> { }

    public static Map<Character, Integer> createMap(Set<Character> Σ) {
        Map<Character, Integer> map = new HashMap<>();
        List<Character> alphaList = new ArrayList<>(Σ);
        Collections.sort(alphaList);
        for (int i = 0; i < alphaList.size(); i++) map.put(alphaList.get(i), i);
        return Collections.unmodifiableMap(map);
    }

    public Map<Character, Integer> map;

    public TransitionFunction(Set<Character> Σ) {
        // Create the map: Σ -> {0, 1, ..., |Σ| - 1}
        this.map = TransitionFunction.createMap(Σ);
    }

    protected TransitionFunction(Map<Character, Integer> map) {
        this.map = map;
    }

}
