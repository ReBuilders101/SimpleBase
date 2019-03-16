package lb.simplebase.javacore;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface DrawCallback extends BiConsumer<Graphics2D, Dimension>{
	
	public void draw(Graphics2D graphics, Dimension size);
	
	@Override
	public default void accept(Graphics2D graphics, Dimension size) {
		draw(graphics, size);
	}
	
}
