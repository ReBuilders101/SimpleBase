package lb.simplebase.scene;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import lb.simplebase.core.Utils;

/**
 * @version 1.1
 * @author LB
 * This scene acts as as a container for other scenes. The subscenes can be resized and positioned in the frame.
 * All subscenes will receive calls to their {@link #tick()} and {@link #draw(Graphics2D)} method.
 * Note that there are special methods for ticking and drawing the container itself.
 */
public abstract class SceneContainer extends Scene  implements MouseInputListener, KeyListener, MouseWheelListener{
	
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
	 * Implementation of the draw() - Method that draws every scene at full size.
	 * @param g The graphics object to draw into 
	 * @param width The width of the area to draw into
	 * @param height The height of the area to draw into
	 */
	@Override
	public void draw(Graphics2D g, int width, int height){
		for(Scene s : subscenes){
			s.draw(g, width, height);
		}
	}
	
	/**
	 * Implementation of the tick() - Method that ticks evers subscene.
	 */
	@Override
	public void tick(){
		for(Scene s : subscenes){
			s.tick();
		}
	}

	/**
	 * Implementation of the getOptions() - Methods. All Subscene Options are grouped into a brodered and named {@link JPanel}.
	 */
	@Override
	public JComponent getOptions() {
		JPanel panel = Utils.getGroupBox(getName());
		for(Scene s : subscenes) {
			panel.add(s.getOptions());
		}
		return panel;
	}

	@Override
	public void enable() {
		for(Scene s : subscenes) {
			s.enable();
		}
	}

	@Override
	public void disable() {
		for(Scene s : subscenes) {
			s.disable();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseListener) ((MouseListener) activeScene).mouseClicked(arg0);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseListener) ((MouseListener) activeScene).mouseEntered(arg0);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseListener) ((MouseListener) activeScene).mouseExited(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseListener) ((MouseListener) activeScene).mousePressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseListener) ((MouseListener) activeScene).mouseReleased(arg0);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseMotionListener) ((MouseMotionListener) activeScene).mouseDragged(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseMotionListener) ((MouseMotionListener) activeScene).mouseMoved(arg0);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof KeyListener) ((KeyListener) activeScene).keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof KeyListener) ((KeyListener) activeScene).keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof KeyListener) ((KeyListener) activeScene).keyTyped(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		for(Scene activeScene : subscenes)
			if(activeScene instanceof MouseWheelListener) ((MouseWheelListener) activeScene).mouseWheelMoved(e);
	}
}
