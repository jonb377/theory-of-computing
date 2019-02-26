package toc.regular.exp;

import toc.regular.exp.operations.Concatenation;
import toc.regular.exp.operations.StarClosure;
import toc.regular.exp.operations.Union;
import toc.regular.nfa.NFA;

import java.util.Set;

/**
 * @author Jon Bolin
 * A regular expression is defined as follows:
 *
 * 1. λ, ϕ, and a ∈ Σ are regular expressions
 * 2. If r1 and r2 are regular expressions, then so are:
 *      r1 r2       Concatenation
 *      r1 + r2     Union
 *      r1*         Star Closure
 *      (r1)        Parenthesis / Grouping
 * 3. A string is a regular expression iff it can be derived from the regular expressions in 1 using a finite
 *    number of applications of the rules in 2.
 */
public abstract class RegularExpression {

    public final Set<Character> Σ;

    public static RegularExpression toRegExp(String r, Set<Character> Σ) {
        if (r.length() == 0) {
            return PrimitiveRegExp.λ(Σ);
        } else if (r.length() == 1) {
            return PrimitiveRegExp.a(r.charAt(0), Σ);
        } else {
            // look for parentheses
            int index = r.indexOf('(');
            if (index >= 0) {
                int j = index + 1;
                int parenCount = 1;
                while (j < r.length() && parenCount > 0) {
                    if (r.charAt(j) == ')') parenCount--;
                    else if (r.charAt(j) == '(') parenCount++;
                    j++;
                }
                if (parenCount > 0) {
                    throw new RuntimeException("Invalid Regexp: unbalanced parentheses");
                }
                return toRegExp(r.substring(0, index), Σ)
                        .append(toRegExp(r.substring(index + 1, j), Σ))
                        .append(toRegExp(r.substring(j + 1), Σ));
            }
        }
    }

    public RegularExpression(Set<Character> Σ) {
        this.Σ = Set.copyOf(Σ);
    }

    public RegularExpression append(RegularExpression other) {
        return new Concatenation(this, other);
    }

    public RegularExpression or(RegularExpression other) {
        return new Union(this, other);
    }

    public RegularExpression star() {
        return new StarClosure(this);
    }

    public abstract NFA toNFA();

}
