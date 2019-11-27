package lb.simplebase.javacore.scene;

import java.awt.geom.AffineTransform;

import lb.simplebase.javacore.Framework;
import lb.simplebase.linalg.Matrix2D;

public class MatrixTransition {

	private TransitionTask m00;
	private TransitionTask m01;
	private TransitionTask m10;
	private TransitionTask m11;
	private Matrix2D last;
	private boolean done;
	
	public MatrixTransition(Matrix2D start) {
		last = start;
		done = true;
	}
	
	public void startTransitionTo(Matrix2D mat, long milliseconds) {
		m00 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds), last.getElementA(), mat.getElementA(), 1);
		m01 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds), last.getElementB(), mat.getElementB(), 1);
		m10 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds), last.getElementC(), mat.getElementC(), 1);
		m11 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds), last.getElementD(), mat.getElementD(), 1);
		last = mat;
		done = false;
	}
	
	public void cancelTransition() {
		done = true;
	}
	
	public void update() {
		if(done) return;
		m00.getNextValue();
		m01.getNextValue();
		m10.getNextValue();
		m11.getNextValue();
	}
	
	public Matrix2D getMatrix() {
		if(done) return last;
		return Matrix2D.of(m00.getCurrentValue(), m01.getCurrentValue(), m10.getCurrentValue(), m11.getCurrentValue());
	}
	
	public AffineTransform getTransform() {
		if(done) return last.getAffineTransform();
		return new AffineTransform(m00.getCurrentValue(), m10.getCurrentValue(), m01.getCurrentValue(), m11.getCurrentValue(), 0, 0);
	}
	
	public boolean isDone() {
		return done;
	}
	
}
