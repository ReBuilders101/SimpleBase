package lb.simplebase.javacore.scene;

import java.awt.Graphics2D;

public interface RangedDrawable {
	
	public void draw(Graphics2D g2d, int width, int height, double minXunits, double minYunits, double maxXunits, double maxYunits);
	
	public static RangedDrawable combine(RangedDrawable top, RangedDrawable bottom) {
		return (g2d, width, height, minXunits, minYunits, maxXunits, maxYunits) -> {
			bottom.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
			top.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
		};
	}
	
	public static RangedDrawable combine(RangedDrawable top, RangedDrawable middle, RangedDrawable bottom) {
		return (g2d, width, height, minXunits, minYunits, maxXunits, maxYunits) -> {
			bottom.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
			middle.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
			top.draw(g2d, width, height, minXunits, minYunits, maxXunits, maxYunits);
		};
	}
	
}
