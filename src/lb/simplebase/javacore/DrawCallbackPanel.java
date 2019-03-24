package lb.simplebase.javacore;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.TimerTask;

import javax.swing.JPanel;

/**
 * An implementation of the {@link JPanel} class that easily lets you set your own code to draw into the component.
 * Does not accept children. 
 */
public class DrawCallbackPanel extends JPanel{
	private static final long serialVersionUID = 6461415152079980509L;

	public DrawCallbackPanel(DrawCallback drawCallback) {
		super(true);
		setBackground(Color.BLACK);
		callback = drawCallback;
	}
	
	private DrawCallback callback;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			callback.draw((Graphics2D) g, getWidth(), getHeight());
		} else {
			System.err.println("Panel Graphics object is not an instance of Graphics2D");
		}
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		System.err.println("Cannot add components to a DrawCallbackPanel");
	}
	
	public TimerTask createUpdateTask() {
		return new TimerTask() {
			
			@Override
			public void run() {
				DrawCallbackPanel.this.repaint();
			}
		};
	}

}
