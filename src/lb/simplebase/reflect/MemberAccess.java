package lb.simplebase.reflect;

import java.util.Optional;

public abstract class MemberAccess implements Cloneable {

	private Exception exception = null;
	
	protected final boolean isStatic;
	protected final Object instance;
	
	protected MemberAccess(final boolean isStatic, final Object instance) {
		this.isStatic = isStatic;
		this.instance = instance;
	}
	
	@Override
	public abstract MemberAccess clone();
	
	public abstract MemberAccess bindToInstance(final Object instance);
	
	protected void checkBind() {
		if(isBound())   throw new UnsupportedOperationException("A instance is already bound to this FieldAccess");
		if(isStatic()) throw new UnsupportedOperationException("Cannot bind an instance to a static field");
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public boolean isBound() {
		return instance != null;
	}
	
	public Exception getLastException() {
		return exception;
	}
	
	public Optional<Exception> getLastExceptionOptional() {
		return Optional.ofNullable(exception);
	}
	
	public boolean hasLastException() {
		return exception != null;
	}
	
	protected void setException(Exception e) {
		this.exception = e;
	}
	
	public void rethrowException() throws Exception {
		throw exception;
	}
	
}
