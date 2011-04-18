package ephemera.loader.maya;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

import ephemera.model.maya.MaNode;
import ephemera.model.maya.MaToJme;
import ephemera.model.maya.MaTransform;

public class MaTransform extends MaNode {
    public static Map<String, MaTransform> nodes = new Hashtable<String, MaTransform>();
    private final Node node;
    private Node parent;
    public static String[] syntax = { "transform" };

    public static MaNode createIfType(final String type, final String[] line)
            throws IOException {
        for (final String element : syntax)
            if (type.equals(element)) {
                final MaTransform maTransform = new MaTransform(line);
                if (maTransform.getParent() == null)
                    MaToJme.getRoot().attachChild(maTransform.node);
                return maTransform;
            }
        return null;
    }

    public MaTransform(final String[] line) {
        super(line);
        nodes.put(name, this);
        node = new Node(name);
        for (int currentWord = 1; currentWord < line.length; currentWord++) {
            final String word = line[currentWord];
            if (word.equals("-parent") || word.equals("-p")) {
                parent = nodes.get(line[currentWord + 1]).getNode();
                parent.attachChild(node);
            }
        }
    }

    public Node getNode() {
        return node;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public void setAttribute(final String[] line) throws IOException {
        super.setAttribute(line);
        for (int currentWord = 1; currentWord < line.length; currentWord++) {
            final String word = line[currentWord];
            if (word.equals(".translate") || word.equals(".t")) {
                final float x = Float.valueOf(line[currentWord + 3]);
                final float y = Float.valueOf(line[currentWord + 4]);
                final float z = Float.valueOf(line[currentWord + 5]);
                node.setLocalTranslation(new Vector3f(x, y, z));
            } else if (word.equals(".scale") || word.equals(".s")) {
                final float x = Float.valueOf(line[currentWord + 3]);
                final float y = Float.valueOf(line[currentWord + 4]);
                final float z = Float.valueOf(line[currentWord + 5]);
                node.setLocalScale(new Vector3f(x, y, z));
            }
        }
    }
}
