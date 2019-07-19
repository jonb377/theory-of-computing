package toc;

import java.util.Set;

/**
 * @author Jon Bolin
 */
public abstract class Acceptor {

    public final Set<Character> Σ;

    public Acceptor(Set<Character> Σ) {
        this.Σ = Set.copyOf(Σ);
    }

    /**
     * Tells if the string is in the language recognized by this acceptor
     * @param s The string to be tested
     * @return true if the string is in the language, false otherwise.
     */
    public abstract boolean recognizes(String s);

}
