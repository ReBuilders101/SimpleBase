package lb.simplebase.core;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * @version 1.0
 * @author LB
 * This is the swing component used as a canvas for drawing. Note that this class should only be used in certain situations, because
 * all instances will always show the currently active scene. A new instance will NOT be automatically updated (this can be done by
 * calling {@link #repaint()} on the instance). Also, every instance has to be rendered seperately, which may slow down the program.
 */
public class DrawPanel extends JPanel{

	private static final long serialVersionUID = 1637432517370654911L;

	/**
	 * Creates a new instance with double buffering active
	 */
	public DrawPanel(){
		super(true);
	}
	
	/**
	 * An empty implementation. No components may be added to this panel
	 */
	@Override
	protected void addImpl(Component arg0, Object arg1, int arg2) {
		//Do nothing at all
	}
	
	/**
	 * Implementation that calls the {@link Framework#paintCallback(Graphics)} method for drawing a scene to this panel.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Framework.getFramework().paintCallback(g);
	}
	
}
