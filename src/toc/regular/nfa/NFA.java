package toc.regular.nfa;

import toc.regular.Acceptor;
import toc.regular.dfa.DFA;
import toc.regular.dfa.TotalTransitionFunction;

import java.util.*;

import static toc.Settings.ALPHABET_SIZE;

/**
 * @author Jon Bolin
 */
public class NFA implements Acceptor {

    private TransitionFunction delta;
    private Set<Integer> F;

    public NFA(TransitionFunction delta, Set<Integer> F) {
        this.delta = delta;
        this.F = F;
    }

    public boolean recognizes(String s) {
        Set<Integer> currStates = new HashSet<>(Arrays.asList(1));
        Set<Integer> nextStates = new HashSet<>();
        Set<Integer> tmp;
        for (char c : s.toCharArray()) {
            for (Integer q : currStates) {
                nextStates.addAll(delta.of(q, c));
            }
            tmp = currStates;
            currStates = nextStates;
            nextStates = tmp;
            nextStates.clear();
        }
        //System.out.println("Resulting States: " + currStates);
        currStates.retainAll(F);
        return currStates.size() > 0;
    }

    /**
     * Converts this NFA into an equivalent DFA.
     * @return The reulting DFA.
     */
    public DFA convertToDFA() {
        // A set of the states yet to be expanded.
        Set<HashSet<Integer>> newStates = new HashSet<>();

        // Add the initial state
        HashSet<Integer> initialState = new HashSet<>(Arrays.asList(1));
        newStates.add(initialState);

        // Trap state
        HashSet<Integer> TRAP = new HashSet<>(Arrays.asList(0));

        // Maps the current set of states to a state name.
        HashMap<HashSet<Integer>, Integer> stateNames = new HashMap<>();
        stateNames.put(TRAP, 0);
        stateNames.put(initialState, 1);

        // The transition function for the DFA
        ArrayList<int[]> transitionFunction = new ArrayList<>();
        transitionFunction.add(new int[ALPHABET_SIZE]); // q0 is the trap state
        transitionFunction.add(new int[ALPHABET_SIZE]); // q1 is the initial state

        while (newStates.size() > 0) {
            HashSet<Integer> currState = newStates.iterator().next();
            //System.out.println("Expanding state " + stateNames.get(currState) + ": " + currState);
            int currStateName = stateNames.get(currState);
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                HashSet<Integer> nextState = new HashSet<>();
                for (int q : currState) {
                    nextState.addAll(delta.of(q, (char) i));
                }
                if (nextState.size() > 0 && !nextState.equals(TRAP)) {
                    if (!stateNames.containsKey(nextState)) {
                        stateNames.put(nextState, stateNames.size());
                        newStates.add(nextState);
                        transitionFunction.add(new int[ALPHABET_SIZE]); // Define this state's transitions
                    }
                    //System.out.println(currStateName + " " + (char) i + "  " + stateNames.get(nextState) + " " + nextState);
                    transitionFunction.get(currStateName)[i] = stateNames.get(nextState);  // Map from this state to next
                }
            }
            newStates.remove(currState);
        }

        Set<Integer> finalStates = new HashSet<>();

        // Create the set of final states. Destroys information about state names.
        for (HashSet<Integer> state : stateNames.keySet()) {
            int stateNumber = stateNames.get(state);
            //System.out.print(stateNumber + ": " + state);
            state.retainAll(F);
            //System.out.println("\t" + state);
            if (state.size() > 0) {
                finalStates.add(stateNumber);
            }
        }
//        //System.out.println("Final States: " + finalStates);
        int[][] transition = new int[transitionFunction.size()][];
        for (int i = 0; i < transitionFunction.size(); i++) {
            transition[i] = transitionFunction.get(i);
//            //System.out.println(Arrays.toString(transition[i]));
        }
        TotalTransitionFunction delta = new TotalTransitionFunction(transition);
        return new DFA(delta, finalStates);
    }
}
