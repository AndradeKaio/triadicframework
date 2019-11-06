package fca;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;


public class Context {

	//number of dimensions
	private int nDimensions;
	//size of elements of each dimension
	private int [] dimensionSize;
	//bitset of each dimension
	private BitSet [] bits;
	//list of elements for each dimension
	private ArrayList<String> [] elements;
	
	
	public Context() {
		super();
	}
	
	
	public Context(int nDimensions, int [] dimensionSize) {
		this.nDimensions = nDimensions;
		this.bits = new BitSet[nDimensions];
		
		
	}
	
	
	
	
	
	
	public void readIncidencesFile(String fileName) {
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
			int c;
			c = br.read();
			int a = Character.getNumericValue(c);
			
			System.out.println(a);
//		    while ((c = br.read()) != -1) {
//		    	System.out.println((char) c);
//		    }
		 }catch (IOException e) {
			 System.err.format("IOException: %s%n", e);
		 }

	}
	
	

	
	

	
	/**
	 * @return the nDimensions
	 */
	public int getnDimensions() {
		return nDimensions;
	}
	/**
	 * @param nDimensions the nDimensions to set
	 */
	public void setnDimensions(int nDimensions) {
		this.nDimensions = nDimensions;
	}
	/**
	 * @return the dimensionsSize
	 */
	public int[] getDimensionsSize() {
		return dimensionSize;
	}
	/**
	 * @param dimensionsSize the dimensionsSize to set
	 */
	public void setDimensionsSize(int[] dimensionsSize) {
		this.dimensionSize = dimensionsSize;
	}
	/**
	 * @return the bits
	 */
	public BitSet[] getBits() {
		return bits;
	}
	/**
	 * @param bits the bits to set
	 */
	public void setBits(BitSet[] bits) {
		this.bits = bits;
	}
	
	
	
}
