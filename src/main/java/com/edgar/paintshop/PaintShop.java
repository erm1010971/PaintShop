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
		Scanner scannerDS = new Scanner(file);//Creating Scanner instance to read input file
		int numCases = scannerDS.nextInt(); //We read the case's number (first line in the file)
		HashMap<Integer,String> finalResult=new HashMap<Integer,String>();

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
					finalResult.put(caseNumber,findCaseSolution(totalCaseColorsArr, colors_N, colors_2N, custumers_M, caseNumber));
				}					
			}
			printFinalResult(finalResult);
		}else {
			System.out.println("INFO - The number of cases is 0, nothing to process");
		}
		scannerDS.close();
	}

	public static String findCaseSolution(Paint[][] totalCaseColorsArr, int colors_N, int colors_2N, int custumers_M, int caseNumber) {		
		ArrayList<PaintCandidate> candidatesAL = new ArrayList<PaintCandidate>();
		StringBuilder finalResultSB = new StringBuilder("Case #").append(caseNumber).append(": "); 
		
		outerloop:
		for(int paint1st=0; paint1st<colors_2N;paint1st++) {//We iterate once all the colors
			if(totalCaseColorsArr[0][paint1st]!=null) {//If we have a value to evaluate 
				Paint possibleResultTmp[] = new Paint[custumers_M];
				possibleResultTmp[0] = totalCaseColorsArr[0][paint1st];//We are always storing the first value at the beginning 

				for(int paint2nd=0; paint2nd<colors_2N;paint2nd++) {//We iterate all the colors
					for(int customer2nd=1; customer2nd<custumers_M;customer2nd++) {//We iterate all the customers from second line to the end
						if(totalCaseColorsArr[customer2nd][paint2nd]!=null) {//If we have a value to evaluate 					
							possibleResultTmp[customer2nd] = totalCaseColorsArr[customer2nd][paint2nd];

							if(possibleResultTmp[custumers_M-1]!=null) {//The array is complete to evaluate*
								if(validateCurrentCombination(possibleResultTmp)) {//The possibility is a valid candidate									
									int totalCandidateMattes = countMattePaints(possibleResultTmp);
									PaintCandidate paintCandidateTmp = new PaintCandidate();
									paintCandidateTmp.setPossibleResultTmp(possibleResultTmp.clone());
									paintCandidateTmp.setTotalCandidateMattes(totalCandidateMattes);
									
									if(validateAllGlossy(possibleResultTmp)) {//If we find a full glossy candidate, it is the one
										candidatesAL = new ArrayList<PaintCandidate>();
										candidatesAL.add(paintCandidateTmp);
										break outerloop;
									}else {
										candidatesAL.add(paintCandidateTmp);
									}									
								}//Not a valid candidate, nothing to do 							
							}
						}
					}
				}
			}
		}

		if(candidatesAL.size()>0) {
			int lessMatteValue = 0;
			Paint finalCandidateList[] = null;
			
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

			for (Paint finalCandidate : finalCandidateList) {
				caseResult.put(finalCandidate.getColor(), (finalCandidate.getType())?0:1);
			}

			for (int i = 1; i <= colors_N; i++) {//We iterate to fill all the colors
				if(caseResult.get(i)==null) {
					caseResult.put(i, 0);
				}
			}

			for (int i = 1; i <= colors_N; i++) {//We iterate to fill all the colors
				finalResultSB.append(caseResult.get(i)).append(" ");
			}

		}else {
			finalResultSB.append("IMPOSSIBLE");
		}

		return finalResultSB.toString();
	}

	public static boolean validateCurrentCombination(Paint possibleResultTmp[]) {
		boolean validConbination = true;

		for (int i = 0; i < possibleResultTmp.length; i++) {//We iterate once all the array
			for (int k = i + 1; k < possibleResultTmp.length; k++) {//We iterate again to compare from the second value
				Paint possible_I = possibleResultTmp[i];
				Paint possible_K = possibleResultTmp[k];

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
	
	public static boolean validateAllGlossy(Paint possibleResultTmp[]) {
		for (int i = 0; i < possibleResultTmp.length; i++) {//We iterate once all the array
			if(!possibleResultTmp[i].getType()) {//If there is a matte
				return false;
			}
		}
		
		return true;
	}

	public static int countMattePaints(Paint possibleResultTmp[]) {
		int totalMattes = 0;

		for (int i = 0; i < possibleResultTmp.length; i++) {
			if(!possibleResultTmp[i].getType()) {
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
