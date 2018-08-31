package lb.simplebase.scene;

import java.awt.Graphics2D;

/**
 * @version 1.1
 * @author LB
 * This scene acts as as a container for other scenes. The subscenes can be resized and positioned in the frame.
 * All subscenes will receive calls to their {@link #tick()} and {@link #draw(Graphics2D)} method.
 * Note that there are special methods for ticking and drawing the container itself.
 */
public abstract class SceneContainer extends Scene{
	
	private Scene[] subscenes;
	
	/**
	 * Get a list of all subscenes countained in this scene
	 * The array fields can be chabged to swap out scenes, however this is not advised and is not guaranteed to work with all implementations.
	 * Implementations may override this method so a copy of the array is returned.
	 * @return The subscene array.
	 */
	public Scene[] getSubscenes(){
		return subscenes;
	}
	
	/**
	 * Searches for a subscene by it's name
	 * @param name The name of the scene
	 * @return The scene or null if no scene was found
	 */
	public Scene getSubscene(String name){
		for(Scene s : subscenes){
			if(s.getName().equals(name)) return s;
		}
		return null;
	}
	
	/**
	 * The draw method for the container background. This method is called before the subscenes are drawn.
	 * @param g The graphics object to draw into 
	 * @param width The width of the area to draw into
	 * @param height The height of the area to draw into
	 */
	public abstract void drawContainer(Graphics2D g, int width, int height);
	
	/**
	 * The draw method for the container foreground. This method is called after the subscenes are drawn.
	 * @param g The graphics object to draw into 
	 * @param width The width of the area to draw into
	 * @param height The height of the area to draw into
	 */
	public abstract void drawContainerForeground(Graphics2D g, int width, int height);
	
	/**
	 * The tick method for the container. As containers rarely do logic on their own, this method will be often empty
	 * This method is called before the tick() - methods of the subscenes
	 */
	public abstract void tickContainer();
	
	/**
	 * An implementation of the draw() - method that calls {@link #drawContainer(Graphics2D)}, then draws all subscenes and then
	 * calls {@link #drawContainerForeground(Graphics2D)}. This method should not be overridden, instead the methods mentioned above may be used.
	 * If it is overridden, the new implementation must make sure that the subscenes are drawn properly.
	 * @param g The graphics object to draw into 
	 * @param width The width of the area to draw into
	 * @param height The height of the area to draw into
	 */
	@Override
	public void draw(Graphics2D g, int width, int height){
		drawContainer(g, width, height);
		for(Scene s : subscenes){
			s.draw(g, width, height);
		}
		drawContainerForeground(g, width, height);
	}
	
	/**
	 * An implementation of the tick() - method that calls {@link #tickContainer(Graphics2D)} and then calls tick() on all subscenes.
	 *  This method should not be overridden, instead the method mentioned above may be used.
	 * If it is overridden, the new implementation must make sure that the subscenes are ticked properly.
	 */
	@Override
	public void tick(){
		tickContainer();
		for(Scene s : subscenes){
			s.tick();
		}
	}
}
