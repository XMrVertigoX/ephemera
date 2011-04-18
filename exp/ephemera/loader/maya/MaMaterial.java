package ephemera.loader.maya;

import java.io.IOException;
import java.util.Hashtable;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

public class MaMaterial extends MaNode {
    private static Hashtable<String, MaMaterial> materials = new Hashtable<String, MaMaterial>();
    private static Hashtable<String, Texture> textures = new Hashtable<String, Texture>();
    public static String[] syntax = { "blinn", "lambert", "file" };

    public static void connectAttribute(final String[] line) throws IOException {
        final String[] from = line[1].split("\\.");
        final String[] to = line[2].split("\\.");
        if (from.length > 0)
            if (materials.containsKey(to[0])
                    && MaMesh.meshes.containsKey(from[0])) {
                final Spatial node = MaMesh.meshes.get(from[0]).getMesh();
                if (to[1].equals("dagSetMembers") || to[1].equals("dsm"))
                    if (to[0].equals(":initialShadingGroup"))
                        defaultMaterial.applyToNode(node);
                    else if (materials.containsKey(to[0]))
                        materials.get(to[0]).applyToNode(node);
                    else {
                    }
            } else if (textures.containsKey(from[0])) {
                final Texture texture = textures.get(from[0]);
                if (materials.containsKey(to[0])
                        && (from[1].equals("outColor") || from[1].equals("oc"))) {
                    materials.get(to[0]).setTexture(texture);
                    if (to[1].equals("color") || to[1].equals("c"))
                        materials.get(to[0]).ms
                                .setColorMaterial(MaterialState.ColorMaterial.Diffuse);
                    if (to[1].equals("incandescence") || to[1].equals("ic"))
                        materials.get(to[0]).ms
                                .setColorMaterial(MaterialState.ColorMaterial.Emissive);
                } else {
                }
            } else if (materials.containsKey(from[0]))
                if ((from[1].equals("outColor") || from[1].equals("oc"))
                        && (to[1].equals("surfaceShader") || to[1].equals("ss")))
                    materials.put(to[0], materials.get(from[0]));

    }

    public static MaNode createIfType(final String type, final String[] line)
            throws IOException {
        for (final String element : syntax)
            if (type.equals(element))
                return new MaMaterial(line);
        return null;
    }

    TextureState ts = DisplaySystem.getDisplaySystem().getRenderer()
            .createTextureState();

    MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
            .createMaterialState();

    private static MaMaterial defaultMaterial = new MaMaterial(
            "DefaultMaterial");

    public MaMaterial(final String name) {
        super(new String[0]);
        this.name = name;
        init();
    }

    public MaMaterial(final String[] line) throws IOException {
        super(line);
        init();
    }

    public void applyToNode(final Spatial target) {
        target.setRenderState(ts);
        target.setRenderState(ms);
    }

    public void init() {
        ms.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
        ms.setDiffuse(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
        ms.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
        ms.setSpecular(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
        ms.setShininess(0);
        ms.setNeedsRefresh(true);
        ms.setEnabled(true);
        ts.setEnabled(false);
        materials.put(name, this);
    }

    @Override
    public void setAttribute(final String[] line) throws IOException {
        for (int currentWord = 1; currentWord < line.length; currentWord++) {
            String word = line[currentWord];
            if (word.equals(".color") || word.equals(".c"))
                ms.setDiffuse(new ColorRGBA(Float
                        .valueOf(line[currentWord + 3]).floatValue(), Float
                        .valueOf(line[currentWord + 4]).floatValue(), Float
                        .valueOf(line[currentWord + 5]).floatValue(), 1.0f));
            else if (word.equals(".fileTextureName") || word.equals(".ftn")) {
                word = line[currentWord += 3];
                currentWord++;
                while (!line[currentWord].equals("ENDOFLINE")) {
                    word = word + " " + line[currentWord];
                    currentWord++;
                }
                textures.put(name, TextureManager.loadTexture(new java.net.URL(
                        "file:" + word),
                        Texture.MinificationFilter.BilinearNearestMipMap,
                        Texture.MagnificationFilter.Bilinear));
            }
        }
    }

    public void setTexture(final Texture texture) {
        ts.setTexture(texture);
        ts.setEnabled(true);
        ts.setNeedsRefresh(true);
    }

}
