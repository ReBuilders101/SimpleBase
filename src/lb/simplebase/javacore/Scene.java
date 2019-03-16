package lb.simplebase.javacore;

import java.awt.Dimension;
import java.awt.Graphics2D;

public abstract class Scene {

	protected Scene(String uniqueName) {
		this.name = uniqueName;
	}
	
	private String name;
	private Scene nextScene;
	private Scene previousScene;
	
	public abstract void update(long tick);
	
	public abstract void draw(Graphics2D g2d, Dimension drawSize);
	
	public String getName() {
		return name;
	}
	
	public Scene getNextScene() {
		return nextScene;
	}
	
	public Scene getPreviousScene() {
		return previousScene;
	}
	
	public void setNextScene(Scene newNextScene) {
		nextScene = newNextScene;
	}
	
	public void setPreviousScene(Scene newPreviousScene) {
		previousScene = newPreviousScene;
	}
	
	public final boolean hasNextScene() {
		return getNextScene() != null;
	}
	
	public final boolean hasPreviousScene() {
		return getPreviousScene() != null;
	}
	
}
