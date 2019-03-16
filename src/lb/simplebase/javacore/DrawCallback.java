package lb.simplebase.javacore;

import java.awt.Dimension;
import java.awt.Graphics2D;
import lb.simplebase.function.TriConsumer;

@FunctionalInterface
public interface DrawCallback extends TriConsumer<Graphics2D, Integer, Integer>{
	
	public void draw(Graphics2D graphics, int width, int height);
	
	@Override
	public default void accept(Graphics2D graphics, Integer width, Integer height) {
		draw(graphics, width, height);
	}
	
	public default void draw(Graphics2D graphics, Dimension size) {
		draw(graphics, size.width, size.height);
	}
	
}
