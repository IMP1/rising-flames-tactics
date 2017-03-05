package cls.game;

import run.Cache;
import cls.map.Hexagon;

public class Unit {
	
	private Actor actor;
	private int i, j;
	private int health;
	private String faceGraphic;
	
	public Unit(Actor actor, int i, int j, String faceGraphic) {
		this.actor = actor;
		this.i = i;
		this.j = j;
		this.faceGraphic = faceGraphic;
		health = actor.getMaxHealth();
	}
	
	public Unit(Actor actor, int i, int j) {
		this(actor, i, j, actor.getName());
	}
	
	public int getHealth() {
		return health;
	}
	
	public void damage(int damage) {
		health -= damage;
		if (health <= 0) {
			health = 0;
			// die();
		}
	}
	
	public void gainExperience(int exp) {
		this.actor.experience += exp;
		this.actor.levelsToGain += (int)this.actor.experience / 100;
	}
	
	/**
	 * Draws the over-world sprite for the character.
	 * @param offsetX an offset in the horizontal dimension.
	 * @param offsetY an offset in the vertical dimension.
	 */
	public void draw(int offsetX, int offsetY) {
		int[] coords = Hexagon.getPixel(i, j);
		int x = coords[0];
		int y = coords[1];
		jog.Graphics.circle(true, x + offsetX, y + offsetY, 4);
	}
	
	/**
	 * Draws the face of the character.
	 * @param x the coordinate for the centre (horizontally) of the face. 
	 * @param y the coordinate for the bottom of the face.
	 */
	public void drawFace(int x, int y) {
		jog.Image img = Cache.actorFace(faceGraphic);
		jog.Graphics.draw(img, x - img.getWidth() / 2, y - img.getHeight());
	}
	
	public boolean isAt(int i, int j) {
		return i == this.i && j == this.j;
	}
	
	public Actor getActor() {
		return actor;
	}

}
