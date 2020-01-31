package lb.simplebase.glcore.model;

import java.util.Arrays;

public class Vertex {
	
	protected final float[] data;
	private final boolean hasTextures;
	private final boolean hasNormals;
	
	protected Vertex(float[] dataFinal, boolean text, boolean norm) {
		this.data = dataFinal;
		this.hasTextures = text;
		this.hasNormals = norm;
	}
	
	public float getPositionX() {
		return data[0];
	}
	
	public float getPositionY() {
		return data[1];
	}
	
	public float getPositionZ() {
		return data[2];
	}
	
	
	public float getTextureU() {
		return data[3];
	}
	
	public float getTextureV() {
		return data[4];
	}
	
	
	public float getNormalX() {
		return data[5];
	}
	
	public float getNormalY() {
		return data[6];
	}
	
	public float getNormalZ() {
		return data[7];
	}
	
	
	public boolean hasValidTextures() {
		return hasTextures;
	}
	
	public boolean hasValidNormals() {
		return hasNormals;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + (hasNormals ? 1231 : 1237);
		result = prime * result + (hasTextures ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (hasNormals != other.hasNormals)
			return false;
		if (hasTextures != other.hasTextures)
			return false;
		return true;
	}

}
