package toc.regular.exp.operations;

import toc.regular.exp.PrimitiveRegExp;
import toc.regular.exp.RegularExpression;

import java.util.Set;

/**
 * @author Jon Bolin
 */
public class RegexpParser {

    private String regexp;
    private int index;
    private Set<Character> Σ;

    public RegexpParser(String regexp, Set<Character> Σ) {
        this.regexp = regexp;
        index = 0;
        this.Σ = Σ;
    }

    public RegularExpression parseValue() {
        return PrimitiveRegExp.a(regexp.charAt(index++), Σ);
    }

    public RegularExpression parse() {

    }

}
