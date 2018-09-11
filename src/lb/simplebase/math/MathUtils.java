package lb.simplebase.math;

import java.math.BigDecimal;

public final class MathUtils {
	
	private MathUtils() {}
	
	/**
	 * Argument order: y,x
	 * result in radians
	 */
	public static double atan2(BigDecimal y, BigDecimal x) {
		if(x.signum() == 1) { //x > 0
//			return 
		} else if(x.signum() == -1 && !(y.signum() == -1)) { //x < 0 && y >= 0
			
		} else if(x.signum() == -1 && y.signum() == -1) { //x < 0 && y < 0
			
		} else if(x.equals(BigDecimal.ZERO) && y.signum() == 1) { //x == 0 && y > 0
			return Math.PI / 2D;
		} else if(x.equals(BigDecimal.ZERO) && y.signum() == -1) { //x == 0 && y < 0
			return -Math.PI / 2D;
		} else if(x.equals(BigDecimal.ZERO) && y.equals(BigDecimal.ZERO)) { //x == 0 && y > 0
			return Double.NaN;
		}
		return 0;
	}
	
	//public static double
	
}
