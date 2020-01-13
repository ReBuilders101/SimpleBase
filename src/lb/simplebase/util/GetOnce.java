package lb.simplebase.util;

import java.util.function.Supplier;

public final class GetOnce<T> implements Supplier<T> {
	private T trans;
	private Supplier<T> transSup;
	
	public GetOnce(T trans) {
		this.trans = trans;
	}
	
	public GetOnce(Supplier<T> transSup) {
		this.transSup = transSup;
	}
	
	@Override
	public T get() {
		if(trans == null) {
			trans = transSup.get();
		}
		return trans;
	}
}