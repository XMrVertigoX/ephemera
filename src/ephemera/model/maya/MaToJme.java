package ephemera.model.maya;

import java.io.FileReader;
import java.io.IOException;

import com.jme.scene.Node;

/**
 * 
 * @author -LANmower-
 */
public class MaToJme {
    public static void main(final String args[]) throws IOException {
        final MaToJme maToJme = new MaToJme("test");
        maToJme.parse();
    }

    private MaReader inFile;
    private static Node root = new Node("MaRoot");

    public static Node getRoot() {
        root.updateRenderState();
        return root;
    }

    public MaToJme(final String fileName) {
        try {
            inFile = new MaReader(new FileReader(fileName + ".ma"));
            root.setName(fileName);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void parse() throws IOException {
        MaNode node = null;
        for (String[] line = inFile.readLine(); line != null; line = inFile
                .readLine()) {
            if (line[0].equals("createNode")) {
                node = null;
                final String type = line[1];
                node = MaTransform.createIfType(type, line);
                if (node == null)
                    node = MaMesh.createIfType(type, line);
                if (node == null)
                    node = MaMaterial.createIfType(type, line);
            } else if (node != null)
                if (line[0].equals("setAttr"))
                    node.setAttribute(line);
            if (line[0].equals("connectAttr"))
                MaMaterial.connectAttribute(line);
        }
    }
}
