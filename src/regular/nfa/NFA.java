package regular.nfa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jon Bolin
 */
public class NFA {

    private TransitionFunction delta;
    private Set<Integer> F;

    public NFA(TransitionFunction delta, Set<Integer> F) {
        this.delta = delta;
        this.F = F;
    }

    public boolean recognizes(String s) {
        Set<Integer> currStates = new HashSet<>(Arrays.asList(1));
        Set<Integer> nextStates = new HashSet<>();
        Set<Integer> tmp;
        for (char c : s.toCharArray()) {
            for (Integer q : currStates) {
                nextStates.addAll(delta.of(q, c));
            }
            tmp = currStates;
            currStates = nextStates;
            nextStates = tmp;
            nextStates.clear();
        }
        currStates.retainAll(F);
        return currStates.size() > 0;
    }

}
