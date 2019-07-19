package toc.regular.nfa;

import toc.regular.TransitionFunction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class to build an NFA.
 *
 * @author Jon Bolin
 */
public class NFABuilder {

    private List<Set<Integer>> λ;
    private List<List<Set<Integer>>> δ;
    private Set<Integer> F;
    public final Set<Character> Σ;
    public final Map<Character, Integer> map;

    public NFABuilder(Set<Character> Σ) {
        δ = new ArrayList<>();
        λ = new ArrayList<>();
        F = new HashSet<>();
        this.Σ = Σ;
        this.map = TransitionFunction.createMap(Σ);
    }

    /**
     * Produce the final NFA.
     * @return An NFA with the states and transitions build using this NFABuilder.
     */
    public NFA build() {
        List<List<Set<Integer>>> delta = δ.stream()
                .map((l) -> Collections.unmodifiableList(l.stream()
                        .map(NFATransitionFunction.MAKE_UNMODIFIABLE)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
        List<Set<Integer>> lambda = Collections.unmodifiableList(λ.stream()
                .map(NFATransitionFunction.MAKE_UNMODIFIABLE)
                .collect(Collectors.toList()));
        NFATransitionFunction nfaδ = NFATransitionFunction.createNFATransitionFunction(delta, lambda, map);
        return new NFA(nfaδ, Σ, F);
    }

    /**
     * Adds an empty state with no transitions.
     */
    public int addState() {
        λ.add(new HashSet<>());
        ArrayList<Set<Integer>> newTransition = new ArrayList<>();
        for (int i = 0; i < Σ.size(); i++) {
            newTransition.add(new HashSet<>());
        }
        δ.add(newTransition);
        return δ.size() - 1;
    }

    /**
     * Adds a transition from q to p with label a.
     * @param q The start state
     * @param a The transition symbol
     * @param p The resulting state
     */
    public void addTransition(int q, char a, int p) {
        δ.get(q).get(map.get(a)).add(p);
    }

    /**
     * Adds a lambda transition from q to p.
     * @param q The start state
     * @param p The resulting state
     */
    public void addλTransition(int q, int p) {
        λ.get(q).add(p);
    }

    /**
     * Adds f to the list of final states.
     * @param f The new final state.
     */
    public void addFinalState(int f) {
        this.F.add(f);
    }

    /**
     * Adds all states and transitions (including λ) to the builder.
     * @param nfa The NFA to add.
     * @return The offset for each state. i.e., the state number in this NFA = the state number in the original + offset
     */
    public int addNFA(NFA nfa) {
        int priorSize = δ.size();
        for (int i = 0; i < nfa.δ.numStates(); i++) {
            List<Set<Integer>> curr = new ArrayList<>();
            for (Set<Integer> set : nfa.δ.transitions.get(i)) {
                Set<Integer> currSet = new HashSet<>();
                if (set != null) {
                    for (int j : set) {
                        currSet.add(j + priorSize);
                    }
                }
                curr.add(currSet);
            }
            δ.add(curr);
        }
        for (Set<Integer> set : nfa.δ.λ) {
            Set<Integer> currSet = new HashSet<>();
            for (int j : set) {
                currSet.add(j + priorSize);
            }
            λ.add(currSet);
        }
        return priorSize;
    }

    @Override
    public String toString() {
        String s = "";
        s += "δ: \n";
        for (int i = 0; i < δ.size(); i++) {
            s += "   " + i + ": " + δ.get(i) + "\n";
        }
        s += "λ: \n";
        for (int i = 0; i < λ.size(); i++) {
            s += "   " + i + ": " + λ.get(i) + "\n";
        }
        s += "F: " + F;
        return s;
    }

}
