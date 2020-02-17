package util;

import java.util.Arrays;
import java.util.HashSet;

import fca.Triadic;

public class TriadicTests {

	private Triadic context;
	
	public TriadicTests(int objects, int attributes, int conditions, String fileName) {
		context = new Triadic(objects, attributes, conditions);
		context.readContext(fileName);
	}
	

	public void testPrimeObjectsOperator() {
		HashSet<Integer>result = new HashSet<>(Arrays.asList(1));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "ERROR PRIME OBJECTS: (1)'";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		assert  result.equals(context.primeObjects(new HashSet<>(Arrays.asList(1)))) : "error";
		result = new HashSet<>(Arrays.asList(2));
		
		
	}
	
	
	
	
	
}
