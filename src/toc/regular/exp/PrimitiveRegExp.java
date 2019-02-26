package toc.regular.exp;

import toc.regular.nfa.NFA;
import toc.regular.nfa.NFATransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class PrimitiveRegExp extends RegularExpression {

    public static PrimitiveRegExp ϕ(Set<Character> Σ) {
        Map<Character, Set<Integer>>[] t = new Map[]{new HashMap()};
        NFATransitionFunction δ = NFATransitionFunction.createNFATransition(t, Σ);
        NFA nfa = new NFA(δ, Σ, new HashSet<>());
        return new PrimitiveRegExp(nfa, Σ);
    }

    public static PrimitiveRegExp λ(Set<Character> Σ) {
        Map<Character, Set<Integer>>[] t = new Map[]{new HashMap<>()};
        NFATransitionFunction δ = NFATransitionFunction.createNFATransition(t, Σ);
        NFA nfa = new NFA(δ, Σ, new HashSet<>(Arrays.asList(0)));
        return new PrimitiveRegExp(nfa, Σ);
    }

    public static PrimitiveRegExp a(char a, Set<Character> Σ) {
        if (!Σ.contains(a)) {
            throw new RuntimeException("Primitive Character is not in alphabet!");
        }
        Map<Character, Set<Integer>>[] t = new Map[]{new HashMap(), new HashMap()};
        t[0].put(a, new HashSet<>(Arrays.asList(1)));
        NFATransitionFunction δ = NFATransitionFunction.createNFATransition(t, Σ);
        NFA nfa = new NFA(δ, Σ, new HashSet<>(Arrays.asList(1)));
        return new PrimitiveRegExp(nfa, Σ);
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
