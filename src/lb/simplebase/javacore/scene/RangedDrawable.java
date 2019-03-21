package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;

public interface RangedDrawable {
	
	public void draw(Graphics2D g2d, int width, int height, double minXunits, double minYunits, double maxXunits, double maxYunits);
	
}
