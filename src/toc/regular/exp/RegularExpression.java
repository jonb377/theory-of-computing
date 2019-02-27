package toc.regular.exp;

import toc.regular.dfa.DFA;
import toc.regular.exp.operations.Concatenation;
import toc.regular.exp.operations.StarClosure;
import toc.regular.exp.operations.Union;
import toc.regular.nfa.NFA;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author Jon Bolin
 * A regular expression is defined as follows:
 *
 * 1. λ, ϕ, and a ∈ Σ are regular expressions
 * 2. If r1 and r2 are regular expressions, then so are:
 *      r1 r2       Concatenation
 *      r1 + r2     Union
 *      r1*         Star Closure
 *      (r1)        Parenthesis / Grouping
 * 3. A string is a regular expression iff it can be derived from the regular expressions in 1 using a finite
 *    number of applications of the rules in 2.
 */
public abstract class RegularExpression {

    private static class Symbol {
        RegularExpression regexp;
        char character;

        public Symbol(RegularExpression reg) {
            this.regexp = reg;
        }

        public Symbol(char c) {
            this.character = c;
        }
    }

    /**
     * Parse a regular expression from a string.
     * @param s The string to parse
     * @param Σ The alphabet this regexp is defined over
     * @return A regular expression for the string.
     */
    public static RegularExpression parse(String s, Set<Character> Σ) {
        ArrayList<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                int parenCount = 1;
                int currInd = i + 1;
                while (currInd < s.length() && parenCount > 0) {
                    if (s.charAt(currInd) == ')') parenCount--;
                    else if (s.charAt(currInd) == '(') parenCount++;
                    currInd++;
                }
                if (parenCount != 0) {
                    char[] arr = new char[i];
                    for (int j = 0; j < i; j++) arr[j] = ' ';
                    String spaces = new String(arr);
                    System.err.println("Unbalanced parentheses: " + s);
                    System.err.println("                        " + spaces + "^");
                    throw new RuntimeException();
                }
                symbols.add(new Symbol(parse(s.substring(i+1, currInd-1), Σ)));
                i = currInd-1;
            } else if (Σ.contains(s.charAt(i))) {
                symbols.add(new Symbol(PrimitiveRegExp.a(s.charAt(i), Σ)));
            } else if (s.charAt(i) == 'λ') {
                symbols.add(new Symbol(PrimitiveRegExp.λ(Σ)));
            } else if (s.charAt(i) == 'ϕ') {
                symbols.add(new Symbol(PrimitiveRegExp.ϕ(Σ)));
            } else if (s.charAt(i) == '*' || s.charAt(i) == '+') {
                symbols.add(new Symbol(s.charAt(i)));
            } else {
                char[] arr = new char[i];
                for (int j = 0; j < i; j++) arr[j] = ' ';
                String spaces = new String(arr);
                System.err.println("Unknown symbol: " + s);
                System.err.println("                " + spaces + "^");
                throw new RuntimeException();
            }
        }

        // Expand the star-closures
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).character == '*') {
                if (i == 0) {
                    throw new RuntimeException("String can't start with *!");
                }
                System.out.println("Starring regular expression");
                symbols.get(i - 1).regexp = symbols.get(i - 1).regexp.star();
                symbols.remove(i);
                i--;
            }
        }

        // Expand concatenations
        for (int i = 0; i < symbols.size() - 1; i++) {
            if (symbols.get(i).regexp != null && symbols.get(i + 1).regexp != null) {
                symbols.get(i).regexp = symbols.get(i).regexp.append(symbols.get(i + 1).regexp);
                symbols.remove(i + 1);
                i--;
            }
        }

        // Expand union
        for (int i = 1; i < symbols.size() - 1; i++) {
            if (symbols.get(i - 1).regexp != null && symbols.get(i).character == '+' && symbols.get(i + 1).regexp != null) {
                symbols.get(i - 1).regexp = symbols.get(i - 1).regexp.or(symbols.get(i + 1).regexp);
                symbols.remove(i);
                symbols.remove(i);
                i--;
            }
        }

        if (symbols.size() > 1) {
            throw new RuntimeException("Bad regexp!");
        } else if (symbols.size() == 0) {
            return PrimitiveRegExp.λ(Σ);
        }
        return symbols.get(0).regexp;
    }

    public final Set<Character> Σ;

    public RegularExpression(Set<Character> Σ) {
        this.Σ = Set.copyOf(Σ);
    }

    /**
     * @param other The regexp to append
     * @return A regexp with this followed by other.
     */
    public RegularExpression append(RegularExpression other) {
        return new Concatenation(this, other);
    }

    /**
     * @param other The regexp to 'or' with.
     * @return A new regexp that recognizes this or the other.
     */
    public RegularExpression or(RegularExpression other) {
        return new Union(this, other);
    }

    /**
     * @return A new regexp that recognizes the star closure of this regexp.
     */
    public RegularExpression star() {
        return new StarClosure(this);
    }

    /**
     * @return An NFA representing this regexp.
     */
    public abstract NFA toNFA();

    /**
     * Converts the regexp to a DFA and optimizes it.
     * @return A minimal DFA that recognizes the same language as this regular expression.
     */
    public DFA toOptimizedDFA() {
        return toNFA().convertToDFA().reduceStates();
    }

}
