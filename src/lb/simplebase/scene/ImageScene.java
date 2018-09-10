package lb.simplebase.scene;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import lb.simplebase.core.Utils;

public class ImageScene extends Scene implements MouseMotionListener, MouseWheelListener{

	public static final BufferedImage emptyImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
	static {
		Graphics2D g = emptyImage.createGraphics();
		g.setPaint(Color.BLACK);
		g.fillRect(0, 0, 64, 64);
	}
	
	private String name;
	private BufferedImage image;
	private boolean zoomable;
	private ImageTiling tiling;
	
	private int lastWidth;
	private int lastHeight;
	private BufferedImage lastScaled;
	
	
	public ImageScene(String name, BufferedImage image, boolean zoomable, ImageTiling tiling) {
		this.name = name == null ? "NULL" : name;
		this.image = image == null ? emptyImage : image;
		this.zoomable = zoomable;
		this.tiling = tiling == null ? ImageTiling.ORIGINAL : tiling;
	}

	public void setImage(BufferedImage image) {
		this.image = image == null ? emptyImage : image;
		this.lastScaled = null;
	}
	
	@Override
	public void tick() {}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		if(lastScaled == null || width != lastWidth || height != lastHeight) { //Reapply tiling
			lastWidth = width;
			lastHeight = height;
			applyTiling();
		}
		
		int originX = (width - lastScaled.getWidth()) / 2;
		int originY = (height - lastScaled.getHeight()) / 2;
		g.drawImage(lastScaled, originX, originY, null);
	}

	private void applyTiling() {
		switch (tiling) {
		case FILL:
			//this is just a copy of below but the if condition is reversed
			double whImage = (double) image.getWidth() / (double) image.getHeight();
			double whFrame = (double) lastWidth / (double) lastHeight;
			int newWidth, newHeight;
			if(whImage < whFrame) { //touch u/d
				newWidth = lastWidth;
				double hFactor = (double) lastWidth / (double) image.getWidth();
				newHeight = (int) (image.getHeight() * hFactor);
			} else { //touch r/l
				newWidth = lastHeight;
				double wFactor = (double) lastHeight / (double) image.getHeight();
				newHeight = (int) (image.getHeight() * wFactor);
			}
			lastScaled = Utils.scaleImage(image, newWidth, newHeight, 0);
			break;
		case FIT:
			//Calc w/h:
			double whImage0 = (double) image.getWidth() / (double) image.getHeight();
			double whFrame0 = (double) lastWidth / (double) lastHeight;
			int newWidth0, newHeight0;
			if(whImage0 > whFrame0) { //touch r/l
				newWidth0 = lastWidth;
				double hFactor0 = (double) lastWidth / (double) image.getWidth();
				newHeight0 = (int) (image.getHeight() * hFactor0);
			} else { //touch u/d
				newWidth0 = lastHeight;
				double wFactor0 = (double) lastHeight / (double) image.getHeight();
				newHeight0 = (int) (image.getHeight() * wFactor0);
			}
			lastScaled = Utils.scaleImage(image, newWidth0, newHeight0, 0);
			break;
		case ORIGINAL:
			lastScaled = image;
			break;
		case STRETCH:
			lastScaled = Utils.scaleImage(image, lastWidth, lastHeight, 0);
			break;
		default:
			lastScaled = emptyImage;
			break;
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public JComponent getOptions() {
		// TODO Auto-generated method stub
		//Reset zoom, text box for zoom factor, reset drag.
		
		return null;
	}

	@Override
	public void enable() {}

	@Override
	public void disable() {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//Zoom
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//Drag
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	/**
	 * @version 1.0
	 * @author LB
	 * Determines how the image is tiled or stretched in the {@link ImageScene}.
	 * STRETCH - The image is stretched to the size of the Scene
	 * FILL - The image is not stretched, but it will be scaled so there are no blank spots on the screen
	 * FIT - The image is scaled so it is completely visible
	 * ORIGINAL - The image is drawn at its original size and centered 
	 */
	public static enum ImageTiling {
		STRETCH, FILL, FIT, ORIGINAL;
	}
	
}
