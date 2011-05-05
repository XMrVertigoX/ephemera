package ephemera.controller;

import java.io.IOException;
import java.net.URL;

import ephemera.model.Ephemera;

import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.util.CloneImportExport;
import com.model.md5.importer.MD5Importer;


public class ModelController {

	private CloneImportExport clone = new CloneImportExport();
	private MD5Importer importer;
	private Node n;
	
	public ModelController(){
		importer = new MD5Importer();
		n = this.loadModel();
		clone.saveClone(n);
	}
	public Node getNode(){
		Node raus = (Node)clone.loadClone();
		return raus;
	}
	
	protected MD5Node loadModel() {
		URL bodyMesh = Ephemera.class.getClassLoader().getResource("test/model/md5/data/bob.md5mesh");
		URL bodyAnim = Ephemera.class.getClassLoader().getResource("test/model/md5/data/bob.md5anim");
		try {
			this.importer.load(bodyMesh, "ModelNode", bodyAnim, "BodyAnimation", Controller.RT_WRAP);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		MD5Node body = (MD5Node) this.importer.getMD5Node();
		return body;
	}
}
