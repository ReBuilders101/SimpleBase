package lb.simplebase.dynamicdata;

public interface DataFormat {
	
	public default Data createDynamic() {
		return new DynamicData(this);
	}
	
	public Data createStatic(DataTypeNode<?> type);
	
	public DataMap createMap();
	
	public DataList createArray();
	
}
