package ephemera.model;

/**
 * Diese Klasse enthält die Parameter nach denen sich Fliegen und Jäger innerhalb der Welt bewegen sollen.
 * @author Semjon Mooraj
 */
public class Rules{
	private float speed;
	private float leittierSpeed;
	private float maxspeed;
	private float maxforce;
	private float coh_weight;
	private float ali_weight;
	private float sep_weight;
	private float follow_weight;
	private float desiredSeparation;
	private float neighborDistance;
	private int flyCount;

	public int getFlyCount() {
		return flyCount;
	}

	public void setFlyCount(int flyCount) {
		this.flyCount = flyCount;
	}

	/**
	 * Konstruktor
	 * Erstelle ein "Standard" Model der Fliege mit Default-Einstellungen
	 */
	public Rules(){
		maxspeed = 10f;
		speed = 0.5f;
		leittierSpeed = 0.015f;
		coh_weight = 0.2f;
		ali_weight = 0.0f;
		sep_weight = 0.8f;
		follow_weight = 0.5f;
		desiredSeparation = 50.0f;
		neighborDistance = 50.0f;			
	}
	
	/**
	 * Setzt die Werte fuer die Regeln auf die Default-Werte.
	 */
	public void reset() {
		maxspeed = 10f;
		speed = 0.5f;
		leittierSpeed = 0.015f;
		coh_weight = 0.2f;
		ali_weight = 0.0f;
		sep_weight = 0.8f;
		follow_weight = 0.5f;
		desiredSeparation = 50.0f;
		neighborDistance = 50.0f;
	}

	public float getMaxspeed() {
		return maxspeed;
	}

	public void setMaxspeed(float maxspeed) {
		this.maxspeed = maxspeed;
	}

	public float getMaxforce() {
		return maxforce;
	}

	public void setMaxforce(float maxforce) {
		this.maxforce = maxforce;
	}

	public float getCoh_weight() {
		return coh_weight;
	}

	public void setCoh_weight(float coh_weight) {
		this.coh_weight = coh_weight;
	}

	public float getAli_weight() {
		return ali_weight;
	}

	public void setAli_weight(float ali_weight) {
		this.ali_weight = ali_weight;
	}

	public float getSep_weight() {
		return sep_weight;
	}

	public void setSep_weight(float sep_weight) {
		this.sep_weight = sep_weight;
	}

	public float getDesiredSeparation() {
		return desiredSeparation;
	}

	public void setDesiredSeparation(float desiredSeparation) {
		this.desiredSeparation = desiredSeparation;
	}

	public float getNeighborDistance() {
		return neighborDistance;
	}

	public void setNeighborDistance(float neighborDistance) {
		this.neighborDistance = neighborDistance;
	}
	
	public float getFollow_weight() {
		return follow_weight;
	}

	public void setFollow_weight(float follow_weight) {
		this.follow_weight = follow_weight;
	}
	
	public float getLeittierSpeed() {
		return leittierSpeed;
	}
	
	public void setLeittierSpeed(float leittierSpeed) {
		this.leittierSpeed = leittierSpeed;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}


}
