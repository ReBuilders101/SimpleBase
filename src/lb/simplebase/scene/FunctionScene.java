package lb.simplebase.scene;

import java.awt.Graphics2D;
import javax.swing.JComponent;

import lb.simplebase.function.RenderDelegate;

/**
 * @version 1.0
 * @author LB
 * An implementation of the {@link Scene} - class that delegates drawing and ticking to functional interfaces. This Scene 
 * does not have any options
 */
public class FunctionScene extends Scene{

	private Runnable tick;
	private RenderDelegate graphics;
	private String name;
	
	public FunctionScene(String name, Runnable tick, RenderDelegate graphics) {
		this.name = name == null ? "NULL" : name;
		this.tick = tick == null ? () -> {} : tick;
		this.graphics = graphics == null ? (t,s,u) -> {} : graphics;
	}
	
	@Override
	public void tick() {
		tick.run();
	}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		graphics.accept(g, width, height);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public JComponent getOptions() {
		return null;
	}

	@Override
	public void enable() {
		
	}

	@Override
	public void disable() {
		
	}

}
