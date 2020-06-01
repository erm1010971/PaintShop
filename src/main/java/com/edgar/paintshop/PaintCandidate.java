package com.edgar.paintshop;

import java.util.ArrayList;

public class PaintCandidate {

	ArrayList<Paint> possibleResultTmp  = new ArrayList<Paint>();;
	int totalCandidateMattes=0;

	public PaintCandidate() {
	}
		
	public ArrayList<Paint> getPossibleResultTmp() {
		return possibleResultTmp;
	}

	public void setPossibleResultTmp(ArrayList<Paint> possibleResultTmp) {
		this.possibleResultTmp = possibleResultTmp;
	}

	public int getTotalCandidateMattes() {
		return totalCandidateMattes;
	}

	public void setTotalCandidateMattes(int totalCandidateMattes) {
		this.totalCandidateMattes = totalCandidateMattes;
	}

}
