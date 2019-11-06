import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import fca.Context;
import fca.TContext;
import util.D;
import util.Pair;


public class Main {
	
	
	public static void main(String[] args) {
		

		TContext pnrks = new TContext(5, 4, 5);
//		pnrks.readGMBContextBinary("src/pnrks");
		pnrks.readContext("src/pnrks_in");
		pnrks.generateBGMContext();
		pnrks.generateMGBContext();
//		pnrks.printGMBContext();
		
		Set<Integer> obj = new HashSet<Integer>();
		obj.add(0);obj.add(2);//obj.add(1);obj.add(2);obj.add(4);
	
		
		System.out.println(pnrks.primeObjectsAttributes(pnrks.stringToInteger(new HashSet<>(Arrays.asList("3", "4", "5")), 0), pnrks.stringToInteger(new HashSet<>(Arrays.asList("P", "N")), 2)));
		
		System.out.println("------------------------------------------------------");
		
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("2")), 0);
		System.out.println();
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("N")), 2);
		System.out.println("------------------------------------------------------");
		System.out.println(pnrks.primeObjectsConditions(pnrks.stringToInteger(new HashSet<>(Arrays.asList("1")), 0), pnrks.stringToInteger(new HashSet<>(Arrays.asList("P")), 2)));
		System.out.println("------------------------------------------------------");
		
		
		System.out.println(pnrks.primeObjects(pnrks.stringToInteger(new HashSet<>(Arrays.asList("2")), 0)).getLeft());
		System.out.println(pnrks.primeObjects(pnrks.stringToInteger(new HashSet<>(Arrays.asList("2")), 0)).getRight());
		System.out.println();
		System.out.println(pnrks.primeConditions(pnrks.stringToInteger(new HashSet<>(Arrays.asList("N")), 2)).getLeft());
		System.out.println(pnrks.primeConditions(pnrks.stringToInteger(new HashSet<>(Arrays.asList("N")), 2)).getRight());
		System.out.println();
		System.out.println(pnrks.primeConditions(pnrks.stringToInteger(new HashSet<>(Arrays.asList("b", "c", "d")), 1)).getLeft());
		System.out.println(pnrks.primeConditions(pnrks.stringToInteger(new HashSet<>(Arrays.asList("b", "c", "d")), 1)).getRight());
		
		
		
		System.out.println("------------------------------------------------------");
		
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("1","3")), 0);
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("1","3")), 0);
//		pnrks.query1D(pnrks.stringToInteger(new HashSet<>(Arrays.asList("P")), 2), 2);
//		System.out.println("------------------------------------------------------");
//		System.out.println(pnrks.primeObjectsConditions(pnrks.stringToInteger(new HashSet<>(Arrays.asList("3")), 0), pnrks.stringToInteger(new HashSet<>(Arrays.asList("P")), 2)));
//		System.out.println("------------------------------------------------------");
//		
//		
//		pnrks.query1D(pnrks.stringToInteger(new HashSet<>(Arrays.asList("K", "S")), 2), 2);
//		System.out.println("------------------------------------------------------");
//		
//		pnrks.printPrimeObjects(new HashSet<>(Arrays.asList("2")));
//		pnrks.printPrimeConditions(new HashSet<>(Arrays.asList("P")));
		System.out.println("------------------------------------------------------");
		pnrks.printPrimeObjects(new HashSet<>(Arrays.asList("1", "3")));
		pnrks.printPrimeAttributes(new HashSet<>(Arrays.asList("a", "b", "d")));
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("2", "3")), 0);
		
		pnrks.query1D(pnrks.stringToInteger(new HashSet<>(Arrays.asList("2", "3")), 0), 0);
		
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("c", "d")), 1);
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("K", "S")), 2);
		pnrks.printQuery2D(new HashSet<>(Arrays.asList("a", "b", "c")), new HashSet<>(Arrays.asList("K")), 1, 2);
		//System.out.println(pnrks.query2D((HashSet)pnrks.stringToInteger(new HashSet<>(Arrays.asList("a")), 1), (HashSet)pnrks.stringToInteger(new HashSet<>(Arrays.asList("K", "S")), 2), 1, 2));
		
	}

}
