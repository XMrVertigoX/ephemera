/**
 * Klasse Regeln 
 * Diese Klasse enthält die Parameter nach denen sich Fliegen und Jäger innerhalb der Welt bewegen sollen 
 */

package ephemera.model;

public class Regeln{
	private float		fluggeschwindigkeit;
	private float		randomWalk_weight;
	private float 		lifeTime;
	private float 		randomWalk;
	private float		leittierSpeed;
	private float 		maxspeed;
	private float		maxforce;
	private float		coh_weight;
	private float		ali_weight;
	private float		sep_weight;
	private float		follow_weight;
	private float		desiredSeparation;
	private float		neighborDistance;

	/**
	 * Konstruktor
	 * Erstelle ein "Standard" Model der Fliege mit Default-Einstellungen
	 */
	public Regeln(){
		randomWalk_weight	=	0f;
		lifeTime			=	30;
		fluggeschwindigkeit	=	2.71f;
		leittierSpeed		=	.01f;
		maxspeed			=	1f;
		maxforce			=	1f;
		coh_weight			=	0.3f;
		ali_weight			=	0.2f;
		sep_weight			=	0.4f;
		follow_weight		=	0.1f;
		desiredSeparation	=	40.0f;
		neighborDistance	=	20.0f;			
	}

	/**
	 * Konstruktor
	 * @param maxspeed
	 * @param maxforce
	 * @param coh_weight
	 * @param ali_weight
	 * @param sep_weight
	 * @param desiredSeparation
	 * @param neighborDistance
	 */
	public Regeln(float maxspeed, float maxforce, float coh_weight, float ali_weight, float sep_weight, float desiredSeparation, float neighborDistance){
		
		this.maxspeed = maxspeed;
		this.maxforce = maxforce;
		this.coh_weight = coh_weight;
		this.ali_weight = ali_weight;
		this.sep_weight = sep_weight;
		this.desiredSeparation = desiredSeparation;
		this.neighborDistance = neighborDistance;
	}

	
	/**
	 * Getter und Setter
	 * @return
	 */


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
	public float getFluggeschwindigkeit() {
		return fluggeschwindigkeit;
	}

	public void setFluggeschwindigkeit(float fluggeschwindigkeit) {
		this.fluggeschwindigkeit = fluggeschwindigkeit;
	}
	public float getLifeTime() {
		return lifeTime;
	}
	public void setLifeTime(float lifeTime) {
		this.lifeTime = lifeTime;
	}
	public float getRandomWalk_weight() {
		return randomWalk_weight;
	}

	public void setRandomWalk_weight(float randomWalk_weight) {
		this.randomWalk_weight = randomWalk_weight;
	}

}
