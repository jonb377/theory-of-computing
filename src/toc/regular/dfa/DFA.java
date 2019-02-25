package toc.regular.dfa;

import toc.regular.Acceptor;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Jon Bolin
 */
public class DFA extends Acceptor<Integer> {

    private DFATransitionFunction δ;
    private Set<Integer> F;

    public DFA(DFATransitionFunction δ, Set<Character> Σ, Set F) {
        super(Σ);
        this.δ = δ;
        this.F = F;
    }

    /**
     * Tests whether the DFA recognizes the string.
     * @param s
     * @return
     */
    public boolean recognizes(String s) {
        int state = 0;
        for (char c : s.toCharArray()) {
            state = δ.of(state, c);
        }
        return F.contains(state);
    }

    /**
     * @return The number of states in the DFA.
     */
    public int numStates() {
        return δ.numStates();
    }

    /**
     * Minimizes the number of states in the DFA.
     * @return A minimized branch.
     */
    public DFA reduceStates() {
        // Step 1: Remove inaccessible states
        Set<Integer> accessible = new HashSet<>();
        Stack<Integer> states = new Stack<>();
        states.add(0);
        accessible.add(0);
        while (!states.isEmpty()) {
            int curr = states.pop();
            for (char a : Σ) {
                int next = δ.of(curr, a);
                if (!accessible.contains(next)) {
                    accessible.add(next);
                    states.push(next);
                }
            }
        }
        DFATransitionFunction newDelta = δ.keepStates(accessible);

        // Step 2: Mark final / nonfinal pairs as distinguishable
        HashSet<Integer>[] distinguishable = new HashSet[newDelta.numStates()];
        IntStream.range(0, newDelta.numStates()).forEach((i) -> distinguishable[i] = new HashSet<>());

        Set<Integer> nonFinal = new HashSet<>();
        for (int i = 0; i < newDelta.numStates(); i++) {
            if (!F.contains(i)) {
                distinguishable[i].addAll(F);
                nonFinal.add(i);
            }
        }
        for (int f : F) {
            if (accessible.contains(f)) {
                distinguishable[f].addAll(nonFinal);
            }
        }

        // Step 3: Mark distinguishable transitions
        // Repeat until there are no new transitions.
        boolean newPair = true;
        while (newPair) {
            newPair = false;
            // For all pairs of distinct states...
            for (int q = 0; q < newDelta.numStates(); q++) {
                for (int p = 0; p < newDelta.numStates(); p++) {
                    if (q == p || distinguishable[q].contains(p)) continue;

                    // ...test if newDelta(p, a) and newDelta(p, q) are distinguishable...
                    for (char a : Σ) {
                        int pa = newDelta.of(p, a);
                        int qa = newDelta.of(q, a);

                        if (distinguishable[pa].contains(qa)) {
                            // ... and if so, mark (p, q) as distinguishable.
                            distinguishable[p].add(q);
                            distinguishable[q].add(p);
                            newPair = true;
                            break;
                        }
                    }
                }
            }
        }

        // Step 4: Find the sets of indistinguishable states
        // Each key is a set of indistinguishable states, and the value is the state number in the new DFA
        int[] newStateNumber = new int[newDelta.numStates()];
        int newStateCount = 0;
        outer:
        for (int i = 0; i < newDelta.numStates(); i++) {
            for (int j = 0; j < i; j++) {
                if (!distinguishable[i].contains(j)) {
                    newStateNumber[i] = newStateNumber[j];
                    continue outer;
                }
            }
            newStateNumber[i] = newStateCount++;
        }
        System.out.println("NewStateNumber: " + Arrays.toString(newStateNumber));

        // Step 5: Construct a new transition function on the sets of indistinguishable states.
        Integer[][] transition = new Integer[newStateCount][Σ.size()];
        for (int i = 0; i < newDelta.numStates(); i++) {
            for (char a : Σ) {
                transition[newStateNumber[i]][δ.map.get(a)] = newStateNumber[newDelta.of(i, a)];
            }
        }

        DFATransitionFunction finalDelta = DFATransitionFunction.createTotalTransitionFunction(transition, δ.map);
        Set<Integer> finalF = new HashSet<>();
        for (int f : F) {
            if (accessible.contains(f)) {
                finalF.add(newStateNumber[f]);
            }
        }
        System.out.println("Reduced from " + numStates() + " to " + newStateCount + " states.");
        return new DFA(finalDelta, Σ, finalF);
    }

}
