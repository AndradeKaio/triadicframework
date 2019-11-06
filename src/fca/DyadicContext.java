package fca;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import util.Pair;


public class DyadicContext {

	

	private int objects;
	private int attributes;
	
	private Set<Integer> setObjects;
	private Set<Integer> setAttributes;
	
	private BitSet[] bitsObjects;
	private BitSet[] bitsAttributes;
	
	
	public DyadicContext() {
		super();
	}
	
	public DyadicContext(int objects, int attributes) {
		this.objects = objects;
		this.attributes = attributes;
		
		this.setAttributes = new HashSet<Integer>();
		this.setObjects = new HashSet<Integer>();
		
		this.bitsObjects = new BitSet[this.objects];
		this.bitsAttributes = new BitSet[this.attributes];
		
		for (int i = 0; i < objects; i++)
			this.bitsObjects[i] = new BitSet(attributes);

		for (int i = 0; i < attributes; i++)
			this.bitsAttributes[i] = new BitSet(objects);
		
	}
	
	public DyadicContext(ArrayList<Integer> objectsArrayList, ArrayList<Integer> attributesArrayList, List<Pair<Integer, Integer>> incidences) {
//		System.out.println("objects " + objects + " attributes " + attributes);
		
		this.objects = objectsArrayList.size();
		this.attributes = attributesArrayList.size();
		
		this.setAttributes = new HashSet<Integer>();
		this.setObjects = new HashSet<Integer>();
		
		this.bitsObjects = new BitSet[this.objects];
		this.bitsAttributes = new BitSet[this.attributes];
		
		for (int i = 0; i < objects; i++)
			this.bitsObjects[i] = new BitSet(attributes);

		for (int i = 0; i < attributes; i++)
			this.bitsAttributes[i] = new BitSet(objects);
		
		int obj;
		int att;
		
		for (Pair<Integer, Integer> incidence : incidences) {
			
			obj = objectsArrayList.indexOf(incidence.getLeft());
			att = attributesArrayList.indexOf(incidence.getRight());
			
			this.setObjects.add(obj);
			this.setAttributes.add(att);		
			
			this.bitsObjects[obj].set(att, true);
			this.bitsAttributes[att].set(obj, true);
		}
		
	}
	
	 
	public Set<Integer> primeAttributes(Set<Integer> attributes) {

		BitSet b = new BitSet(this.objects);
		b.set(0, this.objects);
		

		for(int attribute : attributes) {
			b.and(this.bitsAttributes[attribute]);
		}
		
		return getObjects(b);
	}
	
	public Set<Integer> primeObjects(Set<Integer> objects) {
		
		BitSet b = new BitSet(this.attributes);
		b.set(0, this.attributes);

		for(int object : objects) {
			b.and(this.bitsObjects[object]);
		}
		
		return getAttributes(b);
	}
	
	public Set<Integer> getObjects(BitSet b){
		Set<Integer> objects = new HashSet<Integer>();
		
		for (int i = 0; i < this.objects; i++)
			if(b.get(i))
				objects.add(i);

		return objects;
	}
	
	public Set<Integer> getAttributes(BitSet b){
		Set<Integer> attributes = new HashSet<Integer>();

		for (int i = 0; i < this.attributes; i++)
			if(b.get(i))
				attributes.add(i);
		return attributes;
	}
	
	
	
	public List<Pair<Set<Integer>, Set<Integer>>> allConcepts(){
		
		List<Pair<Set<Integer>, Set<Integer>>> concepts = new ArrayList<Pair<Set<Integer>, Set<Integer>>>();
		
//		ArrayList<DyadicFormalConcept> concepts = new ArrayList<DyadicFormalConcept>();
		
		Set<Integer> gprime = this.primeObjects(this.setObjects);
		
		if(!gprime.isEmpty())
			concepts.add(new Pair(this.setObjects, gprime));
		
		Set<Integer> b = this.primeObjects(this.setObjects);

		while(!b.equals(this.setAttributes)) {

			b = nextClosure(b);

			
			Set<Integer> extension = this.primeAttributes(b);
			Set<Integer> intension = new HashSet<Integer>(b); 
			if (!extension.isEmpty() && !intension.isEmpty())
				concepts.add(new Pair(extension, intension));
//				concepts.add(new DyadicFormalConcept(extension, intension));
		}
		
		return concepts;
	}
	public Set<Integer> nextClosure(Set<Integer> b) {
		
		
		for (int attribute : this.setAttributes) {

			if (!(b.contains(attribute))) {
				b.add(attribute);
				
				Set<Integer> bprime = this.primeAttributes(b);

				Set<Integer> db = this.primeObjects(bprime);

				
				Set<Integer> aux = new HashSet<Integer>(db);
				
				
				aux.removeAll(b);


				boolean flag = true;
				for (int j : aux)
					if (j > attribute)
						flag = false;
				
				if (flag)
					return db;
				
					
			}
			b.remove(attribute);
		}
		return b;
	}
	
	
	
	public void readContextGBFromFile(String filename){
		int i = 0, j = 0;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
			int c;
		    while ((c = br.read()) != -1) {
//		    	System.out.println("i = " +  i + " j = " + j);
		    	if (j % this.attributes == 0)
		    		j = 0;
		    	this.setObjects.add(i);
		    	this.setAttributes.add(j);
		    	if((char)c == '0') {
		    		this.bitsObjects[i].set(j, false);
		    		this.bitsAttributes[j].set(i, false);
		    		j++;
		    	}else if((char)c == '1') {
		    		this.bitsObjects[i].set(j, true);
		    		this.bitsAttributes[j].set(i, true);
		    		j++;
		    	}else if((char)c == '\n') {
		    		i++;
		    	}
		    }
		 }catch (IOException e) {
			 System.err.format("IOException: %s%n", e);
		 }

	}
	
	public void readDyadicContext() {
		Scanner sc = new Scanner(System.in);
		int bit = 0;
		System.out.println("reading gmb...");
		for (int i = 0; i < this.objects; i++) {
			this.setObjects.add(i);
			for (int j = 0; j < this.attributes; j++) {
				this.setAttributes.add(j);
				System.out.print("object " + i + " attribute " + j);
				bit = sc.nextInt();
				if (bit == 0) {
					this.bitsObjects[i].set(j, false);
					this.bitsAttributes[j].set(i, false);
				}
				else {
					this.bitsObjects[i].set(j, true);
					this.bitsAttributes[j].set(i, true);
				}

			}
			System.out.println();
		}
		sc.close();
	}
	
	public void printGBContext() {
//		System.out.println("Dyadic context.\n" + this.objects + " objects, " + this.attributes + " attributes ");
		for (int i = 0; i < this.objects; i++) {
			for (int j = 0; j < this.attributes; j++) {
				if (j % this.attributes == 0)
					System.out.print(" ");
				System.out.print(this.bitsObjects[i].get(j) ? "1" : "0");
			}
			System.out.println();
		}
	}
	
	public void printBGContext() {
		System.out.println("Dyadic context.\n" + this.objects + " objects, " + this.attributes + " attributes ");
		for (int i = 0; i < this.attributes; i++) {
			for (int j = 0; j < this.objects; j++) {
				if (j % this.objects == 0)
					System.out.print(" ");
				System.out.print(this.bitsAttributes[i].get(j) ? "1" : "0");
			}
			System.out.println();
		}
	}

	public Set<Integer> getSetObjects() {
		return setObjects;
	}

	public void setSetObjects(Set<Integer> setObjects) {
		this.setObjects = setObjects;
	}

	public Set<Integer> getSetAttributes() {
		return setAttributes;
	}

	public void setSetAttributes(Set<Integer> setAttributes) {
		this.setAttributes = setAttributes;
	}
	
	
}
