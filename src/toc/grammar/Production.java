package toc.grammar;

/**
 * @author Jon Bolin
 */
public class Production {

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
    public String toString() {
        return LHS + " -> " + RHS;
    }

}
