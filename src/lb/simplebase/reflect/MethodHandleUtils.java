package lb.simplebase.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lb.simplebase.reflect.FieldAccess.GetAndSetFieldAccess;
import lb.simplebase.reflect.FieldAccess.ReflectionFieldAccess;

public class MethodHandleUtils {

	private static final Lookup LOOKUP = MethodHandles.lookup();
	
	public static MethodHandle unreflect(final Method method) {
		if(method == null) return null;
		try {
			return LOOKUP.unreflect(method);
		} catch (IllegalAccessException e) {
			if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not create MethodHandle: Unreflected method not accessible");
			return null;
		}
	}
	
	public static MethodHandle unreflectGetter(final Field field) {
		if(field == null) return null;
		try {
			return LOOKUP.unreflectGetter(field);
		} catch (IllegalAccessException e) {
			if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not create MethodHandle: Unreflected field not accessible");
			return null;
		}
	}
	
	public static MethodHandle unreflectSetter(final Field field) {
		if(field == null) return null;
		try {
			return LOOKUP.unreflectSetter(field);
		} catch (IllegalAccessException e) {
			if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not create MethodHandle: Unreflected field not accessible");
			return null;
		}
	}
	
	public static MethodHandle unreflect(final MethodAccess<?> method) {
		if(method == null) return null;
		return method.createHandle();
	}
	
	public static MethodHandle unreflectGetter(final FieldAccess<?> field) {
		if(field == null) return null;
		try {
			if(field instanceof ReflectionFieldAccess<?>) {
				final MethodHandle handle = LOOKUP.unreflectGetter(((ReflectionFieldAccess<?>) field).field);
				if(field.isBound()) return handle.bindTo(field.instance);
				return handle;
			} else if(field instanceof GetAndSetFieldAccess<?>) {
				return ((GetAndSetFieldAccess<?>) field).getterFunction.createHandle();
			} else {
				if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not create MethodHandle: Unsupported FieldAccess implementation");
				return null;
			}
		} catch (IllegalAccessException e) {
			if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not create MethodHandle: Unreflected field not accessible");
			return null;
		}
	}
	
	public static MethodHandle unreflectSetter(final FieldAccess<?> field) {
		if(field == null) return null;
		try {
			if(field instanceof ReflectionFieldAccess<?>) {
				return LOOKUP.unreflectSetter(((ReflectionFieldAccess<?>) field).field);
			} else if(field instanceof GetAndSetFieldAccess<?>) {
				return ((GetAndSetFieldAccess<?>) field).setterFunction.createHandle();
			} else {
				if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not create MethodHandle: Unsupported FieldAccess implementation");
				return null;
			}
		} catch (IllegalAccessException e) {
			if(BaseReflectionUtils.enabled) BaseReflectionUtils.REF_LOG.error("Could not create MethodHandle: Unreflected field not accessible");
			return null;
		}
	}
}
