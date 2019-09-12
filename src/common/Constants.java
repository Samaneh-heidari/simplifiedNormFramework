package common;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import normFramework.Group;
import property.HouseType;
import valueFramework.AbstractValue;

public final class Constants {
	// Initialize graphics
	public static final Font FONT_SMALL = new Font("Tahoma", Font.PLAIN , 10);
	
	// Initialize icon paths
	public static final String ICON_HAPPY = "./icons/happy_human.png";
	public static final String ICON_SAD = "./icons/sad_human.png";
	public static final String ICON_HOUSE = "./icons/house.png";
	public static final String ICON_OWNED = "./icons/owned.png";
	public static final String ICON_NOT_OWNED = "./icons/not_owned.png";
	
	// Initialize important IDs
	public static final String ID_CONTEXT = "normframework";
	public static final String ID_SPACE = "space";
	public static final String ID_GRID = "grid";
	public static final String ID_VALUE_LAYER = "value layer";
	
	// Initialize population generate parameters
	public static final int HUMAN_INIT_STARTING_MONEY = 100000;
	public static final int HUMAN_INIT_MAX_WAGE = 5000;
	public static final int HUMAN_INIT_MIN_WAGE = 5000;
	
	//initialize population parameter 
	public static final double HUMAN_PROB_GET_HOUSE = 0.5;
	public static final double MONEY_DANGER_LEVEL = 1000;
	public static final double DONATE_MONEY_MINIMUM_SAVINGS_WITHOUT_INCOME = 10000;
	
	/*
	 * group ids
	 */
	public static final String[] GROUP_NAMES = {HouseType.CHEAP.name(), HouseType.STANDARD.name(), HouseType.EXPENSIVE.name(), HouseType.HOMELESS.name()}; 
	public static final int CHEAP_GROUP_ID = 0;
	public static final int STANDARD_GROUP_ID = 1;
	public static final int EXPENSIVE_GROUP_ID = 2;
	public static final int HOMELESS_GROUP_ID = 3;
	
	// Initialize building parameters
	/*public static final int NUMBER_OF_HOUSES_CHEAP = 100;
	public static final int NUMBER_OF_HOUSES_STANDARD = 50;
	public static final int NUMBER_OF_HOUSES_EXPENSIVE = 10;	*/
	
	
	/*
	 * values and prioritized neighborhoods
	 */
	public static final String[] POWER_NEIGHBORHOOD_PRIORITY = {HouseType.EXPENSIVE.name(), HouseType.STANDARD.name(), HouseType.CHEAP.name()};
	public static final String[] UNIVERSALISM_NEIGHBORHOOD_PRIORITY = {HouseType.CHEAP.name(), HouseType.STANDARD.name(), HouseType.EXPENSIVE.name()};

	/*
	 * variables of function of following neighbors behavior percentage over time
	 * take a look at read me.docx for more info
	 */
	public static final int T_OBSERVATION = 0; //t_0
	public static final int T_ADOPTATION = 5; //t_1
	public static final int T_INTERNALIZATION = 10; //t_2
	public static final int T_DISAPPEARING = 50; //t_3
	public static final double FOLLOW_NORM_PROBABILITY_OSERVATION_PHASE = 0.0;//y_0
	public static final double FOLLOW_NORM_PROBABILITY_ADAPTATION_PHASE = 0.005; //y_1
	public static final double FOLLOW_NORM_PROBABILITY_INTERNALIZATION_PHASE = 0.7; //y_3
	public static final double SLOP_OBSERVATION_PHASE = ((FOLLOW_NORM_PROBABILITY_ADAPTATION_PHASE - FOLLOW_NORM_PROBABILITY_OSERVATION_PHASE)/(T_ADOPTATION - T_OBSERVATION));

	/* 
	 * probability of considering previous groups norms in decisions
	 * and
	 * probability of considering other groups norms in decisions.
	 */
	public static final double CONSIDERING_PREVIOUS_GROUPS_PERCENTAGE = 0.30;
//	public static final double CONSIDERING_OTHER_GROUPS_PERCENTAGE = 0.0;
	
	
	/*
	 * default values for donation norm of each group:
	 * norms can be dynamically changed. These would be the defualt interpretation of donation norm of each group
	 */	
	public static final List NORM_TYPE_LIST = Collections.unmodifiableList(new ArrayList<String>() {{
	    add("ACTION");
	}});
	
	public static final int NORM_REPETITION_NEW_MEMBER = 0;
	public static final double NORMATIVE_DONATION_RANGE = 10;
	
	// Initialize groups size parameters
	public static final double TOTAL_NUMBER_OF_POPULATION = 200;

	//old parameters
	public static final int NUMBER_OF_HOUSES_CHEAP = 100;
	public static final int NUMBER_OF_HOUSES_STANDARD = 20;
	public static final int NUMBER_OF_HOUSES_EXPENSIVE = 10;

	// initialize group location parameters
	public static final double NUMBER_OF_ROWS_GROUP_LOCATION = 5;
	public static final double DISTANCE_BETWEEN_GROUPS_LOCATIONS = 2;
	
	// Initialize world parameters
	public static final int LOCATION_DIMENTION = 5;
	public static final int GRID_WIDTH = 200;//NUMBER_OF_HOUSES_CHEAP * 2 + NUMBER_OF_HOUSES_STANDARD + NUMBER_OF_HOUSES_EXPENSIVE;
	public static final int GRID_HEIGHT = 50;
	public static final int GRID_CELL_SIZE = 25;
	public static final int GRID_VILLAGE_START = 18;
	public static final int GRID_SEA_START = GRID_WIDTH - 12;
//	public static final double NEW_RESIDENT_PROB = 0.05;
	public static final int TICKS_PER_MONTH = 4; // If you change this make sure you change the interval parameters of the repast charts
	public static final int TICKS_PER_YEAR = 12 * TICKS_PER_MONTH;
	
	
	//re-housing
	/*
	 * Controlling the environmental changes
	 */
	public static final boolean CONTROLLED_ENVIRONMENT = true;
	//(UNIVERSALISM, TRADITION, POWER, SELF-DIRECTION)
	public static final String[] VALUE_ORDERS_IN_CONTROLLED_SETTINGS_LIST = {AbstractValue.UNIVERSALISM.name(),AbstractValue.POWER.name()};
	public static final String[] GROUP_ORDERS_IN_CONTROLLED_SETTINGS_LIST = {HouseType.CHEAP.name(), HouseType.STANDARD.name(), HouseType.EXPENSIVE.name()};
	
	public static final String INITIAL_GROUP_SIZE_VALUED_POPULATION_CHEAP = "50,50";//"49,0";
	public static final String INITIAL_GROUP_SIZE_VALUED_POPULATION_STANDARD = "0,0";
	public static final String INITIAL_GROUP_SIZE_VALUED_POPULATION_EXPENSIVE = "5,5";//"4,0";
	public static String[] INITIAL_GROUPS_VALUED_SIZE;
	
	public static final int[] INITIAL_VALUED_POPULATION_ORIGINAL = {(int) (TOTAL_NUMBER_OF_POPULATION * 0.60),(int) (TOTAL_NUMBER_OF_POPULATION * 0.40)};//be careful, the summation should be the initial population size 
	public static int[] INITIAL_VALUED_POPULATION_COPY ;//be careful, the summation should be the initial population size 

	private static final String[] VALUE_POWER_UNIVERSALIS_THRESHOLD  = {"5,95"/*"45,55","45,55","20,80", "10,90", "5,95", "15,85" ,"35,65", "25,75"*/};
	private static final String[] VALUE_UNIVERSALISM_POWER_THRESHOLD = {"95,5"/*"55,45","80,20", "90,10", "95,5", "85,15", "75,25", "65,35"*/};
	public static final String[][] VALUE_THRESHOLDS = {VALUE_UNIVERSALISM_POWER_THRESHOLD, VALUE_POWER_UNIVERSALIS_THRESHOLD};

	public static final int NORM_INIT_REPETITION = 0;
	public static final char[] VALID_OPERATORS = {'>','=','<'};
	public static final String CHEAP_DONATION_DEFAULT_NORM_TITLE = "|donation| = 0";//percentage
	public static final String STANDARD_DONATION_DEFAULT_NORM_TITLE = "|donation| = 0";
	public static final String EXPENSIVE_DONATION_DEFAULT_NORM_TITLE = "|donation| = 0";
	
	public static final ArrayList<String> CONTROLLED_JOIN_GROUP_POPULATION_TIME_ORIGINAL = 
			//group name, tick, u, p. it need be sorted based on tick
			new ArrayList<String>(Arrays.asList(
		/*HouseType.CHEAP.name()+ ","+"50,0,1",
		HouseType.EXPENSIVE.name()+ ","+"50,0,1"*/
		/*HouseType.CHEAP.name()+ ","+"50,0,50",
		HouseType.EXPENSIVE.name()+ ","+"50,0,4"*/
		/*HouseType.CHEAP.name()+ ","+"5,0,5",
		HouseType.EXPENSIVE.name()+ ","+"5,0,1",
		HouseType.CHEAP.name()+ ","+"52,0,5",
		HouseType.EXPENSIVE.name()+ ","+"52,0,1",
		HouseType.CHEAP.name()+ ","+"120,0,5",
		HouseType.EXPENSIVE.name()+ ","+"120,0,1",
		HouseType.CHEAP.name()+ ","+"200,0,5",
		HouseType.EXPENSIVE.name()+ ","+"200,0,1",
		HouseType.CHEAP.name()+ ","+"360,0,5",
		HouseType.EXPENSIVE.name()+ ","+"360,0,1",
		HouseType.CHEAP.name()+ ","+"420,0,5",
		HouseType.CHEAP.name()+ ","+"420,0,5",
		HouseType.CHEAP.name()+ ","+"550,0,5",
		HouseType.CHEAP.name()+ ","+"620,0,5",
		HouseType.CHEAP.name()+ ","+"690,0,5",
		HouseType.CHEAP.name()+ ","+"760,0,5",
		HouseType.CHEAP.name()+ ","+"810,0,5",
		HouseType.CHEAP.name()+ ","+"880,0,5",
		HouseType.CHEAP.name()+ ","+"950,0,5"*/
		/*HouseType.CHEAP.name()+ ","+"9,0,10",
		HouseType.EXPENSIVE.name()+ ","+"9,0,1",
		HouseType.CHEAP.name()+ ","+"220,0,10",//20%
		HouseType.EXPENSIVE.name()+ ","+"220,0,1",
		HouseType.CHEAP.name()+     "," + "440,0,10"*//*,
		HouseType.EXPENSIVE.name()+ "," + "120,0,1",
		HouseType.CHEAP.name()+     "," + "140,-1,0",
		HouseType.EXPENSIVE.name()+ "," + "145,1,0",
		HouseType.EXPENSIVE.name()+ ","+"170,-3,0",
		HouseType.CHEAP.name()+ ","+"175,2,0"*//*,
		HouseType.CHEAP.name()+ ","+"170,-30,0",
		HouseType.EXPENSIVE.name()+ ","+"170,-3,0",
		HouseType.CHEAP.name()+ ","+"250,-40,0",//40%
		HouseType.EXPENSIVE.name()+ ","+"250,-4,0" //40%
		*/));
	public static ArrayList<String> CONTROLLED_JOIN_GROUP_POPULATION_TIME;
	public static final ArrayList<Integer> TIME_TO_GET_HISTOGRAM = new ArrayList<Integer>(Arrays.asList
			(/*2,10,20,30,50,70,100,120,140,160,180,200,220,240,260,280,300,320,330,350,370,390,410,430,450,
					460,480,500,520,530,550,570,890,600,620,640,670,690,710,720,790,810,830,850,860,
					880,900,920,930,950,970,1000,1050,1100*/
					2,5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100,105,110,115,120,125,130,135,
					140,145,150,155,160,165,170,175,180,185,190,195,200,300,305,310,315,320,325,330,335,340,345,
					350,400,500,600,710,810,900,
					1000,1050,1100));
	public static final int HISTOGRAM_DONATION_CLUSTER_SIZE = 5;
}
