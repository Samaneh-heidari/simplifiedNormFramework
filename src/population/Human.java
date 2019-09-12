package population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sf.jasperreports.engine.ReturnValue;
import normFramework.Group;
import normFramework.Norm;
import normFramework.ParsNormLogic;
import property.House;
import property.HouseType;
import property.Property;
import common.Constants;
import common.HumanUtils;
import common.Logger;
import common.SimUtils;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import saf.v3d.scene.VSpatial;
import valueFramework.AbstractValue;
import valueFramework.NormedDecisionMaker;
import valueFramework.ValuedDecisionMaker;

public class Human {
	private final int id;
//	private double avgLivingGroupDonationPercentage; 	
	private double money;
	private HashMap<Integer, VSpatial> spatialImages = new HashMap<Integer, VSpatial>();//owning a house or homeless
	
	private ValuedDecisionMaker valuedDecisionMaker;
	private NormedDecisionMaker normedDecisionMaker;
	// Variable initialization
	private ArrayList<Integer> propertyIds = new ArrayList<Integer>();	
	protected ArrayList<String> agentInfo = new ArrayList<>();
	
	private double lastDonationPercentage;
	private double myValueBasedDonationPercentage;
	private double normBasedDonationPercentage;
	private double wage;
	
	private double joiningTime = -1;
	
	public Human(int id, double money, double wage, String values) {
		this.id = id;
		this.setMoney(money);		
		this.wage = wage;
		addToContext();		
		agentInfo.add("Tick,id,money,house,P Thr., U Thr.,s_don,s_free_ev");
		valuedDecisionMaker = new ValuedDecisionMaker(values);
		setNormedDecisionMaker(new NormedDecisionMaker());
	}

	public Human(int id2, double money2, double wage2, int livingPlaceId,
			double lastDonationPercentage2, String groupList2, String normList2, String values) {
		valuedDecisionMaker = new ValuedDecisionMaker(values);
		setNormedDecisionMaker(new NormedDecisionMaker(groupList2, normList2));
		id = id2;
		setMoney(money2);
		this.wage = wage2;
		lastDonationPercentage = lastDonationPercentage2;
		if(livingPlaceId >= 0) 
			propertyIds.add(livingPlaceId);		
		
	}

	private void addToContext() {
		SimUtils.getContext().add(this);		
		final NdPoint pt = SimUtils.getSpace().getLocation(this);
		if (!SimUtils.getGrid().moveTo(this, (int) pt.getX(), (int) pt.getY())) {
			Logger.logError("Human could not be placed, coordinate: " + pt.toString());
		}
	}
	
	public double getLastDonationPercentage() {
		return lastDonationPercentage;
	}

	public void setLastDonationPercentage(double donationPercent) {
//		lastDonationPercentage = donationAmount * 100.0 / getMoney();
		lastDonationPercentage = donationPercent;
//		Logger.logDebug("H" + getId() + " is setting last donation %" + lastDonationPercentage + " donationAmount" + donationPercent);		
	}
	
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}
	
	public String getPropertyIdsString() {
		
		String datum = "";
		for (Integer propertyId : propertyIds) {
			if (datum.equals("")) {
				datum += Integer.toString(propertyId);
			}
			else {
				datum += "," + Integer.toString(propertyId);
			}
		}		
		return datum;
	}
	
	public ArrayList<Integer> getPropertyIds() {
		return propertyIds;
	}

	public double getThreshold(AbstractValue value) {
		return valuedDecisionMaker.getValueThreshold(value);
	}

	public void initialHousingFromFile(int livingPlaceId){
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if(tick >= 1) return;
		ArrayList<House> availableHouses = SimUtils.getHousesAvailableAllRandom(House.class);
		House myHouse = null;
		
		for (int i = 0; i < availableHouses.size(); i++) {
			if(availableHouses.get(i).getId() == livingPlaceId){
				myHouse = availableHouses.get(i);
			}
		}
		if(myHouse == null){
			Logger.logError("H" + getId() + " wants to initialized livingPlaceId " + livingPlaceId + ". But it doens't exist.");
			return;
		}
		actionBuyHouse(myHouse);
	}

	public void actionBuyHouse(House hs) {
		int repetition = 0;
		boolean init = false;
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if(tick < 0){
			repetition = Constants.NORM_INIT_REPETITION;
			init = true;
		}else{
			repetition = Constants.NORM_REPETITION_NEW_MEMBER;
			init = false;
		}
		
		if(hs != null){
			connectProperty(hs.getId());
			if(normedDecisionMaker.becomeGroupMemberByGroupName(hs.getHouseType().name(), repetition, getId()))
				setJoiningTime(tick);
			if(init)
				setLastDonationPercentageFromInit(hs.getHouseType());
			//else use your own history
				
		}
		else{
			if(normedDecisionMaker.becomeGroupMemberByGroupName(HouseType.HOMELESS.name(), repetition, getId()))
				setJoiningTime(tick);
			Logger.logDebug("TODO: mush check here. why homeless gets chosen!");
		}
	}
		
	private void actionSellHouseByName(String myHouseTypeName) {
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		Logger.logAction("H" + getId() + " sells house");
		removeAndSellProperty(SimUtils.getPropertyByIdByOwnerId(this.getId()).getId());
		normedDecisionMaker.leaveGroup(getGroupIdByName(myHouseTypeName));
		if(normedDecisionMaker.becomeGroupMemberByGroupName(HouseType.HOMELESS.name(), 0, getId()))
			setJoiningTime(tick);
	}
	
	/**
	 * Remove and sells property
	 * @param propertyId
	 */
	public void removeAndSellProperty(int propertyId) {
		Logger.logDebug("H" + id + " remove property: " + propertyId);
		Property property = SimUtils.getPropertyById(propertyId);
		property.removeOwner(id);
		propertyIds.remove(propertyIds.indexOf(propertyId));		
	}
	
	private int getGroupIdByName(String houseName) {
		int groupId = -1;
		if (equals(HouseType.CHEAP.name()))
			groupId = Constants.CHEAP_GROUP_ID;
		else if(houseName.equals(HouseType.EXPENSIVE.name()) )
			groupId = Constants.EXPENSIVE_GROUP_ID;
		else if(houseName.equals(HouseType.STANDARD.name()))
			groupId = Constants.STANDARD_GROUP_ID;
		else if(houseName.equals(HouseType.HOMELESS.name()) )
			groupId = Constants.HOMELESS_GROUP_ID;
		return groupId;
	}
	
	public void connectProperty(int propertyId) {
		
		if (!propertyIds.contains(propertyId)) {
			SimUtils.getHouseById(propertyId).setOwner(id);
			propertyIds.add(propertyId);
			Logger.logDebug("H" + getId() + " connects to property"+ propertyId);
			return;
		}
		Logger.logError("House " + propertyId + " already contained in H" + id);
	}
		
	public Vector<ArrayList<House>> sortHousesByValue(
			ArrayList<House> availableHouses, boolean oneHouseType) {
		Vector<ArrayList<House>> sortedVector = null;
		String imprtValName = valuedDecisionMaker.getTheMostImportantValuesTitle();
		Logger.logDebug("H" + getId() + " the most important value is : " + imprtValName);
		String[] neighborHoodPriority = null;
		boolean ifHighValueAvailable = true;
		
		if(imprtValName.equals(AbstractValue.POWER.name()))
			neighborHoodPriority = Constants.POWER_NEIGHBORHOOD_PRIORITY;
		else if(imprtValName.equals( AbstractValue.UNIVERSALISM.name()))
			neighborHoodPriority = Constants.UNIVERSALISM_NEIGHBORHOOD_PRIORITY;
		else
			ifHighValueAvailable = false;
		
		if(ifHighValueAvailable){
			if(!oneHouseType){
				sortedVector = sortHousesByValueName(availableHouses, neighborHoodPriority);
			}
			else{
				String[] oneNeighborhood = {neighborHoodPriority[0]};
				sortedVector = sortHousesByValueName(availableHouses, oneNeighborhood);			
			}
		}
		else{
			sortedVector = new Vector<ArrayList<House>>();
			ArrayList<House> temporal = new ArrayList<House>();
			for (int j = 0; j < availableHouses.size()/2; j++) {
				temporal.add(availableHouses.get(j));
			}
			sortedVector.add(temporal);
			ArrayList<House> temporal2 = new ArrayList<House>();
			for (int j = availableHouses.size()/2 +1; j < availableHouses.size(); j++) {
				temporal2.add(availableHouses.get(j));
			}
			sortedVector.add(temporal2);
		}				

		return sortedVector;		
	}

	

	private Vector<ArrayList<House>> sortHousesByValueName(
			ArrayList<House> availableProperties,
			String[] neighborhoodPriority) {
		Vector<ArrayList<House>> sortedVector = new Vector<ArrayList<House>>();
		
		for (int i = 0; i < neighborhoodPriority.length; i++) {
			ArrayList<House> temporal = new ArrayList<House>();
			for (int j = 0; j < availableProperties.size(); j++) {
				if(availableProperties.get(j).getHouseType().name().equals(neighborhoodPriority[i]))
					temporal.add(availableProperties.get(j));
			}
			sortedVector.add(temporal);
		}
		return sortedVector;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	public void setValueTreesFromData(List<String> wVars) {
		valuedDecisionMaker.setValueTreesFromData(wVars);		
	}

	/*public double getMyValueBasedDonationAmount() {
		return myValueBasedDonationAmount;
	}

	public void setMyValueBasedDonationAmount(double valueBasedDonation) {
		this.myValueBasedDonationAmount = valueBasedDonation;
	}
*/
	public ValuedDecisionMaker getValuedDecisionMaker() {
		return valuedDecisionMaker;
	}

	public void setValuedDecisionMaker(ValuedDecisionMaker valuedDecisionMaker) {
		this.valuedDecisionMaker = valuedDecisionMaker;
	}

	public void stepLocation() {
		updateLocation();
	}
	
	/**
	 * TODO for now this function makes sure Humans are moved when they are on top of
	 * each other
	 */
	public void updateLocation() {//TODO: agents need to move when they change their hosue. not every tick
		
		final Grid<Object> grid = SimUtils.getGrid();
		GridPoint newLocation = grid.getLocation(this);
		
		double currentTick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if (currentTick % 2 == 0 | currentTick < 0) {//TODO: they 
			HouseType livingPlaceType = HumanUtils.getLivingPlaceType(this);
			if(livingPlaceType!= null ){
				Property livingPlace = HumanUtils.getLivingPlace(this);
				if (livingPlace != null) {
					newLocation = livingPlace.getFreeLocationExcluded(this);
					if (newLocation == null)  {
//						livingPlace.getLocation();
						Logger.logDebug("H" + id + " living place is full: " + livingPlace.getName());
					}
				}
				else {
					Logger.logError("H" + id + " has no living place");
				}
			}
			
		}
		if(newLocation!=null)
		grid.moveTo(this, newLocation.getX(), newLocation.getY());
		
	}

	public String getHumanVarsAsString() {		
		return getId() + "," + getMoney() + "," + wage + "," + getLivingPlaceId() + "," + normedDecisionMaker.getGroupListAsString() + "," + normedDecisionMaker.getNormListAsString() + "," + valuedDecisionMaker.getThresholdsAsString(); 
	}
	
	public int getLivingPlaceId(){
		return HumanUtils.getLivingPlace(this).getId();
	}
	
	@SuppressWarnings("rawtypes")
	public void stepHousing() {
		Logger.logDebug("H" + getId() +" start of stepHousing livingPlace is " + HumanUtils.getLivingPlaceType(this));
		boolean condition = true;//RandomHelper.nextDouble() > Constants.HUMAN_PROB_GET_HOUSE/* && !HumanUtils.isOwningHouse(this)*/;
		ArrayList<House> availableProperties = SimUtils.getHousesAvailableAllRandom(House.class);
		Vector<ArrayList<House>> availablePropertiesSorted = sortHousesByValue(availableProperties, false);
		List[] controlledSortedAvailableHouses = new List[2];
		if(Constants.CONTROLLED_ENVIRONMENT)
			controlledSortedAvailableHouses = sortAccordingToControlledConditions(availablePropertiesSorted);					
		if(condition){
			housing(controlledSortedAvailableHouses);
		}
		if(!HumanUtils.getLivingPlaceType(this).name().equals(getLivingGroupName()))
			Logger.logError("H" + getId() + " has different group name than living group!" );
		Logger.logDebug("H" + getId() + " end of stepHousing livingPlace is " + HumanUtils.getLivingPlaceType(this).name() + " Group " + normedDecisionMaker.getLivingGroupName());
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void housing(/*Vector<ArrayList<House>>*/ List[] availableProperties){
		Vector<ArrayList<House>> joiningProperties = (Vector<ArrayList<House>>) availableProperties[0];
		ArrayList<String> leavingProperties = (ArrayList<String>) availableProperties[1];
		
		outerloop:
		for(ArrayList<House> houseArr : joiningProperties){
//			Logger.logDebug("H" + getId() + " wants to get a house " + houseArr.toString());		
			for (House house : houseArr) {
				if (updatePopulationTiming(house.getHouseType().name(), getLivingGroupName()) == 0) {//buy
					actionBuyHouse(house);
					break outerloop;			
				}
			}
		}
		if(leavingProperties != null && leavingProperties.size() != 0)	
			for(String houseTypes : leavingProperties){
				int updateHousing = updatePopulationTiming(houseTypes, getLivingGroupName());
				if ( updateHousing == 1) {//sell
					Logger.logDebug("H" + getId() + " wants to leave a house " + houseTypes);		
					actionSellHouseByName(houseTypes);
					return;
				}				
				Logger.logInfo("H" + getId() + " doesn't have proper housing actions! " + updateHousing);
			}			
	}

	private int updatePopulationTiming(String houseTypeName, String currentHouseType) {
		int idxValueSetting = Arrays.asList(Constants.VALUE_ORDERS_IN_CONTROLLED_SETTINGS_LIST).indexOf(valuedDecisionMaker.getTheMostImportantValuesTitle());
		int returnedValue = -1;
		for(String ctrlJoin : Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME){
			String[] ctrlJoinArr = ctrlJoin.split(",");
			int indexOfnewChange = Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME.indexOf(ctrlJoin);
			if(ctrlJoinArr[0].equals(houseTypeName)){
				Logger.logDebug("H" + getId() + " get ctrlJoin " + ctrlJoin);
				int possiblePopSize = new Integer(ctrlJoinArr[idxValueSetting+2]);						
				if(currentHouseType.equals(HouseType.HOMELESS.name()) && !houseTypeName.equals(HouseType.HOMELESS.name())) {//join
					if(possiblePopSize > 0){
						possiblePopSize --;
						ctrlJoinArr[idxValueSetting+2] = Integer.toString(possiblePopSize);
						String newPopTimeString = ctrlJoinArr[0];
						for(int i = 1; i < ctrlJoinArr.length; i++)
							newPopTimeString += "," + ctrlJoinArr[i];
						if(possiblePopSize == 0)
							Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME.remove(ctrlJoin);
						else
							Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME.set(indexOfnewChange, newPopTimeString);
						Logger.logDebug("H" + getId() + " joined group "+ houseTypeName);
						returnedValue = 0;
						break;
					}
				}
				else if(!currentHouseType.equals(HouseType.HOMELESS.name()) && houseTypeName.equals(currentHouseType)) {//leave
					if(possiblePopSize < 0){
						possiblePopSize ++;
						ctrlJoinArr[idxValueSetting+2] = Integer.toString(possiblePopSize);
						String newPopTimeString = ctrlJoinArr[0];
						for(int i = 1; i < ctrlJoinArr.length; i++)
							newPopTimeString += "," + ctrlJoinArr[i];
						if(possiblePopSize == 0)
							Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME.remove(ctrlJoin);
						else
							Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME.set(indexOfnewChange, newPopTimeString);
						Logger.logDebug("H" + getId() + " left group "+ houseTypeName);
						returnedValue = 1;
						break;
					}
				}
			}					
		}
		return returnedValue;
	}

	public void stepWork() {
		money += wage;
	}

	public void stepDonate() {
		
		ArrayList<String> possibleActions = new ArrayList<String>();
//		possibleActions.add("Donate nothing");
		if ((getMoney() > 0 && getMoney() > Constants.MONEY_DANGER_LEVEL) || getMoney() > Constants.DONATE_MONEY_MINIMUM_SAVINGS_WITHOUT_INCOME) {
			possibleActions.add("Donate to council");
			Logger.logDebug("H" + getId() + " added donate to council as a possible action");
		}
		else {
			Logger.logInfo("H" + getId() + " donation not possible, not enough money or income");
			return ;
		}
		Logger.logAction("H" + getId() + " possible actions: " + possibleActions);
		
		/*****************
		 * normative decision
		*****************/		
		int neighborId = normedDecisionMaker.getLivingGroupId();
		normedDecisionMaker.stepUpdateRepetitionNorms(getId(), getAvgLivingGroupDonationPercentage(), neighborId);
		
		double donationPercent = calculateNormativeDonationPercentage();
		if (donationPercent != 0.0) {
			setLastDonationPercentage(donationPercent);
			executeActionDonate(donationPercent * getMoney()/100.0);
//			setAvgLivingGroupDonationPercentage(donationAmount);			
		}
		else {
			Logger.logInfo("H " + getId() + " nothing to donate!");
		}
	}
	

	public double calculateValueBasedDonationPercentage() {
		double donationpercentage = -1.0;
		String imprtValName = valuedDecisionMaker.getTheMostImportantValuesTitle();
		double threshold = valuedDecisionMaker.getValueThresholdByName(imprtValName);
		if ((getMoney() > 0 && (getMoney() > Constants.MONEY_DANGER_LEVEL)) || getMoney() > Constants.DONATE_MONEY_MINIMUM_SAVINGS_WITHOUT_INCOME) 
		{
			double myMoney = getMoney();
			double donationAmount = -1;
			if(imprtValName.equals(AbstractValue.UNIVERSALISM.name()))
				donationAmount =  myMoney * threshold / 100.0;
			if(imprtValName.equals(AbstractValue.POWER.name()))
				donationAmount =  myMoney * (100 - threshold) / 100.0;	
			donationpercentage = donationAmount * 100.0/myMoney;
			Logger.logInfo("H" + this.getId() + " wants to donate, %"+ donationpercentage + " = $" + donationAmount + " according to his values");
		}
		else
		{
			donationpercentage = 0.0;
			Logger.logError("H" + this.getId() + " has $ " + this.getMoney() + " not enough for donation");
		}
		return donationpercentage;
	}
	
	private double calculateNormativeDonationPercentage() {
		int neighborId = normedDecisionMaker.getLivingGroupId();
		double[] prvGrpNormProbAmt = normedDecisionMaker.calculatePreferenceAccordingToPreviousGroups(getId(), neighborId);
		//it is an array with 2 elements. the first one is probability and the second one is the norm
		boolean ifLivingIndependent = (neighborId == 0 | neighborId == 1 | neighborId == 2);
		//TODO:gourpIds needs to be constants
		double normativeDonationPercentage = 0;
		boolean condition = (ifLivingIndependent && getMoney() > 0 && (getMoney() > Constants.MONEY_DANGER_LEVEL)) || getMoney() > Constants.DONATE_MONEY_MINIMUM_SAVINGS_WITHOUT_INCOME;
		Logger.logDebug("H" + getId() + " has $" + getMoney() + ", living independent : " + ifLivingIndependent);
		if(ifLivingIndependent ){		
			if(condition){
				double myValueBasedDonationPercentage = calculateValueBasedDonationPercentage();
				setMyValueBasedDonationPercentage(myValueBasedDonationPercentage);
//			    Logger.logDebug("H" + getId() + " is living independently in G" + neighborId );
				//calculating norm repetition
				String normativeAction = normedDecisionMaker.getRepetitionGroupNorm(getId(), neighborId, getAvgLivingGroupDonationPercentage());
				double groupNormativePercentage = normedDecisionMaker.getNormativeAmountFromRange(getId(), normativeAction);
				setNormBasedDonationPercentage(groupNormativePercentage);
				//calculating follow_percentage neighbors or personal preference
				double followNeighborsProbability = normedDecisionMaker.calculateFollowingNeighborsProbability(getId(), neighborId, normativeAction);
				double followPreferenceProb = 1.0-followNeighborsProbability;
				Logger.logDebug("H" + getId() + ", followNeighborsProb = " + followNeighborsProbability);
				double considerPrvGrProb = prvGrpNormProbAmt[0] * Constants.CONSIDERING_PREVIOUS_GROUPS_PERCENTAGE;
				double considerValueBasedProb = followPreferenceProb - considerPrvGrProb;
				
				//calculating normative donation amount
				normativeDonationPercentage = followNeighborsProbability * groupNormativePercentage +
										  considerValueBasedProb * myValueBasedDonationPercentage +
										  considerPrvGrProb * prvGrpNormProbAmt[1];
				Logger.logInfo("H" + getId() + " normativeDoantionPercentage is : " + normativeDonationPercentage);
				Logger.logInfo("H" + getId() + " followNeighborPro " + followNeighborsProbability + " groupNorm " + groupNormativePercentage);
				Logger.logInfo("H" + getId() + " valueBasedProb " + considerValueBasedProb + " valuebased% " + myValueBasedDonationPercentage);
				Logger.logInfo("H" + getId() + " prvGroupProb "+ considerPrvGrProb + " prvGroupNorm " + prvGrpNormProbAmt[1]);
			}else
			{
				normativeDonationPercentage = 0.0;
				Logger.logError("H" + this.getId() + " has $ " + this.getMoney() + " not enough for donation");
			}
			
		}
		else
			System.out.println("H" + getId() + " is living with others or homeless, G" + neighborId);
		return normativeDonationPercentage;
	}

	private void executeActionDonate(double donationAmount) {
		this.money -= donationAmount;
	}
	
	public NormedDecisionMaker getNormedDecisionMaker() {
		return normedDecisionMaker;
	}

	public void setNormedDecisionMaker(NormedDecisionMaker normedDecisionMaker) {
		this.normedDecisionMaker = normedDecisionMaker;
	}

	public VSpatial getSpatialImage() {
		int livingPlace = normedDecisionMaker.getLivingGroupId();
		if(livingPlace < 0)
			return spatialImages.get(1);
		return spatialImages.get(0);
	}

	public void setSpatialImages(HashMap<Integer, VSpatial> spatialImages) {
		this.spatialImages = spatialImages;	
	}

	public String getLabel() {

		return Integer.toString(id) + "|" + wage;
	}

	public String getLivingGroupName() {
		int numOfGroups = 0;
		String groupTtl = "";
		for (Group myGrp : normedDecisionMaker.getGroupList()) {
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
			Logger.logError("H" + getId() + " is a member of " + numOfGroups + " neighborhoods!" );
			return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public List[] sortAccordingToControlledConditions(
			Vector<ArrayList<House>> availablePropertiesSorted) {
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String imprtValName = valuedDecisionMaker.getTheMostImportantValuesTitle();
		int idxValueSetting = Arrays.asList(Constants.VALUE_ORDERS_IN_CONTROLLED_SETTINGS_LIST).indexOf(imprtValName);
//		Logger.logDebug("H" + getId() + ", idxValueSetting " + idxValueSetting);
		
		ArrayList<String> controlledJoiningHouseTp = new ArrayList<String>();		
		List[] allControlloedHouses = new List[2];
		List[] returnedValue = new List[2];
		allControlloedHouses[0] = new Vector<ArrayList<House>>();
		allControlloedHouses[1] = new ArrayList<String>();
		if(tick < 1)//initial condition
			controlledJoiningHouseTp = createListOfInitialHousing();
		else{//rest of the simulation
			allControlloedHouses = createListOfPossibleHousing();
			controlledJoiningHouseTp = (ArrayList<String>) allControlloedHouses[0];
		}
		controlledJoiningHouseTp.add(HouseType.HOMELESS.name());
		//remove unwanted houseTypes from the list
//		Logger.logDebug("H" + getId() + " availableSorted before : " + availablePropertiesSorted.toString());
		for(ArrayList<House> houseArr : availablePropertiesSorted){
			Iterator<House> hsArrItr = houseArr.iterator();
			while (hsArrItr.hasNext()) {
				House hs = hsArrItr.next();
//				Logger.logDebug("H" + getId() + " houseType " + hs.getHouseType().name() + ", in the array : " + controlledHouseTypes.toString());
				if(!controlledJoiningHouseTp.contains(hs.getHouseType().name()))
					hsArrItr.remove();
			}
		}
		returnedValue[0] = availablePropertiesSorted;
		returnedValue[1] = allControlloedHouses[1];
//		Logger.logDebug("type check availablePropertiesSorted : "+ availablePropertiesSorted.getClass().getTypeName());
//		Logger.logDebug("type check allControlloedHouses : "+ allControlloedHouses[1].getClass().getTypeName());
		Logger.logDebug("H" + getId() + " availableSorted after : " + availablePropertiesSorted.toString());		
		return returnedValue;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List[] createListOfPossibleHousing() {
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String imprtValName = valuedDecisionMaker.getTheMostImportantValuesTitle();
		int idxValueSetting = Arrays.asList(Constants.VALUE_ORDERS_IN_CONTROLLED_SETTINGS_LIST).indexOf(imprtValName);
		List[] allControlloedHouses = new List[2];		
		allControlloedHouses[0] = new ArrayList<String>();
		allControlloedHouses[1] = new ArrayList<String>();
		for(String ctrlJoin : Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME){
			Logger.logDebug("H" + getId() + " createListOfPossibleHousing- get ctrlJoin "+ ctrlJoin);
			String[] ctrlJoinArr = ctrlJoin.split(",");
			if(new Integer(ctrlJoinArr[1]) >= tick && new Integer(ctrlJoinArr[1]) <= tick + Constants.TICKS_PER_MONTH){
				int possiblePopSize = new Integer(ctrlJoinArr[idxValueSetting+2]);
				if(possiblePopSize > 0){//should join
					Logger.logDebug("H" + getId() + " might join group " + ctrlJoin);
					((ArrayList<String>)allControlloedHouses[0]).add(ctrlJoinArr[0]);		
				}				
				else if (possiblePopSize < 0){//should leave
					Logger.logDebug("H" + getId() + " might leave the group : " + ctrlJoin);
					((ArrayList<String>)allControlloedHouses[1]).add(ctrlJoinArr[0]);
				}
			}
			else
				break;				
		}
		return allControlloedHouses;
	}

	private ArrayList<String> createListOfInitialHousing() {
		String imprtValName = valuedDecisionMaker.getTheMostImportantValuesTitle();
		int idxValueSetting = Arrays.asList(Constants.VALUE_ORDERS_IN_CONTROLLED_SETTINGS_LIST).indexOf(imprtValName);
		ArrayList<String> controlledHouseTypes = new ArrayList<String>();		
		for(int idx = 0; idx < Constants.INITIAL_GROUPS_VALUED_SIZE.length; idx ++){
			String groupSize = Constants.INITIAL_GROUPS_VALUED_SIZE[idx];
			String[] valuedPopSize = groupSize.split(",");
			if(!valuedPopSize[idxValueSetting].equals("0")){
				controlledHouseTypes.add(Constants.GROUP_ORDERS_IN_CONTROLLED_SETTINGS_LIST[idx]);
			}
		}
		return controlledHouseTypes;
	}

	public double getAvgLivingGroupDonationPercentage() {
		double[] avgDonationList = HumanUtils.avgDonationOfNeighbors(getId());
		if(getLivingGroupName().equals(HouseType.CHEAP.name())){
			return  avgDonationList[0];
		}else if(getLivingGroupName().equals(HouseType.STANDARD.name())){
			return  avgDonationList[1];
		}else if(getLivingGroupName().equals(HouseType.EXPENSIVE.name())){
			return  avgDonationList[2];
		}
//		Logger.logError("H" + getId() + " cannot find his neighborhood! " + getLivingGroupName());
		Logger.logDebug("H"+ getId() + " averageDonation : " + Arrays.toString(avgDonationList));
		return -1;
//		return avgLivingGroupDonationPercentage;
	}

	/*public void setAvgLivingGroupDonationPercentage(
			double avgLivingGroupDonationPercentage) {
		this.avgLivingGroupDonationPercentage = avgLivingGroupDonationPercentage;
	}*/

	public void setLastDonationPercentageFromInit(HouseType house) {
		double[] avgDonationList = null;
		if(house.name().equals(HouseType.CHEAP.name())){
			avgDonationList = ParsNormLogic.getDonationAmount(Constants.CHEAP_DONATION_DEFAULT_NORM_TITLE);
		}else if(house.name().equals(HouseType.STANDARD.name())){
			avgDonationList = ParsNormLogic.getDonationAmount(Constants.STANDARD_DONATION_DEFAULT_NORM_TITLE);
		}else if(house.name().equals(HouseType.EXPENSIVE.name())){
			avgDonationList = ParsNormLogic.getDonationAmount(Constants.EXPENSIVE_DONATION_DEFAULT_NORM_TITLE);
		}		
		
		if(avgDonationList[0]!=0.0){
			if(avgDonationList[1]!= 0.0){
				lastDonationPercentage = (avgDonationList[0] + avgDonationList[1]) /2.0;
				Logger.logDebug("min!=0 and max!=0");
			}
			else
				
				lastDonationPercentage = avgDonationList[0];
		}
		else{
			if(avgDonationList[1]!= 0.0)
				lastDonationPercentage = avgDonationList[1];
			else
				lastDonationPercentage = 0.0;
		}
		
		Logger.logDebug("H" + getId() + " setinitDonation. HouseType " + house.name() + " avgDonationPars " + Arrays.toString(avgDonationList));
		Logger.logDebug("H" + getId() + " set initi donation%" + lastDonationPercentage);
	}
	
	public String printNorm(){
		String returnedValue = "";
		for(int groupId: normedDecisionMaker.getNormList().keySet()){
			returnedValue += getId() + "," + groupId;
			for(Norm n: normedDecisionMaker.getNormList().get(groupId)){
				returnedValue +=  "," + n.getTitle();
			}
			returnedValue += "\n";
		}
		return returnedValue;
	}

	public String printLivingGroupDonationNorm(){
		String returnedValue = "";
		Collection<Norm> normList = normedDecisionMaker.getNormList().get(normedDecisionMaker.getLivingGroupId());
		if(normList == null) return null;
		for(Norm n: normList){
			returnedValue +=  n.getTitle() + ",";
		}
		if(returnedValue.length() > 0)
			returnedValue = returnedValue.substring(0, returnedValue.length()-1) + "\n"; 
		return returnedValue;
	}
	
	
	public int getLivingGroupId(){
		return normedDecisionMaker.getLivingGroupId();
	}
	
	public double getJoiningTime() {
		return joiningTime;
	}

	public void setJoiningTime(double joiningTime) {
		this.joiningTime = joiningTime;
	}

	public double getMyValueBasedDonationPercentage() {
		return myValueBasedDonationPercentage;
	}

	public void setMyValueBasedDonationPercentage(
			double myValueBasedDonationPercentage) {
		this.myValueBasedDonationPercentage = myValueBasedDonationPercentage;
	}

	public double getNormBasedDonationPercentage() {
		return normBasedDonationPercentage;
	}

	public void setNormBasedDonationPercentage(double normBasedDonationPercentage) {
		this.normBasedDonationPercentage = normBasedDonationPercentage;
	}
	
}
