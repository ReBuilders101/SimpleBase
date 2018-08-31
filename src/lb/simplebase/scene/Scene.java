package lb.simplebase.scene;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import lb.simplebase.core.Framework;

/**
 * @version 1.2
 * @author LB
 * A scene represents a certain state of the application, for example in a game there could be a scene for the menu,
 * for the actual game and for an intro animation. Scenes receive calls to their {@link #tick()} and {@link #draw(Graphics2D)}
 * methods by the {@link Framework} instance as long as they are active. Scenes may be activated and deactivated at any time,
 *  and can be re-activated
 */
public abstract class Scene {

	/**
	 * This method is called by the {@link Framework} instance at a constant rate (the TPS). All logic,
	 * like moving game objects, should happen here. The code should be as fast as possible.
	 * This method should not be called manually. 
	 */
	public abstract void tick();
	/**
	 * This method is called by the {@link Framework} instance at a constant rate (the FPS). All graphics
	 * and rendering, should happen here. The code should be as fast as possible.
	 * This method should not be called manually. 
	 */
	public abstract void draw(Graphics2D g);
	/**
	 * A unique name for every instance. This name is used to refer to it when changing the active scene
	 * @return The scene name.
	 */
	public abstract String getName();
	/**
	 * A {@link JComponent} that may be a single button or slider, or a container with several controls. These controls
	 * will be displayed in the options frame if it is enabled. Event handling must be done completely by the {@link Scene}.
	 * The method may return null if the scene has no options.
	 * @return The component containing all options
	 */
	public abstract JComponent getOptions();
	/**
	 * This method is called when the scene is enabled. After this method was called, this scene will start to receive calls to the {@link #tick()}
	 * and {@link #draw(Graphics2D)} methods. Note that there is no way to prevent the activation.
	 */
	public abstract void enable();
	/**
	 * This method is called when the scene is disabled. After this method was called, this scene will no longer receive calls to the {@link #tick()}
	 * and {@link #draw(Graphics2D)} methods. Note that there is no way to prevent the deactivation. A scene can be activated again by the framework;
	 * then the {@link #enable()} method will be called again.
	 */
	public abstract void disable();
	
}
