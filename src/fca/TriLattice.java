package fca;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import util.Pair;
import util.Triple;

public class TriLattice {

	
	
	public static char emptySet = ';';
	
	
	private short dimRef;
	private ArrayList<String> strObjectsList;
	private ArrayList<String> strAttributesList;
	private ArrayList<String> strConditionsList;
	
	
	private ArrayList<HashSet<Integer>> extent;
	private ArrayList<HashSet<Integer>> intent;
	private ArrayList<HashSet<Integer>> modus;
	
	private Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> OxAC;
	private Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> AxOC;
	private Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> CxOA;
	
	private int nConcepts;
	
	private Map<HashSet<Integer>, HashSet<HashSet<Integer>>> links;
	
	public TriLattice() {
		this.strObjectsList = new ArrayList<String>();
		this.strAttributesList = new ArrayList<String>();
		this.strConditionsList = new ArrayList<String>();
		
		this.extent = new ArrayList<HashSet<Integer>>();
		this.intent = new ArrayList<HashSet<Integer>>();
		this.modus = new ArrayList<HashSet<Integer>>();
		this.links = new HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>>();
		
		this.OxAC = new HashMap<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>>();
		this.AxOC = new HashMap<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>>();
		this.CxOA = new HashMap<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>>();
		
		this.nConcepts = 0;
	}
	
	public TriLattice(short dimRef) {
		super();
		this.dimRef = dimRef;
	}
	
	public TriLattice(short dimRef, String fileName) {
		super();
		this.dimRef = dimRef;
		this.conceptReader(fileName);
	}
	
	public TriLattice(short dimRef, String fileName, ArrayList<String> strObjects, ArrayList<String> strAttributes, ArrayList<String> strConditions) {
		this.dimRef = dimRef;
		this.strObjectsList = strObjects;
		this.strAttributesList = strAttributes;
		this.strConditionsList = strConditions;
		this.extent = new ArrayList<HashSet<Integer>>();
		this.intent = new ArrayList<HashSet<Integer>>();
		this.modus = new ArrayList<HashSet<Integer>>();
		this.links = new HashMap<HashSet<Integer>, HashSet<HashSet<Integer>>>();
		
		if (dimRef == 0)
			this.OxAC = new HashMap<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>>();
		else if (dimRef == 1)
			this.AxOC = new HashMap<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>>();
		else
			this.CxOA = new HashMap<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>>();
		
		this.nConcepts = 0;
		this.conceptReaderContextLoaded(fileName);
	}
	
	/**
	 * Read the concept's file in datapeeler format.
	 * @param fileName
	 */
	public void conceptReaderContextLoaded(String fileName) {
		try {
            File f = new File(fileName);
            Scanner sc = new Scanner(f);
            HashSet<Integer> obj;
            HashSet<Integer> attr;
            HashSet<Integer> cond;
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] details = line.split(" ");
                
                obj = new HashSet<Integer>();
                attr = new HashSet<Integer>();
                cond = new HashSet<Integer>();
                for (int i = 0; i < details.length; i++) {
					String [] elements = details[i].split(",");
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].charAt(0) != this.emptySet) {
							if (i == 0)
								obj.add(strObjectsList.indexOf(elements[j]));
							else if (i == 1)
								attr.add(strAttributesList.indexOf(elements[j]));
							else if (i == 2)
								cond.add(strConditionsList.indexOf(elements[j]));
						}
					}
 				}
                
                if (this.dimRef == 0) {                	
                	if (OxAC.containsKey(obj))
                		OxAC.get(obj).add(new Pair<HashSet<Integer>, HashSet<Integer>>(attr, cond));
                	else {
                		ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>> attCond = new ArrayList<Pair<HashSet<Integer>,HashSet<Integer>>>();
                		attCond.add(new Pair<HashSet<Integer>, HashSet<Integer>>(attr, cond));
                		OxAC.put(obj, attCond);
                	}
                }else if (this.dimRef == 1) {
                	if (AxOC.containsKey(attr))
                		AxOC.get(attr).add(new Pair<HashSet<Integer>, HashSet<Integer>>(obj, cond));
                	else {
                		ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>> objCond = new ArrayList<Pair<HashSet<Integer>,HashSet<Integer>>>();
                		objCond.add(new Pair<HashSet<Integer>, HashSet<Integer>>(obj, cond));
                		AxOC.put(attr, objCond);
                	}                	
                }else {
                	if (CxOA.containsKey(cond))
                		CxOA.get(cond).add(new Pair<HashSet<Integer>, HashSet<Integer>>(obj, attr));
                	else {
                		ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>> objAttr = new ArrayList<Pair<HashSet<Integer>,HashSet<Integer>>>();
                		objAttr.add(new Pair<HashSet<Integer>, HashSet<Integer>>(obj, attr));
                		CxOA.put(cond, objAttr);
                	}                	
                }
                
                if (!this.extent.contains(obj))
                	this.extent.add(obj);
                if (!this.intent.contains(attr))
                	this.intent.add(attr);
                if (!this.modus.contains(cond))
                	this.modus.add(cond);
            }

            sc.close();
        } catch (FileNotFoundException e) {         
            e.printStackTrace();
        }
	}	
	
	/**
	 * DEPRECATED
	 * @param fileName
	 */
	public void conceptReader(String fileName) {
		try {
            File f = new File(fileName);
            Scanner sc = new Scanner(f);
            HashSet<Integer> obj;
            HashSet<Integer> attr;
            HashSet<Integer> cond;
            
            
            int nObjects = 0, nAttributes = 0, nConditions = 0;
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] details = line.split(" ");
                
                obj = new HashSet<Integer>();
                attr = new HashSet<Integer>();
                cond = new HashSet<Integer>();
                for (int i = 0; i < details.length; i++) {
					String [] elements = details[i].split(",");
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].charAt(0) != this.emptySet) {
							if (i == 0) {
								if (strObjectsList.contains(elements[j]))
									obj.add(strObjectsList.indexOf(elements[j]));
								else
									obj.add(nObjects++);
							}
							else if (i == 1) {
								if (strAttributesList.contains(elements[j]))
									attr.add(strAttributesList.indexOf(elements[j]));
								else
									attr.add(nAttributes++);
							}
							else if (i == 2) {
								if (strConditionsList.contains(elements[j]))
									cond.add(strConditionsList.indexOf(elements[j]));
								else
									cond.add(nConditions++);
							}
						}
					}
 				}
                if (!this.extent.contains(obj))
                	this.extent.add(obj);
                if (!this.intent.contains(attr))
                	this.intent.add(attr);
                if (!this.modus.contains(cond))
                	this.modus.add(cond);
            }

            sc.close();
        } catch (FileNotFoundException e) {         
            e.printStackTrace();
        }
	}
	
	
	/**
	 * Order all triadic concepts based on iPred algorithm.
	 * The dimRef class attribute specify which dimension will be used to order the trisets.
	 */
	public void triadicIpred() {
		Map<HashSet<Integer>, HashSet<Integer>> faces = new HashMap<HashSet<Integer>, HashSet<Integer>>();
		

		HashSet<HashSet<Integer>> candidates = new HashSet<HashSet<Integer>>(); 
		
		ArrayList<HashSet<Integer>> dimOrd;
		
		
		
		if (this.dimRef == 0)
			dimOrd = new ArrayList<HashSet<Integer>>(this.extent);
		else if (this.dimRef == 1) 
			dimOrd = new ArrayList<HashSet<Integer>>(this.intent);
		else 
			dimOrd = new ArrayList<HashSet<Integer>>(this.modus);
		
		
		Collections.sort(dimOrd, new Comparator<Set<?>>() {
		    @Override
		    public int compare(Set<?> o1, Set<?> o2) {
		        return Integer.valueOf(o1.size()).compareTo(o2.size());
		    }
		});
		

		for (HashSet<Integer> hashSet : dimOrd) {
			faces.put(hashSet, new HashSet<Integer>());
		}
		HashSet<HashSet<Integer>> border = new HashSet<HashSet<Integer>>();		
		border.add(dimOrd.get(0));
		HashSet<Integer> ei;

		for (int i = 1; i < dimOrd.size(); i++) {
			ei = new HashSet<Integer>(dimOrd.get(i));
			candidates = new HashSet<HashSet<Integer>>();
			
			for (HashSet<Integer> element : border) {
				HashSet<Integer> aux = new HashSet<Integer>(ei);
				aux.retainAll(element);
				candidates.add(aux);
			}

			HashSet<HashSet<Integer>> discarded = new HashSet<HashSet<Integer>>(candidates);
			discarded.removeAll(dimOrd);
			candidates.removeAll(discarded);
			
			for (HashSet<Integer> candidate : candidates) {
				HashSet<Integer> e = faces.get(candidate);
				HashSet<Integer>copy = new HashSet<Integer>(e);
				copy.retainAll(ei);
				if (copy.isEmpty() || discarded.contains(e.addAll(candidate))) {
					links.computeIfAbsent(ei, k -> new HashSet<HashSet<Integer>>()).add(candidate);
					links.get(ei).add(candidate);
					HashSet<Integer> aux = new HashSet<Integer>(ei);
					aux.removeAll(candidate);
					faces.get(candidate).addAll(aux);
					border.removeAll(candidate);
				}
			}
			border.add(ei);
		}

	}


	/**
	 * Get the links of some specific dimension set.
	 * @param dimRef
	 * @return HashSet of HashSet contain all the links.
	 */
	public HashSet<HashSet<Integer>> getLinks(HashSet<Integer> dimRef) {
		HashSet<HashSet<Integer>> result = null;
		if (this.links.containsKey(dimRef))
			result = this.links.get(dimRef);
		return result;
	}
	
	/**
	 * Get all the links of a list of elements of some specific dimension.
	 * @param dimRefList
	 * @return HashSet of HashSet contain all the links.
	 */
	public HashSet<HashSet<Integer>> getLinks(ArrayList<HashSet<Integer>> dimRefList) {
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		for (HashSet<Integer> hashSet : dimRefList) {
			HashSet<HashSet<Integer>> elements = this.links.get(hashSet); 
			if (elements != null)
				result.addAll(elements);
		}
		return result;
	}
	
	/**
	 * Converts a Set of int into a correspondent set of strings.
	 * @param elements
	 * @param dim
	 * @return
	 */
	public Set<String> intToStr(HashSet<Integer> elements, int dim){
		HashSet<String> a = new HashSet<String>();
		
		List<String> b;
		
		if (dim == 0)
			b = this.strObjectsList;
		else if (dim == 1 )
			b = this.strAttributesList;
		else if (dim == 2 )
			b = this.strConditionsList;
		else {
			System.err.format("Dimensions incompatible!");
			return null;
		}
		
		for (Integer element : elements) {
			a.add(b.get(element));
		}

		return a;
	}
	
	/**
	 * Iterates over the HashMap and prints all the k,v elements.
	 */
	public void printLinks() {
		for (Map.Entry<HashSet<Integer>, HashSet<HashSet<Integer>>> entry : this.links.entrySet()) {
			System.out.print(intToStr(entry.getKey(), this.dimRef)+ " - >");
			entry.getValue().forEach(link->System.out.print(intToStr(link, this.dimRef)));
			System.out.println();
		}
	}
	
	/** GETTERS AND SETTERS **/

	public Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> getOxAC() {
		return OxAC;
	}

	public void setOxAC(Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> oxAC) {
		OxAC = oxAC;
	}

	public Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> getAxOC() {
		return AxOC;
	}

	public void setAxOC(Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> axOC) {
		AxOC = axOC;
	}

	public Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> getCxOA() {
		return CxOA;
	}

	public void setCxOA(Map<HashSet<Integer>, ArrayList<Pair<HashSet<Integer>, HashSet<Integer>>>> cxOA) {
		CxOA = cxOA;
	}

	public int getnConcepts() {
		return nConcepts;
	}

	public void setnConcepts(int nConcepts) {
		this.nConcepts = nConcepts;
	}

	public HashSet<HashSet<Integer>> getConceptLinks(HashSet<Integer> conceptDimRef) {
		return this.links.get(conceptDimRef);
	}

	public short getDimRef() {
		return dimRef;
	}

	public void setDimRef(short dimRef) {
		this.dimRef = dimRef;
	}

	public ArrayList<String> getStrObjectsList() {
		return strObjectsList;
	}

	public void setStrObjectsList(ArrayList<String> strObjectsList) {
		this.strObjectsList = strObjectsList;
	}

	public ArrayList<String> getStrAttributesList() {
		return strAttributesList;
	}

	public void setStrAttributesList(ArrayList<String> strAttributesList) {
		this.strAttributesList = strAttributesList;
	}

	public ArrayList<String> getStrConditionsList() {
		return strConditionsList;
	}

	public void setStrConditionsList(ArrayList<String> strConditionsList) {
		this.strConditionsList = strConditionsList;
	}

	public ArrayList<HashSet<Integer>> getExtent() {
		return extent;
	}

	public void setExtent(ArrayList<HashSet<Integer>> extent) {
		this.extent = extent;
	}

	public ArrayList<HashSet<Integer>> getIntent() {
		return intent;
	}

	public void setIntent(ArrayList<HashSet<Integer>> intent) {
		this.intent = intent;
	}

	public ArrayList<HashSet<Integer>> getModus() {
		return modus;
	}

	public void setModus(ArrayList<HashSet<Integer>> modus) {
		this.modus = modus;
	}


	public Map<HashSet<Integer>, HashSet<HashSet<Integer>>> getLinks2() {
		return links;
	}

	public void setLinks2(Map<HashSet<Integer>, HashSet<HashSet<Integer>>> links2) {
		this.links = links2;
	}
}
