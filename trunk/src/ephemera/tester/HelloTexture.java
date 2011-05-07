package ephemera.tester;



import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;


public class HelloTexture extends SimpleGame{

	private TextureState textureState;
	public static void main(String[] args) {
		new HelloTexture().start();
	}
	@Override
	protected void simpleInitGame() {
		// TODO Auto-generated method stub
		TriMesh box = new Box("b",new Vector3f(0,0,0),new Vector3f(1,1,1));
		createTextureState();
		init(box);
	}
	
	 private void createTextureState() {
	        textureState = display.getRenderer().createTextureState();
	        textureState.setEnabled(true);
	        Texture t1 = TextureManager.loadTexture(
	                HelloTexture.class.getClassLoader().getResource("ephemera/ObjTextures/concrete.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
	                Texture.MagnificationFilter.Bilinear);
	        textureState.setTexture(t1);
	 }
	 private void init( TriMesh spatial ) {
	        BlendState alphaState = display.getRenderer().createBlendState();
	        alphaState.setEnabled( true );
	        alphaState.setBlendEnabled( true );
	        alphaState.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
	        alphaState.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );

	        rootNode.attachChild( spatial );
	        spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
	        spatial.setRenderState( alphaState );

	        MaterialState material = display.getRenderer().createMaterialState();
	        material.setShininess( 128 );
	        ColorRGBA color = new ColorRGBA( 0.7f, 0.7f, 0.7f, 1f );
	        material.setDiffuse( color );
	        material.setAmbient( color.mult( new ColorRGBA( 0.1f, 0.1f, 0.1f, 1 ) ) );
	        spatial.setRenderState( material );

	        spatial.setRenderState( textureState );
//	        spatial.setRenderState( display.getRenderer().createWireframeState() );
	    }


}
