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
public class Union extends RegularExpression {

    private RegularExpression r1, r2;

    public Union(RegularExpression r1, RegularExpression r2) {
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

        // Add the new initial state
        builder.addState();

        // Add the two NFA's
        int nfa1offset = builder.addNFA(nfa1);
        int nfa2offset = builder.addNFA(nfa2);

        // Lambda into their respective initial states
        builder.addλTransition(0, nfa1offset);
        builder.addλTransition(0, nfa2offset);

        // Add final state
        int finalState = builder.addState();
        builder.addFinalState(finalState);

        // Lambda into final state from all final states
        for (int f : nfa1.F) {
            builder.addλTransition(f + nfa1offset, finalState);
        }
        for (int f : nfa2.F) {
            builder.addλTransition(f + nfa2offset, finalState);
        }

        return builder.build();
    }

}
