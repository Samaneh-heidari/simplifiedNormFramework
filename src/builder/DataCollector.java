package builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.GroupImpl;

import normFramework.Group;
import normFramework.ParsNormLogic;
import population.Human;
import property.HouseType;
import common.Constants;
import common.Logger;
import common.SimUtils;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.grid.GridPoint;
import sun.util.logging.resources.logging;
import valueFramework.AbstractValue;

public class DataCollector {

	public DataCollector(GridPoint location) {
		
		SimUtils.getContext().add(this);
		if (!SimUtils.getGrid().moveTo(this, location.getX(), location.getY())) {
			Logger.logError("DataCollector could not be placed, coordinate: " + location);
		}
	}

	public String getLabel() {
		
		return "DataCollector";
	}
	
	/*
	 * Normative related methods
	 */
	
	private double donationAmountNeighborhood(String neighborName){
		double amount = 0;
		double count = 0;
		for (Human resident : SimUtils.getObjectsAll(Human.class)) {
//			if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
				if (resident.getLivingGroupName().equals(neighborName)) {
//					Logger.logDebug("H" + resident.getId() + " lastdonation percentage is " + resident.getLastDonationPercentage()) ;
					amount += resident.getLastDonationPercentage();
					count ++;
				}
//			}
		}
		if(count == 0)
			return 0;
		else{
//			Logger.logDebug("DATA COLLECTOR, donationAmountNeighborhood " + neighborName + " is " + amount/count);
			return amount/count;
		}
	}
	
	public double donationAmountCheapNeighborhood() {
		return donationAmountNeighborhood(HouseType.CHEAP.name());
	}
	
	public double donationAmountSTANDARDNeighborhood() {
		return donationAmountNeighborhood(HouseType.STANDARD.name());
	}
	
	public double donationAmountEXPENSIVENeighborhood() {
		return donationAmountNeighborhood(HouseType.EXPENSIVE.name());
	}
	
	///
	private double donationValuedNeighborhood(String neighborName){
		double amount = 0;
		double count = 0;
		for (Human resident : SimUtils.getObjectsAll(Human.class)) {
//			if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
				if (resident.getLivingGroupName().equals(neighborName)) {
//					Logger.logDebug("H" + resident.getId() + " lastdonation percentage is " + resident.getLastDonationPercentage()) ;
					amount += resident.getMyValueBasedDonationPercentage();
					count ++;
				}
//			}
		}
		if(count == 0)
			return 0;
		else{
//			Logger.logDebug("DATA COLLECTOR, donationAmountNeighborhood " + neighborName + " is " + amount/count);
			return amount/count;
		}
	}
	
	public double donationValuedCheapNeighborhood() {
		return donationValuedNeighborhood(HouseType.CHEAP.name());
	}
	
	public double donationValuedSTANDARDNeighborhood() {
		return donationValuedNeighborhood(HouseType.STANDARD.name());
	}
	
	public double donationValuedEXPENSIVENeighborhood() {
		return donationValuedNeighborhood(HouseType.EXPENSIVE.name());
	}
	///
	
	///
		private double donationNormedNeighborhood(String neighborName){
			double amount = 0;
			double count = 0;
			for (Human resident : SimUtils.getObjectsAll(Human.class)) {
//				if (resident.getAge() >= Constants.HUMAN_ADULT_AGE && resident.getAge() < Constants.HUMAN_ELDERLY_AGE) {
					if (resident.getLivingGroupName().equals(neighborName)) {
//						Logger.logDebug("H" + resident.getId() + " lastdonation percentage is " + resident.getLastDonationPercentage()) ;
						amount += resident.getNormBasedDonationPercentage();
						count ++;
					}
//				}
			}
			if(count == 0)
				return 0;
			else{
//				Logger.logDebug("DATA COLLECTOR, donationAmountNeighborhood " + neighborName + " is " + amount/count);
				return amount/count;
			}
		}
		
		public double donationNormedCheapNeighborhood() {
			return donationNormedNeighborhood(HouseType.CHEAP.name());
		}
		
		public double donationNormedSTANDARDNeighborhood() {
			return donationNormedNeighborhood(HouseType.STANDARD.name());
		}
		
		public double donationNormedEXPENSIVENeighborhood() {
			return donationNormedNeighborhood(HouseType.EXPENSIVE.name());
		}
		///
	
	public double getAdultAndElderlyWealthAvg_neighborhood(String groupName) {
			
			ArrayList<Human> humans = SimUtils.getObjectsAll(Human.class);
			int count = 0;
			double money = 0;
			for (Human human : humans) {
//				if (human.getAge() >= Constants.HUMAN_ADULT_AGE && human.getAge() < Constants.HUMAN_ELDERLY_CARE_AGE) {
					if(human.getLivingGroupName().equals(groupName)){
						count ++;
						money += human.getMoney();
					}
//				}
			}
			if (count >= 1)
				return money / count;
			else
				return 0;
	}
	
	public double getAdultAndElderlyWealthAvg_CHEAP() {
		return getAdultAndElderlyWealthAvg_neighborhood(HouseType.CHEAP.name());
	}
	
	public double getAdultAndElderlyWealthAvg_STANDARD() {
		return getAdultAndElderlyWealthAvg_neighborhood(HouseType.STANDARD.name());
	}

	public double getAdultAndElderlyWealthAvg_EXPENSIVE() {
		return getAdultAndElderlyWealthAvg_neighborhood(HouseType.EXPENSIVE.name());
	}


	public int getNumberOfHumanInGroup(String groupName) {
		ArrayList<Human> humen = SimUtils.getObjectsAll(Human.class);
//		Logger.logDebug("number of human : " + humen.size());
		int count = 0;
		for (Human human : humen) {
//			Logger.logDebug("H"+ human.getId() + " is memberof " + groupName + "?");
			if ( human.getNormedDecisionMaker().isMember(groupName)) {
//				Logger.logDebug("H"+ human.getId() + " is a member of " + groupName);
				count ++;
			}
		}
		return count;
	}
	public int getNumberOfAdultsCheapNeighborhood() {
		
		return getNumberOfHumanInGroup(HouseType.CHEAP.name());
	}

	public int getNumberOfAdultsStandardNeighborhood() {
		return getNumberOfHumanInGroup(HouseType.STANDARD.name());
	}
	
	public int getNumberOfAdultsEXPENSIVENeighborhood() {
		return getNumberOfHumanInGroup(HouseType.EXPENSIVE.name());
	}

	public int getNumberOfAgeAndValue(String groupName, String valueName) {
		ArrayList<Human> residents = SimUtils.getObjectsAll(Human.class);
		int count = 0;
		
		for (Human resident : residents) {
			String imprtValName = resident.getValuedDecisionMaker().getTheMostImportantValuesTitle() ;
			if(imprtValName.equals(""))
				Logger.logError("decision maker: cannot find the most important value");
			if (imprtValName.equals(valueName) && resident.getNormedDecisionMaker().isMember(groupName)) {
				count ++;
			}
		}		
		return count;
	}

	public int getNumberOfAdultsCheapUniversalism() {
		return getNumberOfAgeAndValue(HouseType.CHEAP.name(), AbstractValue.UNIVERSALISM.name());
	}

	public int getNumberOfAdultsStandardUniversalism() {
		return getNumberOfAgeAndValue(HouseType.STANDARD.name(), AbstractValue.UNIVERSALISM.name());
	}
	
	public int getNumberOfAdultsEXPENSIVEUniversalism() {
		return getNumberOfAgeAndValue(HouseType.EXPENSIVE.name(), AbstractValue.UNIVERSALISM.name());
	}

	public int getNumberOfAdultsCheapPower() {
		return getNumberOfAgeAndValue(HouseType.CHEAP.name(), AbstractValue.POWER.name());
	}

	public int getNumberOfAdultsStandardPower() {
		return getNumberOfAgeAndValue(HouseType.STANDARD.name(), AbstractValue.POWER.name());
	}
	
	public int getNumberOfAdultsEXPENSIVEPower() {
		return getNumberOfAgeAndValue(HouseType.EXPENSIVE.name(), AbstractValue.POWER.name());
	}

	public String printHumenNorms(){
		ArrayList<Human> residents = SimUtils.getObjectsAll(Human.class);
		String allNorms = "";
		String tempNorm;
		for(Human hmn: residents){
			tempNorm = hmn.printNorm();
			if(tempNorm != null && !tempNorm.equals(""))
				allNorms += tempNorm;				
		}
		return allNorms;
	}
	
	//
	public String printHumenNormsAverageByJoiningTime(){
		String allNorms = "";
		String tmp = "";
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		/*if(tick == 1)
			allNorms = "Tick, group ID, joining time, upper bound, lower bound";*/
		List<Map<Double , String>> groups_time_donation  = getAllDonationNormJoiningTimes();
		for(int i =0; i < groups_time_donation.size();i++){
			Map<Double , String> tdc =  groups_time_donation.get(i);
			for(Double jTime: tdc.keySet()){
				String[] donation_count = tdc.get(jTime).split("-");
				tmp = tick + "," + i + ","+ jTime + "," + (new Double(donation_count[0])/new Double(donation_count[2])) +","+ (new Double(donation_count[0])/new Double(donation_count[2])) + "\n";
				allNorms += tmp;
			}
//			allNorms += "\n";
		}
		allNorms = allNorms.substring(0, allNorms.length()-1);
		return allNorms;
	}
	
	private List<Map<Double, String>> getAllDonationNormJoiningTimes() {
		int groupId;
		String tempNorm;
		double joiningTime = -1;
		ArrayList<Human> residents = SimUtils.getObjectsAll(Human.class);
		List<Map<Double , String>> groups_time_donation  =new ArrayList<Map<Double,String>>();
		//groupname, time_donation_count
		for(int i = 0; i < Constants.GROUP_NAMES.length; i++)
			groups_time_donation.add(new HashMap<Double, String>());
		
		for(Human hmn: residents){
			groupId = hmn.getLivingGroupId();
			joiningTime = hmn.getJoiningTime();
			tempNorm = hmn.printLivingGroupDonationNorm();
			Logger.logDebug("Human H" + hmn.getId() + " has norm : " + tempNorm + " in group G" + groupId);
			Map<Double, String> gtd = null;
			if(tempNorm != null && !tempNorm.equals("")){				
				double[] myDonationBounds = ParsNormLogic.getDonationAmount(tempNorm);
				gtd = groups_time_donation.get(groupId);
//				Logger.logDebug("\tdonationBounds are [" + myDonationBounds[0] + ";"+ myDonationBounds[1] + "]");
				if(gtd.containsKey(joiningTime)){
					String primitive = gtd.get(joiningTime);
					String[] up_lp_cnt = primitive.split("-");
					String replacedby = (new Double(up_lp_cnt[0]) + myDonationBounds[0] )+ "-" +
										(new Double(up_lp_cnt[1]) + myDonationBounds[1]) + "-" +
										(new Double(up_lp_cnt[2]) +  1);
					gtd.put(joiningTime, replacedby);
				}
				else
					gtd.put(joiningTime, (myDonationBounds[0] + "-" + myDonationBounds[1] + "-" + 1));
				Logger.logDebug("\ttime " + joiningTime + " in list is : "+ gtd.get(joiningTime));
				
			}
		}
		return groups_time_donation;
	}
	
	private List<Map<Double, String>> getAllDonationPecentageJoiningTimes() {
		int groupId;
//		String tempNorm;
		double joiningTime = -1;
		ArrayList<Human> residents = SimUtils.getObjectsAll(Human.class);
		List<Map<Double , String>> groups_time_donation  =new ArrayList<Map<Double,String>>();
		//groupname, time_donation_count
		for(int i = 0; i < Constants.GROUP_NAMES.length; i++)
			groups_time_donation.add(new HashMap<Double, String>());
		
		for(Human hmn: residents){
			groupId = hmn.getLivingGroupId();
			joiningTime = hmn.getJoiningTime();
//			tempNorm = hmn.printLivingGroupDonationNorm();
			double mylastDonationPercentage = hmn.getLastDonationPercentage();					
//			Logger.logDebug("Human H" + hmn.getId() + " has norm : " + tempNorm + " in group G" + groupId);
			Map<Double, String> gtd = null;
//			double[] myDonationBounds = ParsNormLogic.getDonationAmount(tempNorm);
			double[] myDonationBounds = {mylastDonationPercentage, mylastDonationPercentage};
			gtd = groups_time_donation.get(groupId);
//				Logger.logDebug("\tdonationBounds are [" + myDonationBounds[0] + ";"+ myDonationBounds[1] + "]");
			if(gtd.containsKey(joiningTime)){
				String primitive = gtd.get(joiningTime);
				String[] up_lp_cnt = primitive.split("-");
				String replacedby = (new Double(up_lp_cnt[0]) + myDonationBounds[0] )+ "-" +
									(new Double(up_lp_cnt[1]) + myDonationBounds[1]) + "-" +
									(new Double(up_lp_cnt[2]) +  1);
				gtd.put(joiningTime, replacedby);
			}
			else
				gtd.put(joiningTime, (myDonationBounds[0] + "-" + myDonationBounds[1] + "-" + 1));
			Logger.logDebug("\ttime " + joiningTime + " in list is : "+ gtd.get(joiningTime));
				
		}
		return groups_time_donation;
	}
	

	public String printHumenNormsAverageByPreviousGroup(){
		
		return null;
	}
	
	public String printHumenNormsAverageByJoiningTimeAndPreviousGroup(){
		
		return null;
	}
	
	//histogram 
	public int[] getNumOfDonationPercentage(int minimum, int maximum, int groupId) {
		
		ArrayList<Human> humans = SimUtils.getObjectsAll(Human.class);
		int countPower = 0;
		int countUniver = 0;
		for (Human human : humans) {
			if (human.getLastDonationPercentage() >= minimum 
					&& (human.getLastDonationPercentage() < maximum || human.getLastDonationPercentage() == 100)
					&& human.getLivingGroupId() == groupId) {
				String importantValue = human.getValuedDecisionMaker().getTheMostImportantValuesTitle(); 
				if(importantValue.equals(AbstractValue.POWER.name()))
					countPower ++;
				else if(importantValue.equals(AbstractValue.UNIVERSALISM.name()))
					countUniver ++;
			}
		}
		int[] returnVal = {countPower,countUniver}; 
		return returnVal;
	}
	
	public String getNumOfDonatorsClusterBigGroup(){
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String returnVal = "";
		int clusterSize = Constants.HISTOGRAM_DONATION_CLUSTER_SIZE;
		Logger.logDebug("************** getNumOfDonatorsCluster tick: " + tick + ", TIME_TO_GET_HISTOGRAM: " + Constants.TIME_TO_GET_HISTOGRAM + ", clusterSize: " + clusterSize);
		if(Constants.TIME_TO_GET_HISTOGRAM.contains(tick)){
			for(int i = 0; i < 100/clusterSize; i++){
				int[] numOfAgents = getNumOfDonationPercentage(i*clusterSize, (i+1)*clusterSize, 0);
				returnVal+= tick + ";" + i*clusterSize +"-" + (i+1)*clusterSize + ";" + numOfAgents[0] + ";" + numOfAgents[1] + "#";
			}
			if(returnVal.endsWith("\n")) returnVal = returnVal.substring(0, returnVal.length()-1);
			Logger.logDebug("write to file : " + returnVal);
		}
		else
			returnVal = "";
		return returnVal;
	}
	
	public String getNumOfDonatorsClusterSmallGroup(){
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String returnVal = "";
		int clusterSize = Constants.HISTOGRAM_DONATION_CLUSTER_SIZE;
		Logger.logDebug("************** getNumOfDonatorsCluster tick: " + tick + ", TIME_TO_GET_HISTOGRAM: " + Constants.TIME_TO_GET_HISTOGRAM + ", clusterSize: " + clusterSize);
		if(Constants.TIME_TO_GET_HISTOGRAM.contains(tick)){
			for(int i = 0; i < 100/clusterSize; i++){
				int[] numOfAgents = getNumOfDonationPercentage(i*clusterSize, (i+1)*clusterSize, 2);
				returnVal+= tick + ";" + i*clusterSize +"-" + (i+1)*clusterSize + ";" + numOfAgents[0] + ";" + numOfAgents[1] + "#";
			}
			if(returnVal.endsWith("\n")) returnVal = returnVal.substring(0, returnVal.length()-1);
			Logger.logDebug("write to file : " + returnVal);
		}
		else
			returnVal = "";
		return returnVal;
	}
	
	//TODO: must change:
	public double printNormsAvgByJTime_G_T(int groupId, int joiningTime){
		double jTime_min = Math.max(joiningTime -5.0, -1.0);
		double jTime_max = joiningTime + 5;
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		List<Map<Double , String>> groups_time_donation  = getAllDonationNormJoiningTimes();
		Map<Double , String> tdc =  groups_time_donation.get(groupId);
		for(double jTime = jTime_min; jTime < jTime_max +1; jTime ++){
			if(tdc.containsKey(jTime)){
				Logger.logDebug("tdc.get(jTime) : " + tdc.get(jTime));
				String[] donation_count = tdc.get(jTime).split("-");
				Logger.logDebug("printNormsAvgByJTime_G_T G" + groupId + ", time " + joiningTime + " avgDonation " + new Double(donation_count[0])/new Double(donation_count[2]));
				return (new Double(donation_count[0])/new Double(donation_count[2]));
			}
		}		
		return 0;
	}

	public double printNormsAvgByJTime_G2_T1(){
		return printNormsAvgByJTime_G_T(2,-1);
	}
	public double printNormsAvgByJTime_G2_T20(){
		return printNormsAvgByJTime_G_T(2,20);
	}

	public double printNormsAvgByJTime_G2_T70(){
		return printNormsAvgByJTime_G_T(2,70);
	}
	public double printNormsAvgByJTime_G2_T120(){
		return printNormsAvgByJTime_G_T(2,120);
	}
	public double printNormsAvgByJTime_G2_T170(){
		return printNormsAvgByJTime_G_T(2,170);
	}
	
	public double printNormsAvgByJTime_G0_T1(){
		return printNormsAvgByJTime_G_T(0,-1);
	}
	public double printNormsAvgByJTime_G0_T20(){
		return printNormsAvgByJTime_G_T(0,20);
	}

	public double printNormsAvgByJTime_G0_T70(){
		return printNormsAvgByJTime_G_T(0,70);
	}
	public double printNormsAvgByJTime_G0_T120(){
		return printNormsAvgByJTime_G_T(0,120);
	}
	public double printNormsAvgByJTime_G0_T170(){
		return printNormsAvgByJTime_G_T(0,170);
	}
	
	//TODO: must change:
		public double printAvgDonationByJTime_G_T(int groupId, int joiningTime){
			double jTime_min = Math.max(joiningTime -5.0, -1.0);
			double jTime_max = joiningTime + 5;
			int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			List<Map<Double , String>> groups_time_donation  = getAllDonationPecentageJoiningTimes();
			Map<Double , String> tdc =  groups_time_donation.get(groupId);
			for(double jTime = jTime_min; jTime < jTime_max +1; jTime ++){
				if(tdc.containsKey(jTime)){
					Logger.logDebug("tdc.get(jTime) : " + tdc.get(jTime));
					String[] donation_count = tdc.get(jTime).split("-");
					Logger.logDebug("printNormsAvgByJTime_G_T G" + groupId + ", time " + joiningTime + " avgDonation " + new Double(donation_count[0])/new Double(donation_count[2]));
					return (new Double(donation_count[0])/new Double(donation_count[2]));
				}
			}		
			return 0;
		}

		public double printAvgDonationByJTime_G2_T1(){
			return printNormsAvgByJTime_G_T(2,-1);
		}
		public double printAvgDonationJTime_G2_T20(){
			return printNormsAvgByJTime_G_T(2,20);
		}

		public double printAvgDonationJTime_G2_T70(){
			return printNormsAvgByJTime_G_T(2,70);
		}
		public double printAvgDonationJTime_G2_T120(){
			return printNormsAvgByJTime_G_T(2,120);
		}
		public double printAvgDonationJTime_G2_T170(){
			return printNormsAvgByJTime_G_T(2,170);
		}
		
		public double printAvgDonationJTime_G0_T1(){
			return printNormsAvgByJTime_G_T(0,-1);
		}
		public double printAvgDonationJTime_G0_T20(){
			return printNormsAvgByJTime_G_T(0,20);
		}

		public double printAvgDonationJTime_G0_T70(){
			return printNormsAvgByJTime_G_T(0,70);
		}
		public double printAvgDonationJTime_G0_T120(){
			return printNormsAvgByJTime_G_T(0,120);
		}
		public double printAvgDonationJTime_G0_T170(){
			return printNormsAvgByJTime_G_T(0,170);
		}
		
}
