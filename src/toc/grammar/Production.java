package toc.grammar;

/**
 * @author Jon Bolin
 */
public class Production implements Comparable<Production> {

    public final String LHS, RHS;

    public Production(String l, String r) {
        LHS = l;
        RHS = r.replaceAll("λ", "");  // Eliminate the empty string
    }

    String sub(String s, int ind) {
        for (int i = 0; i < LHS.length(); i ++){
            if (ind + i >= s.length() || s.charAt(i+ind) != LHS.charAt(i)) return null;
        }
        return s.substring(0, ind) + RHS + s.substring(ind + LHS.length());
    }

    @Override
    public int hashCode() {
        return LHS.hashCode() ^ RHS.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Production && ((Production) other).LHS.equals(LHS) && ((Production) other).RHS.equals(RHS);
    }

    @Override
    public String toString() {
        return LHS + " -> " + RHS;
    }

    @Override
    public int compareTo(Production o) {
        int lc = LHS.compareTo(o.LHS);
        return lc == 0 ? RHS.compareTo(o.RHS) : lc;
    }
}
