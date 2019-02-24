package toc.regular.dfa;

import toc.Settings;
import toc.regular.Acceptor;

import java.util.*;
import java.util.stream.IntStream;

import static toc.Settings.ALPHABET_SIZE;

/**
 * @author Jon Bolin
 */
public class DFA implements Acceptor {

    private TotalTransitionFunction delta;
    private Set<Integer> F;

    public DFA(TotalTransitionFunction delta, Set F) {
        this.delta = delta;
        this.F = F;
    }

    /**
     * Tests whether the DFA recognizes the string.
     * @param s
     * @return
     */
    public boolean recognizes(String s) {
        int state = 1;
        for (char c : s.toCharArray()) {
            state = delta.of(state, c);
        }
        return F.contains(state);
    }

    /**
     * @return The number of states in the DFA.
     */
    public int numStates() {
        return delta.numStates();
    }

    public DFA reduceStates() {
        // Step 1: Remove inaccessible states
        Set<Integer> accessible = new HashSet<>();
        Stack<Integer> states = new Stack<>();
        states.add(1);
        accessible.add(1);
        while (!states.isEmpty()) {
            int curr = states.pop();
            for (char a = 0; a < ALPHABET_SIZE; a++) {
                int next = delta.of(curr, a);
                if (!accessible.contains(next)) {
                    accessible.add(next);
                    states.push(next);
                }
            }
        }
        TotalTransitionFunction newDelta = delta.keepStates(accessible);

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
            distinguishable[f].addAll(nonFinal);
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
                    for (char a = 0; a < ALPHABET_SIZE; a++) {
                        int pa = newDelta.of(p, a);
                        int qa = newDelta.of(q, a);

                        if (distinguishable[pa].contains(qa)) {
                            // ... and if so, mark (p, q) as distinguishable.
                            distinguishable[p].add(q);
                            distinguishable[q].add(p);
                            newPair = true;
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

        // Step 5: Construct a new transition function on the sets of indistinguishable states.
        int[][] transition = new int[newStateCount][ALPHABET_SIZE];
        for (int i = 0; i < newDelta.numStates(); i++) {
            for (char a = 0; a < ALPHABET_SIZE; a++) {
                transition[newStateNumber[i]][a] = newStateNumber[newDelta.of(i, a)];
            }
        }

        TotalTransitionFunction finalDelta = new TotalTransitionFunction(transition);
        Set<Integer> finalF = new HashSet<>();
        for (int f : F) finalF.add(newStateNumber[f]);
        return new DFA(finalDelta, finalF);
    }

}
