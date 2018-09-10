package lb.simplebase.math.expression.render;

import java.awt.Dimension;
import java.awt.Graphics2D;

import lb.simplebase.math.expression.IntegerElement;

public class RenderNumber extends RenderSymbol<IntegerElement> {
	
	private RenderNumber(String mode, boolean arrow) {
		super("IntegerNumber-" + mode, 1);
	}

	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(Graphics2D g, IntegerElement element) {
		String digits = element.toString();
	}
	
	public static final RenderNumber INT_RENDER_NORMAL = new RenderNumber("Normal", false);
	public static final RenderNumber INT_RENDER_ARROW = new RenderNumber("Arrow", true);
	
}
