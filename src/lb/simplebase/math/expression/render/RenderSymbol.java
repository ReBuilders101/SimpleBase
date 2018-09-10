package lb.simplebase.math.expression.render;

import java.awt.Dimension;
import java.awt.Graphics2D;

import lb.simplebase.math.expression.ExpressionElement;

public abstract class RenderSymbol<E extends ExpressionElement> {
	
	private String name;
	private int varCount;
	
	protected RenderSymbol(String name, int varCount) {
		this.name = name;
		this.varCount = varCount;
	}
	
	public int getVariableCount() {
		return varCount;
	}
	
	public  String getName() {
		return name;
	}
	
	public abstract Dimension getSize();
	
	public abstract void draw(Graphics2D g, E element);
}
