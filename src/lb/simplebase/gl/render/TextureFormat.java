package lb.simplebase.gl.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class TextureFormat {
	
	public static TextureFormat create() {
		return new TextureFormat(GL11.GL_NEAREST, GL11.GL_NEAREST, GL11.GL_REPEAT, GL11.GL_REPEAT, true, false);
	}
	
	private TextureFormat(int magFilter, int minFilter, int sWrap, int tWrap, boolean mipmap, boolean flip) {
		this.magFilter = magFilter;
		this.minFilter = minFilter;
		this.sWrap = sWrap;
		this.tWrap = tWrap;
		this.mipmap = mipmap;
		this.flip = flip;
		this.borderColor = null;
	}

	private int magFilter;
	private int minFilter;
	private int sWrap;
	private int tWrap;
	private boolean mipmap;
	private boolean flip;
	private float[] borderColor;
	
	public TextureFormat setMipmaps(boolean value) {
		mipmap = value;
		return this;
	}
	
	public TextureFormat setFlipped(boolean value) {
		flip = value;
		return this;
	}
	
	public TextureFormat setMagnifyFilter(int filterModeId) {
		magFilter = filterModeId;
		return this;
	}
	
	public TextureFormat setMinifyFilter(int filterModeId) {
		minFilter = filterModeId;
		return this;
	}
	
	public TextureFormat setWrapModeX(int wrapModeId) {
		sWrap = wrapModeId;
		return this;
	}
	
	public TextureFormat setWrapModeY(int wrapModeId) {
		tWrap = wrapModeId;
		return this;
	}
	
	public TextureFormat setWrapBorderColor(float r, float g, float b, float a) {
		borderColor = new float[] {r, b, g, a};
		return this;
	}
	
	protected int getMagFilter() {
		return magFilter;
	}
	
	protected int getMinFilter() {
		return minFilter;
	}
	
	protected int getSWrap() {
		return sWrap;
	}
	
	protected int getTWrap() {
		return tWrap;
	}
	
	protected boolean getFlip() {
		return flip;
	}
	
	protected float[] getBorderColor() {
		return borderColor;
	}
	
	public boolean useClamp() {
		return sWrap == GL13.GL_CLAMP_TO_BORDER || tWrap == GL13.GL_CLAMP_TO_BORDER;
	}
	
	protected boolean getMipmaps() {
		return mipmap;
	}
	
}