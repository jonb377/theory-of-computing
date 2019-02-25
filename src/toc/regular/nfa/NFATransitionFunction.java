package toc.regular.nfa;

import toc.regular.TransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class NFATransitionFunction extends TransitionFunction<Set<Integer>> {

    private static final Set<Integer> EMPTY = Collections.unmodifiableSet(new HashSet<>());

    private Set<Integer>[] λ;

    public static NFATransitionFunction createNFATransition(Set<Integer>[][] transitions, Set<Character> Σ) {
        Set<Integer>[] λ = (Set<Integer>[]) new Set[transitions.length];
        for (int i = 0; i < λ.length; i++) {
            λ[i] = EMPTY;
        }
        return createNFATransition(transitions, λ, Σ);
    }

    public static NFATransitionFunction createNFATransition(Set<Integer>[][] transitions, Set<Integer>[] λ, Set<Character> Σ) {
        // Verify that this is a valid transition function
        if (!Arrays.stream(transitions).allMatch(
                (x)-> Arrays.stream(x).allMatch(
                        (s) -> s == null || s.stream().allMatch(
                                (i) -> i >= 0 && i < transitions.length)))) {
            throw new RuntimeException("Busted Transition Function");
        }
        return new NFATransitionFunction(transitions, λ, Σ);
    }

    /**
     * Create a new toc.regular.dfa.DFATransitionFunction from the given mapping.
     * @param transition Transition function. transition[q][a] is delta(q, a).
     *                   q = 0 is the trap state; transition[0] must be {0, 0, ..., 0}
     */
    private NFATransitionFunction(Set<Integer>[][] transition, Set<Integer>[] λ, Set<Character> Σ) {
        super(transition, Σ);
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
            for (int p : λ[q]) {
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
            Set<Integer> step = (Set<Integer>) transitions[q][map.get(a)];
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
