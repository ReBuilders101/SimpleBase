package lb.simplebase.javacore;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Scene {

	protected Scene(String uniqueName) {
		this.name = uniqueName;
	}
	
	private String name;
	private Scene nextScene;
	private Scene previousScene;
	
	public abstract void update(long tick);
	
	public abstract void draw(Graphics2D g2d, int width, int height);
	
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
	
	public static Scene createEmpty(String title) {
		return new EmptyScene(title);
	}
	
	private static class EmptyScene extends Scene {
		
		private EmptyScene(String uniqueName) {
			super(uniqueName);
		}

		@Override
		public void update(long tick) {}

		@Override
		public void draw(Graphics2D g2d, int width, int height) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, width, height);
		}
		
	}
	
}
