package lb.simplebase.javacore;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import javax.swing.JComponent;

public abstract class Scene {

	protected Scene(String uniqueName) {
		this(uniqueName, null);
	}
	
	protected Scene(String uniqueName, String description) {
		this.name = uniqueName;
		this.description = description;
	}
	
	private String name;
	private String description;
	private Scene nextScene;
	private Scene previousScene;
	private boolean active;
	private JComponent options;
	
	protected final boolean isActive() {
		return active;
	}
	
	protected final void setActive(boolean active) {
		this.active = active;
		if(active) {
			onActivate();
		} else {
			onDeactivate();
		}
	}
	
	public abstract void onActivate();
	
	public abstract void onDeactivate();
	
	public abstract void update(long tick);
	
	public abstract void draw(Graphics2D g2d, int width, int height);
	
	protected abstract JComponent createOptions();
	
	public final JComponent getOptions() {
		if(options == null) {
			options = createOptions();
			return options;
		} else {
			return options;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
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
	
	public final boolean hasDescription() {
		return getDescription() != null;
	}
	
	public final Optional<Scene> getNextSceneOptional() {
		return Optional.ofNullable(getNextScene());
	}
	
	public final Optional<Scene> getPreviousSceneOptional() {
		return Optional.ofNullable(getPreviousScene());
	}
	
	public final Optional<String> getDescriptionOptional() {
		return Optional.ofNullable(getDescription());
	}
	
	public static Scene createEmpty(String title) {
		return new EmptyScene(title);
	}
	
	private static class EmptyScene extends Scene {
		
		private EmptyScene(String uniqueName) {
			super(uniqueName, "A default scene implementation that displays a white background");
		}

		@Override
		public void update(long tick) {}

		@Override
		public void draw(Graphics2D g2d, int width, int height) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, width, height);
		}

		@Override
		public void onActivate() {}

		@Override
		public void onDeactivate() {}

		@Override
		protected JComponent createOptions() {
			return null;
		}
		
	}
	
}
