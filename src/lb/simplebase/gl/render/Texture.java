package lb.simplebase.gl.render;

import org.lwjgl.opengl.GL11;

import lb.simplebase.gl.GLHandle;

public class Texture implements GLHandle{

	private final int textureHandle;
	private final int width;
	private final int height;
	
	protected Texture(int textureHandle, int width, int height) {
		this.textureHandle = textureHandle;
		this.width = width;
		this.height = height;
	}

	public void disposeTexture() {
		GL11.glDeleteTextures(textureHandle);
	}
	
	public static void disposeTexture(Texture...textures) {
		if(textures != null) {
			for(Texture t : textures) {
				if(t != null) t.disposeTexture();
			}
		}
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	@Override
	public int getGLHandle() {
		return textureHandle;
	}
	
}
