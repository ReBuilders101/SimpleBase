package lb.simplebase.dynamicdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lb.simplebase.reflect.QuickReflectionUtils;

public class DefaultTypes {

	private static final Map<DataFormat, ClassMap> formatNodes = new HashMap<>();
	
	public static <T> boolean register(DataFormat format, DataTypeNode<T> node) { //Don't remove the <T> !!!
		Objects.requireNonNull(format, "The data format to register a node for can't be null");
		Objects.requireNonNull(node, "The node to register can't be null");
		formatNodes.computeIfAbsent(format, (k) -> new ClassMap());
		ClassMap map = formatNodes.get(format);
		return map.set(node.getDataType(), node);
	}
	
	public static <T> DataTypeNode<T> typedNode(DataFormat format, Class<T> type) {
		Objects.requireNonNull(format, "The data format to look up a node can't be null");
		Objects.requireNonNull(type, "The data type to look up a node can't be null");
		ClassMap map = formatNodes.get(format);
		if(map == null) return null;
		DataTypeNode<T> node = map.get(type);
		return node;
	}
	
	public static DataTypeNode<Byte> byteNode(DataFormat f) {
		return typedNode(f, Byte.class);
	}
	
	public static DataTypeNode<Short> shortNode(DataFormat f) {
		return typedNode(f, Short.class);
	}
	
	public static DataTypeNode<Character> charNode(DataFormat f) {
		return typedNode(f, Character.class);
	}
	
	public static DataTypeNode<Integer> intNode(DataFormat f) {
		return typedNode(f, Integer.class);
	}
	
	public static DataTypeNode<Long> longNode(DataFormat f) {
		return typedNode(f, Long.class);
	}
	
	public static DataTypeNode<Float> floatNode(DataFormat f) {
		return typedNode(f, Float.class);
	}
	
	public static DataTypeNode<Double> doubleNode(DataFormat f) {
		return typedNode(f, Double.class);
	}
	
	public static DataTypeNode<String> stringNode(DataFormat f) {
		return typedNode(f, String.class);
	}
	
	public static DataTypeNode<DataMap> mapNode(DataFormat f) {
		return typedNode(f, DataMap.class);
	}
	
	public static DataTypeNode<DataList> listNode(DataFormat f) {
		return typedNode(f, DataList.class);
	}
	
	@SuppressWarnings("unused")
	private static void parse(Data data) {
		DataMap map = data.getMap();
		Data iNode = map.getElement("int");
		int value = iNode.getIntValue();
		
		Data newString = data.getFormat().createDynamic();
		newString.setStringValue("Test");
		map.setElement("string", newString);
	}
	
	protected static final DataTypeNode<?> UNDEFINED = new DataTypeNode<Object>() {

		@Override
		public Object decode(BaseData data) {
			return null;
		}

		@Override
		public Data encode(Object data) {
			return null;
		}

		@Override
		public DataFormat getDataFormat() {
			return null;
		}

		@Override
		public Class<Object> getDataType() {
			return null;
		}
	};
	
	private static final class ClassMap {
		
		private final Map<Class<?>, DataTypeNode<?>> map;
		
		public ClassMap() {
			map = new HashMap<>();
		}
		
		@SuppressWarnings("unchecked")
		public <T> DataTypeNode<T> get(Class<T> type) {
			Objects.requireNonNull(type, "The class used as a key must not be null");
			DataTypeNode<?> instance = map.get(type);
			if(!QuickReflectionUtils.isSubclassOf(instance.getDataType(), type)) throw new IllegalStateException("Type mismatch between key class and instance value - ClassMap is in an invalid state");
			return (DataTypeNode<T>) instance;
		}
		
		public <T> boolean set(Class<T> type, DataTypeNode<T> instance) {
			Objects.requireNonNull(type, "The class used as a key must not be null");
			Objects.requireNonNull(instance, "The instance used as a value must not be null");
			if(!QuickReflectionUtils.isSubclassOf(instance.getDataType(), type)) throw new IllegalArgumentException("Instance class must match the key instance class");
			if(map.containsKey(type)) {
				return false;
			} else {
				map.put(type, instance);
				return true;
			}
		}
		
//		public <T> void force(Class<T> type, DataTypeNode<T> instance) {
//			Objects.requireNonNull(type, "The class used as a key must not be null");
//			Objects.requireNonNull(instance, "The instance used as a value must not be null");
//			if(!QuickReflectionUtils.isSubclassOf(instance.getDataType(), type)) throw new IllegalArgumentException("Instance class must match the key instance class");
//			map.put(type, instance);
//		}
		
	}
	
}
