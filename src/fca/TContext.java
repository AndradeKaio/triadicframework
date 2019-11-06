package fca;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import util.Pair;
import util.Triple;


public class TContext {

	private int objects;
	private int attributes;
	private int conditions;
	
	
	private int scaleGMB;
	private int scaleMGB;
	private int scaleBGM;

	private BitSet[] gmb;
	private BitSet[] mgb;
	private BitSet[] bgm;
	
	
	private ArrayList<String> objectsList;
	private ArrayList<String> attributesList;
	private ArrayList<String> conditionsList;
	
	
	
	public TContext(int objects, int attributes, int conditions) {
		this.objects = objects;
		this.attributes = attributes;
		this.conditions = conditions;
		
		this.objectsList = new ArrayList<String>();
		this.attributesList = new ArrayList<String>();
		this.conditionsList = new ArrayList<String>();
		
		
		this.scaleGMB = this.attributes * this.conditions;

		this.gmb = new BitSet[this.objects];
		
		for (int i = 0; i < this.objects; i++) {
			this.gmb[i] = new BitSet(this.scaleGMB);
		}

	}
	

	public void query3D(Set<Integer> objects, Set<Integer> attributes, Set<Integer> conditions) {
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConceptsDim1 = query1D(objects, 0);
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConceptsDim2 = query1D(attributes, 1);
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConceptsDim3 = query1D(conditions, 2);
		
		HashSet<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> result = new HashSet<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		//ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> dim1InterDim2 = query2D(objects, attributes, 0, 1);
		
		for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> triple : triConceptsDim3) {
			HashSet<Integer> inter = new HashSet<Integer>(triple.getModus());
			inter.retainAll(conditions);
			if (inter.size() > 0) {
				result.add(triple);
			}
		}
		
	}
	
	/**
	 * |DEBUG PUPORSE|
	 * This methods receives two sets of elements and two integers as the dimension flag.
	 * Calls the query1D two times passing each set as the dimension elements. Get the results and calculate
	 * the intersection between every element of the triconcepts. If two triconcepts has intersection in
	 * both dimensions (dim1, dim2) he's added as one of the 2d query result.
	 * @param firstDimString - first dimension elements
	 * @param secondDimString - second dimension elements 
	 * @param dim1 - first dimension flag
	 * @param dim2 - second dimension flag
	 */
	
	public ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>> query2D(HashSet<Integer> firstDim, HashSet<Integer> secondDim, int dim1, int dim2) {
	
		
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConcepts1, triConcepts2;
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> result = new ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		triConcepts1 = query1D(firstDim, dim1);
		triConcepts2 = query1D(secondDim, dim2);
		
		
				
		boolean flag1 = false, flag2 = false;
		
		
		
		for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> triple1 : triConcepts1) {
				
			for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> triple2 : triConcepts2) {

				if (dim1 == 0 || dim2 == 0) {
					Set<Integer> inter = new HashSet<Integer>(triple1.getExtent());
					inter.retainAll(triple2.getExtent());
					if (inter.size() > 0) {
						if (dim1 == 0) {
							flag1 = true;
						}else {
							flag2 = true;
						}
					}
				}
				if (dim1 == 1 || dim2 == 1) {
					Set<Integer> inter = new HashSet<Integer>(triple1.getIntent());
					inter.retainAll(triple2.getIntent());
					if (inter.size() > 0) {
						if (dim1 == 1) {
							flag1 = true;
						}else {
							flag2 = true;
						}
					}
				}
				if (dim1 == 2 || dim2 == 2){
					Set<Integer> inter = new HashSet<Integer>(triple1.getModus());
					inter.retainAll(triple2.getModus());
					if (inter.size() > 0){
						if (dim1 == 2) {
							flag1 = true;
						}else {
							flag2 = true;
						}
					}
				}
				if (flag1 && flag2) {
					result.add(triple1);
					result.add(triple2);
				}	
			}
		}
		return result;
	}
	
	/**
	 * Receives a integer set of elements and a int to indicate the dimension.
	 * Apply the one dimensional query process (X'',X') to approximate the concept.
	 * @param elements - elements of the dimension to query
	 * @param dim - flag to indicates the dimension
	 */
	public ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> query1D(Set<Integer> elements, int dim) {
		
		assert(dim <= 3);
		
		List<Pair<Set<Integer>, Set<Integer>>> firstDerivateList;
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triconcepts = new ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		
		ArrayList<Set<Integer>> sets = new ArrayList<Set<Integer>>();
		Set<Integer> aux;
		if (dim == 0) {

			//firt derivate of the elements set
			firstDerivateList = this.factorization(this.primeObjects(elements));
			
			for (Pair<Set<Integer>, Set<Integer>> concept : firstDerivateList) {
				aux = this.primeAttributesConditions(concept.getLeft(), concept.getRight());
				sets.add(aux);
				triconcepts.add(new Triple<Set<Integer>, Set<Integer>, Set<Integer>>(aux, concept.getLeft(), concept.getRight()));
			}
		}
		else if (dim == 1) {
			//firt derivate of the elements set
			firstDerivateList = this.factorization(this.primeAttributes(elements));
			
			for (Pair<Set<Integer>, Set<Integer>> concept : firstDerivateList) {
				//second derivate of pairs
				aux = this.primeObjectsConditions(concept.getLeft(), concept.getRight());
				//save the sets for post processing
				sets.add(aux);
				triconcepts.add(new Triple<Set<Integer>, Set<Integer>, Set<Integer>>(concept.getLeft(), aux, concept.getRight()));
			}
		}
		else {
			//firt derivate of the elements set
			firstDerivateList = this.factorization(this.primeConditions(elements));

			for (Pair<Set<Integer>, Set<Integer>> concept : firstDerivateList) {
				//second derivate of pairs
				aux = this.primeObjectsAttributes(concept.getLeft(), concept.getRight());
				//save the sets for post processing
				sets.add(aux);
				triconcepts.add(new Triple<Set<Integer>, Set<Integer>, Set<Integer>>(concept.getLeft(), concept.getRight(), aux));
				
			}
		}
		
		Boolean[] array = new Boolean[sets.size()];
		Arrays.fill(array, Boolean.TRUE);
		
		int sextent = 0, scurrent = 0;
		
		Set<Integer> intersection, setextent, setcurrent;
		
		for (int current = 0; current < sets.size(); current++) {
			for (int extent = current+1; extent < sets.size(); extent++) {

				if (array[current] == true && array[extent] == true) {
					
					setcurrent = sets.get(current);
					setextent = sets.get(extent);
					
					scurrent = setcurrent.size();
					sextent = setextent.size();

					if (scurrent != sextent) {
						
						intersection = new HashSet<Integer>(setcurrent);
						
						intersection.retainAll(setextent);
						
						
						if (intersection.size() < sextent)
							array[extent] = false;
						else 
							array[current] = false;
					}
				}
			}
		}

		
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConcepts = new ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		for (int i = 0; i < array.length; i++)
			if (array[i])
				triConcepts.add(triconcepts.get(i));

		return triConcepts;
	}
	
	/* PRIMES */


	/**
	 * @param objects list of objects to derivate
	 * @return attrCond list of pars resulted of the derivation of list of objects
	 */
	public Pair<ArrayList<Integer>, ArrayList<Integer>> primeObjects(Set<Integer> objects) {

		if (objects.size() > this.objects)
			System.out.println("Number of objects exceeded!");

		
		ArrayList<Integer> attri = new ArrayList<Integer>();
		ArrayList<Integer> condi = new ArrayList<Integer>();
		
		BitSet b = new BitSet(this.scaleGMB);
		b.set(0, this.scaleGMB);
		for (int object : objects)
			b.and(this.gmb[object]);

		int att, cond = 0;
		for (int i = 0; i < this.scaleGMB; i++) {
			if (b.get(i)) {
				att = i % this.attributes;
				cond = i / this.attributes;
				attri.add(att);
				condi.add(cond);
			}
		}

		return new Pair<ArrayList<Integer>, ArrayList<Integer>>(attri, condi);
	}


	/**
	 * prime attributes operate on MxGxB context
	 * @param attributes list of attributes to derivate
	 * @return objCond list of pairs (m, b) resulted of the derivation of the list of attributes
	 */
	public  Pair<ArrayList<Integer>, ArrayList<Integer>> primeAttributes(Set<Integer> attributes) {
		if (attributes.size() > this.attributes)
			System.out.println("Number of attributes exceeded!");
		
		if (this.mgb == null)
			this.generateMGBContext();

		ArrayList<Integer> obj_ = new ArrayList<Integer>();
		ArrayList<Integer> cond_ = new ArrayList<Integer>();


		BitSet b = new BitSet(this.scaleMGB);
		b.set(0, this.scaleMGB);

		for (int attribute : attributes)
			b.and(this.mgb[attribute]);

		int obj, cond = 0;
		for (int i = 0; i < this.scaleMGB; i++) {
			if (b.get(i)) {
				obj = i % this.objects;
				cond = i / this.objects;
				obj_.add(obj);
				cond_.add(cond);
			}
		}
		return new Pair<ArrayList<Integer>, ArrayList<Integer>>(obj_, cond_);
	}


	public Pair<ArrayList<Integer>, ArrayList<Integer>> primeConditions(Set<Integer> conditions) {
		
		if (conditions.size() > this.attributes)
			System.out.println("Number of attributes exceeded!");
		
		if (this.bgm == null)
			this.generateBGMContext();


		ArrayList<Integer> obj_ = new ArrayList<Integer>();
		ArrayList<Integer> attr_ = new ArrayList<Integer>();

		BitSet b = new BitSet(this.scaleBGM);
		b.set(0, this.scaleBGM);
		
		for (int condition : conditions)
			b.and(this.bgm[condition]);

		int obj, attr = 0;
		for (int i = 0; i < this.scaleBGM; i++) {
			if (b.get(i)) {
				obj = i % this.objects;
				attr = i / this.objects;
				
				obj_.add(obj);
				attr_.add(attr);
			}
		}
		
		return new Pair<ArrayList<Integer>, ArrayList<Integer>>(obj_, attr_);

	}


	/**
	 * prime objects and attributes operate on GxMxB context
	 * @param objects 		lista de objetos para derivacao
	 * @param attributes  	lista de atributos para derivacao
	 * @return conditions	lista de condicoes resultante da derivacao do par (g, m)
	 */
	public Set<Integer> primeObjectsAttributes(Set<Integer> objects, Set<Integer> attributes) {
		
		
		if (objects.size() > this.objects || attributes.size() > this.attributes)
			System.err.println("Number of attributes or objects exceeded!");


		Set<Integer> conditions = new HashSet<Integer>();

		BitSet b = new BitSet(this.scaleGMB);
		b.set(0, this.scaleGMB);

		for (int object : objects)
			b.and(this.gmb[object]);
		
		boolean flag = true;
		for (int x = 0; x<this.conditions; x++) {
			for(int attribute : attributes) {
				if (!(b.get(attribute + (this.attributes * x ))))
					flag = false;
			}
			if (flag)
				conditions.add(x);
			flag = true;
		}

		return conditions;

	}
	
	/**
	 * This method operates on BxGxM context
	 * @param objects		set of objects to derivate
	 * @param conditions	set of conditions to derivate
	 * @return attributes	set of attributes resulted of the derivation of the par (g, b)
	 */
	public Set<Integer> primeObjectsConditions(Set<Integer> objects, Set<Integer> conditions) {
		
		if (objects.size() > this.objects || conditions.size() > this.conditions)
			System.err.println("Number of objects or conditions exceeded!");
		
		//check if the contexts already exists
		if (this.bgm == null)
			this.generateBGMContext();
		//result
		Set<Integer> attributes = new HashSet<Integer>();

		//make the and operate
		BitSet b = new BitSet(this.scaleBGM);
		b.set(0, this.scaleBGM);
		for (int condition : conditions)
			b.and(this.bgm[condition]);
		
		//check the and result 
		boolean flag = true;
		for (int x = 0; x<this.attributes; x++) {
			for(int object : objects) {
//				System.out.println(object + (this.objects * x ));
				if (!(b.get(object + (this.objects * x ))))
					flag = false;
			}
			if (flag)
				attributes.add(x);
			flag = true;
		}

		return attributes;
	}

	/**
	 * This method operates on MxGxB context
	 * @param attributes	list of attributes to derivate
	 * @param conditions	list of conditions to derivate
	 * @return objects		list of objects resulted of the derivation of the pair (m,b)
	 */
	public Set<Integer> primeAttributesConditions(Set<Integer>  attributes, Set<Integer>  conditions) {
		if (attributes.size() > this.attributes || conditions.size() > this.conditions)
			System.err.println("Number of attributes or conditions exceeded!");
		
		
		if (this.mgb == null)
			this.generateMGBContext();
		//result
		Set<Integer> objects = new HashSet<Integer>();

		//make the and operate
		BitSet b = new BitSet(this.scaleMGB);
		b.set(0, this.scaleMGB);
		for (int attribute : attributes)
			b.and(this.mgb[attribute]);
		
		//check the and result 
		boolean flag = true;
		for (int x = 0; x<this.objects; x++) {
			for(int condition : conditions) 
				if (!(b.get(x + (this.objects * condition ))))
					flag = false;
			if (flag)
				objects.add(x);
			flag = true;
		}

		return objects;
	}


	
	private List<Pair<Set<Integer>, Set<Integer>>> factorization(Pair<ArrayList<Integer>, ArrayList<Integer>> pair) {

		//number of incidences
		int nObjects    = pair.getLeft().size();
		//aux sets
		Set<Integer> objects = new HashSet<Integer>();
		Set<Integer> attributes = new HashSet<Integer>();
		//create a pair structure
		
		List<Pair<Integer, Integer>> incidences = new ArrayList<Pair<Integer,Integer>>();
		int obj = 0;
		int att = 0;
		//add each incidence to the incidences list
		for (int i = 0; i < nObjects; i++) {
			obj = pair.getLeft().get(i);
			att = pair.getRight().get(i);
			
			incidences.add(new Pair<Integer, Integer>(obj, att));
			
			objects.add(obj);
			attributes.add(att);
		}
		
		//save the relation of each element
		ArrayList<Integer> objectsArrayList = new ArrayList<Integer>();
		ArrayList<Integer> attributesArrayList = new ArrayList<Integer>();
		
		objectsArrayList.addAll(objects);
		attributesArrayList.addAll(attributes);
		
		//create a dyadic context using the index of each element in the arraylist
		DyadicContext cxt = new DyadicContext(objectsArrayList, attributesArrayList, incidences);
		//get all the concepts (next closure)
		List<Pair<Set<Integer>, Set<Integer>>> concepts = cxt.allConcepts();
		//aux structure to get the triadic elements back
		List<Pair<Set<Integer>, Set<Integer>>> finalPair = new ArrayList<Pair<Set<Integer>,Set<Integer>>>(); 
		
		//for each concept generated
		for (Pair<Set<Integer>, Set<Integer>> concept : concepts) {
			Set<Integer> left = new HashSet<Integer>();
			Set<Integer> right = new HashSet<Integer>();
			for (Integer element : concept.getLeft()) {
				left.add(objectsArrayList.get(element));
			}
			for (Integer element : concept.getRight()) {
				right.add(attributesArrayList.get(element));
			}
			finalPair.add(new Pair(left, right));
		}
		//for GC purposes set all to null
		cxt = null;
		objects  = attributes = null;
		objectsArrayList = attributesArrayList = null;
		incidences = null;
		concepts = null;
		

		return finalPair;
	}
	
	

	/*READERS */

	/**
	 * Read the gmb by incidences file.
	 * 
	 * @return boolean
	 * @throws IOException
	 */
	public void readGMBContextBinary(String filename){
		int i = 0, j = 0;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
			int c;
		    while ((c = br.read()) != -1) {
//		    	System.out.println("i = " +  i + " j = " + j);
		    	if (j % this.scaleGMB == 0)
		    		j = 0;
		    	if((char)c == '0') {
		    		this.gmb[i].set(j, false);
		    		j++;
		    	}else if((char)c == '1') {
		    		this.gmb[i].set(j, true);
		    		j++;
		    	}else if((char)c == '\n') {
		    		i++;
		    	}
		    }
		 }catch (IOException e) {
			 System.err.format("IOException: %s%n", e);
		 }
	}
	
	
	
	public void readContext(String fileName) {
		int i = 0, j = 0, flag = 0;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
			int c;
			
			while(flag < 3) {
				c = br.read();
				if ((char) c == '\n') {
					flag++;
					continue;
				}
				if (flag == 0) {
					this.objectsList.add(String.valueOf((char)c));
				}
				else if (flag == 1) {
					this.attributesList.add(String.valueOf((char)c));
				}
				else {
					this.conditionsList.add(String.valueOf((char)c));
				}
			}

		    while ((c = br.read()) != -1) {
//		    	System.out.println("i = " +  i + " j = " + j);
		    	if (j % this.scaleGMB == 0)
		    		j = 0;
		    	if((char)c == '0') {
		    		this.gmb[i].set(j, false);
		    		j++;
		    	}else if((char)c == '1') {
		    		this.gmb[i].set(j, true);
		    		j++;
		    	}else if((char)c == '\n') {
		    		i++;flag++;
		    	}
		    }
		 }catch (IOException e) {
			 System.err.format("IOException: %s%n", e);
		 }
	}

	/**
	 * Read the gmb from terminal
	 * 
	 * @return boolean
	 */
	public void readContext() {
		Scanner sc = new Scanner(System.in);
		int bit = 0;
		System.out.println("reading gmb...");
		for (int i = 0; i < this.objects; i++) {
			for (int j = 0; j < this.scaleGMB; j++) {
				System.out.print("object " + i + " attribute " + ((j) % this.conditions) + " condition "
						+ ((j) / this.conditions) + " = ");
				bit = sc.nextInt();
				if (bit == 0)
					this.gmb[i].set(j, false);
				else
					this.gmb[i].set(j, true);

			}
			System.out.println();
		}
		sc.close();
	}
	
	
	/* SCALE */
	
	public void generateMGBContext() {
		
		//create the MGB context with |M| rows
		this.mgb = new BitSet[this.attributes];
		this.scaleMGB = this.objects * this.conditions;
		
		//create |G| x |B| rows
		for(int i = 0; i<this.attributes; i++)
			this.mgb[i] = new BitSet(this.scaleMGB);
		
		for(int i = 0; i<this.objects; i++) 
			for(int j =0; j<this.scaleGMB; j++)
				this.mgb[j % this.attributes].set((i + (j/this.attributes)*this.objects), gmb[i].get(j) ? true : false );			
		
	}
	
	public void generateBGMContext() {
		
		//create the BGM context with |B| rows
		this.bgm = new BitSet[this.conditions];
		this.scaleBGM = this.objects * this.attributes;
		
		//create |G| x |M| rows
		for(int i = 0; i<this.conditions; i++)
			this.bgm[i] = new BitSet(this.scaleBGM);
		
		for(int i = 0; i<this.objects; i++) 
			for(int j =0; j<this.scaleGMB; j++) {
				//System.out.println("i = " +  j / this.attributes + " j = " + (((j%this.attributes)*this.objects) + i));
				//i = j/atributo
				//j=(((j%atributo)*objetos)+i)
				
				this.bgm[j / this.attributes].set((((j%this.attributes)*this.objects) + i), gmb[i].get(j) ? true : false );
			}

	}
	
		
	/* PRINTS */
	/**
	 * |DEBUG PURPOSE|
	 * Receives a integer set of elements and a int to indicate the dimension.
	 * Apply the one dimensional query process (X'',X').
	 * @param elements - elements of the dimension to query
	 * @param dim - flag to indicates the dimension
	 */
	public void printQuery1D(Set<String> stringElements, int dim) {
		System.out.println("QUERY1D("+stringElements+") = ");
		assert(dim <= 3);
		
		Set<Integer> elements = this.stringToInteger(stringElements, dim);
		
		List<Pair<Set<Integer>, Set<Integer>>> firstDerivateList;		
		
		if (dim == 0) {
			firstDerivateList = this.factorization(this.primeObjects(elements));
			for (Pair<Set<Integer>, Set<Integer>> concept : firstDerivateList) {
				
				System.out.println("{"+this.integerToString(concept.getLeft(), 1)+","+
				this.integerToString(concept.getRight(), 2)+","+this.integerToString(this.primeAttributesConditions(concept.getLeft(), concept.getRight()), 0));
			}
		}
		else if (dim == 1) {
			firstDerivateList = this.factorization(this.primeAttributes(elements));
			for (Pair<Set<Integer>, Set<Integer>> concept : firstDerivateList) {
				System.out.println("{"+this.integerToString(concept.getLeft(), 0)+","+
						this.integerToString(concept.getRight(), 2)+","+this.integerToString(this.primeObjectsConditions(concept.getLeft(), concept.getRight()), 1));
			}
		}
		else {
			firstDerivateList = this.factorization(this.primeConditions(elements));
			for (Pair<Set<Integer>, Set<Integer>> concept : firstDerivateList) {
				System.out.println("{"+this.integerToString(concept.getLeft(), 0)+","+
						this.integerToString(concept.getRight(), 1)+","+this.integerToString(this.primeObjectsAttributes(concept.getLeft(), concept.getRight()), 2));
			}
		}
		System.out.println("------------------------------------------------------");
	}
	
	
public void printQuery2D(HashSet<String> firstDimString, HashSet<String> secondDimString, int dim1, int dim2) {
		
		
		HashSet<Integer> firstDim  = (HashSet<Integer>)this.stringToInteger(firstDimString, dim1);
		HashSet<Integer> secondDim = (HashSet<Integer>)this.stringToInteger(secondDimString, dim2);
		
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConcepts1, triConcepts2, result;
		
		triConcepts1 = query1D(firstDim, dim1);
		triConcepts2 = query1D(secondDim, dim2);
		
		System.out.println(triConcepts1);
		boolean flag1 = false, flag2 = false;
		
		result = new ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		ArrayList<HashSet<String>> aux = new ArrayList<HashSet<String>>(Collections.nCopies(3, new HashSet<String>()));

		aux.set(dim1, (HashSet<String>) this.integerToString(firstDim, dim1));
		aux.set(dim2, (HashSet<String>) this.integerToString(secondDim, dim2));
		
		System.out.println("QUERY2D("+aux+")=");
		
		
		for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> triple1 : triConcepts1) {
				
			for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> triple2 : triConcepts2) {

				if (dim1 == 0 || dim2 == 0) {
					Set<Integer> inter = new HashSet<Integer>(triple1.getExtent());
					inter.retainAll(triple2.getExtent());
					if (inter.size() > 0) {
						if (dim1 == 0) {
							flag1 = true;
						}else {
							flag2 = true;
						}
					}
				}
				
				if (dim1 == 1 || dim2 == 1) {
					Set<Integer> inter = new HashSet<Integer>(triple1.getIntent());
					inter.retainAll(triple2.getIntent());
					if (inter.size() > 0) {
						if (dim1 == 1) {
							flag1 = true;
						}else {
							flag2 = true;
						}
					}
				}
				if (dim1 == 2 || dim2 == 2){
					Set<Integer> inter = new HashSet<Integer>(triple1.getModus());
					inter.retainAll(triple2.getModus());
					if (inter.size() > 0){
						if (dim1 == 2) {
							flag1 = true;
						}else {
							flag2 = true;
						}
					}
				}

				if (flag1 && flag2) {

					if (!result.contains(triple1)) {
						System.out.println("{"+this.integerToString(triple1.getExtent(), 0)+","+this.integerToString(triple1.getIntent(), 1)+","+this.integerToString(triple1.getModus(), 2)+"}");
						result.add(triple1);
					}
					if (!result.contains(triple2)) {
						System.out.println("{"+this.integerToString(triple2.getExtent(), 0)+","+this.integerToString(triple2.getIntent(), 1)+","+this.integerToString(triple2.getModus(), 2)+"}");
						result.add(triple2);
					}
				}	
			}
		}		
	}
	
	
	
	
	
	/**
	 * Receives a list of objects strings and apply the prime objects operator and print the output.
	 * @param objects
	 */
	public void printPrimeObjects(HashSet<String> objects) {
		System.out.println("prime objects("+objects+")=");
		Pair<ArrayList<Integer>, ArrayList<Integer>> result = this.primeObjects(this.stringToInteger(objects, 0));
		
		ArrayList<String> attributeList = this.getAttributesList();
		ArrayList<String> conditionsList = this.getConditionsList();
		int lIndex, rIndex = 0;
		for (int i = 0; i < result.getLeft().size(); i++) {
			lIndex = result.getLeft().get(i);
			rIndex = result.getRight().get(i);
			System.out.println("("+attributeList.get(lIndex)+","+conditionsList.get(rIndex)+")");
		}
		System.out.println();
		attributeList = null;
		conditionsList = null;
		result = null;
		System.out.println("------------------------------------------------------");
	}
	/**
	 * Receives a list of attributes strings and apply the prime attributes operator and print the output.
	 * @param attributes
	 */
	public void printPrimeAttributes(HashSet<String> attributes) {
		System.out.println("prime attributes("+attributes+")=");
		Pair<ArrayList<Integer>, ArrayList<Integer>> result = this.primeAttributes(this.stringToInteger(attributes, 1));
		
		ArrayList<String> objectsList = this.getObjectsList();
		ArrayList<String> conditionsList = this.getConditionsList();
		int lIndex, rIndex = 0;
		for (int i = 0; i < result.getLeft().size(); i++) {
			lIndex = result.getLeft().get(i);
			rIndex = result.getRight().get(i);
			System.out.println("("+objectsList.get(lIndex)+","+conditionsList.get(rIndex)+")");
		}
		System.out.println();
		objectsList = null;
		conditionsList = null;
		result = null;
		System.out.println("------------------------------------------------------");
	}
	/**
	 * Receives a list of conditions strings and apply the prime conditions operator and print the output.
	 * @param conditions
	 */
	public void printPrimeConditions(HashSet<String> conditions) {
		System.out.println("prime conditions("+conditions+")=");
		Pair<ArrayList<Integer>, ArrayList<Integer>> result = this.primeConditions(this.stringToInteger(conditions, 2));
		
		ArrayList<String> objectsList = this.getObjectsList();
		ArrayList<String> attributesList = this.getAttributesList();
		int lIndex, rIndex = 0;
		for (int i = 0; i < result.getLeft().size(); i++) {
			lIndex = result.getLeft().get(i);
			rIndex = result.getRight().get(i);
			System.out.println("("+objectsList.get(lIndex)+","+attributesList.get(rIndex)+")");
		}
		System.out.println();
		objectsList = null;
		attributesList = null;
		result = null;
	}

	public static void printBits(BitSet b) {
		System.out.print(" ");
		for (int i = 0; i < 8; i++) {
			System.out.print(b.get(i) ? "1" : "0");
		}
		System.out.println();
	}

	public void printGMBContext() {
		System.out.println("Triadic gmb.\n" + this.objects + " objects, " + this.attributes + " attributes "
				+ this.conditions + " conditions.");
		for (int i = 0; i < this.objects; i++) {
			for (int j = 0; j < this.scaleGMB; j++) {
				if (j % this.attributes == 0)
					System.out.print(" ");
				System.out.print(gmb[i].get(j) ? "1" : "0");
			}
			System.out.println();
		}
	}
	
	public void printMGBContext() {
		System.out.println("Triadic mgb.\n" + this.attributes + " objects, " + this.objects + " attributes "
				+ this.conditions + " conditions.");
		for (int i = 0; i < this.attributes; i++) {
			for (int j = 0; j < this.scaleMGB; j++) {
				if (j % this.objects == 0)
					System.out.print(" ");
				System.out.print(this.mgb[i].get(j) ? "1" : "0");
			}
			System.out.println();
		}
	}
	
	public void printBGMContext() {
		System.out.println("Triadic bgm.\n" + this.conditions + " objects, " + this.objects + " attributes "
				+ this.attributes + " conditions.");
		for (int i = 0; i < this.conditions; i++) {
			for (int j = 0; j < this.scaleBGM; j++) {
				if (j % this.objects == 0)
					System.out.print(" ");
				System.out.print(this.bgm[i].get(j) ? "1" : "0");
			}
			System.out.println();
		}
	}
	
	/* UTIL */
	/**
	 * Receive a set of integer elements of some dimension
	 * and return a set of strings with labels of each element
	 * @param elements - set of integer
	 * @param dim - flag dimension
	 * @return Set<String> 
	 */
	public Set<String> integerToString(Set<Integer> elements, int dim){
		Set<String> a = new HashSet<String>();
		
		List<String> b;
		
		if (dim == 0 && elements.size() <= this.objects)
			b = this.getObjectsList();
		else if (dim == 1 && elements.size() <= this.attributes)
			b = this.getAttributesList();
		else if (dim == 2 && elements.size() <= this.conditions)
			b = this.getConditionsList();
		else {
			System.err.format("Dimensions incompatible!");
			return null;
		}
		
		for (Integer element : elements) {
			a.add(b.get(element));
		}

		return a;
	}

	
	public Set<Integer> stringToInteger(Set<String> elements, int dim){
		Set<Integer> a = new HashSet<Integer>();
		
		List<String> b;
		
		if (dim == 0 && elements.size() <= this.objects)
			b = this.getObjectsList();
		else if (dim == 1 && elements.size() <= this.attributes)
			b = this.getAttributesList();
		else if (dim == 2 && elements.size() <= this.conditions)
			b = this.getConditionsList();
		else {
			System.err.format("Dimensions incompatible!");
			return null;
		}
		
		for (String element : elements) {
			if (b.contains(element))
				a.add(b.indexOf(element));
			else {
				System.err.format("This element don't belong to the context!");
				return null;
			}
		}
		return a;
	}
	
	/* GETTERS AND SETTERS */


	public int getObjects() {
		return objects;
	}

	public int getAttributes() {
		return attributes;
	}

	public int getConditions() {
		return conditions;
	}

	public void setObjects(int objects) {
		this.objects = objects;
	}

	public void setAttributes(int attributes) {
		this.attributes = attributes;
	}

	public void setConditions(int conditions) {
		this.conditions = conditions;
	}
	/**
	 * @return the objectsList
	 */
	public ArrayList<String> getObjectsList() {
		return objectsList;
	}

	/**
	 * @param objectsList the objectsList to set
	 */
	public void setObjectsList(ArrayList<String> objectsList) {
		this.objectsList = objectsList;
	}


	/**
	 * @return the attributesList
	 */
	public ArrayList<String> getAttributesList() {
		return attributesList;
	}

	/**
	 * @param attributesList the attributesList to set
	 */
	public void setAttributesList(ArrayList<String> attributesList) {
		this.attributesList = attributesList;
	}

	/**
	 * @return the conditionsList
	 */
	public ArrayList<String> getConditionsList() {
		return conditionsList;
	}

	/**
	 * @param conditionsList the conditionsList to set
	 */
	public void setConditionsList(ArrayList<String> conditionsList) {
		this.conditionsList = conditionsList;
	}

}
