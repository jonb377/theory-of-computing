package toc.contextfree;

import toc.grammar.CFGBuilder;
import toc.grammar.Grammar;
import toc.grammar.Production;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class ContextFreeGrammar extends Grammar {

    private boolean inCNF;

    public ContextFreeGrammar(Set<Character> T, Set<Character> V, Set<Production> P, Character S) {
        super(T, V, P, S);
    }


    @Override
    protected boolean verifyProductions(Set<Character> T, Set<Character> V, Set<Production> P) {
        for (Production p : P) {
            if (p.LHS.length() != 1) return false;
        }
        return true;
    }

    /**
     * Create a new CFG with all useless productions removed.
     * A useless production contains a useless variable.
     * A useless variable is never used in the production of a word in the language.
     * @return
     */
    public ContextFreeGrammar removeUselessProductions() {
        Set<Character> V1 = new HashSet<>();
        boolean added = true;
        while (added) {
            added = false;
            for (Production p : P) {
                char v = p.LHS.charAt(0);
                if (V1.contains(v)) continue;
                boolean addToV1 = true;
                for (char c : p.RHS.toCharArray()) {
                    if (!T.contains(c) && !V1.contains(c)) {
                        addToV1 = false;
                        break;
                    }
                }
                if (addToV1) {
                    V1.add(v);
                    added = true;
                }
            }
        }
        Set<Production> P1 = new HashSet<>();
        for (Production p : P) {
            if (!V1.contains(p.LHS.charAt(0))) continue;
            boolean addToP1 = true;
            for (char c : p.RHS.toCharArray()) {
                if (!T.contains(c) && !V1.contains(c)) {
                    addToP1 = false;
                    break;
                }
            }
            if (addToP1) P1.add(p);
        }

        // Create the dependency graph in V1 and remove any nodes unreachable from S.
        HashMap<Character, ArrayList<Character>> adjlist = new HashMap<>();
        for (char c : V1) {
            adjlist.put(c, new ArrayList<>());
        }
        for (Production p : P1) {
            ArrayList<Character> curr = adjlist.get(p.LHS.charAt(0));
            for (char c : p.RHS.toCharArray()) {
                if (V1.contains(c)) curr.add(c);
            }
        }

        // Add nodes reachable from S
        Set<Character> V2 = new HashSet<>();
        Stack<Character> s = new Stack<>();
        s.push(S);
        while (!s.isEmpty()) {
            char curr = s.pop();
            if (V2.contains(curr)) continue;
            V2.add(curr);
            for (char c : adjlist.get(curr)) {
                s.push(c);
            }
        }
        Set<Production> P2 = new HashSet<>();
        for (Production p : P) {
            if (!V2.contains(p.LHS.charAt(0))) continue;
            boolean addToP2 = true;
            for (char c : p.RHS.toCharArray()) {
                if (!T.contains(c) && !V2.contains(c)) {
                    addToP2 = false;
                    break;
                }
            }
            if (addToP2) P2.add(p);
        }

        return new ContextFreeGrammar(T, V2, P2, S);
    }

    /**
     * Adds the empty string to the language.
     * @return A new grammar with the empty string.
     */
    public ContextFreeGrammar addλ() {
        char v;
        for (v = 'A'; T.contains(v) || V.contains(v); v++);
        Set<Production> newp = new HashSet<>(P);
        newp.add(new Production(String.valueOf(v), ""));
        newp.add(new Production(String.valueOf(v), String.valueOf(S)));
        Set<Character> newv = new HashSet<>(V);
        newv.add(v);
        return new ContextFreeGrammar(T, newv, newp, v);
    }

    /**
     * Produce a new context-free grammar with no lambda productions.
     */
    public ContextFreeGrammar removeLambdaProductions() {
        Set<Character> Vn = new HashSet<>();
        boolean added = true;
        while (added) {
            added = false;
            for (Production p : P) {
                boolean addToVn = true;
                for (char c : p.RHS.toCharArray()) {
                    if (T.contains(c) || !Vn.contains(c)) {
                        addToVn = false;
                        break;
                    }
                }
                if (addToVn && !Vn.contains(p.LHS.charAt(0))) {
                    added = true;
                    Vn.add(p.LHS.charAt(0));
                }
            }
        }
        Set<Production> P1 = new HashSet<>();
        for (Production p : P) {
            ArrayList<Character> nullable = new ArrayList<>();
            for (char c : p.RHS.toCharArray()) {
                if (Vn.contains(c) && !nullable.contains(c)) nullable.add(c);
            }
            // Try each possible combination of removing nullable variables.
            for (long i = 0; i < (1L << nullable.size()); i++) {
                Set<Character> remove = new HashSet<>();
                for (int j = 0; j <= nullable.size(); j ++) {
                    if ((i & (1 << j)) != 0) {
                        remove.add(nullable.get(j));
                    }
                }
                String rhs = p.RHS;
                for (char c : remove) {
                    rhs = rhs.replaceAll(String.valueOf(c), "");
                }
                if (rhs.length() > 0) {
                    P1.add(new Production(p.LHS, rhs));
                }
            }
            if (p.RHS.length() > 0) {
                P1.add(p);
            }
        }
        return new ContextFreeGrammar(T, V, P1, S);
    }

    /**
     * Create a new ContextFreeGrammar without any unit productions.
     * @return
     */
    public ContextFreeGrammar removeUnitProductions() {
        ContextFreeGrammar res = removeLambdaProductions();
        // Create the dependency graph in V1 and remove any nodes unreachable from S.
        HashMap<Character, Set<Character>> adjlist = new HashMap<>();
        for (char c : res.V) {
            adjlist.put(c, new HashSet<>());
        }
        for (Production p : res.P) {
            if (p.RHS.length() == 1 && res.V.contains(p.RHS.charAt(0))) {
                adjlist.get(p.LHS.charAt(0)).add(p.RHS.charAt(0));
            }
        }

        // Add non-unit productions to the set.
        Set<Production> P1 = new HashSet<>();
        for (Production p : P) {
            if (p.RHS.length() != 1 || T.contains(p.RHS.charAt(0))) {
                P1.add(p);
            }
        }

        // Create new productions that aren't reliant on unit productions.
        // DFS in adjacency graph to find all chains of A -> B, B -> C, etc
        Stack<Character> stack = new Stack<>();
        for (char v : V) {
            Set<Character> visited = new HashSet<>();
            for (char c : adjlist.get(v)) {
                stack.push(c);
            }
            while (!stack.isEmpty()) {
                char curr = stack.pop();
                if (visited.contains(curr)) continue;
                visited.add(curr);
                Set<Production> tmp = new HashSet<>();
                for (Production p : P1) {
                    if (p.LHS.charAt(0) == curr) {
                        tmp.add(new Production(String.valueOf(v), p.RHS));
                    }
                }
                P1.addAll(tmp);
                adjlist.get(curr).forEach(stack::push);
            }
        }
        return new ContextFreeGrammar(T, V, P1, S);
    }

    /**
     * Chomsky Normal Form: All productions are of the form A -> BC or A -> a,
     * a elem T; A, B, and C elem V
     * @return An equivalent CFG (perhaps with the empty string removed)
     */
    public ContextFreeGrammar toChomskyNormalForm() {
        if (inCNF) return this;
        // Remove lambda and unit productions
        ContextFreeGrammar cfg = removeUnitProductions();
        System.out.println(cfg);
        CFGBuilder builder = new CFGBuilder(cfg.S);
        HashMap<Character, Character> termVariables = new HashMap<>();
        // The current variable.
        char v = 'A';
        for (char t : cfg.T) {
            // Create a new variable for each terminal symbol, with production A -> a
            while (v < Character.MAX_VALUE && (cfg.T.contains(v) || cfg.V.contains(v) || termVariables.containsValue(v))) v++;
            termVariables.put(t, v);
            builder.addProduction(String.valueOf(v), String.valueOf(t));
        }

        // Add all productions of length 1
        for (Production p : cfg.P) {
            if (p.RHS.length() == 1) {
                // Add if length 1... A -> a
                builder.addProduction(p.LHS, p.RHS);
            } else {
                String rhs = p.RHS;
                char lastV = p.LHS.charAt(0);
                for (int i = 0; i < rhs.length() - 2; i++) {
                    // Find the next unused variable name
                    while (v < Character.MAX_VALUE && (cfg.T.contains(v) || cfg.V.contains(v) || termVariables.containsValue(v)))
                        v++;
                    char curr = rhs.charAt(i);
                    builder.addProduction("" + lastV, (T.contains(curr) ? termVariables.get(curr) : curr) + "" + v);
                    lastV = v++;
                }
                String sub = p.RHS.substring(p.RHS.length() - 2);
                if (T.contains(sub.charAt(0))) sub = sub.replace(sub.charAt(0), termVariables.get(sub.charAt(0)));
                if (T.contains(sub.charAt(1))) sub = sub.replace(sub.charAt(1), termVariables.get(sub.charAt(1)));
                builder.addProduction("" + lastV, sub);
            }
        }
        ContextFreeGrammar g = builder.build();
        g.inCNF = true;
        return g;
    }

    /**
     * Tests if the given string can be generated by this grammar using the CYK algorithm.
     * @param s
     * @return
     */
    public boolean isMember(String s) {
        if (!inCNF) throw new RuntimeException("Convert to CNF before checking membership.");
        Set<Character>[][] v = (Set<Character>[][]) new Set[s.length()][s.length()];
        for (int length = 0; length < s.length(); length++) {
            for (int i = 0; i < s.length() - length; i++) {
                v[i][i+length] = new HashSet<>();
                if (length == 0) {
                    // Add unit productions resulting in the current character.
                    for (Production p : P) {
                        if (p.RHS.charAt(0) == s.charAt(i)) {
                            v[i][i+length].add(p.LHS.charAt(0));
                        }
                    }
                } else {
                    for (Production p : P) {
                        if (p.RHS.length() < 2) continue;
                        char l = p.RHS.charAt(0);
                        char r = p.RHS.charAt(1);
                        for (int k = i; k < i+length; k++) {
                            if (v[i][k].contains(l) && v[k+1][i+length].contains(r)) {
                                v[i][i+length].add(p.LHS.charAt(0));
                                break;
                            }
                        }
                    }
                }
            }
        }
        return v[0][s.length() - 1].contains(S);
    }
}
