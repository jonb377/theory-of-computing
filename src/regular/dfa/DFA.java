package regular.dfa;

import java.util.Set;

/**
 * @author Jon Bolin
 */
public class DFA {

    private TotalTransitionFunction delta;
    private Set<Integer> F;

    public DFA(TotalTransitionFunction delta, Set F) {
        this.delta = delta;
        this.F = F;
    }

    /**
     * Tests whether the DFA recognizes the string.
     * @param s
     * @return
     */
    public boolean recognizes(String s) {
        int state = 1;
        for (char c : s.toCharArray()) {
            state = delta.of(state, c);
        }
        return F.contains(state);
    }

}
