package toc.regular;

import java.util.Collections;
import java.util.Set;

/**
 * @author Jon Bolin
 */
public abstract class Acceptor<T> {

    public final Set<Character> Σ;
    private TransitionFunction<T> δ;

    public Acceptor(Set<Character> Σ) {
        this.Σ = Collections.unmodifiableSet(Σ);
    }

    /**
     * Tells if the string is in the language recognized by this acceptor
     * @param s The string to be tested
     * @return true if the string is in the language, false otherwise.
     */
    public abstract boolean recognizes(String s);

}
