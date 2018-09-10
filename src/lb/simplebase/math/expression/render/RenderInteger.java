package lb.simplebase.math.expression.render;

import java.awt.Dimension;
import java.awt.Graphics2D;

import lb.simplebase.math.expression.IntegerElement;

@Deprecated
public class RenderInteger extends RenderSymbol<IntegerElement> {
	
	private RenderInteger() {
		super("Integer-", 1);
	}
	
	@Override
	public Dimension getSize(Graphics2D g, IntegerElement element) {
		return null;
	}

	@Override
	public void draw(Graphics2D g, IntegerElement element) {
		
	}
	
	public static final RenderInteger INTEGER_RENDER = new RenderInteger();
	
}
