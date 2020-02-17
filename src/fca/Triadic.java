package fca;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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


public class Triadic {

	
	public boolean isLatticeBuilt;
	
	
	private int objects;
	private int attributes;
	private int conditions;
	
	
	private int scaleGMB;
	private int scaleMGB;
	private int scaleBGM;

	private BitSet[] gmb;
	private BitSet[] mgb;
	private BitSet[] bgm;
	
	
	private ArrayList<String> strObjectsList;
	private ArrayList<String> strAttributesList;
	private ArrayList<String> strConditionsList;
	
	private TriLattice lattice;
	
	public Triadic(int objects, int attributes, int conditions) {
		
		this.isLatticeBuilt = false;
		
		this.objects = objects;
		this.attributes = attributes;
		this.conditions = conditions;
		
		this.strObjectsList = new ArrayList<String>();
		this.strAttributesList = new ArrayList<String>();
		this.strConditionsList = new ArrayList<String>();
		
		
		this.scaleGMB = this.attributes * this.conditions;

		this.gmb = new BitSet[this.objects];
		
		for (int i = 0; i < this.objects; i++) {
			this.gmb[i] = new BitSet(this.scaleGMB);
		}

	}
	
	public void buildLattice(short dimRef, String fileName) {
		this.lattice = new TriLattice(dimRef, fileName, this.strObjectsList, this.strAttributesList, this.strConditionsList);
		if (lattice != null && isLatticeBuilt == false) {
			this.isLatticeBuilt = true;
			lattice.triadicIpred();
		}
	}
	
	
	public void query(HashSet<Integer> objects, HashSet<Integer> attributes, HashSet<Integer> conditions) {
	}
	
	/**
	 * 
	 * @param objects
	 * @param attributes
	 * @param conditions
	 * @return
	 */
	public HashSet<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> query3D(HashSet<Integer> objects, HashSet<Integer> attributes, HashSet<Integer> conditions) {

		HashSet<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> result = new HashSet<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> dim1InterDim2 = query2D(objects, attributes, 0, 1);
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConceptsDim3 = query1D(conditions, 2);

		
		HashSet<Integer> inter;
		for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> triple1 : dim1InterDim2) {
			for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> triple2 : triConceptsDim3) {
				inter = new HashSet<Integer>(triple1.getExtent());
				inter.retainAll(triple2.getExtent());
				if (!(inter.size() > 0))
					continue;
				inter = new HashSet<Integer>(triple1.getIntent());
				inter.retainAll(triple2.getIntent());
				if (!(inter.size() > 0))
					continue;

				inter = new HashSet<Integer>(triple1.getModus());
				inter.retainAll(triple2.getModus());
				if (!(inter.size() > 0))
					continue;
				else { 
					inter.retainAll(conditions);
					if (!(inter.size() > 0)) {
						continue;
					}
				}
	
				if (!result.contains(triple1)) {
					result.add(triple1);
				}
				if (!result.contains(triple2)) {
					result.add(triple2);
				}
			}
		}
		
		return result;
		
	}

	/**
	 * Rokia's approach
	 * @param firstDim
	 * @param secondDim
	 * @param dim1
	 * @param dim2
	 */
	public void printQuery2DNew(HashSet<Integer> firstDim, HashSet<Integer> secondDim, int dim1, int dim2) {
		
		assert(dim1 > dim2);
		
		HashSet<Integer> objects, attributes, conditions;
		if (dim1 == 0 && dim2 == 1) {
			conditions = (HashSet<Integer>) primeObjectsAttributes(firstDim, secondDim);
			objects = (HashSet<Integer>) primeAttributesConditions(secondDim, conditions);
			attributes = (HashSet<Integer>) primeObjectsConditions(objects, conditions);
		}else if (dim1 == 0 && dim2 == 2) {
			attributes = (HashSet<Integer>) primeObjectsConditions(firstDim, secondDim);
			objects = (HashSet<Integer>) primeAttributesConditions(attributes, secondDim);
			conditions = (HashSet<Integer>) primeObjectsAttributes(objects, attributes);
		}else {
			objects = (HashSet<Integer>) primeAttributesConditions(firstDim, secondDim);
			conditions = (HashSet<Integer>) primeObjectsAttributes(objects, firstDim);
			attributes = (HashSet<Integer>) primeAttributesConditions(objects, conditions);
		}
		Triple<HashSet<Integer>, HashSet<Integer>, HashSet<Integer>> triconcept = 
				new Triple<HashSet<Integer>, HashSet<Integer>, HashSet<Integer>>(objects, attributes, conditions);
		
		ArrayList<HashSet<String>> aux = new ArrayList<HashSet<String>>(Collections.nCopies(3, new HashSet<String>()));

		aux.set(dim1, (HashSet<String>) this.intToStr(firstDim, dim1));
		aux.set(dim2, (HashSet<String>) this.intToStr(secondDim, dim2));
		
		System.out.println("\nQUERY2D("+aux+")=\n");
		System.out.println("(Upper bound)");
		System.out.println("{"+this.intToStr(triconcept.getExtent(), 0)+","+ this.intToStr(triconcept.getIntent(), 1)+","+this.intToStr(triconcept.getModus(), 2)+"}");
		System.out.print("\n(Lower bound)");
//		ArrayList<HashSet<Integer>> lower = lowerBound1D(triconcept.getExtent(), (HashSet<Integer>)elements);

	}
	
	/**
	 * |DEPRECATED|
	 * This methods receives two sets of elements and two integers as the dimension offset.
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
				
				if (dim1 == 0)
				
				
				
				if (dim1 == 0 || dim2 == 0) {
					Set<Integer> inter = new HashSet<Integer>(triple1.getExtent());
					inter.retainAll(triple2.getExtent());
					if (dim1 == 0)
						inter.retainAll(firstDim);
					else
						inter.retainAll(secondDim);
					if (inter.size() > 0) {
						if (dim1 == 0) {
							inter.retainAll(firstDim);
							if (inter.size() > 0)
								flag1 = true;
						}else {
							inter.retainAll(secondDim);
							if (inter.size() > 0)
								flag2 = true;
						}
					}
				}
				if (dim1 == 1 || dim2 == 1) {
					Set<Integer> inter = new HashSet<Integer>(triple1.getIntent());
					inter.retainAll(triple2.getIntent());
					if (dim1 == 1)
						inter.retainAll(firstDim);
					else
						inter.retainAll(secondDim);
					if (inter.size() > 0) {
						if (dim1 == 1) {
							inter.retainAll(firstDim);
							if (inter.size() > 0)
								flag1 = true;
						}else {
							inter.retainAll(secondDim);
							if (inter.size() > 0)
								flag2 = true;
						}
					}
				}
				if (dim1 == 2 || dim2 == 2){
					Set<Integer> inter = new HashSet<Integer>(triple1.getModus());
					inter.retainAll(triple2.getModus());
					if (dim1 == 2)
						inter.retainAll(firstDim);
					else
						inter.retainAll(secondDim);
					if (inter.size() > 0){
						if (dim1 == 2) {
							inter.retainAll(firstDim);
							if (inter.size() > 0)
								flag1 = true;
						}else {
							inter.retainAll(secondDim);
							if (inter.size() > 0)
								flag2 = true;
						}
					}
				}
				if (flag1 && flag2) {
					if (!result.contains(triple1))
						result.add(triple1);
					if (!result.contains(triple2))
						result.add(triple2);
				}
				flag1 = flag2 = false;
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
		
		assert(dim < 3);
		
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
		
		Boolean[] array = immediateSuccessors(sets);

		
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triConceptsImmediateSuccessors = new ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		//get the elements of the dimension of reference to compute lower bounds
		HashSet<HashSet<Integer>> dimRefElements = new HashSet<HashSet<Integer>>();
		
		for (int i = 0; i < array.length; i++)
			if (array[i]) {
				triConceptsImmediateSuccessors.add(triconcepts.get(i));
				dimRefElements.add((HashSet<Integer>) triconcepts.get(i).getExtent());
			}
		
//		ArrayList<HashSet<Integer>> lower = lowerBound1D(dimRefElements, (HashSet<Integer>)elements);

		return triConceptsImmediateSuccessors;
	}
	
	/**
	 * 
	 * @param upperBound - Concepts of the Upper Bound set
	 * @param queryElements - Elements of the query
	 * @param dimRef - Dimension
	 * @return
	 */
	public HashSet<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> LowerBound1D (ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> upperBound, HashSet<Integer> queryElements, int dimRef){
		
		HashSet<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> lowerBound = null;
		

		
		if (this.isLatticeBuilt) {
			
			lowerBound = new HashSet<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
			HashSet<Integer> aux;
			
			for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> concept : upperBound) {
				
				//ASSUMING THAT THE LATTICE IS ORDER ALWAYS BY EXTENT (NEED TO BE CHANGE)
				HashSet<HashSet<Integer>> links = this.lattice.getLinks((HashSet<Integer>)concept.getExtent());
				
				//for each extent linked to the concept.extent
				for (HashSet<Integer> link : links) {
					
					//check the dimref of the query
					if (dimRef == 0) {
						aux = new HashSet<Integer>(link);
						aux.retainAll(queryElements);
						if (aux.size() > 0) {
							for (Pair<HashSet<Integer>, HashSet<Integer>> pair: this.lattice.getOxAC().get(link)) {
								lowerBound.add(new Triple<Set<Integer>, Set<Integer>, Set<Integer>>(link, pair.getLeft(), pair.getRight()));
							}
						}
					}else if (dimRef == 1) {
						
						for (Pair<HashSet<Integer>, HashSet<Integer>> pair: this.lattice.getOxAC().get(link)) {
							aux = new HashSet<Integer>(pair.getLeft());
							aux.retainAll(queryElements);
							if (aux.size() > 0) {
								lowerBound.add(new Triple<Set<Integer>, Set<Integer>, Set<Integer>>(link, pair.getLeft(), pair.getRight()));
							}
						}
						
					}else{
						for (Pair<HashSet<Integer>, HashSet<Integer>> pair: this.lattice.getOxAC().get(link)) {
							aux = new HashSet<Integer>(pair.getRight());
							aux.retainAll(queryElements);
							if (aux.size() > 0) {
								lowerBound.add(new Triple<Set<Integer>, Set<Integer>, Set<Integer>>(link, pair.getLeft(), pair.getRight()));
							}
						}
					}
				}
			}
		}else {
			System.out.println("ERROR: LATTICE WAS NOT BUILDED!");
		}
		return lowerBound;
	}
	
	
	/**
	 * 
	 * @param dimRefElements - Concepts dimension query
	 * @param queryElements - Query elements
	 * @return - 
	 */
	public ArrayList<HashSet<Integer>> lowerBound1D(HashSet<HashSet<Integer>> dimRefElements, HashSet<Integer> queryElements) {
		
		ArrayList<HashSet<Integer>> result = null;
		
		if (this.isLatticeBuilt) {
			
			result = new ArrayList<HashSet<Integer>>();
			HashSet<Integer> aux;
			
			for (HashSet<Integer> element: dimRefElements) {
				HashSet<HashSet<Integer>> links = this.lattice.getLinks(element); 
				if (links != null) {
					for (HashSet<Integer> link : links) {
						aux = new HashSet<Integer>(link);
						aux.retainAll(queryElements);
						if (aux.size() > 0)
							result.add(link);
					}
				}
			}
		}
		return result;
	}
	
	
	
	public Boolean[] immediateSuccessors(ArrayList<Set<Integer>> sets) {
		
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
		return array;
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
			System.out.println("Number of attributes exceded!");
		
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
		
		if (conditions.size() > this.conditions)
			System.out.println("Number of attributes exceded!");
		
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
	 * Read the context in the data-peeler (.data.out) format.
	 */
	public void readContextDataPeelerFormat(String fileName) {
		try {
			File f = new File(fileName);
			Scanner sc = new Scanner(f);

			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				
				String obj  = line.split(" ")[0];
				String attr = line.split(" ")[1];
				String cond = line.split(" ")[2];
				
				if (!this.strObjectsList.contains(obj))
					this.strObjectsList.add(obj);
				
				if (!this.strAttributesList.contains(attr))
					this.strAttributesList.add(attr);
				
				if (!this.strConditionsList.contains(cond))
					this.strConditionsList.add(cond);
				
				int indexAttr = this.strAttributesList.indexOf(attr);
				int indexCond = this.strConditionsList.indexOf(cond);
				
				int p = (this.attributes * (indexCond + 1)) - this.attributes + indexAttr;
				
				this.gmb[this.strObjectsList.indexOf(obj)].set(p, true);
				
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
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
		    	}else if((char)c == '\n' || (char)c == '\r') {
		    		i++;
		    	}
		    }
		 }catch (IOException e) {
			 System.err.format("IOException: %s%n", e);
		 }
	}
	
	public void readContext2(String fileName) {
		try {
            File f = new File(fileName);
            Scanner sc = new Scanner(f);

            if (sc.hasNextLine()) {
            	String line = sc.nextLine();
            	String [] elements = line.split(" ");
            	String [] objects = elements[0].split(",");
            	String [] attributes = elements[1].split(",");
            	String [] conditions = elements[2].split(",");
            	
            	this.strObjectsList.addAll(Arrays.asList(objects));
            	this.strAttributesList.addAll(Arrays.asList(attributes));
            	this.strConditionsList.addAll(Arrays.asList(conditions));

            }
            int x = 0, j = 0, lines = 0;
            char c;
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                for (int i = 0; i < line.length(); i++) {
                	c = line.charAt(i);
    		    	
                	if (c == '1')
                		this.gmb[lines].set(i, true);
                	else
						this.gmb[lines].set(i, false);
                }
                lines++;
            }
            sc.close();
        } catch (FileNotFoundException e) {         
            e.printStackTrace();
        }
	}
	
	public void readContext(String fileName) {
		int i = 0, j = 0, flag = 0;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
			int c;
			
			while(flag < 3) {
				c = br.read();

				if ((char) c == '\n' || (char)c == '\r') {
					flag++;
					continue;
				}
				if (flag == 0) {
					this.strObjectsList.add(String.valueOf((char)c));
				}
				else if (flag == 1) {
					System.out.println("oi");
					this.strAttributesList.add(String.valueOf((char)c));
				}
				else {
					this.strConditionsList.add(String.valueOf((char)c));
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
		    	}else if((char)c == '\n' || (char)c == '\r') {
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
		
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> triconcepts = new ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		Set<Integer> elements = this.strToInt(stringElements, dim);
		
		triconcepts = this.query1D(elements, dim);
		if (!triconcepts.isEmpty())
			for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> concept : triconcepts)
				System.out.println("{"+this.intToStr(concept.getExtent(), 0)+","+ this.intToStr(concept.getIntent(), 1)+","+this.intToStr(concept.getModus(), 2)+"}");
		else
			System.out.println("{}");
		
//		ArrayList<HashSet<Integer>> lower = lowerBound1D(dimRefElements, (HashSet<Integer>)elements);
		
	}
	
	public void printQuery2D(HashSet<String> firstDimString, HashSet<String> secondDimString, int dim1, int dim2) {
		
		
		HashSet<Integer> firstDim  = (HashSet<Integer>)this.strToInt(firstDimString, dim1);
		HashSet<Integer> secondDim = (HashSet<Integer>)this.strToInt(secondDimString, dim2);
		
		ArrayList<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> result = new ArrayList<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		
		ArrayList<HashSet<String>> aux = new ArrayList<HashSet<String>>(Collections.nCopies(3, new HashSet<String>()));

		aux.set(dim1, (HashSet<String>) this.intToStr(firstDim, dim1));
		aux.set(dim2, (HashSet<String>) this.intToStr(secondDim, dim2));
		
		System.out.println("QUERY2D("+aux+")=");
		
		
		result = query2D(firstDim, secondDim, dim1, dim2);
		
		if (!result.isEmpty())
			for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> concept : result)
				System.out.println("{"+this.intToStr(concept.getExtent(), 0)+","+ this.intToStr(concept.getIntent(), 1)+","+this.intToStr(concept.getModus(), 2)+"}");
		else
			System.out.println("{}");

	}

	
	public void printQuery3D(HashSet<String> objectsString, HashSet<String> attributesString, HashSet<String> conditionsString) {

		
		HashSet<Integer> objects = (HashSet<Integer>) this.strToInt(objectsString, 0);
		HashSet<Integer> attributes = (HashSet<Integer>) this.strToInt(attributesString, 1);
		HashSet<Integer> conditions = (HashSet<Integer>) this.strToInt(conditionsString, 2);
		HashSet<Triple<Set<Integer>, Set<Integer>, Set<Integer>>> result = new HashSet<Triple<Set<Integer>,Set<Integer>,Set<Integer>>>();
		result = query3D(objects, attributes, conditions);
		System.out.println("QUERY3D("+objectsString+", "+attributesString+", "+conditionsString+")=");
		
		if (!result.isEmpty())
			for (Triple<Set<Integer>, Set<Integer>, Set<Integer>> concept : result)
				System.out.println("{"+this.intToStr(concept.getExtent(), 0)+","+ this.intToStr(concept.getIntent(), 1)+","+this.intToStr(concept.getModus(), 2)+"}");
		else
			System.out.println("{}");
	}
	
	
	/**
	 * Receives a list of objects strings and apply the prime objects operator and print the output.
	 * @param objects
	 */
	public void printPrimeObjects(HashSet<String> objects) {
		System.out.println("prime objects("+objects+")=");
		Pair<ArrayList<Integer>, ArrayList<Integer>> result = this.primeObjects(this.strToInt(objects, 0));
		
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
		Pair<ArrayList<Integer>, ArrayList<Integer>> result = this.primeAttributes(this.strToInt(attributes, 1));
		
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
		Pair<ArrayList<Integer>, ArrayList<Integer>> result = this.primeConditions(this.strToInt(conditions, 2));
		
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
	public Set<String> intToStr(Set<Integer> elements, int dim){
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

	
	public Set<Integer> strToInt(Set<String> elements, int dim){
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

	public TriLattice getLattice() {
		return lattice;
	}

	public void setLattice(TriLattice lattice) {
		this.lattice = lattice;
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
		return strObjectsList;
	}

	/**
	 * @param objectsList the objectsList to set
	 */
	public void setObjectsList(ArrayList<String> objectsList) {
		this.strObjectsList = objectsList;
	}


	/**
	 * @return the attributesList
	 */
	public ArrayList<String> getAttributesList() {
		return strAttributesList;
	}

	/**
	 * @param attributesList the attributesList to set
	 */
	public void setAttributesList(ArrayList<String> attributesList) {
		this.strAttributesList = attributesList;
	}

	/**
	 * @return the conditionsList
	 */
	public ArrayList<String> getConditionsList() {
		return strConditionsList;
	}

	/**
	 * @param conditionsList the conditionsList to set
	 */
	public void setConditionsList(ArrayList<String> conditionsList) {
		this.strConditionsList = conditionsList;
	}

}
