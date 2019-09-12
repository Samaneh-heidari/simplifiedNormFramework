package valueFramework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jdk.nashorn.internal.ir.ReturnNode;
import property.HouseType;
import repast.simphony.engine.environment.RunEnvironment;
import common.Constants;
import common.HumanUtils;
import common.Logger;
import normFramework.Group;
import normFramework.Norm;
import normFramework.ParsNormLogic;

public class NormedDecisionMaker {
	//norm related values
	private ArrayList<Group> groupList ;
	private Map<Integer, Collection<Norm>> normList;
			//key is the groupId that the norm belongs to it. 

	public NormedDecisionMaker(String groupList, String normList) {
		String[] groupArray = groupList.split(";");
		String[] normArray = normList.split(";");		
		
		for (int i = 0; i < normArray.length; i++) {
			//Type + "-" + title + "-" + repetition + "-" + noRepetition + "-" + groupId;
			String[] normInfo = normArray[i].split("-");
			int grpId = new Integer(normInfo[4]);
			Norm nrm = new Norm(normInfo[0], normInfo[1], (int)new Integer(normInfo[2]), (int)new Integer(normInfo[3]), grpId);
			if(this.normList.containsKey(grpId)){
				this.normList.get(grpId).add(nrm);//TODO: check if the new norm is added to hashmap
			}
			else{
				Collection<Norm> norms = new ArrayList<Norm>();
				norms.add(nrm);
				this.normList.put(grpId, norms);
			}
		}
		for (int i = 0; i < groupArray.length; i++) {
			String[] groupInfo = groupArray[i].split("-");
			Group grp = new Group(new Integer(groupInfo[0]), groupInfo[1]);
			this.groupList.add(grp);
		}
	}

	public NormedDecisionMaker() {
		groupList = new ArrayList<Group>();
		normList = new HashMap<Integer, Collection<Norm>>();
	}
	
	public boolean isMember(int groupId) {
		for(Group gr : groupList){
			if(gr.getId() == groupId)
				return true;
		}
		return false;
	}

	public boolean isMember(String groupName) {
		for(Group gr : groupList){
			if(gr.getTitle().equals(groupName))
				return true;
		}
		return false;
	}
	
	public void leaveGroup(int groupID){
		for (int idx = 0; idx < groupList.size(); idx ++){			
			if(groupList.get(idx).getId() == groupID){
				groupList.remove(idx);
				Logger.logInfo("eliminating membership from group G" + groupID) ;
				return;
			}
		}
		Logger.logInfo("This agent is not a member of group " + groupID ) ;
	}

	public boolean becomeGroupMember(Group gr, int normInitRepetition, int humanId){
		boolean alreadyAmember = false;
		for (Group myGrp : groupList) {			
			if(myGrp.getId() == gr.getId()){
				alreadyAmember = true;
				break;
			}
		}
		if(!alreadyAmember){
			groupList.add(gr);
			addNeighborhoodNorms(gr.getId(), normInitRepetition, humanId);
			return true;
		}else
			Logger.logInfo( "This agent is already a member of group " + gr.getId()+ " entitled " + gr.getTitle()) ;
		return false;
	}
	
	public boolean becomeGroupMemberByGroupName(String groupName, int normInitRepetition, int humanId) {
		leaveGroup(getLivingGroupId());
		Group group = null; 
//		Logger.logDebug("calling becomeGroupMemberByGroupName, gname " + groupName + ", norm");
	
		if(groupName!=null){
			if (groupName.equals(HouseType.CHEAP.name()))
				group =new Group(Constants.CHEAP_GROUP_ID,HouseType.CHEAP.name());
			else if(groupName.equals(HouseType.EXPENSIVE.name()) )
				group = new Group(Constants.EXPENSIVE_GROUP_ID,HouseType.EXPENSIVE.name());
			else if(groupName.equals(HouseType.STANDARD.name()))
				group = new Group(Constants.STANDARD_GROUP_ID,HouseType.STANDARD.name());
			else if(groupName.equals(HouseType.HOMELESS.name()) )
				group= new Group(Constants.HOMELESS_GROUP_ID,HouseType.HOMELESS.name());			
		}
		
		if(group != null)
			return becomeGroupMember(group, normInitRepetition, humanId);
		else
			Logger.logInfo("couldn't find the correct houseType " + groupName);
		return false;
	}


	public String getLivingGroupName() {
		int numOfGroups = 0;
		String groupTtl = "";
		for (Group myGrp : groupList) {
			groupTtl = myGrp.getTitle();
			if(groupTtl.equals(HouseType.CHEAP.name()) | 
					groupTtl.equals(HouseType.EXPENSIVE.name()) |
					groupTtl.equals(HouseType.HOMELESS.name()) |
					groupTtl.equals(HouseType.STANDARD.name()) ){
				numOfGroups ++;
			}				
		}
		if(numOfGroups <=1 )
			return groupTtl;
//		else
			Logger.logError(" This agent is a member of " + numOfGroups + " neighborhoods!" );
			return null;
	}
	
	public int getLivingGroupId() {
		int gId = -1;
		int numOfGroups = 0;
		String groupTtl = "";
		for (Group myGrp : groupList) {
			groupTtl = myGrp.getTitle();
			for (HouseType grName : HouseType.values()) {
				if(groupTtl.equals(grName.name())){
					numOfGroups++;
					gId = myGrp.getId();
					break;
				}
			}				
		}
		if(numOfGroups <=1 )
			return gId;
		Logger.logError("This agent is a member of " + numOfGroups + " neighborhoods!" );
		return -1;
	}

	public String getGroupListAsString(){
		String  returned = "[";
		for(Group g: groupList){
			returned += g.toString();
			returned += ";";
		}
		return returned.substring(0, returned.length()-1) + "]";
	}
	
	public String getNormListAsString(){
		String returned = "[";
		for(Collection<Norm> nrmCol: normList.values()){
			for(Norm nrm: nrmCol){
				returned += nrm.toString() + ";";				
			}
		}
		return returned.substring(0, returned.length()-1) + "]";
	}
	
	protected String getMinNotRepitiedNorm(int id, ArrayList<String> normativeActions, int groupID) {
		double minRepetition = Integer.MAX_VALUE;
		String returnedNormTitle = "";
		Logger.logDebug("H"+ id + " in getMinNotRepitiedNorm, normativeActions : " + normativeActions);
		for (Norm norm : normList.get(groupID)) {
			for (String normTtl : normativeActions) {
				if(norm.getTitle().equals(normTtl)){
					Logger.logDebug("H"+ id + " normTtl : " + normTtl);
					if(norm.getNoRepetition() < minRepetition){
						minRepetition = norm.getNoRepetition();
						returnedNormTitle = normTtl;
					}
				}						
			}
		}
		return returnedNormTitle;
	}
	
	protected String getMaxRepitiedNorm(ArrayList<String> normativeActions, int groupID) {
		double maxRepetition = -1;
		String returnedNormTitle = "";
		for (Norm norm : normList.get(groupID)) {
			for (String normTtl : normativeActions) {
				if(norm.getTitle().equals(normTtl)){
					if(norm.getRepetition() > maxRepetition){
						maxRepetition = norm.getRepetition();
						returnedNormTitle = normTtl;
					}
				}						
			}
		}
		return returnedNormTitle;
	}
	
	protected ArrayList<String> ifGroupDonationIsNormative( int id,
			double donationPercentage, int groupID) {
		//TODO: here the norm, which is saved in logical format, should be parsed
		//TODO: for now we look for the comparative sign and the number after that as we only have donation amount as a norm
		// we implemented a simple sample of it.
		Logger.logDebug("H" + id + " is checking normative action of his neighbors G" + groupID +", avgNeighbors " + donationPercentage);
		ArrayList<String> normativeActions  = null;
		normativeActions = findNormActionOfGroup(id, donationPercentage, groupID);
		if(normativeActions !=null && normativeActions.size() <= 0)			
			Logger.logAction("H" + id + "; There is no norm in group G" +groupID + " that matches the action");
		return normativeActions;
	}
	
	private ArrayList<String> findNormActionOfGroup(int id, double avgNeighborsDonationAmount, int groupID) {
		ArrayList<String> normativeActions = new ArrayList<String>();		
		Logger.logDebug("H"+ id + " normlist.get(G" + groupID + ") is " + normList.get(groupID));
		if(normList.get(groupID) != null)
		for (Norm norm : normList.get(groupID)) {
			if(norm == null | norm.equals("")) //he deons't have this norm for group groupID
				continue;
			Logger.logDebug("H" + id + " has norm " + norm.getTitle() + " repeated " + norm.getRepetition() + "; notRepited " + norm.getNoRepetition());
			String myNorm = norm.getTitle();
			double[] minMaxNormativeAmount = ParsNormLogic.getDonationAmount(myNorm);
//			String operator = ParsNormLogic.getDonationOperation(myNorm);			
			boolean minCondition = (minMaxNormativeAmount[0] >=0) ? avgNeighborsDonationAmount >= minMaxNormativeAmount[0] : true;
			boolean maxCondition = (minMaxNormativeAmount[1] >=0) ? avgNeighborsDonationAmount <= minMaxNormativeAmount[1] : true;
			Logger.logDebug("H" + id + " min " + minCondition + ", max " + maxCondition + ", minx " + minMaxNormativeAmount[0] + ", max " + minMaxNormativeAmount[1]) ;
			if(minCondition & maxCondition){
				normativeActions.add(myNorm);
				break;
			}
			else{
				//TODO:would it be a good idea to update norm here?!  
			}
		}
		return normativeActions;
	}
	
	public double[] calculatePreferenceAccordingToPreviousGroups(int agentId, int currentGroup) {
		//it is an array with 2 elements. the first one is probability and the second one is the amount
		double[] returnedArray = new double[2];// = {0.0,0.0};//[prob, normative amount]
		//calculate amount
		int maxRepeated = -1; int groupId = -1; int minNotRepeated = Integer.MAX_VALUE;
		Norm selectedNorm = null;
		//get the most repeated norm
		for (int grpId : getNormList().keySet()) {
			if(!hasGroup(grpId) && grpId != currentGroup){
				for (Norm norm : getNormList().get(grpId)) {
					if(norm.getRepetition() > maxRepeated){
						maxRepeated = norm.getRepetition();
						minNotRepeated = norm.getNoRepetition();
						selectedNorm = norm;
						groupId = grpId;
					}else if(norm.getRepetition() == maxRepeated){
						if(norm.getNoRepetition() < minNotRepeated){
							minNotRepeated = norm.getNoRepetition();
							selectedNorm = norm;
							groupId = grpId;
						}
					}
				}
			}
		}		
		if(selectedNorm != null){
			returnedArray[1] = getNormativeAmountFromRange(agentId, selectedNorm.getTitle());
			double prob = calculateFollowingNeighborsProbability(agentId, groupId, selectedNorm.getTitle());
			returnedArray[0] = prob;
		}else{
			returnedArray[0] = 0.0;
			returnedArray[1] = 0.0;
		}		
		return returnedArray;		
	}
	
	//returns a prob number in [0:1]
	public double calculateFollowingNeighborsProbability(int agentId, int neighborId, String normativeAction) {
		Logger.logInfo("H" + agentId + " is trying to calculateFollowingNeighborsProbability for G" + neighborId + " and norm " + normativeAction);
		int repetition = getRepetition(neighborId, normativeAction);
		int noRepetition = getNoRepetition(neighborId, normativeAction);
		double lastRepeatedProb = getLastRepeatedProbability(neighborId, normativeAction);
		double followNeighborsProbability = 0;
		//this function can be changed based on modelers preference
		if(normativeAction.equals(null) || normativeAction.equals("")){
			Logger.logDebug("H"+ agentId + ", normativeAction is null" );			
			return followNeighborsProbability;
		}if(repetition < 0 ){//norm list is null
			Logger.logError("H"+ agentId + " normList is null. repetition : " + repetition + " noRepetition : " + noRepetition);
			return followNeighborsProbability;
		}
		
		followNeighborsProbability = calculateProbAccordingToNormStage(agentId, noRepetition, repetition, lastRepeatedProb);
	
		if(repetition < 0 && noRepetition > 0)
			Logger.logDebug("H" + agentId + "; norm " + normativeAction + " repetition: " + repetition + "; noRepetition : " + noRepetition);
		return followNeighborsProbability;
	}
	
	private double calculateProbAccordingToNormStage(int agentId, int noRepetition, int repetition, double lastRepeatedProb) {
		double followNeighborsProbability;
		if(noRepetition == 0){
			if(repetition < Constants.T_ADOPTATION){
//					call observation function
				followNeighborsProbability = Norm.observationFunction(repetition);
				Logger.logDebug("H"+ agentId + " is in observation phase for normativeAction ");				
			} else if(repetition < Constants.T_INTERNALIZATION){
//					call internalization function
				Logger.logDebug("H"+ agentId + " is in adoptation phase for normativeAction " );
				followNeighborsProbability = Norm.adoptionFunction(repetition);
			}else{
//					call internalization function
				Logger.logDebug("H"+ agentId + " is in internalization phase for normativeAction ");
				followNeighborsProbability = Norm.internalizationFunction(repetition);
			}
		}else{
			//call disappearing function
			Logger.logDebug("H"+ agentId + " is in disappearing phase for normativeAction ");
			followNeighborsProbability = Norm.disappearingFunction(noRepetition, lastRepeatedProb);
		}
		return followNeighborsProbability;
	}

	public double getNormativeAmountFromRange(int agentId, String normTitle) {
		double[] minMaxNormativeAmount = ParsNormLogic.getDonationAmount(normTitle);
		if(minMaxNormativeAmount[0] >= 0 && minMaxNormativeAmount[1] >=0)
			return (minMaxNormativeAmount[0] + minMaxNormativeAmount[1])/2.0;
		if(minMaxNormativeAmount[0] < 0 && minMaxNormativeAmount [1] < 0){
			Logger.logError("H" + agentId + " is trying to getNormativeAmount of " + normTitle + ", which has upper and lowerbound less than 0");
			return -1;
		}
		return Math.max(minMaxNormativeAmount[0], minMaxNormativeAmount[1]);
	}
	
	protected boolean hasGroup(int gId){
		for (Group myGr : groupList) {
			if(myGr.getId() == gId)
				return true;			
		}
		return false;
	}
	private void addNeighborhoodNorms(int groupID, int normInitRepetition, int humanId) {
//		Logger.logDebug("becoming group member G" + groupID + " and trying to add norms. normList " + ((normList != null) ? normList.size() : "null"));
		Collection<Norm> norms = new ArrayList<Norm>();
			
		String livingGroupName = getLivingGroupName();
		String normTitle = getNormTitle( livingGroupName, humanId);
		Norm groupNorm = null;
		if(!normTitle.equals("")){
			groupNorm = new Norm((String) Constants.NORM_TYPE_LIST.get(0), normTitle, groupID);
			groupNorm.setRepetition(normInitRepetition);			
		}
		else
			groupNorm = null;
		if(groupNorm != null){
			norms.add(groupNorm);
			normList.put(groupID, norms);
			Logger.logDebug("becoming group member G" + groupID + " and added norms. normList " + ((normList != null) ? normList.size() : "null"));
		}
		else
			Logger.logInfo("joined group G"+ groupID + " which doesn't have any norm.");
	}
	
	private String getNormTitle(String livingGroupName, int humanId) {
		double currentTick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String returnedNormTitle = "";
		if (currentTick < 0 ){
			if(Constants.NORM_INIT_REPETITION == 0){
				Logger.logDebug("It's initialization time. Uses average value based preference");
				double[] avgDonationList = HumanUtils.avgValueBasedDonationOfNeighbors(humanId);
				returnedNormTitle = "|donation| = ";
				if(livingGroupName.equals(HouseType.CHEAP.name())){
					returnedNormTitle +=  avgDonationList[0];
				}else if(livingGroupName.equals(HouseType.STANDARD.name())){
					returnedNormTitle +=  avgDonationList[1];
				}else if(livingGroupName.equals(HouseType.EXPENSIVE.name())){
					returnedNormTitle +=  avgDonationList[2];
				}
			}
			else
			{
				if(livingGroupName.equals(HouseType.CHEAP.name())){
				returnedNormTitle =  Constants.CHEAP_DONATION_DEFAULT_NORM_TITLE;
			}else if(livingGroupName.equals(HouseType.STANDARD.name())){
				returnedNormTitle =  Constants.STANDARD_DONATION_DEFAULT_NORM_TITLE;
			}else if(livingGroupName.equals(HouseType.EXPENSIVE.name())){
				returnedNormTitle =  Constants.EXPENSIVE_DONATION_DEFAULT_NORM_TITLE;
			}
			}
		}else{
			Logger.logDebug("It's in the middle of the simulation. Uses average values");
			double[] avgDonationList = HumanUtils.avgDonationOfNeighbors(humanId);
			returnedNormTitle = "|donation| = ";
			if(livingGroupName.equals(HouseType.CHEAP.name())){
				returnedNormTitle +=  avgDonationList[0];
			}else if(livingGroupName.equals(HouseType.STANDARD.name())){
				returnedNormTitle +=  avgDonationList[1];
			}else if(livingGroupName.equals(HouseType.EXPENSIVE.name())){
				returnedNormTitle +=  avgDonationList[2];
			}
		}
		return returnedNormTitle;
	}

	public ArrayList<Group> getGroupList() {
		return groupList;
	}

	public void setGroupList(ArrayList<Group> groupList) {
		this.groupList = groupList;
	}

	public Map<Integer, Collection<Norm>> getNormList() {
		return normList;
	}

	public void setNormList(Map<Integer, Collection<Norm>> normList) {
		this.normList = normList;
	}	

	public String getRepetitionGroupNorm(int agentId, int neighborId,
			double donationAmount) {
		Logger.logDebug("in getRepetitionGroupNorm donationAmount is " + donationAmount);
		ArrayList<String> normativeActions = ifGroupDonationIsNormative(agentId, donationAmount, neighborId);
		ArrayList<String> notRepeatingNorms = getNotRepeatingNorms(neighborId, normativeActions);
		String returnedAction = "";		
		if (normativeActions != null && normativeActions.size() > 0)
			returnedAction = getMaxRepitiedNorm(normativeActions, neighborId);
		else if (notRepeatingNorms != null && notRepeatingNorms.size() > 0)
			returnedAction = getMinNotRepitiedNorm(agentId, notRepeatingNorms, neighborId);
		Logger.logDebug("H" + agentId + " updated norm repetition and returnedAction is : " + returnedAction);
		return returnedAction;
	}
	
		
	public void stepUpdateRepetitionNorms(int agentId, double donationAmount, int neighborId){
		Logger.logDebug("H"+ agentId + " is updating repetition of norms.");
		updateRepetitionGroupNorm(agentId, neighborId, donationAmount);
		updateRepetitionOfPrviousNorms(agentId, donationAmount, neighborId);
		
	}
	
	private ArrayList<String> getNotRepeatingNorms(int neighborId,
			ArrayList<String> normativeActions) {
		ArrayList<String> returnedVal = new ArrayList<String>();
		if(getNormList().get(neighborId) != null)
		for(Norm norm: getNormList().get(neighborId)){
			if(normativeActions ==null || !normativeActions.contains(norm.getTitle()) )
				returnedVal.add(norm.getTitle());
		}
		return returnedVal;
	}

	private void updateRepetitionGroupNorm(int agentId, int neighborId,
			double donationAmount) {
		ArrayList<String> normativeActions = ifGroupDonationIsNormative(agentId, donationAmount, neighborId);
		ArrayList<String> notRepeatingNorms = getNotRepeatingNorms(neighborId, normativeActions);
		String returnedAction = "";
		Logger.logDebug("H"+ agentId + " : neighborId " + neighborId + " normativeAct of neighbors : " + normativeActions.size() + " notRepeated norms : " + notRepeatingNorms.size());
		Logger.logDebug("H"+ agentId + " getNormListByGroupId(neighborId) is " + (getNormListByGroupId(neighborId) ==null ? "null" : (getNormListByGroupId(neighborId).size())));
		if(normativeActions != null && normativeActions.size() > 0){
			for(String normTtl : normativeActions){
				if(getNormListByGroupId(neighborId) != null && getNormListByGroupId(neighborId).size() !=0){
					increaseNormRepetitionByTitle(neighborId, normTtl);
					Logger.logDebug("H" + agentId + " normativeAct not null; norm has member : returendAction : " + returnedAction);				
				}
				else
					Logger.logError("H" +agentId + " has no norm for group " + neighborId);
			}			
		}
		if(notRepeatingNorms != null && notRepeatingNorms.size() > 0){
			for(String normTtle : notRepeatingNorms){
				resetExistingNormRepetition(agentId, neighborId, normTtle);
				Logger.logDebug("H" +agentId + " has " + notRepeatingNorms.size() + " norms assgined to group G" + neighborId + " that that has not been repeated");
			}
		}
		
	}
	
	protected String resetExistingNormRepetition(int agentId, int groupID, String normTtl) {
		String returnedNormTitle = null;
		System.out.println("H" + agentId + " in resetExistingNormRepetition " + this.normList.get(groupID).size());
		Norm norm = null;
		for (Norm nr : this.normList.get(groupID)) {
			if (nr.getTitle().equals(normTtl))
				norm = nr;
		}
		if(norm == null) return null;
		
		returnedNormTitle = norm.getTitle();
		System.out.println("H" + agentId + " normTitle " + returnedNormTitle + " type " + norm.getType() + " noRepetition time " + norm.getNoRepetition());
		if(norm.getNoRepetition() > Constants.T_DISAPPEARING){
//			norm.setRepetition(0);
//			norm.setNoRepetition(0);
			this.normList.remove(norm);
			//create new norm.
			addNeighborhoodNorms(groupID, Constants.NORM_REPETITION_NEW_MEMBER, agentId);
			//TODO: remove norm 
		}else{
//			norm.setLastRepetition(norm.getRepetition());
			norm.setLastRepeatedProb(norm.getRepetition(), norm.getNoRepetition());
			norm.setRepetition(0);
			norm.incraseNoRepetition();
		}				
		return returnedNormTitle;
	}
	
	public void increaseNormRepetitionByTitle(int groupID, String normTitle) {
		for (Norm nr : this.normList.get(groupID)) {
			Logger.logDebug("before increasing repetition : " + this.normList.get(groupID));
			if(nr.getTitle().equals(normTitle))
				nr.increaseRepeatingNorm();
			Logger.logDebug("after increasing repetition : " + this.normList.get(groupID));
		}
	}
	
	public void increaseNormRepetitionByType(int groupID, String normType) {
		for (Norm nr : this.normList.get(groupID)) {
			Logger.logDebug("before increasing repetition : " + this.normList.get(groupID));
			if(nr.getType().equals(normType))
				nr.increaseRepeatingNorm();
			Logger.logDebug("after increasing repetition : " + this.normList.get(groupID));
		}
	}
	
	public Collection<Norm> getNormListByGroupId(int groupID){
		return normList.get(groupID);
	}
	
	private void updateRepetitionOfPrviousNorms(int agentId, double normativeDonationAmount, int currentGroup) {
		for(int grId: getNormList().keySet()){
			if(grId == currentGroup || isMember(grId))
				continue;
			getRepetitionGroupNorm(agentId, grId, normativeDonationAmount);
		}
	}

	protected int getRepetition(int groupID, String normTitle) {
		if(normList != null){
			for (Norm nr : this.normList.get(groupID)) {
				if(nr.getTitle().equals(normTitle))
					return nr.getRepetition();
			}
		}
		return -1;
	}
	
	protected int getNoRepetition(int groupID, String normTitle) {
		for (Norm nr : this.normList.get(groupID)) {
			if(nr.getTitle().equals(normTitle))
				return nr.getNoRepetition();
		}
		return -1;
	}

	protected double getLastRepeatedProbability(int groupID, String normTitle){
		for (Norm nr : this.normList.get(groupID)) {
			if(nr.getTitle().equals(normTitle))
				return nr.getLastRepeatedProb();
		}
		return -1;
	}


}
