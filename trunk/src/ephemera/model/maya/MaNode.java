package ephemera.model.maya;

import java.io.IOException;

public abstract class MaNode {
    public static void connectAttribute(final String[] line) throws IOException {
    }

    public static MaNode createIfType(final String type) {
        return null;
    }

    public String[] syntax;

    String name;

    public MaNode(final String[] line) {
        for (int currentWord = 1; currentWord < line.length; currentWord++) {
            final String word = line[currentWord];
            if (word.equals("-name") || word.equals("-n"))
                name = line[currentWord + 1];
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return name;
    }

    public void setAttribute(final String[] line) throws IOException {
    }
}
