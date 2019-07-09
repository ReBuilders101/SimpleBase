package lb.simplebase.javacore.scene;

import java.awt.geom.AffineTransform;

import lb.simplebase.javacore.Framework;
import lb.simplebase.linalg.Matrix2D;
import lb.simplebase.linalg.Vector2D;

public class PolarMatrixTransition {

	private TransitionTask r0;
	private TransitionTask t0;
	private TransitionTask r1;
	private TransitionTask t1;
	private Matrix2D last;
	private boolean done;
	
	public PolarMatrixTransition(Matrix2D start) {
		last = start;
		done = true;
	}
	
	public void startTransitionTo(Matrix2D mat, long milliseconds) {
		
		
		r0 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds),
				last.getFirstColumn().getLength(), mat.getFirstColumn().getLength(), 1);
		t0 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds),
				last.getFirstColumn().getPolarAngle(), mat.getFirstColumn().getPolarAngle(), 1);
		r1 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds),
				last.getSecondColumn().getLength(), mat.getSecondColumn().getLength(), 1);
		t1 = new TransitionTask(Transition.EASE_IN_OUT_CUBIC, Framework.getTicks(milliseconds),
				last.getSecondColumn().getPolarAngle(), mat.getSecondColumn().getPolarAngle(), 1);
		
		if(last.isRightHanded() != mat.isRightHanded()) {
			Framework.displayInformation("Flip Matrix");
		} else {
			Framework.displayInformation("Normal Matrix");
		}
		
		last = mat;
		done = false;
	}
	
	public void cancelTransition() {
		done = true;
	}
	
	public void update() {
		if(done) return;
		r0.getNextValue();
		t0.getNextValue();
		r1.getNextValue();
		t1.getNextValue();
	}
	
	public Matrix2D getMatrix() {
		if(done) return last;
		return Matrix2D.ofCols(Vector2D.ofPolar(r0.getCurrentValue(), t0.getCurrentValue()), Vector2D.ofPolar(r1.getCurrentValue(), t0.getCurrentValue()));
	}
	
	public AffineTransform getTransform() {
		if(done) return last.getAffineTransform();
		return new AffineTransform(Math.cos(t0.getCurrentValue()) * r0.getCurrentValue(), Math.cos(t1.getCurrentValue()) * r1.getCurrentValue(),
				Math.sin(t0.getCurrentValue()) * r0.getCurrentValue(), Math.sin(t1.getCurrentValue()) * r1.getCurrentValue(), 0, 0);
	}
	
	public boolean isDone() {
		return done;
	}
	
}
