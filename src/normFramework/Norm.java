package normFramework;

import common.Constants;
import common.Logger;

//TODO: when writing toString function, consider its usage in VillageBuilder.generateHumen()



public class Norm {
	private String type = ""; //it can be action, intention, preference, decision style, or anything that can be a norm
	private String title = "";// i.e. |donation| > 10
	private int repetition = 0;
	private int noRepetition = 0;
	private int groupId = -1;
	private double lastRepeatedProb;
	
	public String toString(){
		return type + "-" + title + "-" + repetition + "-" + noRepetition + "-" + groupId;
	}
	
	public Norm(String normType, String normTitle, int groupID) {
		setType(normType);
		setTitle(normTitle);
		setGroupId(groupID);
	}
	
	public Norm(String normType, String normTitle, int repetition1, int notRepeated, int groupID) {
		setType(normType);
		setTitle(normTitle);
		setGroupId(groupID);
		setRepetition(repetition1);
		setNoRepetition(notRepeated);
	}
	
	public void increaseRepeatingNorm(){
		setRepetition(getRepetition() + 1);
	}
	
	public void incraseNoRepetition(){
		this.noRepetition ++;
	}
	
	public void resetNorm(){
		setRepetition(0);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title1) {
		this.title = title1;
	}

	public int getRepetition() {
		return repetition;
	}

	public void setRepetition(int repetition1) {
		this.repetition = repetition1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getNoRepetition() {
		return noRepetition;
	}

	public void setNoRepetition(int noRepetition) {
		this.noRepetition = noRepetition;
	}

	public static double disappearingFunction(double noRepetition, double lastRepeatedProb) {
		//it's part of sigmoid funtion
		//=1/(1+0,0078*POWER(0,5;25-noRepetition))
//		double prob = 1.0/(1.0+0.0078*Math.pow(0.5,25-noRepetition));
//		double prob = 1.0/(1.0+0.0078*Math.pow(0.5,35-noRepetition));
		double prob = 0.0;
		//we use generalized logistic function
		//f(T) = A + (K-A)/ ((C+ Q * exp(-B*(T-M)))^(1/V))
		//then we mirror the function about Y axis and shift it 50 to the right. 
		//50 is the necessary time to disappear a norm
		//f2(T) = A + (K-A)/ ((C+ Q * exp(B*(T-50-M)))^(1/V))
		//f_X( 0, b_var[b_i], 1, a_var[a_i], k_var[k_i], 1, 1, x)
		//   ( A, B         , C, K         , Q         , M, V, T)
		double shift = Constants.T_DISAPPEARING;
		double A = 0.0; double C = 1.0; double M = 1.0; double V = 1.0; double Q = 100;
		double B = lastRepeatedProb/2.0; double K = lastRepeatedProb;
		prob = A + (K-A)/ ( (C+ Q * Math.pow(Math.exp(B*(noRepetition-shift-M)), (1/V))) );
		return prob;
	}

	public static double internalizationFunction(double repetition) {
//		1-1/repetition^0,5
		double prob = 1-1/Math.pow(repetition, 0.5);
		return prob;
	}

	public static double adoptionFunction(double repetition) {
		//fun = e ^(x-h) +k; k and h should be find according by choosing two points.
		//here i find them according to (5, 0,005) , (10, 0,07)
		double h = 10.35708268;
		double k = -0.00028536;
		double prob = Math.exp(repetition-h) + k;
		return prob;
	}

	public static double observationFunction(double repetition) {
		//linear function
		double prob = Constants.SLOP_OBSERVATION_PHASE * repetition;
		return prob;
	}

	public double getLastRepeatedProb() {
		return lastRepeatedProb;
	}

	public void setLastRepeatedProb(int lastRepetition, int lstNoRepetition) {
		double followNeighborsProbability = 0;
		if(lastRepetition == 0 && lstNoRepetition == 0){
			if(lstNoRepetition < Constants.T_ADOPTATION){
//					call observation function
				followNeighborsProbability = Norm.observationFunction(lastRepeatedProb);								
			} else if(lastRepetition < Constants.T_INTERNALIZATION){
				followNeighborsProbability = Norm.adoptionFunction(lastRepeatedProb);
			}else{
				followNeighborsProbability = Norm.internalizationFunction(lastRepeatedProb);
			}
		}
		lastRepeatedProb = followNeighborsProbability;
	}

}

