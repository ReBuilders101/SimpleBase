package lb.simplebase.function;

import java.awt.Graphics2D;

@FunctionalInterface
public interface RenderDelegate extends TriConsumer<Graphics2D, Integer, Integer>{

}
