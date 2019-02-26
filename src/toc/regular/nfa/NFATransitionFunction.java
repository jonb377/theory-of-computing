package toc.regular.nfa;

import toc.regular.TransitionFunction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jon Bolin
 */
public class NFATransitionFunction extends TransitionFunction<Set<Integer>> {

    private static final Set<Integer> EMPTY = Collections.unmodifiableSet(new HashSet<>());
    public static final UnmodifiableFunction<Set<Integer>> MAKE_UNMODIFIABLE = (t) -> t == null ? null : Set.copyOf(t);

    public static NFATransitionFunction createNFATransition(Map<Character, Set<Integer>>[] transitions, Set<Character> Σ) {
        List<Set<Integer>> λ = new ArrayList<>();
        for (int i = 0; i < transitions.length; i++) {
            λ.add(EMPTY);
        }
        return createNFATransition(transitions, λ, Σ);
    }

    public static NFATransitionFunction createNFATransition(Map<Character, Set<Integer>>[] transitions, List<Set<Integer>> λ, Set<Character> Σ) {
        // Verify that this is a valid transition function
        for (Map<Character, Set<Integer>> map : transitions) {
            for (char c : map.keySet()) {
                if (!Σ.contains(c)) {
                    throw new RuntimeException("Busted Transition Function");
                } else if (map.get(c) != null) {
                    for (int q : map.get(c)) {
                        if (q < 0 || q >= transitions.length) {
                            throw new RuntimeException("Busted Transition Function: " + q + "\t" + transitions.length);
                        }
                    }
                }
            }
        }
        return new NFATransitionFunction(transitions, λ, Σ);
    }

    /**
     * Create a new NFA Transition Function using the transitions specified.
     * @param transitions
     * @param λ
     * @param map
     * @return
     */
    public static NFATransitionFunction createNFATransitionFunction(List<List<Set<Integer>>> transitions, List<Set<Integer>> λ, Map<Character, Integer> map) {
        for (List<Set<Integer>> arr : transitions) {
            for (Set<Integer> set : arr) {
                if (set != null) {
                    for (int q : set) {
                        if (q < 0 || q >= transitions.size()) {
                            throw new RuntimeException("Busted transition function");
                        }
                    }
                }
            }
        }
        for (Set<Integer> set : λ) {
            if (set != null) {
                for (int q : set) {
                    if (q < 0 || q >= transitions.size()) {
                        throw new RuntimeException("Busted transition function");
                    }
                }
            }
        }
        return new NFATransitionFunction(transitions, λ, map);
    }

    public final List<Set<Integer>> λ;

    /**
     * Create a new toc.regular.dfa.DFATransitionFunction from the given mapping.
     * @param transition Transition function. transition[q][a] is delta(q, a).
     *                   q = 0 is the trap state; transition[0] must be {0, 0, ..., 0}
     */
    private NFATransitionFunction(Map<Character, Set<Integer>>[] transition, List<Set<Integer>> λ, Set<Character> Σ) {
        super(transition, Σ, MAKE_UNMODIFIABLE);
        this.λ = Collections.unmodifiableList(λ.stream().map(MAKE_UNMODIFIABLE).collect(Collectors.toList()));
    }

    private NFATransitionFunction(List<List<Set<Integer>>> transition, List<Set<Integer>> λ, Map<Character, Integer> map) {
        super(transition, map);
        this.λ = λ;
    }

    public Set<Integer> expandLambda(Set<Integer> states) {
        // Expand out by lambda transitions.
        Set<Integer> startStates = new HashSet<>();
        startStates.addAll(states);
        Stack<Integer> stack = new Stack<>();
        stack.addAll(states);
        while (!stack.isEmpty()) {
            int q = stack.pop();
            for (int p : λ.get(q)) {
                if (!startStates.contains(p)) {
                    startStates.add(p);
                    stack.push(p);
                }
            }
        }
//        System.out.println("Start States: " + startStates + " given " + states);
        return startStates;
    }

    @Override
    public Set<Integer> of(int state, char a) {
        Set<Integer> startStates = expandLambda(new HashSet<>(Arrays.asList(state)));

        // Evaluate the transition
        Set<Integer> result = new HashSet<>();
        for (int q : startStates) {
            Set<Integer> step = transitions.get(q).get(map.get(a));
//            System.out.println("\tstep: " + step);
            result.addAll(step == null ? EMPTY : step);
        }
//        System.out.println("Initial Result: " + result);

        // Add in possible lambda transitions
        result.addAll(expandLambda(result));
//        System.out.println("Final Result: " + result);
        return result;
    }

}
