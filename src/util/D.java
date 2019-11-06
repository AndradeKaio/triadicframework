package util;

public enum D {
	OBJECTS(0),
	ATTRIBUTES(1),
	CONDITIONS(2);
	
	private final int dim;
	
	private D (int dim) {
		this.dim = dim;
	}
	
	public int intValue() {
		return dim;
	}
	
}
