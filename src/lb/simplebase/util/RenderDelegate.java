package lb.simplebase.util;

import java.awt.Graphics2D;

@FunctionalInterface
public interface RenderDelegate extends TriConsumer<Graphics2D, Integer, Integer>{

	@Override
	public default void accept(Graphics2D t, Integer s, Integer u) {
		draw(t, s, u);
	}

	public void draw(Graphics2D t, int s, int u);
	
}
