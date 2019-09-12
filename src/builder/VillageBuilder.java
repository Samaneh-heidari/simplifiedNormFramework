package builder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import population.Human;
import property.House;
import property.HouseType;
import property.SocialCare;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.grid.GridPoint;
import sun.util.logging.resources.logging;
import common.Constants;
import common.HumanUtils;
import common.Logger;
import common.SimUtils;

public class VillageBuilder {

//	private static Map<String, Integer> globalValueTrees = new HashMap<String, Integer>();
//	private static int valueNumber = 0;
	
	public void buildVillage() {
		createHouses();
		new SocialCare(SimUtils.getNewPropertyId(), new GridPoint(Constants.GRID_VILLAGE_START + 25, 2));
//		new DataForGraph();
	}

	/*public void createHouses2() {		
		Logger.logMain("Create groups location. location names: Groupless, Big, Meduim, Small group");
		double pop_total = Constants.TOTAL_NUMBER_OF_POPULATION + Constants.NUMBER_OF_GROUPLESS;
		double pop_big = Constants.NUMBER_OF_BIG_GROUP_VACANCIES;
		double pop_medium = Constants.NUMBER_OF_MEDIUM_GROUP_VACANCIES;
		double pop_small = Constants.NUMBER_OF_SMALL_GROUP_VACANCIES;
		double pop_groupless = Constants.NUMBER_OF_GROUPLESS;
		double vertial_rows = Constants.NUMBER_OF_ROWS_GROUP_LOCATION;
		double distance = Constants.DISTANCE_BETWEEN_GROUPS_LOCATIONS;
		double location_size = Constants.LOCATION_DIMENTION;
		
		double max_horizontal_size_of_group = Math.ceil(pop_total / (vertial_rows ));
		double max_horizontal_size = max_horizontal_size_of_group * (distance + location_size);
		
		int last_x = 0;
		double big_x = (int) Math.ceil((pop_big-max_horizontal_size_of_group)/max_horizontal_size_of_group * location_size);
		int x_max_big = (big_x) >= 0 ? (int)(distance + last_x + (Math.ceil(big_x)+1)*location_size) : -1;
		int x_min_big = (int) (distance + last_x);		
		last_x = x_max_big >= 0 ? x_max_big : last_x;
		
		double med_x = (int) Math.ceil((pop_medium-max_horizontal_size_of_group)/max_horizontal_size_of_group * location_size);
		int x_max_medium = (med_x >= 0) ? (int)(distance + last_x + (Math.ceil(med_x)+1)*location_size) : -1;
		int x_min_medium = (int) (distance + last_x);
		last_x = x_max_medium >= 0 ? x_max_medium : last_x;
		
		double small_x = (int) Math.ceil((pop_small-max_horizontal_size_of_group)/max_horizontal_size_of_group * location_size);
		int x_max_small = (small_x >= 0) ? (int)(distance + last_x + (Math.ceil(small_x)+1)*location_size) : -1;
		int x_min_small = (int) (distance + last_x);
		last_x = x_max_small >= 0 ? x_max_small : last_x;
		
		int x_max = (int) (3*distance + max_horizontal_size_of_group * location_size);//3 is the number of vertical groups
		int y_horizontal = (int) Math.ceil(pop_groupless/x_max);
		int y_max = (int) (y_horizontal + 2*distance + max_horizontal_size); 
		
		Logger.logDebug("gridsize : " + Constants.GRID_WIDTH + ", " + Constants.GRID_HEIGHT);
		Logger.logMain("Create " + Constants.NUMBER_OF_GROUPLESS + " groupless agents' place");
		for (int y = y_max-1; y >= y_horizontal; y-=location_size) {
			for(int x = 0; x <= x_max-1 ; x+=location_size){
				Logger.logDebug("Location: "+ x + ", " + y);
				final GridPoint location = new GridPoint(x, y);
				new House(SimUtils.getNewPropertyId(), HouseType.HOMELESS, location);
			}
		}
		
		Logger.logMain("Create " + Constants.NUMBER_OF_BIG_GROUP_VACANCIES + " CHEAP houses");
		if(x_max_big >= x_min_big){
			for(int x = x_min_big; x <= x_max_big; x+=location_size){
				for(int y = 0; y <= max_horizontal_size_of_group-distance; y+=location_size){
					final GridPoint location = new GridPoint(x,y);
					new House(SimUtils.getNewPropertyId(), HouseType.CHEAP, location);
				}
			}
		}
		
		Logger.logMain("Create " + Constants.NUMBER_OF_MEDIUM_GROUP_VACANCIES + " STANDARD houses");
		if(x_max_medium >= x_min_medium){
			for(int x = x_min_medium; x <= x_max_medium; x+=location_size){
				for(int y = 0; y <= max_horizontal_size_of_group-distance; y+=location_size){
					final GridPoint location = new GridPoint(x,y);
					new House(SimUtils.getNewPropertyId(), HouseType.CHEAP, location);
				}
			}
		}
		
		
		Logger.logMain("Create " + Constants.NUMBER_OF_SMALL_GROUP_VACANCIES + " EXPENSIVE houses");
		if(x_max_small >= x_min_small){
			for(int x = x_min_small; x <= x_max_small; x+=location_size){
				for(int y = 0; y <= max_horizontal_size_of_group-distance; y+=location_size){
					final GridPoint location = new GridPoint(x,y);
					new House(SimUtils.getNewPropertyId(), HouseType.EXPENSIVE, location);
				}
			}
		}
		
	}
*/
	public void createHouses() {
		
		Logger.logMain("Create " + Constants.NUMBER_OF_HOUSES_CHEAP + " cheap houses");
		int x = 0, y = 0;
		for (int i = 0; i < Constants.NUMBER_OF_HOUSES_CHEAP; ++i) {
			
			final GridPoint location = new GridPoint(Constants.GRID_VILLAGE_START + 1 + x * 5, 1 + y * 3);
			new House(SimUtils.getNewPropertyId(), HouseType.CHEAP, location);
			if (y == 7) {
				y = 0;
				x ++;
			}
			else {
				y ++;
			}
		}
		
		Logger.logMain("Create " + Constants.NUMBER_OF_HOUSES_STANDARD + " standard houses");
		x = 0;
		y = 0;
		for (int i = 0; i < Constants.NUMBER_OF_HOUSES_STANDARD; ++i) {
			
			final GridPoint location = new GridPoint(Constants.GRID_VILLAGE_START + Constants.NUMBER_OF_HOUSES_CHEAP + x * 6, 2 + y * 5);
			new House(SimUtils.getNewPropertyId(), HouseType.STANDARD, location);
			if (y == 4) {
				y = 0;
				x ++;
			}
			else {
				y ++;
			}
		}
		
		Logger.logMain("Create " + Constants.NUMBER_OF_HOUSES_EXPENSIVE + " expensive houses");
		x = 0;
		for (int i = 0; i < Constants.NUMBER_OF_HOUSES_EXPENSIVE; ++i) {
			
			final GridPoint location = new GridPoint(Constants.GRID_VILLAGE_START + 1 + x * 7, Constants.GRID_HEIGHT - 6);
			new House(SimUtils.getNewPropertyId(), HouseType.EXPENSIVE, location);
			x ++;
		}
	}

	public List<String> readFile(String filePathAndName) {
		BufferedReader reader;
		List<String> data = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(filePathAndName));
			String line = reader.readLine();
			while (line != null) {
				data.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void generatePopulation(String filePath, String fileName) {
		
		List<String> dataAll = readFile(filePath + "/" + fileName + ".txt");
		List<String> dataHumans = new ArrayList<String>();
		List<String> dataOwners = new ArrayList<String>();
		List<String> valueTrees = new ArrayList<String>();
		int typeOfData = -1;
		for (String datum : dataAll) {
			if (!datum.startsWith("%")) {
				switch(typeOfData) {
				case 0:	dataHumans.add(datum); break;
				case 2:	dataOwners.add(datum); break;
				case 3:	valueTrees.add(datum); break;
				default: //Nothing
				}
			}
			else {
				typeOfData = Integer.parseInt(datum.substring(1,2));
			}
		}
		
		generateHumen(dataHumans);
		generateHumenArrays(dataOwners, valueTrees);
	}
	
	public void generateHumenArrays(List<String> dataOwners, List<String> valueTrees) {
				
		for (String propertyString : dataOwners) {
			List<String> pVars = Arrays.asList(propertyString.split(","));
			Human owner = HumanUtils.getHumanById(Integer.parseInt(pVars.get(0)));
			for (int i = 1; i < pVars.size(); i ++) {
				owner.connectProperty(Integer.parseInt(pVars.get(i)));
			}
		}
		
		for (String valueTreeString : valueTrees) {
			List<String> wVars = Arrays.asList(valueTreeString.split(","));
			Human resident = HumanUtils.getHumanById(Integer.parseInt(wVars.get(0)));
			resident.setValueTreesFromData(wVars);//TODO:tobe checked
		}		
	}

	public void generateHumen(List<String> dataHumans) {
		
		int maxHumanId = -1;
		for (String humanString : dataHumans) {
			List<String> hVars = Arrays.asList(humanString.split(","));
			int id = Integer.parseInt(hVars.get(0));
			double money = Double.parseDouble(hVars.get(1));
			double wage = Double.parseDouble(hVars.get(1));
			int livingPlaceId = Integer.parseInt(hVars.get(3));
			double lastDonationPercentage = Double.parseDouble(hVars.get(4));
			String groupList = hVars.get(5); //groupIdseparated by ; TODO: check when writing the file, only include groupIds
			String normList = hVars.get(6);
			String values = hVars.get(7);
			
			Human resident = new Human(id, money, wage, livingPlaceId, lastDonationPercentage, groupList, normList,values);
			resident.initialHousingFromFile(livingPlaceId);
			Logger.logInfo("Initialized H" + resident.getId() + " $money : " + resident.getMoney());
			if (maxHumanId < id)
				maxHumanId = id;
		}
		HumanUtils.setHumanId(maxHumanId + 1);
	}

	@SuppressWarnings("unchecked")
	public void initialHousing(Human human) {
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if(tick >= 1) return;
		ArrayList<House> availableProperties = SimUtils.getHousesAvailableAllRandom(House.class);
		
		Vector<ArrayList<House>> availablePropertiesSorted = human.sortHousesByValue(availableProperties, false);
		if(Constants.CONTROLLED_ENVIRONMENT)
			availablePropertiesSorted = (Vector<ArrayList<House>>) human.sortAccordingToControlledConditions(availablePropertiesSorted)[0];		
		Logger.logDebug("H" + human.getId() + ", availableProperties () " + availableProperties.toString());
		House myHouse = human.getValuedDecisionMaker().chooseHouse(availablePropertiesSorted);
		human.actionBuyHouse(myHouse);
	}
	
}
