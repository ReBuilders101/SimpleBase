package lb.simplebase.glcore.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public final class ModelLoader {

	private ModelLoader() {}

	public static ModelPrefab empty() {
		return new ModelPrefab(MaterialLibrary.DISABLED);
	}

	public static ModelPrefab loadFromResource(String name) throws ModelFormatException, IOException, FileNotFoundException {
		return loadFromResource(name, ModelLoader.class.getClassLoader(), null);
	}

	public static ModelPrefab loadFromResource(String name, ClassLoader loader) throws ModelFormatException, IOException, FileNotFoundException {
		return loadFromResource(name, loader, null);
	}
	
	public static ModelPrefab loadFromResource(String name, LoadPath materialLookupPath) throws ModelFormatException, IOException, FileNotFoundException {
		return loadFromResource(name, ModelLoader.class.getClassLoader(), materialLookupPath);
	}

	public static ModelPrefab loadFromResource(String name, ClassLoader loader, LoadPath materialLookupPath) throws ModelFormatException, IOException, FileNotFoundException {
		Objects.requireNonNull(name, "Resource name must not be null");
		Objects.requireNonNull(loader, "Class Loader name must not be null");
		try (InputStream resource = loader.getResourceAsStream(name)) {
			if(resource == null) throw new FileNotFoundException("Error loading resource: " + name);
			return loadFromStreamImpl(resource).build(materialLookupPath);
		}
	}

	
	
	public static ModelPrefab loadFromFile(File file) throws ModelFormatException, IOException, FileNotFoundException {
		Objects.requireNonNull(file, "File name must not be null");
		try (InputStream resource = new FileInputStream(file)) {
			return loadFromStreamImpl(resource).build(LoadPath.fromFile(file.getParentFile()));
		}
	}

	public static ModelPrefab loadFromPath(Path path) throws ModelFormatException, IOException, FileNotFoundException {
		Objects.requireNonNull(path, "File path must not be null");
		return loadFromLinesIO(Files.lines(path, StandardCharsets.UTF_8)).build(LoadPath.fromPath(path.getParent())); //Use lazy IO safe version
	}

	private static ModelBuilder loadFromStreamImpl(InputStream stream) throws ModelFormatException, IOException {
		try(InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
			return loadFromLinesIO(br.lines());
		}
	}
	
	public static ModelPrefab loadFromStream(InputStream stream, LoadPath materialLookupPath) throws ModelFormatException, IOException {
		Objects.requireNonNull(stream, "Stream must not be null");
		return loadFromStreamImpl(stream).build(materialLookupPath);
	}
	
	public static ModelPrefab loadFromStream(InputStream stream) throws ModelFormatException, IOException {
		return loadFromStream(stream, null);
	}

	private static ModelBuilder loadFromLinesIO(Stream<String> modelSource) throws ModelFormatException, IOException {
		try {
			return loadIteratorImpl(modelSource.iterator());
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public static ModelPrefab loadFromLines(Stream<String> modelSource) throws ModelFormatException {
		return loadFromLines(modelSource, null);
	}
	
	public static ModelPrefab loadFromLines(Stream<String> modelSource, LoadPath materialLookupPath) throws ModelFormatException {
		return loadIteratorImpl(modelSource.iterator()).build(materialLookupPath);
	}

	public static ModelPrefab loadFromLines(Iterable<String> modelSource) throws ModelFormatException {
		return loadFromLines(modelSource, null);
	}
	
	public static ModelPrefab loadFromLines(Iterable<String> modelSource, LoadPath materialLookupPath) throws ModelFormatException {
		return loadIteratorImpl(modelSource.iterator()).build(materialLookupPath);
	}

	private static ModelBuilder loadIteratorImpl(Iterator<String> lines) throws ModelFormatException {
		ModelBuilder builder = new ModelBuilder();
		int lineNum = 0;
		while(lines.hasNext()) {
			lineNum++;
			String line = lines.next();
			String[] split = line.split(" ");
			if(split.length < 1) continue; //Skip empty lines
			String command = split[0];
			switch (command) {
			case "#":
				continue; //Ignore Comments
			case "mtllib":
				builder.appendMaterialLibrary(check1String(split, true, builder,
						"No material library path present after command 'mtllib' @l" + lineNum + " (expecetd 1 string)"));
				continue;
			case "o":
				builder.beginMesh(check1String(split, false, builder,
						"No model name present after command 'o' @l" + lineNum + " (expected 1 string (no whitespace))"));
				continue;
			case "v":
				if(split.length <= 4) { //command + 3 floats (or less for error)
					builder.appendVertex3(check3Float(split, builder,
							"Invalid vertex data after command 'v' @l" + lineNum + " (expected 3 floats)", lineNum));
				} else { //command + 4+ floats
					builder.appendVertex4(check4Float(split, builder,
							"Invalid vertex data after command 'v' @l" + lineNum + " (expected 4 floats)", lineNum));
				}
				continue;
			case "vn":
				builder.appendNormal(check3Float(split, builder,
						"Invalid vertex normal data after command 'vn' @l" + lineNum + " (expected 3 floats)", lineNum));
				continue;
			case "vt":
				builder.appendTextureCoordinate(check2Float(split, builder,
						"Invalid vertex texture data after command 'vt' @l" + lineNum + " (expected 2 floats)", lineNum));
				continue;
			case "g":
				builder.beginFaceGroup(check1String(split, false, builder,
						"No face group name present after command 'g' @l" + lineNum + " (expected 1 string (no whitespace))"));
				continue;
			case "usemtl":
				builder.setFaceMaterial(check1String(split, false, builder,
						"No face material name after command 'usemtl' @l" + lineNum + " (expected 1 string (no whitespace))"));
				continue;
			case "s":
				String smoothOff = check1String(split, false, builder,
						"No smoothing group id after command 's' @l" + lineNum + " (expected 1 integer or string 'off')");
				if("off".equalsIgnoreCase(smoothOff)) {
					builder.endSmoothFaceGroup();
				} else {
					builder.beginSmoothFaceGroup(check1IntTo32(split, builder,
							"No smoothing group id after command 's' @l" + lineNum + " (expected 1 integer or string 'off')", lineNum));
				}
				continue;
			case "f":
				if(split.length == 4) { //Command + 3 vertices
					int[] indices1 = decodeFaceVertex(split[1], builder, lineNum);
					int[] indices2 = decodeFaceVertex(split[2], builder, lineNum);
					int[] indices3 = decodeFaceVertex(split[3], builder, lineNum);
//					determine the face declaration type
					if(indices1[1] == -1 || indices2[1] == -1 || indices3[1] == -1) { //Texture is not present in some
						if(indices1[1] == -1 || indices2[1] == -1 || indices3[1] == -1) { //Texture is not present in all
							//TODO rewrite if
						} else { //Texture is not present in some, but not all -> invalid
							throw new ModelFormatException("Face declaration has no texture coordinates for some, but not all vertices @l"
									+ lineNum + " (face declaration type must be consistent for all vertices in one face)", builder);
						}
					}
					if(indices1[2] == -1 || indices2[2] == -1 || indices3[2] == -1) { //Normal is not present in some
						if(indices1[2] == -1 || indices2[2] == -1 || indices3[2] == -1) { //Normal is not present in all
							//TODO rewrite if
						} else { //Normal is not present in some, but not all -> invalid
							throw new ModelFormatException("Face declaration has no normal coordinates for some, but not all vertices @l"
									+ lineNum + " (face declaration type must be consistent for all vertices in one face)", builder);
						}
					}
					//All indices are now prechecked
					builder.appendFaceV(indices1, indices2, indices3, split[1], split[2], split[3], lineNum);
				} else {
					throw new ModelFormatException("This model parser only supports faces made of exactly 3 vertices (found face with " +
							(split.length-1) + " vertices @l" + lineNum, builder);
				}
				continue;
			default:
				System.err.println("Model loader: found unknown command '" + command + "', skipping and continuing");
				continue;
			}

		}
		return builder;
	}

	private static String check1String(String[] parts, boolean concat, ModelBuilder builder, String errorMessage) throws ModelFormatException {
		if(parts.length > 2 && concat) {
			return String.join("", Arrays.copyOfRange(parts, 1, parts.length));
		} else if(parts.length >= 2) {
			return parts[1];
		} else {
			throw new ModelFormatException(errorMessage, builder);
		}
	}

	private static int check1IntTo32(String[] parts, ModelBuilder builder, String errorMessage, int line) throws ModelFormatException {
		if(parts.length == 2) { //Command + 1 int
			int value = decodeInt(parts[1], builder, "Invalid smoothing group id: value (" + parts[1] + ")@l" + line + " is not a valid integer");
			if(value >= 0 && value <= 32) {
				return value;
			} else {
				throw new ModelFormatException("Invalid smoothing group id: value (" + parts[1] + ")@l" + line + " must be between 0 and 32", builder);
			}

		} else {
			throw new ModelFormatException(errorMessage, builder);
		}
	}

	private static float[] check2Float(String[] parts, ModelBuilder builder, String errorMessage, int line) throws ModelFormatException {
		if(parts.length == 3) { //command + at least 2 floats = 3
			float[] data = new float[2];
			data[0] = decodeFloat(parts[1], builder, "Invalid vertex data: value 1 (" + parts[1] + ")@l" + line + " is not a valid float value");
			data[1] = decodeFloat(parts[2], builder, "Invalid vertex data: value 2 (" + parts[2] + ")@l" + line + " is not a valid float value");
			return data;
		} else {
			throw new ModelFormatException(errorMessage, builder);
		}
	}

	private static float[] check3Float(String[] parts, ModelBuilder builder, String errorMessage, int line) throws ModelFormatException {
		if(parts.length == 4) { //command + at least 3 floats = 4
			float[] data = new float[3];
			data[0] = decodeFloat(parts[1], builder, "Invalid vertex data: value 1 (" + parts[1] + ")@l" + line + " is not a valid float value");
			data[1] = decodeFloat(parts[2], builder, "Invalid vertex data: value 2 (" + parts[2] + ")@l" + line + " is not a valid float value");
			data[2] = decodeFloat(parts[3], builder, "Invalid vertex data: value 3 (" + parts[3] + ")@l" + line + " is not a valid float value");
			return data;
		} else {
			throw new ModelFormatException(errorMessage, builder);
		}
	}

	private static float[] check4Float(String[] parts, ModelBuilder builder, String errorMessage, int line) throws ModelFormatException {
		if(parts.length == 5) { //command + at least 4 floats = 5
			float[] data = new float[4];
			data[0] = decodeFloat(parts[1], builder, "Invalid vertex data: value 1 (" + parts[1] + ")@l" + line + " is not a valid float value");
			data[1] = decodeFloat(parts[2], builder, "Invalid vertex data: value 2 (" + parts[2] + ")@l" + line + " is not a valid float value");
			data[2] = decodeFloat(parts[3], builder, "Invalid vertex data: value 3 (" + parts[3] + ")@l" + line + " is not a valid float value");
			data[3] = decodeFloat(parts[4], builder, "Invalid vertex data: value 4 (" + parts[4] + ")@l" + line + " is not a valid float value");
			return data;
		} else {
			throw new ModelFormatException(errorMessage, builder);
		}
	}

	private static float decodeFloat(String number, ModelBuilder builder, String errorMessage) throws ModelFormatException {
		try {
			return Float.valueOf(number);
		} catch (NumberFormatException e) {
			throw new ModelFormatException(errorMessage, e, builder);
		}
	}

	private static int decodeInt(String number, ModelBuilder builder, String errorMessage) throws ModelFormatException {
		try {
			return Integer.valueOf(number);
		} catch (NumberFormatException e) {
			throw new ModelFormatException(errorMessage, e, builder);
		}
	}

	private static int[] decodeFaceVertex(String vertexDescription, ModelBuilder builder, int line) throws ModelFormatException {
		int[] ids = new int[3]; //vertex, texture and normal, -1 for unused
		String[] parts = vertexDescription.split("/");
		if(parts.length == 1) { //just vertex
			int num = decodeInt(parts[0], builder,
					"Invalid vertex id in face declaration: value (" + parts[0] + ")@l" + line + " is not a valid integer");
			if(num < 0) throw new ModelFormatException("Invalid vertex id in face declaration: value ("
					+ parts[0] + ")@l" + line + "(expected 1 positive integer)", builder);
			ids[0] = num;
			ids[1] = -1;
			ids[2] = -1;
		} else if(parts.length == 2) { //vertex and texture
			int num1 = decodeInt(parts[0], builder,
					"Invalid vertex id in face declaration: value (" + parts[0] + ")@l" + line + " is not a valid integer");
			if(num1 < 0) throw new ModelFormatException("Invalid vertex id in face declaration: value ("
					+ parts[0] + ")@l" + line + "(expected 1 positive integer)", builder);
			int num2 = decodeInt(parts[1], builder,
					"Invalid vertex texture id in face declaration: value (" + parts[1] + ")@l" + line + " is not a valid integer");
			if(num2 < 0) throw new ModelFormatException("Invalid vertex texture id in face declaration: value ("
					+ parts[1] + ")@l" + line + "(expected 1 positive integer)", builder);
			ids[0] = num1;
			ids[1] = num2;
			ids[2] = -1;
		} else if(parts.length == 3) { //vertex [and texture] and normal
			if("".equals(parts[1])) { //middle is empty
				int num1 = decodeInt(parts[0], builder,
						"Invalid vertex id in face declaration: value (" + parts[0] + ")@l" + line + " is not a valid integer");
				if(num1 < 0) throw new ModelFormatException("Invalid vertex id in face declaration: value ("
						+ parts[0] + ")@l" + line + "(expected 1 positive integer)", builder);
				int num3 = decodeInt(parts[2], builder,
						"Invalid vertex normal id in face declaration: value (" + parts[2] + ")@l" + line + " is not a valid integer");
				if(num3 < 0) throw new ModelFormatException("Invalid vertex texture id in face declaration: value ("
						+ parts[2] + ")@l" + line + "(expected 1 positive integer)", builder);
				ids[0] = num1;
				ids[1] = -1;
				ids[2] = num3;
			} else {
				int num1 = decodeInt(parts[0], builder,
						"Invalid vertex id in face declaration: value (" + parts[0] + ")@l" + line + " is not a valid integer");
				if(num1 < 0) throw new ModelFormatException("Invalid vertex id in face declaration: value ("
						+ parts[0] + ")@l" + line + "(expected 1 positive integer)", builder);
				int num2 = decodeInt(parts[1], builder,
						"Invalid vertex texture id in face declaration: value (" + parts[1] + ")@l" + line + " is not a valid integer");
				if(num2 < 0) throw new ModelFormatException("Invalid vertex texture id in face declaration: value ("
						+ parts[1] + ")@l" + line + "(expected 1 positive integer)", builder);
				int num3 = decodeInt(parts[2], builder,
						"Invalid vertex normal id in face declaration: value (" + parts[2] + ")@l" + line + " is not a valid integer");
				if(num3 < 0) throw new ModelFormatException("Invalid vertex texture id in face declaration: value ("
						+ parts[2] + ")@l" + line + "(expected 1 positive integer)", builder);
				ids[0] = num1;
				ids[1] = num2;
				ids[2] = num3;
			}
		} else {
			throw new ModelFormatException("Face vertices were in an unknown format @l" + line + " (expected v or v/t or v//n or v/t/n)", builder);
		}
		return ids;
	}
	
	public static interface LoadPath {
		public InputStream openResource(String name) throws IOException;
		
		public static LoadPath fromFile(final File folderPath) {
			Objects.requireNonNull(folderPath, "Folder Path object cannot be null");
			return fromPath(folderPath.toPath());
		}
		
		public static LoadPath fromPath(final Path folderPath) {
			Objects.requireNonNull(folderPath, "Folder Path object cannot be null");
			return name -> Files.newInputStream(folderPath.resolve(name), StandardOpenOption.READ);
		}
		
		public static LoadPath fromResource(final String packageName) {
			Objects.requireNonNull(packageName, "Package name string cannot be null");
			return name -> {
				final String resName;
				if(packageName.endsWith("/") && name.startsWith("/")) { //Cut one out
					resName = packageName + name.substring(1);
				} else if(!packageName.endsWith("/") && !name.startsWith("/")) { //Add one
					resName = packageName + "/" + name;
				} else { //perfect
					resName = packageName + name;
				}
				return ModelLoader.class.getClassLoader().getResourceAsStream(resName);
			};
		}
	}

}
