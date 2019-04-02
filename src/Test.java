import toc.contextfree.ContextFreeGrammar;
import toc.contextfree.CFGBuilder;
import toc.grammar.Production;
import toc.regular.Acceptor;
import toc.regular.dfa.DFA;
import toc.regular.dfa.DFATransitionFunction;
import toc.regular.exp.PrimitiveRegExp;
import toc.regular.exp.RegularExpression;
import toc.regular.exp.operations.Concatenation;
import toc.regular.exp.operations.StarClosure;
import toc.regular.exp.operations.Union;
import toc.regular.grammar.RightLinearGrammar;
import toc.regular.nfa.NFA;
import toc.regular.nfa.NFATransitionFunction;

import java.util.*;

/**
 * @author Jon Bolin
 */
public class Test {

    public static void main(String[] args) {
//        dfaTest();
//        nfaTest();
//        testReduceStates();
//        testNFAtoDFA();
//        testConcatenation();
//        testStarClosure();
//        testUnion();
//        testRegexp();
//        testRLG();
//        testRLGGeneration();
//        testCFG();
//        testNullableCFG();
//        testUselessCFG();
//        testUnitCFG();
//        testCNF();
        testCFGMembership();
    }

    public static void testCFGMembership() {
        CFGBuilder builder = new CFGBuilder('S');
        builder.addProduction("S", "AB");
        builder.addProduction("A", "BB|a");
        builder.addProduction("B", "AB|b");
        ContextFreeGrammar cfg = builder.build().toChomskyNormalForm();
        Scanner in = new Scanner(System.in);
        while (true) {
            String s = in.nextLine();
            System.out.println(cfg.isMember(s));
        }
    }

    public static void testCNF() {
        CFGBuilder builder = new CFGBuilder('S');
        builder.addProduction("S", "ABa");
        builder.addProduction("B", "Ac");
        builder.addProduction("A", "aab");
        ContextFreeGrammar cfg = builder.build();
        System.out.println(cfg);
        System.out.println(cfg.toChomskyNormalForm());
    }

    public static void testUnitCFG() {
        CFGBuilder builder = new CFGBuilder('S');
        builder.addProduction("S", "Aa|B");
        builder.addProduction("B", "A|bb");
        builder.addProduction("A", "a|bc|B");
        ContextFreeGrammar cfg = builder.build();
        System.out.println(cfg);
        System.out.println(cfg.removeUnitProductions().removeUselessProductions());
    }

    public static void testUselessCFG() {
        Set<Character> T = new HashSet<>(Arrays.asList('a','b','c'));
        Set<Character> V = new HashSet<>(Arrays.asList('A', 'B', 'C', 'S'));
        Set<Production> P = new HashSet<>();
        P.add(new Production("S","A"));
        P.add(new Production("S", "B"));
        P.add(new Production("A","aA"));
        P.add(new Production("A","a"));
        P.add(new Production("B", "bB"));
        P.add(new Production("C", "S"));
        ContextFreeGrammar cfg = new ContextFreeGrammar(T, V, P, 'S');
        cfg = cfg.removeUselessProductions();
        System.out.println(cfg);
    }

    public static void testNullableCFG() {
        Set<Character> T = new HashSet<>(Arrays.asList('a','b','c'));
        Set<Character> V = new HashSet<>(Arrays.asList('A', 'B', 'C', 'S'));
        Set<Production> P = new HashSet<>();
        P.add(new Production("S","AB"));
        P.add(new Production("A","aA"));
        P.add(new Production("A", ""));
        P.add(new Production("B", "bB"));
        P.add(new Production("B", ""));
        ContextFreeGrammar cfg = new ContextFreeGrammar(T, V, P, 'S');
        cfg = cfg.removeLambdaProductions();
        System.out.println(cfg);
    }

    public static void testCFG() {
        Set<Character> T = new HashSet<>(Arrays.asList('a','b','c'));
        Set<Character> V = new HashSet<>(Arrays.asList('A', 'B', 'C', 'S'));
        Set<Production> P = new HashSet<>();
        P.add(new Production("S","aSb"));
        P.add(new Production("S","λ"));
        ContextFreeGrammar cfg = new ContextFreeGrammar(T, V, P, 'S');
        for (String s : cfg.produce(10)) {
            System.out.println(s);
        }
    }

    public static void testRLGGeneration() {
        RegularExpression reg = RegularExpression.parse("(c+d)(ab)*", new HashSet<>(Arrays.asList('a','b','c','d')));
        RightLinearGrammar rlg = reg.toNFA().toRLG();
        for (String s : rlg.produce(4)) {
            System.out.println(s);
        }
    }

    public static void testRLG() {
        Set<Character> T = new HashSet<>(Arrays.asList('a','b','c'));
        Set<Character> V = new HashSet<>(Arrays.asList('A', 'B', 'C', 'S'));
        Set<Production> P = new HashSet<>();
        P.add(new Production("S","abS"));
        P.add(new Production("S","a"));
        RightLinearGrammar rlg = new RightLinearGrammar(T, V, P, 'S');
        for (String s : rlg.produce(10)) {
            System.out.println(s);
        }
        NFA nfa = rlg.toNFA();
        testAcceptor(nfa);
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

    public static void testRegexp() {
        Set<Character> Σ = new HashSet<>(Arrays.asList('a', 'b', 'c'));
        RegularExpression regex = RegularExpression.parse("λϕ+a*", Σ);
        NFA nfa = regex.toNFA();
        DFA dfa = regex.toOptimizedDFA();
        System.out.println(nfa.δ.numStates() + "\t" + dfa.numStates());

        testAcceptor(dfa);
    }

    public static void testReduceStates() {
        Map<Character, Integer>[] t = DFATransitionFromString(
                "(0 0 1)(0 1 2)(1 0 2)(1 1 3)(2 0 2)(2 1 4)(3 0 3)(3 1 3)(4 0 4)(4 1 4)(5 0 5)(5 1 4)"
        );
        Set<Character> Σ = new HashSet<>(Arrays.asList('0', '1'));
        DFATransitionFunction transition = DFATransitionFunction.createTotalTransitionFunction(t, Σ);

        Set F = new HashSet(Arrays.asList(3,4));
        DFA dfa = new DFA(transition, Σ, F);
        DFA reduced = dfa.reduceStates();
        System.out.println("Original: " + dfa.numStates());
        System.out.println("Reduced:  " + reduced.numStates());

        testAcceptor(dfa, reduced);
    }

    public static void testConcatenation() {
        Set<Character> Σ = new HashSet<>(Arrays.asList('a', 'b', 'c'));
        PrimitiveRegExp a = PrimitiveRegExp.a('a', Σ);
        PrimitiveRegExp b = PrimitiveRegExp.a('b', Σ);
        PrimitiveRegExp c = PrimitiveRegExp.a('c', Σ);
        RegularExpression cab = new Concatenation(c, new Concatenation(a, b));

        testAcceptor(cab.toNFA());
    }

    public static void testStarClosure() {
        // Matches ca*b
        Set<Character> Σ = new HashSet<>(Arrays.asList('a', 'b', 'c'));
        PrimitiveRegExp a = PrimitiveRegExp.a('a', Σ);
        PrimitiveRegExp b = PrimitiveRegExp.a('b', Σ);
        PrimitiveRegExp c = PrimitiveRegExp.a('c', Σ);
        RegularExpression cab = new Concatenation(c, new Concatenation(new StarClosure(a), b));

        testAcceptor(cab.toNFA());
    }

    public static void testUnion() {
        // Matches c(a+b)*c
        Set<Character> Σ = new HashSet<>(Arrays.asList('a', 'b', 'c'));
        PrimitiveRegExp a = PrimitiveRegExp.a('a', Σ);
        PrimitiveRegExp b = PrimitiveRegExp.a('b', Σ);
        PrimitiveRegExp c = PrimitiveRegExp.a('c', Σ);
        RegularExpression cab = new Concatenation(c, new Concatenation(new StarClosure(new Union(a, b)), c));

        testAcceptor(cab.toNFA());
    }


    public static void testNFAtoDFA() {
        Map<Character, Set<Integer>>[] t = NFATransitionFromString(
                "0 0 1\n" +
                        "1 0 0 2\n" +
                        "1 1 1 2\n" + "2 0 2\n" +
                        "2 1 1"
        );
        for (Map<Character, Set<Integer>> s : t) {
            System.out.println(s);
        }
        List<Set<Integer>> λ = new ArrayList<>();
        λ.add(new HashSet<>(Arrays.asList(1)));
        λ.add(new HashSet<>());
        λ.add(new HashSet<>());

        Set<Character> Σ = new HashSet<>(Arrays.asList('0', '1'));
        NFATransitionFunction delta = NFATransitionFunction.createNFATransition(t, λ, Σ);

        Set F = new HashSet(Arrays.asList(1));
        NFA nfa = new NFA(delta, Σ, F);
        DFA dfa = nfa.convertToDFA();
        DFA reduced = dfa.reduceStates();
        testAcceptor(nfa, dfa, reduced);
    }

    public static Map<Character, Integer>[] DFATransitionFromString(String s) {
        s = s.replaceAll("[\\(\\),]", " ");
        Scanner in = new Scanner(s);
        Map<Integer, Map<Character, Integer>> map = new HashMap<>();
        int maxState = -1;
        while (in.hasNext()) {
            int state = in.nextInt();
            char transition = in.next().charAt(0);
            int result = in.nextInt();

            if (!map.containsKey(state)) {
                if (state > maxState) maxState = state;
                map.put(state, new HashMap<>());
            }
            map.get(state).put(transition, result);
        }
        Map<Character, Integer>[] arr = new Map[maxState];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = map.getOrDefault(i, new HashMap<>());
        }
        return arr;
    }

    public static Map<Character, Set<Integer>>[] NFATransitionFromString(String s) {
        s = s.replaceAll("[\\(\\)]", "");
        Scanner in = new Scanner(s);
        Map<Integer, Map<Character, Set<Integer>>> map = new HashMap<>();
        int maxState = -1;
        while (in.hasNextLine()) {
            Scanner currLine = new Scanner(in.nextLine());
            int state = currLine.nextInt();
            char transition = currLine.next().charAt(0);

            if (!map.containsKey(state)) {
                if (state > maxState) maxState = state;
                map.put(state, new HashMap<>());
            }
            if (!map.get(state).containsKey(transition)) {
                map.get(state).put(transition, new HashSet<>());
            }
            while (currLine.hasNextInt()) {
                int result = currLine.nextInt();
                map.get(state).get(transition).add(result);
            }
        }
        Map<Character, Set<Integer>>[] arr = new Map[maxState + 1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = map.getOrDefault(i, new HashMap<>());
        }
        return arr;
    }
}
