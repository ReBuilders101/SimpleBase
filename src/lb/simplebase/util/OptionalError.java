package lb.simplebase.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class OptionalError<T, E extends Throwable> implements Supplier<T>{
	
	public abstract boolean isValue();
	public abstract boolean isException();
	public Optional<T> getValueOptional() {
		return isValue() ? Optional.of(getValue()) : Optional.empty();
	}
	public Optional<E> getExceptionOptional() {
		return isException() ? Optional.of(getException()) : Optional.empty();
	}
	
	public abstract T getValue() throws NoSuchElementException;
	public abstract E getException() throws NoSuchElementException;
	
	public void ifValue(Consumer<T> task) {
		if(isValue()) task.accept(getValue());
	}
	public void ifException(Consumer<E> task) {
		if(isException()) task.accept(getException());
	}
	
	public abstract <R> OptionalError<R, E> mapValue(Function<T, R> mapper);
	public abstract <R> OptionalError<R, E> flatMapValue(Function<T, Optional<R>> mapper, Supplier<E> exception);
	public abstract <R, E2 extends Throwable> OptionalError<R, E2> flatMapValue(Function<T, Optional<R>> mapper, Supplier<E2> exception, Function<E, E2> exceptionTransform);
	
	public T orElse(T value) {
		return isValue() ? getValue() : value;
	}
	public T orElseGet(Supplier<T> value) {
		return isValue() ? getValue() : value.get();
	}
	public T orElseNull() {
		return isValue() ? getValue() : null;
	}
	public T orElseThrow() throws E {
		if(isValue()) return getValue();
		throw getException();
	}
	
	@Override
	public T get() {
		return orElseNull();
	}
	
	public static <T> OptionalError<T, ? extends Throwable> ofValue(T value) {
		return valueImpl(value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> OptionalError<T, E> ofValue(T value, Class<E> exceptionType) {
		return (OptionalError<T, E>) valueImpl(value);
	}
	
	public static <E extends Throwable> OptionalError<?, E> ofException(E exception) {
		return errorImpl(exception);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> OptionalError<T, E> ofException(E exception, Class<T> valueType) {
		return (OptionalError<T, E>) errorImpl(exception);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> OptionalError<T, E> ofNullable(T value, Supplier<E> exception) {
		if(value == null) {
			return (OptionalError<T, E>) errorImpl(exception.get());
		} else {
			return (OptionalError<T, E>) valueImpl(value);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> OptionalError<T, E> ofNullable(T value, Function<String, E> exceptionFactory, String exceptionMessage) {
		if(value == null) {
			return (OptionalError<T, E>) errorImpl(exceptionFactory.apply(exceptionMessage));
		} else {
			return (OptionalError<T, E>) valueImpl(value);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> OptionalError<T, E> ofOptional(Optional<T> value, Supplier<E> exception) {
		if(value.isPresent()) {
			return (OptionalError<T, E>) valueImpl(value.get());
		} else {
			return (OptionalError<T, E>) errorImpl(exception.get());
		}
	}
	
	public static <T, E extends Throwable> OptionalError<T, E> ofOptional(Optional<T> value, Function<String, E> exceptionFactory, String exceptionMessage) {		
		return ofOptional(value, () -> exceptionFactory.apply(exceptionMessage));
	}
	
	private static <T> OptionalError<T, ? extends Throwable> valueImpl(T value) {
		return new ValueImpl<>(value);
	}
	
	private static <E extends Throwable> OptionalError<?, E> errorImpl(E exception) {
		return new ErrorImpl<>(exception);
	}
	
	private static final class ErrorImpl<T, E extends Throwable> extends OptionalError<T, E> {
		
		private final E exception;
		
		private ErrorImpl(E exception) {
			this.exception = exception;
		}

		@Override
		public boolean isValue() {
			return false;
		}

		@Override
		public boolean isException() {
			return true;
		}

		@Override
		public T getValue() throws NoSuchElementException {
			throw new NoSuchElementException("OptionalError: no value present");
		}

		@Override
		public E getException() throws NoSuchElementException {
			return exception;
		}

		@Override
		public <R> OptionalError<R, E> mapValue(Function<T, R> mapper) {
			return new ErrorImpl<>(exception);
		}

		@Override
		public <R> OptionalError<R, E> flatMapValue(Function<T, Optional<R>> mapper, Supplier<E> exception) {
			return new ErrorImpl<>(this.exception);
		}

		@Override
		public <R, E2 extends Throwable> OptionalError<R, E2> flatMapValue(Function<T, Optional<R>> mapper,
				Supplier<E2> exception, Function<E, E2> exceptionTransform) {
			return new ErrorImpl<>(exceptionTransform.apply(this.exception));
		}
	}
	
	private static final class ValueImpl<T, E extends Throwable> extends OptionalError<T, E> {

		private final T value;
		
		private ValueImpl(T value) {
			this.value = value;
		}
		
		@Override
		public boolean isValue() {
			return true;
		}

		@Override
		public boolean isException() {
			return false;
		}

		@Override
		public T getValue() throws NoSuchElementException {
			return value;
		}

		@Override
		public E getException() throws NoSuchElementException {
			throw new NoSuchElementException("OptionalError: no exception present");
		}

		@Override
		public <R> OptionalError<R, E> mapValue(Function<T, R> mapper) {
			return new ValueImpl<>(mapper.apply(value));
		}

		@Override
		public <R> OptionalError<R, E> flatMapValue(Function<T, Optional<R>> mapper, Supplier<E> exception) {
			final Optional<R> result = mapper.apply(value);
			return result.isPresent() ? new ValueImpl<>(result.get()) : new ErrorImpl<>(exception.get());
		}

		@Override
		public <R, E2 extends Throwable> OptionalError<R, E2> flatMapValue(Function<T, Optional<R>> mapper,
				Supplier<E2> exception, Function<E, E2> exceptionTransform) {
			final Optional<R> result = mapper.apply(value);
			return result.isPresent() ? new ValueImpl<>(result.get()) : new ErrorImpl<>(exception.get());
		}
	}
}
