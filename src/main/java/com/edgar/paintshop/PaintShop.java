package com.edgar.paintshop;

/**
 * Created by Edgar Rubio on March 2020.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PaintShop {

	public PaintShop() {
	}

	public static void main(String[] args) {
		if (args.length > 0) {      
			String fileRoute = args[0];
			File file = new File(args[0]);

			if(file.exists() && !file.isDirectory()) { 
				try {

					processPaintShopFile(file);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.err.println(new StringBuilder("ERROR - The file was not foud at \"").append(fileRoute).append("\""));
				}catch (Exception e) {
					e.printStackTrace();
					System.err.println("ERROR - There was an error, contact your system administrator");
				}
			}else {
				System.err.println(new StringBuilder("ERROR - The file at route \"").append(fileRoute).append("\" doesn't exist or is a directory"));
			}
		}else {
			System.err.println("ERROR - You need to provide a route to a file");
		}
	}


	public static void processPaintShopFile(File file) throws FileNotFoundException  {
		//Creating Scanner instance to read input file
		Scanner scannerDS = new Scanner(file);
		int numCases = scannerDS.nextInt(); //We read the case's number (first line in the file)
		HashMap<Integer,String> finalResult=new HashMap<Integer,String>();
		HashMap<Integer,Integer> totalColorsPerCustomer=new HashMap<Integer,Integer>();		

		if(numCases>0) {
			for(int caseNumber=1; caseNumber<=numCases; caseNumber++) {//Iteration over number of cases.
				int colors_N = scannerDS.nextInt();
				int colors_2N = colors_N*2;
				int custumers_M = scannerDS.nextInt();
				boolean someoneWantsMoreThan1Matte = false;
				int caseGlossyColors = 0;
				boolean allCaseCustomersWantGlossy = false;				

				Paint totalCaseColorsArr[][] = new Paint[custumers_M][colors_2N];
				
				for(int customer=0; customer<custumers_M; customer++) {//Iteration over number of customers.
					int customerColors_T = scannerDS.nextInt();
					int matteColorsPerCustomer = 0;
					totalColorsPerCustomer.put(customer, customerColors_T);

					for(int color=0; color<customerColors_T; color++) {//Iteration over line colors_T.
						int color_X = scannerDS.nextInt();						
						int currentTypeColor=scannerDS.nextInt();
						
						boolean typeColor_Y = false;
						if(currentTypeColor==0) {
							typeColor_Y = true;							
						}else {
							matteColorsPerCustomer++;
							typeColor_Y = false;
						}

						totalCaseColorsArr[customer][color]= new Paint(color_X, typeColor_Y);
					}					
					if(matteColorsPerCustomer>1) {//The customer wants more than 1 matte color
						someoneWantsMoreThan1Matte=true;
					}else if(matteColorsPerCustomer==0) {//All are glossy colors
						caseGlossyColors++;
						if(caseGlossyColors >= custumers_M) {
							allCaseCustomersWantGlossy = true;
						}
					}
				}//The main array is full now
				
				if(someoneWantsMoreThan1Matte) {//A customer wants more than 1 matte color ----- BROKEN RULE
					finalResult.put(caseNumber, new StringBuilder("Case #").append(caseNumber).append(": IMPOSSIBLE").toString());
				}else if(allCaseCustomersWantGlossy){//All the customers want glossy colors
					StringBuilder caseFinalResultSB = new StringBuilder("Case #").append(caseNumber).append(": ");
					for(int color=0; color<colors_N; color++) {//Iteration over number of colors.
						caseFinalResultSB.append("0").append(" ");
					}					

					finalResult.put(caseNumber, caseFinalResultSB.toString());
				}else {
					finalResult.put(caseNumber,findCaseSolution(totalCaseColorsArr, totalColorsPerCustomer, colors_N, colors_2N, custumers_M, caseNumber));
				}	
			}

			printFinalResult(finalResult);

		}else {
			System.out.println("INFO - The number of cases is 0, nothing to process");
		}
		scannerDS.close();
	}
	
	public static String findCaseSolution(Paint[][] totalCaseColorsArr, HashMap<Integer,Integer> totalColorsPerCustomer, int colors_N, int colors_2N, int custumers_M, int caseNumber) {				
		StringBuilder finalResultSB = new StringBuilder("Case #").append(caseNumber).append(": "); 
		boolean continueCombinations = true;
		HashMap<Integer,Integer> statusPerCustomer=new HashMap<Integer,Integer>();
		ArrayList<PaintCandidate> candidatesAL = new ArrayList<PaintCandidate>();
		ArrayList<Paint> combinationCandidateAL = new ArrayList<Paint>();
		int custumersShort_1 = custumers_M-1;
		int custumersShort_2 = custumers_M-2;				
		
		boolean nullPaintFound = false;

		//We intitialize the HashMap with 0 in all positions 
		for (int i = 0; i < custumersShort_1; i++) {
			statusPerCustomer.put(i, 0);
		}
		
		outerloop:
		while(continueCombinations) {			
			for (int customer = 0; customer < custumersShort_1; customer++) {//We iterate the custumersShort to get the position
				Paint paintTmp = totalCaseColorsArr[customer][statusPerCustomer.get(customer)];
				if(paintTmp!=null) {//If we have a value to process
					combinationCandidateAL.add(paintTmp);
				}else {
					nullPaintFound = true;
					combinationCandidateAL.clear();
					break;//with a null value we can not evaluate
				}
			}
				
			if(!nullPaintFound) {//Means the combination's first part was completed and we can continue
				//Here we iterate the last customer with all the colors, we missed it because that one has to be full iterated everytime with all the colors
				for (int color = 0; color < colors_N; color++) {
					Paint paintTmp = totalCaseColorsArr[custumersShort_2+1][color];
					
					if(paintTmp != null) {//Means the whole combination was completed and we can evaluate
						combinationCandidateAL.add(paintTmp);
												
						//WE EVALUATE THE COMBINATION
						if(validateCurrentCombination(combinationCandidateAL)) {//The possibility is a valid candidate									
							int totalCandidateMattes = countMattePaints(combinationCandidateAL);
							PaintCandidate paintCandidateTmp = new PaintCandidate();
							paintCandidateTmp.setPossibleResultTmp((ArrayList<Paint>)combinationCandidateAL.clone());
							paintCandidateTmp.setTotalCandidateMattes(totalCandidateMattes);
							
							if(validateAllGlossy(combinationCandidateAL)) {//If we find a full glossy candidate, it is the one
								candidatesAL.clear();
								candidatesAL.add(paintCandidateTmp);
								break outerloop;
							}else {
								candidatesAL.add(paintCandidateTmp);
							}									
						}//Not a valid candidate, nothing to do
												
						combinationCandidateAL.remove(combinationCandidateAL.size()-1);
					}
				}
				combinationCandidateAL.clear();//We clear the list for the next loop
			}

			//WE MOVE THE POSITIONS FOR NEXT COMBINATION
			for (int i = custumersShort_2; i >= 0; i--) {//We iterate from the penultimate to the first value
				int colorPosition = statusPerCustomer.get(i);
				int color_T = totalColorsPerCustomer.get(i);
				
				if(colorPosition == color_T-1) {
					if(i==0) {
						continueCombinations=false;
						break;
					}
					//We are the end of the colors, we have to reset this line and make movements on the others
					statusPerCustomer.replace(i, 0);					
				}else {
					statusPerCustomer.replace(i, colorPosition+1);
					break;
				}
			}
			
			nullPaintFound = false;//We reset the value for the next loop
		}
		
		if(candidatesAL.size()>0) {//We look for some valid option
			int lessMatteValue = 0;
			ArrayList<Paint> finalCandidateList = null;
			
			for (int i = 0; i < candidatesAL.size(); i++) {
				if(i==0) {
					lessMatteValue = candidatesAL.get(i).getTotalCandidateMattes();
					finalCandidateList = candidatesAL.get(i).getPossibleResultTmp();
				}else {
					int tmpMatteValue = candidatesAL.get(i).getTotalCandidateMattes();
					if(tmpMatteValue < lessMatteValue) {
						lessMatteValue = tmpMatteValue;
						finalCandidateList = candidatesAL.get(i).getPossibleResultTmp();
					}					
				}				
			}

			HashMap<Integer,Integer> caseResult=new HashMap<Integer,Integer>();			
			for (Paint finalCandidate : finalCandidateList) {//We add the colors from the best combination
				int color_x = finalCandidate.getColor();				
				caseResult.put(color_x, (finalCandidate.getType())?0:1);
			}


			for (int i = 1; i <= colors_N; i++) {//We iterate to fill all the missing colors
				if(caseResult.get(i)==null) {
					caseResult.put(i, 0);
				}
			}

			for (int i = 1; i <= colors_N; i++) {//We iterate to construct the final string for the case 
				finalResultSB.append(caseResult.get(i)).append(" ");
			}
		}else {//if there is no valid option, it is impossible
			finalResultSB.append("IMPOSSIBLE");
		}
		
		return finalResultSB.toString();
	}
	
	public static boolean validateCurrentCombination(ArrayList<Paint> possibleResultTmp) {
		boolean validConbination = true;

		for (int i = 0; i < possibleResultTmp.size(); i++) {//We iterate once all the array
			for (int k = i + 1; k < possibleResultTmp.size(); k++) {//We iterate again to compare from the second value
				Paint possible_I = possibleResultTmp.get(i);
				Paint possible_K = possibleResultTmp.get(k);

				if (i != k) {//if it is the same value it's not useful
					if(possible_I.getColor().equals(possible_K.getColor())) {
						if(possible_I.getType() == possible_K.getType()) {//If they are the same there is no problem
							validConbination = true;
						}else {//They are glossy and matte, it is invalid
							return false;//if one is invalid, all the combination is invalid
						}
					}
				}
			}
		}

		return validConbination;
	}
	
	public static boolean validateAllGlossy(ArrayList<Paint>  possibleResultTmp) {
		for (int i = 0; i < possibleResultTmp.size(); i++) {//We iterate once all the array
			if(!possibleResultTmp.get(i).getType()) {//If there is a matte
				return false;
			}
		}
		
		return true;
	}

	public static int countMattePaints(ArrayList<Paint> possibleResultTmp) {
		int totalMattes = 0;

		for (int i = 0; i < possibleResultTmp.size(); i++) {
			if(!possibleResultTmp.get(i).getType()) {
				totalMattes++;
			}
		}

		return totalMattes;
	}

	public static void printFinalResult(HashMap<Integer,String> finalResultHM) {		
		for(Map.Entry m : finalResultHM.entrySet()){    
			System.out.println(m.getValue());    
		} 
	}

}

