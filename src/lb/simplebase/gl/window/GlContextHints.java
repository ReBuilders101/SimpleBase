package lb.simplebase.gl.window;

import org.lwjgl.glfw.GLFW;
import lb.simplebase.gl.GlUtils;
import lb.simplebase.gl.GLValue;

public interface GlContextHints {

	public void apply();
	public boolean hasAPI();
	
	public static OpenGlHints openGl() {
		return new OpenGlHints();
	}
	
	public static OpenGlESHints openGlES() {
		return new OpenGlESHints();
	}
	
	public static NoContextHints noGlContext() {
		return new NoContextHints();
	}
	
	
	public static class OpenGlHints extends OpenGlESHints {
		private OpenGlHints() {}
		
		private int forwardCompatible = GLFW.GLFW_FALSE;
		private int debugContext = GLFW.GLFW_FALSE;
		private int openGlProfile = GLFW.GLFW_OPENGL_ANY_PROFILE;
		
		public OpenGlHints setForwardCompatible(boolean value) {
			forwardCompatible = GlUtils.glfwBool(value);
			return this;
		}
		
		@Override
		public OpenGlHints setContextCreationAPI(ContextAPI api) {
			super.setContextCreationAPI(api);
			return this;
		}

		@Override
		public OpenGlHints setGlContextVersion(int majorVersion, int minorVersion) {
			super.setGlContextVersion(majorVersion, minorVersion);
			return this;
		}

		@Override
		public OpenGlHints setNoError(boolean value) {
			super.setNoError(value);
			return this;
		}

		@Override
		public OpenGlHints setContextRobustness(ContextRobustness value) {
			super.setContextRobustness(value);
			return this;
		}

		@Override
		public OpenGlHints setReleaseBehavior(ContextReleaseBehavior value) {
			super.setReleaseBehavior(value);
			return this;
		}

		public OpenGlHints setDebugContext(boolean value) {
			debugContext = GlUtils.glfwBool(value);
			return this;
		}
		
		public OpenGlHints setProfile(OpenGlProfile profile) {
			openGlProfile = profile.getGLValue();
			return this;
		}
		
		public OpenGlHints setProfile(boolean useCoreProfile) {
			openGlProfile = useCoreProfile ? GLFW.GLFW_OPENGL_CORE_PROFILE : GLFW.GLFW_OPENGL_COMPAT_PROFILE;
			return this;
		}
		
		
		
		@Override
		public void apply() {
			super.apply();
			//Overwrite client api
			GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
			
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, forwardCompatible);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, debugContext);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, openGlProfile);
			
		}

		@Override
		public boolean hasAPI() {
			return true;
		}
		
	}
	
	public static class OpenGlESHints implements GlContextHints {
		private OpenGlESHints() {}
		
		private int creationAPI = GLFW.GLFW_NATIVE_CONTEXT_API;
		private int majorVersion = 1;
		private int minorVersion = 0;
		private int robustness = GLFW.GLFW_NO_ROBUSTNESS;
		private int releaseBehavior = GLFW.GLFW_ANY_RELEASE_BEHAVIOR;
		private int noError = GLFW.GLFW_FALSE;
		
		public OpenGlESHints setContextCreationAPI(ContextAPI api) {
			this.creationAPI = api.getGLValue();
			return this;
		}
		
		public OpenGlESHints setGlContextVersion(int majorVersion, int minorVersion) {
			this.majorVersion = majorVersion;
			this.minorVersion = minorVersion;
			return this;
		}
		
		public OpenGlESHints setNoError(boolean value) {
			noError = GlUtils.glfwBool(value);
			return this;
		}
		
		public OpenGlESHints setContextRobustness(ContextRobustness value) {
			robustness = value.getGLValue();
			return this;
		}
		
		public OpenGlESHints setReleaseBehavior(ContextReleaseBehavior value) {
			releaseBehavior = value.getGLValue();
			return this;
		}
		
		@Override
		public void apply() {
			GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_ES_API);
			
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, creationAPI);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, majorVersion);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minorVersion);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_ROBUSTNESS, robustness);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR, releaseBehavior);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_NO_ERROR, noError);
		}

		@Override
		public boolean hasAPI() {
			return true;
		}
		
	}

	public static class NoContextHints implements GlContextHints {
		private NoContextHints() {}
		
		@Override
		public void apply() {
			GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
		}
		
		@Override
		public boolean hasAPI() {
			return false;
		}
	}
	
	
	public static enum ContextAPI implements GLValue {
		NATIVE(GLFW.GLFW_NATIVE_CONTEXT_API),
		EGL(GLFW.GLFW_EGL_CONTEXT_API),
		OSMESA(GLFW.GLFW_OSMESA_CONTEXT_API);

		private final int value;
		
		private ContextAPI(int value) {
			this.value = value;
		}
		
		@Override
		public int getGLValue() {
			return value;
		}
	}
	
	public static enum OpenGlProfile implements GLValue {
		CORE(GLFW.GLFW_OPENGL_CORE_PROFILE),
		COMPAT(GLFW.GLFW_OPENGL_COMPAT_PROFILE),
		ANY(GLFW.GLFW_OPENGL_ANY_PROFILE);

		private final int value;
		
		private OpenGlProfile(int value) {
			this.value = value;
		}
		
		@Override
		public int getGLValue() {
			return value;
		}
	}
	
	public static enum ContextRobustness implements GLValue {
		NO_ROBUSTNESS(GLFW.GLFW_NO_ROBUSTNESS),
		NO_RESET_NOTIFICATION(GLFW.GLFW_NO_RESET_NOTIFICATION),
		LOSE_CONTEXT_ON_RESET(GLFW.GLFW_LOSE_CONTEXT_ON_RESET);

		private final int value;
		
		private ContextRobustness(int value) {
			this.value = value;
		}
		
		@Override
		public int getGLValue() {
			return value;
		}
	}
	
	public static enum ContextReleaseBehavior implements GLValue {
		ANY(GLFW.GLFW_ANY_RELEASE_BEHAVIOR),
		FLUSH(GLFW.GLFW_RELEASE_BEHAVIOR_FLUSH),
		NONE(GLFW.GLFW_RELEASE_BEHAVIOR_NONE);

		private final int value;
		
		private ContextReleaseBehavior(int value) {
			this.value = value;
		}
		
		@Override
		public int getGLValue() {
			return value;
		}
	}
}
