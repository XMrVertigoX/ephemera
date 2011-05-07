/**
 * WorldController 2011 by Semjon Mooraj
 * diese Klasse stellt die Welt dar in der sich die Fliegen bewegen.
 * Die Welt ist standartmäig ein Quadratischer Würfel der Kantenlänge 2000 
 */

package ephemera.controller;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.shape.Box;
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
		Texture north = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear); // custom/1.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture east = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture south = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture west = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture up = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture down = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		
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
	public Node generateRandomObjects(int N){
		Node obj = new Node("Objekte");
		for (int i=0;i<N;i++){
			float x = FastMath.nextRandomInt(1, 100);
			float y = FastMath.nextRandomInt(1, 100);
			float z = FastMath.nextRandomInt(1, 100);
			
			Box box = new Box("Box_"+i,new Vector3f(0,0,0),new Vector3f(x,y,z));
			box.setModelBound(new BoundingBox());
			x = FastMath.nextRandomInt(-1000, 1000);
			y = FastMath.nextRandomInt(-1000, 1000);
			z = FastMath.nextRandomInt(-1000, 1000);
			box.setLocalTranslation(new Vector3f(x,y,z));
			obj.attachChild(box);
		}
		return obj;
	}	
}
