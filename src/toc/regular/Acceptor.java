package toc.regular;

/**
 * @author Jon Bolin
 */
public interface Acceptor {

    /**
     * Tells if the string is in the language recognized by this acceptor
     * @param s The string to be tested
     * @return true if the string is in the language, false otherwise.
     */
    boolean recognizes(String s);

}
