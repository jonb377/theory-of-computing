package toc.contextfree;

import toc.Acceptor;

import java.util.Set;

/**
 * @author Jon Bolin
 */
public class NPDA extends Acceptor {

    public NPDA(Set<Character> Σ) {
        super(Σ);
    }

    @Override
    public boolean recognizes(String s) {
        return false;
    }
}
