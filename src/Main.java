import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import fca.TriLattice;
import fca.Triadic;
import util.D;
import util.Pair;



public class Main {
	
	
	public static void teste3d(HashSet<String> sobjects, HashSet<String> sattributes, HashSet<String> sconditions) {
		Triadic pnrks = new Triadic(5, 4, 5);

		pnrks.readContext("src/contexts/pnrks_in");
		pnrks.generateBGMContext();
		pnrks.generateMGBContext();
		
		HashSet<Integer> objects = (HashSet<Integer>) pnrks.strToInt(sobjects, 0);
		HashSet<Integer> attributes = (HashSet<Integer>) pnrks.strToInt(sattributes, 1);
		HashSet<Integer> conditions = (HashSet<Integer>) pnrks.strToInt(sconditions, 2);
		
		long startTime, endTime, duration;
		
		startTime = System.nanoTime();
		pnrks.query3D(objects, attributes,conditions);
		endTime = System.nanoTime();
		duration = (endTime-startTime);
		pnrks.printQuery3D(sobjects, sattributes, sconditions);
		System.out.println("time " + duration/1000000 + "ms\n");
		
	}
	
	public static void teste2d(HashSet<String> selements1, HashSet<String> selements2, int dim1, int dim2) {
		Triadic pnrks = new Triadic(5, 4, 5);

		pnrks.readContext("src/contexts/pnrks_in");
		pnrks.generateBGMContext();
		pnrks.generateMGBContext();
		
		HashSet<Integer> elements1 = (HashSet<Integer>) pnrks.strToInt(selements1, dim1);
		HashSet<Integer> elements2 = (HashSet<Integer>) pnrks.strToInt(selements2, dim2);
		long startTime, endTime, duration;
		
		startTime = System.nanoTime();
		pnrks.query2D(elements1, elements2, dim1, dim2);
		endTime = System.nanoTime();
		duration = (endTime-startTime);
		pnrks.printQuery2D(selements1, selements2, dim1, dim2);
		System.out.println("time " + duration/1000000 + "ms\n");
		
	}
	
	public static void teste1d(HashSet<String> selements, int dim) {
		Triadic pnrks = new Triadic(5, 4, 5);

		pnrks.readContext("src/contexts/pnrks_in");
		pnrks.generateBGMContext();
		pnrks.generateMGBContext();
		
		long startTime, endTime, duration;
		HashSet<Integer> elements = (HashSet<Integer>) pnrks.strToInt(selements, dim);
		
		startTime = System.nanoTime();
		pnrks.query1D(elements, dim);
		endTime = System.nanoTime();
		duration = (endTime-startTime);
		pnrks.printQuery1D(selements, dim);
		System.out.println("time " + duration/1000000 + "ms\n");
	}
	
	
	public static void testes() {
		Triadic pnrks = new Triadic(5, 4, 5);

		pnrks.readContext("src/contexts/pnrks_in");
		pnrks.generateBGMContext();
		pnrks.generateMGBContext();

		System.out.println("ONE DIMENSION TESTES:");
		
	
		System.out.println("----------------------------------------------------");
		System.out.println("EXTENT");
		System.out.println("----------------------------------------------------");
		teste1d(new HashSet<>(Arrays.asList("1", "2", "3", "5")), 0);
		teste1d(new HashSet<>(Arrays.asList("1", "4")), 0);
		teste1d(new HashSet<>(Arrays.asList("1")), 0);
		teste1d(new HashSet<>(Arrays.asList("2")), 0);
		teste1d(new HashSet<>(Arrays.asList("4")), 0);
		
		System.out.println("----------------------------------------------------");
		System.out.println("INTENT");
		System.out.println("----------------------------------------------------");
		teste1d(new HashSet<>(Arrays.asList("P", "R")), 2);
		teste1d(new HashSet<>(Arrays.asList("K", "S")), 2);
		teste1d(new HashSet<>(Arrays.asList("N")), 2);
		teste1d(new HashSet<>(Arrays.asList("N", "R", "K")), 2);
		teste1d(new HashSet<>(Arrays.asList("P", "S")), 2);
		
		System.out.println("----------------------------------------------------");
		System.out.println("MODUS");
		System.out.println("----------------------------------------------------");
		
		teste1d(new HashSet<>(Arrays.asList("a", "b")), 1);
		teste1d(new HashSet<>(Arrays.asList("b", "d")), 1);
		teste1d(new HashSet<>(Arrays.asList("b")), 1);
		teste1d(new HashSet<>(Arrays.asList("c", "d")), 1);
		teste1d(new HashSet<>(Arrays.asList("a", "c")), 1);
		
		
		
		System.out.println("----------------------------------------------------");
		System.out.println("TWO DIMENSIONS TESTES:");
		System.out.println("----------------------------------------------------");
		teste2d(new HashSet<>(Arrays.asList("a", "b", "c")), new HashSet<>(Arrays.asList("K")), 1, 2);
		teste2d(new HashSet<>(Arrays.asList("a", "b")), new HashSet<>(Arrays.asList("P", "N", "K")), 1, 2);
		teste2d(new HashSet<>(Arrays.asList("1")), new HashSet<>(Arrays.asList("R")), 0, 2);
		teste2d(new HashSet<>(Arrays.asList("1", "3")), new HashSet<>(Arrays.asList("P", "K")), 0, 2);
		teste2d(new HashSet<>(Arrays.asList("1", "3", "5")), new HashSet<>(Arrays.asList("a")), 0, 1);
		teste2d(new HashSet<>(Arrays.asList("2")), new HashSet<>(Arrays.asList("a", "b", "c", "d")), 0, 1);
		teste2d(new HashSet<>(Arrays.asList("2")), new HashSet<>(Arrays.asList("a", "b", "d")), 0, 1);
		
		
		
		System.out.println("----------------------------------------------------");
		System.out.println("THREE DIMENSIONS TESTES:");
		System.out.println("----------------------------------------------------");
		
		
		teste3d(new HashSet<>(Arrays.asList("1", "4")), new HashSet<>(Arrays.asList("a", "b", "d")), new HashSet<>(Arrays.asList("P")));
		teste3d(new HashSet<>(Arrays.asList("4")), new HashSet<>(Arrays.asList("a", "b", "d")), new HashSet<>(Arrays.asList("K", "P")));
		teste3d(new HashSet<>(Arrays.asList("3", "5")), new HashSet<>(Arrays.asList("a")), new HashSet<>(Arrays.asList("P", "R")));
		teste3d(new HashSet<>(Arrays.asList("1", "3", "4")), new HashSet<>(Arrays.asList("a", "b", "d")), new HashSet<>(Arrays.asList("P", "N", "K", "S")));		
		teste3d(new HashSet<>(Arrays.asList("1", "5")), new HashSet<>(Arrays.asList("a", "b", "c")), new HashSet<>(Arrays.asList("K")));
		teste3d(new HashSet<>(Arrays.asList("4")), new HashSet<>(Arrays.asList("b")), new HashSet<>(Arrays.asList("K","P","N","R")));
		teste3d(new HashSet<>(Arrays.asList("1", "2", "3", "4", "5")), new HashSet<>(Arrays.asList("a", "b", "c", "d")), new HashSet<>(Arrays.asList("P", "N", "K", "R", "S")));
		
		
		
		
	}
	
	
	
	public static void main(String[] args) {
		

		Triadic pnrks = new Triadic(5, 5, 4);
//		pnrks.readGMBContextBinary("src/pnrks");
		pnrks.readContext2("src/contexts/pnrks_in2");
//		pnrks.generateBGMContext();
//		pnrks.generateMGBContext();
//		pnrks.printGMBContext();
		
		
		/**
		System.out.println(pnrks.primeObjectsAttributes(pnrks.strToInt(new HashSet<>(Arrays.asList("3", "4", "5")), 0), pnrks.strToInt(new HashSet<>(Arrays.asList("P", "N")), 2)));
		
		System.out.println("------------------------------------------------------");
		
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("2")), 0);
		System.out.println();
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("N")), 2);
		System.out.println("------------------------------------------------------");
		System.out.println(pnrks.primeObjectsConditions(pnrks.strToInt(new HashSet<>(Arrays.asList("1")), 0), pnrks.strToInt(new HashSet<>(Arrays.asList("P")), 2)));
		System.out.println("------------------------------------------------------");
		
		
		System.out.println(pnrks.primeObjects(pnrks.strToInt(new HashSet<>(Arrays.asList("2")), 0)).getLeft());
		System.out.println(pnrks.primeObjects(pnrks.strToInt(new HashSet<>(Arrays.asList("2")), 0)).getRight());
		System.out.println();
		System.out.println(pnrks.primeConditions(pnrks.strToInt(new HashSet<>(Arrays.asList("N")), 2)).getLeft());
		System.out.println(pnrks.primeConditions(pnrks.strToInt(new HashSet<>(Arrays.asList("N")), 2)).getRight());
		System.out.println();
		System.out.println(pnrks.primeConditions(pnrks.strToInt(new HashSet<>(Arrays.asList("b", "c", "d")), 1)).getLeft());
		System.out.println(pnrks.primeConditions(pnrks.strToInt(new HashSet<>(Arrays.asList("b", "c", "d")), 1)).getRight());
		
		
		
		System.out.println("------------------------------------------------------");
		
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("1","3")), 0);
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("1","3")), 0);
//		pnrks.query1D(pnrks.strToInt(new HashSet<>(Arrays.asList("P")), 2), 2);
//		System.out.println("------------------------------------------------------");
//		System.out.println(pnrks.primeObjectsConditions(pnrks.strToInt(new HashSet<>(Arrays.asList("3")), 0), pnrks.strToInt(new HashSet<>(Arrays.asList("P")), 2)));
//		System.out.println("------------------------------------------------------");
//		
//		
//		pnrks.query1D(pnrks.strToInt(new HashSet<>(Arrays.asList("K", "S")), 2), 2);
//		System.out.println("------------------------------------------------------");
//		
//		pnrks.printPrimeObjects(new HashSet<>(Arrays.asList("2")));
//		pnrks.printPrimeConditions(new HashSet<>(Arrays.asList("P")));
		System.out.println("------------------------------------------------------");
		pnrks.printPrimeObjects(new HashSet<>(Arrays.asList("1", "3")));
		pnrks.printPrimeAttributes(new HashSet<>(Arrays.asList("a", "b", "d")));
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("2", "3")), 0);
		
		pnrks.query1D(pnrks.strToInt(new HashSet<>(Arrays.asList("2", "3")), 0), 0);
		
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("c", "d")), 1);
		pnrks.printQuery1D(new HashSet<>(Arrays.asList("K", "S")), 2);
		pnrks.printQuery2D(new HashSet<>(Arrays.asList("a", "b", "c")), new HashSet<>(Arrays.asList("K")), 1, 2);
		pnrks.printQuery3D(new HashSet<>(Arrays.asList("1","3", "4")), 
				new HashSet<>(Arrays.asList("a", "b", "d")), 
				new HashSet<>(Arrays.asList("P")));
		
		
		
		long startTime = System.nanoTime();
		pnrks.query2D(new HashSet<>(Arrays.asList(0, 1, 3)), new HashSet<>(Arrays.asList(0)), 1, 2);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime); 
		System.out.println(duration/1000000);
		
		pnrks.printQuery2D(new HashSet<>(Arrays.asList("a", "b", "c")), new HashSet<>(Arrays.asList("K")), 1, 2);

		
		pnrks.printQuery3D(new HashSet<>(Arrays.asList("1","3", "4")), 
				new HashSet<>(Arrays.asList("a", "b", "d")), 
				new HashSet<>(Arrays.asList("P")));
*/



		pnrks.buildLattice((short)0, "src/concepts/pnrks");
		pnrks.getLattice().printLinks();
		System.out.println(pnrks.query1D(pnrks.strToInt(new HashSet<>(Arrays.asList("1", "3")), 0), 0));
		
		System.out.println(pnrks.LowerBound1D(pnrks.query1D(pnrks.strToInt(new HashSet<>(Arrays.asList("1", "3")), 0), 0), (HashSet<Integer>)pnrks.strToInt(new HashSet<>(Arrays.asList("1", "3")), 0), 0));
		
//		pnrks.printQuery1D(new HashSet<>(Arrays.asList("1","3")), 0);
//		pnrks.query2DNew((HashSet<Integer>)pnrks.strToInt(new HashSet<>(Arrays.asList("1")), 0), (HashSet<Integer>)pnrks.strToInt(new HashSet<>(Arrays.asList("a", "b")),2), 0, 2);
		
		
		
		Triadic mushroom = new Triadic(2104, 16, 8);
//		mushroom.readContextDataPeelerFormat("src/contexts/mush8416x32x4.data");
		mushroom.readContextDataPeelerFormat("src/contexts/mush2104x16x8.data");
		mushroom.printGMBContext();
		
		
//		long startTime = System.nanoTime();
////		mushroom.printPrimeObjects(new HashSet<>(Arrays.asList("o_2104", "o_2103", "o_2101", "o_2105", "o_2106", "o_2107", "o_2108"
////				, "o_2109", "o_2190", "o_2001", "o_101", "o_8000", "o_5")));
//		mushroom.printQuery1D(new HashSet<>(Arrays.asList("o_2104", "o_2103", "o_2101", "o_2105", "o_2106", "o_2107", "o_2108"
//				, "o_2109", "o_2190", "o_2001", "o_101", "o_8000", "o_5")), 0);
////		mushroom.printQuery1D(new HashSet<>(Arrays.asList("a", "b", "c")), 2);
//		long endTime = System.nanoTime();
//		System.out.println((endTime - startTime)/1000000);
		
		
	}

}
