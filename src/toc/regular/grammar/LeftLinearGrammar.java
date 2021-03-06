package toc.regular.grammar;

import toc.grammar.Grammar;
import toc.grammar.Production;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jon Bolin
 */
public class LeftLinearGrammar extends Grammar {
    public LeftLinearGrammar(Set<Character> T, Set<Character> V, Set<Production> P, Character S) {
        super(T, V, P, S);
    }

    @Override
    protected boolean verifyProductions(Set<Character> T, Set<Character> V, Set<Production> P) {
        for (Production p : P) {
            if (p.LHS.length() != 1) return false;
            // First character could be terminal or variable
            // No variables in the second part of the string.
            for (int i = 1; i < p.RHS.length(); i++) {
                if (V.contains(p.RHS.charAt(i))) return false;
            }
        }
        return true;
    }

    public RightLinearGrammar reverse() {
        Set<Production> rlgP = new HashSet<>();
        for (Production p : P) {
            StringBuilder sb = new StringBuilder(p.RHS.length());
            for (int i = p.RHS.length() - 1; i >= 0; i--) {
                sb.append(p.RHS.charAt(i));
            }
            rlgP.add(new Production(p.LHS, sb.toString()));
        }
        return new RightLinearGrammar(T, V, rlgP, S);
    }
}
