import regular.dfa.DFA;
import regular.dfa.TotalTransitionFunction;
import regular.nfa.NFA;
import regular.nfa.TransitionFunction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**=
 * @author Jon Bolin
 */
public class Test {

    public static void main(String[] args) {
        nfaTest();
    }

    public static void nfaTest() {
        Set[][] t = new Set[4][256];
        t[1]['a'] = new HashSet<>(Arrays.asList(1, 2));
        t[1]['b'] = new HashSet<>(Arrays.asList(2));
        t[2]['a'] = new HashSet<>(Arrays.asList(3));
        t[2]['b'] = new HashSet<>(Arrays.asList(1, 2));
        TransitionFunction delta = new TransitionFunction(t);

        Set F = new HashSet(Arrays.asList(2));
        NFA nfa = new NFA(delta, F);

        Scanner in = new Scanner(System.in);
        while (true) {
            String s = in.nextLine();
            System.out.println(nfa.recognizes(s));
        }
    }

    public static void dfaTest() {
        int[][] transition = new int[3][256];
        transition[1]['a'] = 1;
        transition[1]['b'] = 2;
        transition[2]['a'] = 0;
        transition[2]['b'] = 2;
        TotalTransitionFunction t = new TotalTransitionFunction(transition);
        Set F = new HashSet(Arrays.asList(2));
        DFA dfa = new DFA(t, F);

        Scanner in = new Scanner(System.in);
        while (true) {
            String s = in.nextLine();
            System.out.println(dfa.recognizes(s));
        }
    }
}
