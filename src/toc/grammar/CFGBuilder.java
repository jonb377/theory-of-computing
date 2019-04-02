package toc.grammar;

import toc.contextfree.ContextFreeGrammar;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Jon Bolin
 */
public class CFGBuilder {

    private Set<Character> V;
    private Set<Character> T;
    private Set<Production> P;
    private Character S;

    public CFGBuilder(char start) {
        S = start;
        T = new HashSet<>();
        P = new HashSet<>();
        V = new HashSet<>();
    }

    public void setVariables(Collection<Character> v) {
        V = new HashSet<>(v);
        for (Production p : P) {
            for (char c : p.LHS.toCharArray()) {
                if (!V.contains(c)) T.add(c);
            }
            for (char c : p.RHS.toCharArray()) {
                if (!V.contains(c)) T.add(c);
            }
        }
    }

    public void setTerminalSymbols(Collection<Character> t) {
        T = new HashSet<>(t);
        for (Production p : P) {
            for (char c : p.LHS.toCharArray()) {
                if (!T.contains(c)) V.add(c);
            }
            for (char c : p.RHS.toCharArray()) {
                if (!T.contains(c)) V.add(c);
            }
        }
    }

    public Set<Production> getProductionSet() {
        return Set.copyOf(P);
    }

    public void addProduction(String lhs, String rhs) {
        for (String s : rhs.split("\\|")) {
            P.add(new Production(lhs, s));
        }
    }

    public ContextFreeGrammar build() {
        if (T.isEmpty()) {
            // Default to uppercase english characters as variables
            ArrayList<Character> v = new ArrayList<>(26);
            for (Production p : P) {
                if (p.LHS.charAt(0) >= 'A' && p.LHS.charAt(0) <= 'Z') v.add(p.LHS.charAt(0));
                for (char c : p.RHS.toCharArray()) if (c >= 'A' && c <= 'Z') v.add(c);
            }
            setVariables(v);
        }
        return new ContextFreeGrammar(T, V, P, S);
    }

}
