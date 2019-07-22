package toc.contextfree;

import toc.TransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class PDATransitionFunction extends TransitionFunction {

    private static final Map<Character, Set<Integer>> EMPTY = Collections.emptyMap();

    private Map<Character, Map<Character, Set<Integer>>>[] map;

    public PDATransitionFunction(Set<Character> Σ, Map<Character, Map<Character, Set<Integer>>>[] map) {
        super(Σ);
        this.map = map;
    }

    public Set<Integer> expandLambda(Set<Integer> states, char stackTop) {
        Set<Integer> startStates = new HashSet<>(states);
        Stack<Integer> stack = new Stack<>();
        stack.addAll(states);
        while (!stack.isEmpty()) {
            int q = stack.pop();
            for (int p : map[q].getOrDefault(stackTop, EMPTY).getOrDefault('λ', Collections.emptySet())) {
                if (!startStates.contains(p)) {
                    startStates.add(p);
                    stack.push(p);
                }
            }
        }
        return startStates;
    }

    public Set<Integer> of(Set<Integer> currStates, Character stack, Character c) {
        Set<Integer> startStates = new HashSet<Integer>(currStates);
        startStates.addAll(expandLambda(startStates, stack));
        Set<Integer> newStates = new HashSet<>();
        for (int i : startStates) {
            newStates.addAll(map[i].getOrDefault(stack, EMPTY).getOrDefault(c, Collections.emptySet()));
        }
        return newStates;
    }

}
