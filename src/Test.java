import toc.regular.Acceptor;
import toc.regular.dfa.DFA;
import toc.regular.dfa.DFATransitionFunction;
import toc.regular.nfa.NFA;
import toc.regular.nfa.NFATransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class Test {

    public static final int ALPHABET_SIZE = 256;

    public static void main(String[] args) {
//        dfaTest();
//        nfaTest();
//        testReduceStates();
        testNFAtoDFA();
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
        Set[][] t = new Set[6][ALPHABET_SIZE];
        t[0]['a'] = new HashSet(Arrays.asList(1, 4));
        t[1]['a'] = new HashSet(Arrays.asList(2));
        t[2]['a'] = new HashSet(Arrays.asList(3));
        t[3]['a'] = new HashSet();
        t[4]['a'] = new HashSet(Arrays.asList(5));
        t[5]['a'] = new HashSet(Arrays.asList(4));
        Set<Character> Σ = new HashSet<>(Arrays.asList('a'));
        NFATransitionFunction delta = NFATransitionFunction.createNFATransition(t, Σ);

        Set F = new HashSet(Arrays.asList(3, 5));
        NFA nfa = new NFA(delta, Σ, F);
        DFA dfa = nfa.convertToDFA();
        testAcceptor(nfa, dfa);
    }

    public static void dfaTest() {
        Integer[][] transition = new Integer[3][ALPHABET_SIZE];
        transition[0]['a'] = 0;
        transition[0]['b'] = 1;
        transition[1]['a'] = 2;
        transition[1]['b'] = 1;
        transition[2]['a'] = 2;
        transition[2]['b'] = 2;
        Set<Character> Σ = new HashSet<>(Arrays.asList('a', 'b'));
        DFATransitionFunction t = DFATransitionFunction.createTotalTransitionFunction(transition, Σ);
        Set F = new HashSet(Arrays.asList(1));
        DFA dfa = new DFA(t, Σ, F);

        testAcceptor(dfa);
    }

    public static void testReduceStates() {
        Integer[][] t = new Integer[6][ALPHABET_SIZE];
        t[0]['0'] = 1;
        t[0]['1'] = 2;
        t[1]['0'] = 2;
        t[1]['1'] = 3;
        t[2]['0'] = 2;
        t[2]['1'] = 4;
        t[3]['0'] = 3;
        t[3]['1'] = 3;
        t[4]['0'] = 4;
        t[4]['1'] = 4;
        t[5]['0'] = 5;
        t[5]['1'] = 4;
        Set<Character> Σ = new HashSet<>(Arrays.asList('0', '1'));
        DFATransitionFunction transition = DFATransitionFunction.createTotalTransitionFunction(t, Σ);

        Set F = new HashSet(Arrays.asList(3,4));
        DFA dfa = new DFA(transition, Σ, F);
        DFA reduced = dfa.reduceStates();
        System.out.println("Original: " + dfa.numStates());
        System.out.println("Reduced:  " + reduced.numStates());

        testAcceptor(dfa, reduced);
    }

    public static void testNFAtoDFA() {
        Set[][] t = new Set[3][ALPHABET_SIZE];
        t[0]['0'] = new HashSet<>(Arrays.asList(1));
        t[1]['0'] = new HashSet<>(Arrays.asList(0, 2));
        t[1]['1'] = new HashSet<>(Arrays.asList(1, 2));
        t[2]['0'] = new HashSet<>(Arrays.asList(2));
        t[2]['1'] = new HashSet<>(Arrays.asList(1));

        Set[] λ = new Set[3];
        λ[0] = new HashSet<>(Arrays.asList(1));
        λ[1] = new HashSet<>();
        λ[2] = new HashSet<>();

        Set<Character> Σ = new HashSet<>(Arrays.asList('0', '1'));
        NFATransitionFunction delta = NFATransitionFunction.createNFATransition(t, λ, Σ);

        Set F = new HashSet(Arrays.asList(1));
        NFA nfa = new NFA(delta, Σ, F);
        DFA dfa = nfa.convertToDFA();
        DFA reduced = dfa.reduceStates();
        testAcceptor(nfa, dfa, reduced);
    }
}
