/**
 * WorldController 2011 by Semjon Mooraj
 * diese Klasse stellt die Welt dar in der sich die Fliegen bewegen.
 * Die Welt ist standartmäig ein Quadratischer Würfel der Kantenlänge 2000 
 */
package Controller;

import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.util.TextureManager;

public class WorldController {

	private Skybox sky;
	
	public WorldController(){
		sky = new Skybox("Skybox",2000,2000,2000);
		initSky();
	}
	
	public WorldController(int xSize,int ySize,int zSize){
		sky = new Skybox("WorldCubeBox",xSize,ySize,zSize);
		initSky();
	}
	
	
	/**
	 * Lade die Texturen und verknuepfe diese mit Skybox
	 */
	public void initSky(){
		// Lade die Texturen 
		Texture north = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("SkyboxSkin/cubemap_arch/arch_positive_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear); // custom/1.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture east = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("SkyboxSkin/cubemap_arch/arch_negative_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture south = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("SkyboxSkin/cubemap_arch/arch_negative_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture west = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("SkyboxSkin/cubemap_arch/arch_positive_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture up = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("SkyboxSkin/cubemap_arch/arch_positive_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture down = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("SkyboxSkin/cubemap_arch/arch_negative_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		
		sky.setTexture(Skybox.Face.North, north);
		sky.setTexture(Skybox.Face.East, east);
		sky.setTexture(Skybox.Face.South, south);
		sky.setTexture(Skybox.Face.West, west);
		sky.setTexture(Skybox.Face.Up, up);
		sky.setTexture(Skybox.Face.Down, down);
		
		sky.preloadTextures();
		sky.updateRenderState();	
	}
	
	
	/**
	 * Szenenknoten des Wuerfels
	 * @return
	 */
	public Node getCubeNode(){
		return (Node)sky;
	}
	
	
	/**
	 * Methode, die Objekte erzeugt und einen Szeneknoten zurueck gibt
	 */
	public Node getObject(){
		Node objects = new Node("Objekte");
		// TO DO...
		
		return objects;
	}
	
}
