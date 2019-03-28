package toc.grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jon Bolin
 */
public abstract class Grammar {

    // Set of terminal symbols
    public final Set<Character> T;

    // Set of variables
    public final Set<Character> V;

    // Set of productions
    public final Set<Production> P;

    // Start symbol
    public final Character S;

    public Grammar(Set<Character> T, Set<Character> V, Set<Production> P, Character S) {
        verifyGrammar(T, V, P, S);
        this.T = Set.copyOf(T);
        this.P = Set.copyOf(P);
        this.V = V;
        this.S = S;
    }

    private void verifyGrammar(Set<Character> T, Set<Character> V, Set<Production> P, Character S) {
        // Start symbol is a variable
        if (!V.contains(S)) throw new RuntimeException("Invalid Grammar: Start state is not a variable.");

        // V intersect T is empty
        HashSet<Character> TV = new HashSet<>(T);
        TV.retainAll(V);
        if (TV.size() > 0) throw new RuntimeException("Invalid Grammar: Terminals and variables have common characters");

        // Verify productions
        if (!verifyProductions(T, V, P)) throw new RuntimeException("Production verification failed.");
        // Make sure LHS and RHS are valid.
        for (Production p : P) {
            for (char c : p.LHS.toCharArray()) {
                if (!V.contains(c) && !T.contains(c)) throw new RuntimeException("LHS of production contains unknown symbols: " + p);
            }
            for (char c : p.RHS.toCharArray()) {
                if (!V.contains(c) && !T.contains(c)) throw new RuntimeException("RHS of production contains unknown symbols: " + p);
            }
        }
    }

    public ArrayList<String> produce(int depth) {
        ArrayList<String> res = new ArrayList<>();
        produce(String.valueOf(S), depth, res);
        return res;
    }

    /**
     * Produces all strings up to the specified depth in the parse tree. Stores the strings in result.
     * @param s
     * @param depth
     * @param result
     */
    private void produce(String s, int depth, ArrayList<String> result) {
        // Check if s is a sentence
        boolean sentence = true;
        for (char c : s.toCharArray()) {
            if (V.contains(c)) {
                // S is a sentential form, not a leaf
                sentence = false;
                break;
            }
        }
        if (sentence) {
            result.add(s);
            return;
        }
        if (depth == 0) {
            // can't search any deeper
            return;
        }
        for (int i = 0; i < s.length(); i++) {
            // TODO: Make production set into a trie and further optimizations
            for (Production p : P) {
                String sub = p.sub(s, i);
                if (sub != null) {
                    produce(sub, depth - 1, result);
                }
            }
        }
    }

    /**
     * Verify the form of the productions.
     * @param T
     * @param V
     * @param P
     * @return
     */
    protected abstract boolean verifyProductions(Set<Character> T, Set<Character> V, Set<Production> P);

}
