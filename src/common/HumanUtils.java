package common;

import java.util.ArrayList;

import population.Human;
import property.House;
import property.HouseType;
import property.Property;
import property.SocialCare;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import valueFramework.AbstractValue;

public final strictfp class HumanUtils {

	private static int newHumanId = 0;

	/**
	 * This returns a new Id. ++ is used after the variable to make sure
	 * the current newHumanId is returned
	 * @return a new unused id for a resident
	 */
	public static int getNewHumanId() {
		return newHumanId++;
	}

	public static void resetHumanId() {
		newHumanId = 0;
	}
	
	public static void setHumanId(int newHumanId) {
		HumanUtils.newHumanId = newHumanId;
	}
	
	public static Human getHumanById(int id) {
		
		ArrayList<Human> humans = SimUtils.getObjectsAllRandom(Human.class);
		for (Human human : humans) {
			if (human.getId() == id) {
				return human;
			}
		}
		Logger.logError("Human with id:" + id + " does not exist");
		return null;
	}
	
	/**
	 * The same as getHumanById but gives no error message when the human is not found
	 * @param id
	 * @return
	 */
	public static Human getHumanByIdNoException(int id) {
		
		ArrayList<Human> humans = SimUtils.getObjectsAllRandom(Human.class);
		for (Human human : humans) {
			if (human.getId() == id) {
				return human;
			}
		}
		return null;
	}
	
		
	/**
	 * Returns the house the agents lives in and other wise
	 * the homelesscare
	 * @return
	 */
	public static Property getLivingPlace(Human human) {
		return getOwnedHouse(human);			
	}
	
	public static Property getOwnedHouse(Human human) {
		
		for (Integer propertyId : human.getPropertyIds()) {
			House property = SimUtils.getHouseById(propertyId);
			return property;
		}		
		return SimUtils.getObjectsAll(SocialCare.class).get(0);
	}
	/**
	 * Returns the house the agents lives in and other wise
	 * the homeless care
	 * @return
	 */
	public static HouseType getLivingPlaceType(Human human) {
		Property prp = getOwnedHouse(human);
		if(prp instanceof House)
			return ((House)prp).getHouseType();
		return HouseType.HOMELESS;
	}
		
	public static void printAverageValues() {
		
		double u = 0;
		double p = 0;
		int count = 0;
		
		ArrayList<Human> residents = SimUtils.getObjectsAllRandom(Human.class);
		for (Human resident : residents) {
			u += resident.getThreshold(AbstractValue.UNIVERSALISM);
			p += resident.getThreshold(AbstractValue.POWER);
			count ++;
		}
		Logger.logExtreme("Average values for population: u:" + (u / count)
													+ ", p:" + (p / count));
	}
	
	
	public static boolean cellFreeOfHumans(GridPoint cellLocation) {

		Grid<Object> grid = (Grid<Object>) SimUtils.getGrid();
		Iterable<Object> objectsOnGrid = grid.getObjectsAt(cellLocation.getX(), cellLocation.getY());
		for (final Object object : objectsOnGrid) {
			if (object instanceof Human) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean cellFreeOfHumansExcluded(GridPoint cellLocation, Human humanExcluded) {
		
		Grid<Object> grid = (Grid<Object>) SimUtils.getGrid();
		Iterable<Object> objectsOnGrid = grid.getObjectsAt(cellLocation.getX(), cellLocation.getY());
		for (final Object object : objectsOnGrid) {
			if (object instanceof Human) {
				if ((Human) object != humanExcluded) {
					return false;
				}
			}
		}
		return true;
	}
	
	public double getNumberOfGrpupMates(int groupId, int humanId){
		ArrayList<Human> allResidents = SimUtils.getObjectsAll(Human.class);
		double numOfGroupmates = 0.0;
		for (Human person : allResidents) 
			if(person.getNormedDecisionMaker().isMember(groupId) && person.getId() != humanId)
				numOfGroupmates++;
		return numOfGroupmates;
	}
	
	private static double getAverageDonationAmountOfGroup(int groupId) {
		ArrayList<Human> allResidents = SimUtils.getObjectsAll(Human.class);
		double neighborsDonationAmount = 0.0;
		double numOfGroupmates = 0.0;
		for (Human person : allResidents) {
			if(person.getNormedDecisionMaker().isMember(groupId)){
					neighborsDonationAmount += person.getLastDonationPercentage();
					Logger.logDebug("H" + person.getId() + " in group G" + groupId + " last donation %" + person.getLastDonationPercentage());
					numOfGroupmates++;
				}
//			}
		}
		double avgDonationAmount = neighborsDonationAmount / numOfGroupmates;
		Logger.logDebug(", #avgDonationAmount " + avgDonationAmount + "in group G" + groupId);
		return avgDonationAmount;
	}
	
	/*public static double[] avgDonationOfNeighbors() {
		double[] returnedList = new double[HouseType.values().length];
		returnedList[Constants.CHEAP_GROUP_ID] = getAverageDonationAmountOfGroup(Constants.CHEAP_GROUP_ID);
		returnedList[Constants.STANDARD_GROUP_ID] = getAverageDonationAmountOfGroup(Constants.STANDARD_GROUP_ID);
		returnedList[Constants.EXPENSIVE_GROUP_ID] = getAverageDonationAmountOfGroup(Constants.EXPENSIVE_GROUP_ID);
		return returnedList;
	}*/
	
	public static double[] avgDonationOfNeighbors(int humanId) {
		double[] returnedList = new double[HouseType.values().length];
		returnedList[Constants.CHEAP_GROUP_ID] = getAverageDonationAmountOfGroup(Constants.CHEAP_GROUP_ID, humanId);
		returnedList[Constants.STANDARD_GROUP_ID] = getAverageDonationAmountOfGroup(Constants.STANDARD_GROUP_ID, humanId);
		returnedList[Constants.EXPENSIVE_GROUP_ID] = getAverageDonationAmountOfGroup(Constants.EXPENSIVE_GROUP_ID, humanId);
		return returnedList;
	}
	
	private static double getAverageDonationAmountOfGroup(int groupId, int humanId) {
		ArrayList<Human> allResidents = SimUtils.getObjectsAll(Human.class);
		double neighborsDonationPercentage = 0.0;
		double numOfGroupmates = 0.0;
		for (Human person : allResidents) {
			if(person.getNormedDecisionMaker().isMember(groupId) && person.getId() != humanId){
					neighborsDonationPercentage += person.getLastDonationPercentage();
//					Logger.logDebug("H" + person.getId() + " in group G" + groupId + " last donation %" + person.getLastDonationPercentage());
					numOfGroupmates++;
				}
//			}
		}
		double avgDonationPercentage = neighborsDonationPercentage / numOfGroupmates;
		Logger.logDebug("H" + humanId + " avgDonation %" + avgDonationPercentage + "in group G" + groupId);
		return avgDonationPercentage;
	}

	public static double[] avgValueBasedDonationOfNeighbors(int humanId) {
		double[] returnedList = new double[HouseType.values().length];
		returnedList[Constants.CHEAP_GROUP_ID] = getAverageValuedPreferenceOfGroup(Constants.CHEAP_GROUP_ID, humanId);
		returnedList[Constants.STANDARD_GROUP_ID] = getAverageValuedPreferenceOfGroup(Constants.STANDARD_GROUP_ID, humanId);
		returnedList[Constants.EXPENSIVE_GROUP_ID] = getAverageValuedPreferenceOfGroup(Constants.EXPENSIVE_GROUP_ID, humanId);
		return returnedList;
	}
	
	private static double getAverageValuedPreferenceOfGroup(int groupId, int humanId) {
		ArrayList<Human> allResidents = SimUtils.getObjectsAll(Human.class);
		double neighborsDonationPercentage = 0.0;
		Human candidate = null;
		double numOfGroupmates = 0.0;
		for (Human person : allResidents) {
			if(person.getId() != humanId){
			if(person.getNormedDecisionMaker().isMember(groupId)){
					neighborsDonationPercentage += person.calculateValueBasedDonationPercentage();
//					Logger.logDebug("H" + person.getId() + " in group G" + groupId + " last donation %" + person.getLastDonationPercentage());
					numOfGroupmates++;
				}
//			}
			}else
				candidate = person;
		}
		double avgDonationPercentage;
		if(numOfGroupmates == 0)
			avgDonationPercentage = candidate.calculateValueBasedDonationPercentage();
		else
			avgDonationPercentage = neighborsDonationPercentage / numOfGroupmates;
		Logger.logDebug("H" + humanId + " avg value-based preference %" + avgDonationPercentage + "in group G" + groupId);
		return avgDonationPercentage;
	}
}
