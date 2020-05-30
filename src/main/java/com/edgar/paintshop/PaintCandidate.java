package com.edgar.paintshop;

public class PaintCandidate {
	Paint possibleResultTmp[];
	int totalCandidateMattes=0;


	public PaintCandidate() {
	}
	
	public Paint[] getPossibleResultTmp() {
		return possibleResultTmp;
	}

	public void setPossibleResultTmp(Paint[] possibleResultTmp) {
		this.possibleResultTmp = possibleResultTmp;
	}

	public int getTotalCandidateMattes() {
		return totalCandidateMattes;
	}

	public void setTotalCandidateMattes(int totalCandidateMattes) {
		this.totalCandidateMattes = totalCandidateMattes;
	}

}
