package lb.simplebase.scene;

import java.awt.Graphics2D;

import javax.swing.JComponent;

public abstract class Scene {

	public abstract void tick();
	public abstract void draw(Graphics2D g);
	public abstract String getName();
	public abstract JComponent getOptions();
	public abstract void enable();
	public abstract void disable();
	
}
