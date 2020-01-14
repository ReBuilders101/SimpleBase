package lb.simplebase.javacore.scene;

import lb.simplebase.javacore.Utils;
import lb.simplebase.javacore.transitions.ProgressUpdater;
import lb.simplebase.javacore.transitions.Transition;
import lb.simplebase.javacore.transitions.TransitionBehavior;
import lb.simplebase.javacore.transitions.TransitionTask;
import lb.simplebase.linalg.Matrix2D;

public class MatrixTransition extends TransitionTask<Matrix2D> {

	private Matrix2D start;
	private Matrix2D end;
	
	public MatrixTransition(Transition transition, ProgressUpdater update, Matrix2D startMatrix) {
		super(transition, update, TransitionBehavior.ONCE);
		setActive(false);
		this.start = startMatrix;
		this.end = startMatrix;
	}

	public void startTransitionTo(Matrix2D endMatirx) {
		reset();
		start = end; //Old animation end is the new start
		end = endMatirx;
		setActive(true);
	}
	
	@Override
	public Matrix2D getValue() {
		return Matrix2D.of(
				Utils.scale(completionRatio, start.getElementTopLeft(), end.getElementTopLeft(), 0, 1),
				Utils.scale(completionRatio, start.getElementTopRight(), end.getElementTopRight(), 0, 1),
				Utils.scale(completionRatio, start.getElementBottomLeft(), end.getElementBottomLeft(), 0, 1),
				Utils.scale(completionRatio, start.getElementBottomRight(), end.getElementBottomRight(), 0, 1));
	}

}
