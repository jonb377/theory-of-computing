package toc.regular.exp.operations;

import toc.regular.exp.RegularExpression;
import toc.regular.nfa.NFA;
import toc.regular.nfa.NFABuilder;

/**
 * @author Jon Bolin
 */
public class StarClosure extends RegularExpression {

    private RegularExpression r1;

    public StarClosure(RegularExpression r1) {
        super(r1.Σ);
        this.r1 = r1;
    }

    @Override
    public NFA toNFA() {
        NFABuilder builder = new NFABuilder(Σ);
        builder.addState();
        NFA r1nfa = r1.toNFA();
        int r1Offset = builder.addNFA(r1nfa);
        int finalState = builder.addState();

        // Add the wrapper states
        builder.addλTransition(0, 1);       // From new initial to r1's start
        builder.addλTransition(0, finalState); // From new initial to new final
        builder.addλTransition(finalState, 0); // From new final to new initial
        for (int f : r1nfa.F) {
            builder.addλTransition(f + r1Offset, finalState);
        }

        // Add the final state
        builder.addFinalState(finalState);

        return builder.build();
    }
}
