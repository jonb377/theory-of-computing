package toc.regular.nfa;

import toc.grammar.Production;
import toc.Acceptor;
import toc.regular.dfa.DFA;
import toc.regular.dfa.DFATransitionFunction;
import toc.regular.grammar.RightLinearGrammar;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class NFA extends Acceptor {

    public final NFATransitionFunction δ;
    public final Set<Integer> F;

    public NFA(NFATransitionFunction δ, Set<Character> Σ, Set<Integer> F) {
        super(Σ);
        this.δ = δ;
        this.F = Set.copyOf(F);
    }

    public boolean recognizes(String s) {
        Set<Integer> currStates = δ.expandLambda(new HashSet<>(Arrays.asList(0)));
        Set<Integer> nextStates = new HashSet<>();
        Set<Integer> tmp;
        for (char c : s.toCharArray()) {
            for (Integer q : currStates) {
                nextStates.addAll(δ.of(q, c));
            }
            tmp = currStates;
            currStates = nextStates;
            nextStates = tmp;
            nextStates.clear();
        }
        System.out.println("Resulting States: " + currStates);
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
        HashSet<Integer> initialState = new HashSet<>(Arrays.asList(0));
        newStates.add(initialState);

        // Maps the current set of states to a state name.
        HashMap<HashSet<Integer>, Integer> stateNames = new HashMap<>();
        stateNames.put(initialState, 0);

        // The transition function for the DFA
        ArrayList<List<Integer>> transitionFunction = new ArrayList<>();
        transitionFunction.add(new ArrayList<>(Arrays.asList(new Integer[Σ.size()]))); // q1 is the initial state

        while (newStates.size() > 0) {
            HashSet<Integer> currState = newStates.iterator().next();
            //System.out.println("Expanding state " + stateNames.get(currState) + ": " + currState);
            int currStateName = stateNames.get(currState);
            for (char a : Σ) {
                HashSet<Integer> nextState = new HashSet<>();
                for (int q : currState) {
                    nextState.addAll(δ.of(q, a));
                }
                if (!stateNames.containsKey(nextState)) {
                    stateNames.put(nextState, stateNames.size());
                    newStates.add(nextState);
                    transitionFunction.add(new ArrayList<>(Arrays.asList(new Integer[Σ.size()]))); // Define this state's transitions
                }
                //System.out.println(currStateName + " " + a + "  " + stateNames.get(nextState) + " " + nextState);
                transitionFunction.get(currStateName).set(δ.map.get(a), stateNames.get(nextState));  // Map from this state to next
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

        // Add lambda to the DFA if it's in the language.
        if (this.recognizes("")) {
            finalStates.add(0);
        }

        //System.out.println("Final States: " + finalStates);
        List<List<Integer>> transition = new ArrayList<>();
        for (int i = 0; i < transitionFunction.size(); i++) {
            transition.add(List.copyOf(transitionFunction.get(i)));
            //System.out.println(Arrays.toString(transition[i]));
        }
        DFATransitionFunction δ = DFATransitionFunction.createTotalTransitionFunction(transition, this.δ.map);
        return new DFA(δ, Σ, finalStates);
    }

    private char getStateName(HashMap<Integer, Character> variables, int state) {
        if (variables.containsKey(state)) return variables.get(state);
        for (char c = 'A'; c < Character.MAX_VALUE; c++) {
            if (!Σ.contains((char) (c + state)) && !variables.containsKey((char) (c + state))) {
                variables.put(state, (char) (c + state));
                return (char) (c + state);
            }
        }
        throw new RuntimeException("Unable to allocate new variable name!");
    }

    public RightLinearGrammar toRLG() {
        Set<Production> productions = new HashSet<>();
        Set<Character> V = new HashSet<>();
        HashMap<Integer, Character> variables = new HashMap<>();
        char currvar = 'A';
        for (int i = 0; i < δ.numStates(); i++) {
            char currVar = getStateName(variables, i);
            V.add(currVar);
            for (char c : Σ) {
                for (Integer res : δ.of(i, c)) {
                    productions.add(new Production(String.valueOf(currVar), String.valueOf(c) + (char) ('A' + res)));
                }
            }
            if (F.contains(i)) {
                productions.add(new Production(String.valueOf(currVar), "λ"));
            }
        }
        for (Production p : productions) System.out.println(p);
        return new RightLinearGrammar(Σ, V, productions, 'A');
    }
}
