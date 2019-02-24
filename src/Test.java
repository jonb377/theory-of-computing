import toc.regular.Acceptor;
import toc.regular.dfa.DFA;
import toc.regular.dfa.TotalTransitionFunction;
import toc.regular.nfa.NFA;
import toc.regular.nfa.TransitionFunction;

import java.util.*;

import static toc.Settings.ALPHABET_SIZE;

/**=
 * @author Jon Bolin
 */
public class Test {

    public static void main(String[] args) {
        testReduceStates();
    }

    public static void testAcceptor(Acceptor... acceptors) {
        Scanner in = new Scanner(System.in);
        while (true) {
            String s = in.nextLine();
            for (Acceptor a : acceptors) {
                System.out.println(a.recognizes(s));
            }
        }
    }

    public static void nfaTest() {
        Set[][] t = new Set[4][ALPHABET_SIZE];
        t[1]['a'] = new HashSet<>(Arrays.asList(1, 2));
        t[1]['b'] = new HashSet<>(Arrays.asList(2));
        t[2]['a'] = new HashSet<>(Arrays.asList(3));
        t[2]['b'] = new HashSet<>(Arrays.asList(1, 2));
        TransitionFunction delta = new TransitionFunction(t);

        Set F = new HashSet(Arrays.asList(2));
        NFA nfa = new NFA(delta, F);

        testAcceptor(nfa);
    }

    public static void dfaTest() {
        int[][] transition = new int[3][ALPHABET_SIZE];
        transition[1]['a'] = 1;
        transition[1]['b'] = 2;
        transition[2]['a'] = 0;
        transition[2]['b'] = 2;
        TotalTransitionFunction t = new TotalTransitionFunction(transition);
        Set F = new HashSet(Arrays.asList(2));
        DFA dfa = new DFA(t, F);

        testAcceptor(dfa);
    }

    public static void testReduceStates() {
        int[][] t = new int[7][ALPHABET_SIZE];
        t[1]['0'] = 2;
        t[1]['1'] = 3;
        t[2]['0'] = 3;
        t[2]['1'] = 4;
        t[3]['0'] = 3;
        t[3]['1'] = 5;
        t[4]['0'] = 4;
        t[4]['1'] = 4;
        t[5]['0'] = 5;
        t[5]['1'] = 5;
        t[6]['0'] = 6;
        t[6]['1'] = 5;
        TotalTransitionFunction transition = new TotalTransitionFunction(t);

        Set F = new HashSet(Arrays.asList(4,5));
        DFA dfa = new DFA(transition, F);
        DFA reduced = dfa.reduceStates();
        System.out.println("Original: " + dfa.numStates());
        System.out.println("Reduced:  " + reduced.numStates());

        testAcceptor(dfa, reduced);
    }

    public static void testNFAtoDFA() {
        Set[][] t = new Set[3][ALPHABET_SIZE];
        t[1]['a'] = new HashSet<>(Arrays.asList(1, 2));
        t[1]['b'] = new HashSet<>(Arrays.asList(2));
        t[2]['b'] = new HashSet<>(Arrays.asList(1, 2));
        TransitionFunction delta = new TransitionFunction(t);

        Set F = new HashSet(Arrays.asList(2));
        NFA nfa = new NFA(delta, F);
        DFA dfa = nfa.convertToDFA();
        testAcceptor(nfa, dfa);
    }
}
