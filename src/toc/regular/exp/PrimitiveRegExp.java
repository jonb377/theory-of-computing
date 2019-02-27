package toc.regular.exp;

import toc.regular.nfa.NFA;
import toc.regular.nfa.NFABuilder;
import toc.regular.nfa.NFATransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class PrimitiveRegExp extends RegularExpression {

    /**
     * The regular expression for the empty language.
     * @param Σ The alphabet set this regex is defined over
     * @return A Regular Expression that accepts nothing.
     */
    public static PrimitiveRegExp ϕ(Set<Character> Σ) {
        NFABuilder builder = new NFABuilder(Σ);
        builder.addState();
        return new PrimitiveRegExp(builder.build(), Σ);
    }

    /**
     * The regular expression for the empty string.
     * @param Σ The alphabet set this regex is defined over.
     * @return A regular expression that recognizes the empty string.
     */
    public static PrimitiveRegExp λ(Set<Character> Σ) {
        NFABuilder builder = new NFABuilder(Σ);
        builder.addFinalState(builder.addState());
        return new PrimitiveRegExp(builder.build(), Σ);
    }

    /**
     * A regular expression that identifies a single character from the alphabet.
     * @param a The letter to be recognized
     * @param Σ The alphabet set to define the regex over
     * @return A regular expression that recognizes the string 'a'.
     */
    public static PrimitiveRegExp a(char a, Set<Character> Σ) {
        if (!Σ.contains(a)) {
            throw new RuntimeException("Primitive Character is not in alphabet!");
        }
        NFABuilder builder = new NFABuilder(Σ);
        int start = builder.addState();
        int end = builder.addState();
        builder.addTransition(start, a, end);
        builder.addFinalState(end);
        return new PrimitiveRegExp(builder.build(), Σ);
    }

    private final NFA recognizer;

    private PrimitiveRegExp(NFA regcognizer, Set<Character> Σ) {
        super(Σ);
        this.recognizer = regcognizer;
    }

    @Override
    public NFA toNFA() {
        return recognizer;
    }
}
