package lb.simplebase.math.expression.render;

import java.awt.Dimension;
import java.awt.Graphics2D;
import lb.simplebase.math.expression.NumberElement;

public class RenderNumber extends RenderSymbol<NumberElement>{

	private RenderNumber() {
		super("Number", 1);
	}

	@Override
	public Dimension getSize(Graphics2D g, NumberElement element) {
		//Something
		return null;
	}

	@Override
	public void draw(Graphics2D g, NumberElement element) {
		
	}

	public static final RenderNumber NUMBER_RENDER = new RenderNumber();
	
}
