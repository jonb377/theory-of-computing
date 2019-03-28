package toc.regular.grammar;

import toc.grammar.Grammar;
import toc.grammar.Production;
import toc.regular.nfa.NFA;
import toc.regular.nfa.NFABuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jon Bolin
 */
public class RightLinearGrammar extends Grammar {

    public RightLinearGrammar(Set<Character> T, Set<Character> V, Set<Production> P, Character S) {
        super(T, V, P, S);
    }

    @Override
    protected boolean verifyProductions(Set<Character> T, Set<Character> V, Set<Production> P) {
        for (Production p : P) {
            if (p.LHS.length() != 1) return false;
            // No variables in the first part of the string.
            for (int i = 0; i < p.RHS.length() - 1; i++) {
                if (V.contains(p.RHS.charAt(i))) return false;
            }
            // Last character could be variable or terminal.
        }
        return true;
    }

    public NFA toNFA() {
        NFABuilder builder = new NFABuilder(T);
        HashMap<Character, Integer> variableStates = new HashMap<>();
        variableStates.put(S, builder.addState());
        for (Production p : P) {
            int currState = variableStates.getOrDefault(p.LHS.charAt(0), builder.addState());
            for (int i = 0; i < p.RHS.length() - 1; i++) {
                int next = builder.addState();
                builder.addTransition(currState, p.RHS.charAt(i), next);
                currState = next;
            }
            char last = p.RHS.charAt(p.RHS.length() - 1);
            if (V.contains(last)) {
                // Lambda from current char state to variable state.
                builder.addλTransition(currState, variableStates.getOrDefault(last, builder.addState()));
            } else {
                // Production ends in a terminal symbol
                int next = builder.addState();
                builder.addTransition(currState, last, next);
                builder.addFinalState(next);
            }
        }
        return builder.build();
    }

    public LeftLinearGrammar reverse() {
        Set<Production> rlgP = new HashSet<>();
        for (Production p : P) {
            StringBuilder sb = new StringBuilder(p.RHS.length());
            for (int i = p.RHS.length() - 1; i >= 0; i--) {
                sb.append(p.RHS.charAt(i));
            }
            rlgP.add(new Production(p.LHS, sb.toString()));
        }
        return new LeftLinearGrammar(T, V, rlgP, S);
    }
}
