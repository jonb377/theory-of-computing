package toc.regular.exp.operations;

import toc.regular.exp.RegularExpression;
import toc.regular.nfa.NFA;
import toc.regular.nfa.NFABuilder;
import toc.regular.nfa.NFATransitionFunction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jon Bolin
 */
public class Concatenation extends RegularExpression {

    private RegularExpression r1, r2;

    public Concatenation(RegularExpression r1, RegularExpression r2) {
        super(r1.Σ);
        if (!r1.Σ.equals(r2.Σ)) {
            throw new RuntimeException("Cannot define operation on regular expressions from different alphabets.");
        }
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public NFA toNFA() {
        NFA nfa1 = r1.toNFA();
        NFA nfa2 = r2.toNFA();

        NFABuilder builder = new NFABuilder(Σ);

        // Add the nfa's
        builder.addNFA(nfa1);
        int nfa2offset = builder.addNFA(nfa2);

        // Link the final states of nfa1 to the initial state of nfa2
        for (int f : nfa1.F) {
            builder.addλTransition(f, nfa2offset);
        }

        // Add nfa2's final states
        for (int f : nfa2.F) {
            builder.addFinalState(f + nfa2offset);
        }

        return builder.build();
    }
}
