package ephemera.tester;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.scene.Skybox;
import com.jme.util.TextureManager;

import ephemera.model.World;


public class HelloSkybox extends SimpleGame{

	public static void main(String[] args) {
		new HelloSkybox().start();
	}
	@Override
	protected void simpleInitGame() {
		// TODO Auto-generated method stub
		Skybox sky = new Skybox("tata",250,250,250);
		
		Texture north = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear); // custom/1.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture east = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture south = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture west = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture up = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture down = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		
		sky.setTexture(Skybox.Face.North, north);
		sky.setTexture(Skybox.Face.East, east);
		sky.setTexture(Skybox.Face.South, south);
		sky.setTexture(Skybox.Face.West, west);
		sky.setTexture(Skybox.Face.Up, up);
		sky.setTexture(Skybox.Face.Down, down);
		
		sky.preloadTextures();
		sky.updateRenderState();
		
		this.rootNode.attachChild(sky);
	}

}
