package builder;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import population.Human;
import property.HouseType;
import common.Constants;
import common.HumanUtils;
import common.Logger;
import common.RepastParam;
import common.SimUtils;
import batch.BatchRun;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.valueLayer.GridValueLayer;

public class NormFrameworkContextBuilder implements ContextBuilder<Object> {
	@Override
	public Context<Object> build(Context<Object> context) {
		System.out.println("it's building");
		// Batch run
		boolean initFramework = BatchRun.setEnable(false);
		// Reset human id
		HumanUtils.resetHumanId();
		SimUtils.resetPropertyId();
		Logger.enableLogger();
		
		if (BatchRun.getEnable()) {
			BatchRun.setRepastParameters();
			Logger.setLoggerAll(true, false, false, false, false, false);
//			Log.disableLogger();
		}
		else {
			RepastParam.setRepastParameters();
			Logger.setLoggerAll(true, true, false, true, true, false);
//					Log.disableLogger();
		}
		
		// Add context to this ID
		Logger.logMain("Set context ID and add context to context");
		context.setId(Constants.ID_CONTEXT);
		context.add(this); //this can be removed if there is no ScheduledMethod in this contextbuilder
		
		// Create space and grid
		Logger.logMain("Create continuous space and grid");
		@SuppressWarnings("unused")
		final ContinuousSpace<Object> space = createContinuousSpace(context);
		@SuppressWarnings("unused")
		final Grid<Object> grid = createGrid(context);

		// Create value layer
		final GridValueLayer valueLayer = createValueLayer();
		context.addValueLayer(valueLayer);
		generateNature(valueLayer);
		
		// Set Context for SimUtils
		SimUtils.setContext(context);
		SimUtils.getGrid();
		SimUtils.getValueLayer();
		
		// Create village
		VillageBuilder villageBuilder = new VillageBuilder();
		villageBuilder.buildVillage();
		
		// Create data collector
		new DataCollector(new GridPoint(2,2));
		
		//initialize parameters:
		initalizeParameters();
		
		// Create population
		initializePopulation();
		
//		generateGroups();
		
		updateWorldToRunningCondition();//TODO:batch run related
		
		return context;
	}
	
	private void initalizeParameters() {
		Constants.INITIAL_GROUPS_VALUED_SIZE = new String[] {Constants.INITIAL_GROUP_SIZE_VALUED_POPULATION_CHEAP,
				Constants.INITIAL_GROUP_SIZE_VALUED_POPULATION_STANDARD,
				Constants.INITIAL_GROUP_SIZE_VALUED_POPULATION_EXPENSIVE};
		Constants.INITIAL_VALUED_POPULATION_COPY = Constants.INITIAL_VALUED_POPULATION_ORIGINAL.clone();//be careful, the summation should be the initial population size
		Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME = new ArrayList<String>();
		for(String st: Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME_ORIGINAL)
			Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME.add(st);
	}

	/*
	 * Runs a fullStep, apart from the scheduler
	 * Used to generate a starting population that has some properties/children
	 * @param tick the current tick
	 */
	@ScheduledMethod(start = 1, interval = 1, priority = 0)
	public void fullStep() {
		
		Logger.logMain("Run fullstep");
		int pauseRunTick = RepastParam.getSimulationPauseInt();
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

		Logger.logMain("------------------------------------------------------------------------------");
		Logger.logMain("0TICK: Starting tick: "+ tick );
		
		/*if (tick % Constants.TICKS_PER_YEAR == 1) {
			step1Year();
		}
		step2Tick();*/
		if (tick % Constants.TICKS_PER_MONTH == 1) {
//			step3Month();
			stepHousing();
		}
		step4Tick();
		/*if (tick % Constants.TICKS_PER_MONTH == 1) {
			step5Month();
		}*/
		step6Tick();
		
		int stopYear = RepastParam.getPopGenTickLimit();
		boolean saveToFile = RepastParam.getPopGenToFile();
		if ((tick == pauseRunTick && pauseRunTick >= 1) || (saveToFile && (tick == Constants.TICKS_PER_YEAR * stopYear))) {
			
			if (BatchRun.getEnable()) {
				BatchRun.saveRunData();
				RunEnvironment.getInstance().endRun();
				Logger.logExtreme("End simulation at : " + pauseRunTick + ", batch run: " + BatchRun.getRunNumber());
				Logger.logExtreme("------------------------------------------------------------------------------");
			}
			else {
//				SimUtils.getDataCollector().saveMigrationData();
				Logger.logMain("Save migration data");
				Logger.logMain("------------------------------------------------------------------------------");
				RunEnvironment.getInstance().pauseRun();
				Logger.logMain("Pause simulation at : " + pauseRunTick);
				Logger.logMain("------------------------------------------------------------------------------");
			}
		}
	}
		
	private ContinuousSpace<Object> createContinuousSpace(final Context<Object> context) {
		
		final ContinuousSpace<Object> space = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null).
				createContinuousSpace( 	Constants.ID_SPACE, context,
										new RandomCartesianAdder<Object>(),
										new repast.simphony.space.continuous.BouncyBorders(),
										Constants.GRID_WIDTH, Constants.GRID_HEIGHT);
		return space;
	}
	
	private Grid<Object> createGrid(final Context<Object> context) {
		
		final Grid<Object> grid = GridFactoryFinder.createGridFactory(null).createGrid(
										Constants.ID_GRID, context,
										new GridBuilderParameters<Object>(
										new repast.simphony.space.grid.BouncyBorders(),
										new SimpleGridAdder<Object>(), true,
										Constants.GRID_WIDTH, Constants.GRID_HEIGHT));
		return grid;
	}

	private GridValueLayer createValueLayer() {
		
		final GridValueLayer valueLayer = new GridValueLayer(
		        Constants.ID_VALUE_LAYER,
		        true,
		        new repast.simphony.space.grid.BouncyBorders(),
		        Constants.GRID_WIDTH,
		        Constants.GRID_HEIGHT);
		return valueLayer;
	}

	private void generateNature(GridValueLayer valueLayer) {
		
		for (int i = 0; i < Constants.GRID_WIDTH; i ++) {
			for (int j = 0; j < Constants.GRID_HEIGHT; j ++) {
				
				if (i < Constants.GRID_VILLAGE_START) { // Rubble
					valueLayer.set(RandomHelper.nextDoubleFromTo(2.9, 2.99), i, j);
				}
				else if (i < Constants.GRID_SEA_START) { // Grass
					valueLayer.set(RandomHelper.nextDoubleFromTo(0.95, 0.99), i, j);
				}
				else { // Water
					valueLayer.set(RandomHelper.nextDoubleFromTo(1.9, 1.99), i, j);
				}
			}
		}
	}
	
	private void initializePopulation() {
		
		boolean initializePopulationFromFile = RepastParam.getPopInitFromFile();
		VillageBuilder villageBuilder = new VillageBuilder();
		
		
		if (initializePopulationFromFile) {//TODO: to be checked
			String fileName = RepastParam.getPopInitFileName();
			Logger.logMain("Initialize " + Constants.TOTAL_NUMBER_OF_POPULATION + " humans from file : ./output/" + fileName + ".txt");
			villageBuilder.generatePopulation("./output", fileName);
		}
		else
		{
			Logger.logMain("Initialize " + Constants.TOTAL_NUMBER_OF_POPULATION + " humans from scratch");
			for (int i = 0; i < Constants.TOTAL_NUMBER_OF_POPULATION; ++i) {	
				// Humans are automatically added to the context and placed in the grid
				String values = getInitialValue();
				Human human = new Human(HumanUtils.getNewHumanId(), 
						RandomHelper.nextDoubleFromTo(0, Constants.HUMAN_INIT_STARTING_MONEY),
						RandomHelper.nextDoubleFromTo(Constants.HUMAN_INIT_MIN_WAGE, Constants.HUMAN_INIT_MAX_WAGE), values);
				Logger.logInfo("Create H" + human.getId() + ", values: " + values );
			}
			Logger.logDebug("initialize housing");
			for (final Human human: SimUtils.getObjectsAllRandom(Human.class)){				
				villageBuilder.initialHousing(human);
			}			
		}
		
		HumanUtils.printAverageValues();
		// Do a location step
		step6Tick();
	}
	
	private String getInitialValue() {
		ArrayList<Integer> possibleValueSelections = new ArrayList<Integer>();
		String selectedValue = "";
		for(int i = 0 ; i < Constants.INITIAL_VALUED_POPULATION_COPY.length; i++){
			if(Constants.INITIAL_VALUED_POPULATION_COPY[i] > 0)
				possibleValueSelections.add(i);
		}
		if(possibleValueSelections.size() >=1){
			Collections.shuffle(possibleValueSelections);
			String[] possibleThresholds = Constants.VALUE_THRESHOLDS[possibleValueSelections.get(0)];
			int thresholdIdx = RandomHelper.nextIntFromTo(0, possibleThresholds.length-1);
			selectedValue = possibleThresholds[thresholdIdx];
			Constants.INITIAL_VALUED_POPULATION_COPY[possibleValueSelections.get(0)]--;
		}
		return selectedValue;
		
	}

	private void updateWorldToRunningCondition() {
		Logger.logMain("Running condition: " + BatchRun.getRunningCondition().name());
		switch(BatchRun.getRunningCondition()) {
		case NONE:
			return;
		case NO_DONATION:
			//See resident
			break;
		}
	}
	
	/*
	 * Norm related
	 */
	
	/*public void generateGroups() {		
		Logger.logMain("Generating Groups Based on Living Place");
		ArrayList<Human> allHuman = SimUtils.getObjectsAll(Human.class);
		for (Human hmn : allHuman) {
			HouseType house = HumanUtils.getLivingPlaceType(hmn);
			Logger.logDebug("H"+ hmn.getId() + " is generating group : house " + house + ", name " + house.name());
			hmn.getNormedDecisionMaker().becomeGroupMemberByGroupName(house.name(), Constants.NORM_INIT_REPETITION, hmn.getId());
			hmn.setLastDonationPercentageFromInit(house);
		}
	}*/
	

	
	/**
	 * Step 3 month: housing
	 */
	//@ScheduledMethod(start = 1, interval = Constants.TICKS_PER_MONTH, priority = -2)
	/*public void step3Month() {
		
		Logger.logMain("3MONTH: housing");
		
		final ArrayList<Human> residents = SimUtils.getObjectsAllRandom(Human.class);
		Logger.logMain("- Run Human.stepHousing");
		for (final Human resident: residents) {
			HouseType oldLivingPlace = HumanUtils.getLivingPlaceType(resident);
//			Logger.logDebug("H" + resident.getId() + ", step3Month(), oldLivingPlace : " + oldLivingPlace.name() + " Group " + resident.getLivingGroupName());
			resident.stepHousing();
		}	
	}*/
	
	public void stepHousing() {
		/*int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		
		Logger.logMain(" housing");
		for(String ctrlJoin : Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME){
			String[] ctrlJoinArr = ctrlJoin.split(",");
			if(new Integer(ctrlJoinArr[1]) >= tick && new Integer(ctrlJoinArr[1]) <= tick + Constants.TICKS_PER_YEAR){
				if(!ctrlJoinArr[idxValueSetting+2].equals("0"))
					controlledHouseTypes.add(ctrlJoinArr[0]);
				Constants.CONTROLLED_JOIN_GROUP_POPULATION_TIME.remove(ctrlJoin);					
			}
			else
				break;				
		}*/
		
		
		
		final ArrayList<Human> residents = SimUtils.getObjectsAllRandom(Human.class);
		Logger.logMain("- Run Human.stepHousing");
		for (final Human resident: residents) {
//			HouseType oldLivingPlace = HumanUtils.getLivingPlaceType(resident);
//			Logger.logDebug("H" + resident.getId() + ", step3Month(), oldLivingPlace : " + oldLivingPlace.name() + " Group " + resident.getLivingGroupName());
			resident.stepHousing();
		}	
	}

	/**
	 * Step 4 week: working and social events
	 */
	//@ScheduledMethod(start = 1, interval = 1, priority = -3)
	public void step4Tick() {
		
		Logger.logMain("4TICK: work and donation");		
		
		final ArrayList<Human> residents = SimUtils.getObjectsAllRandom(Human.class);
		Logger.logMain("- Run Human.stepDonate");
//		double[] avgDonationList = HumanUtils.avgDonationOfNeighbors();
//		int livingGroupId = -1;
		for (final Human resident: residents) {
//			livingGroupId = resident.getNormedDecisionMaker().getLivingGroupId();
			/*if(livingGroupId >= 0)
				resident.setAvgLivingGroupDonationPercentage(avgDonationList[resident.getNormedDecisionMaker().getLivingGroupId()]);
			*/
			resident.stepWork();
			resident.stepDonate();
		}
	}

	/**
	 * Step 6 tick: movement
	 */
	//@ScheduledMethod(start = 1, interval = 1, priority = -5)
	public void step6Tick() {
	
		if (!BatchRun.getEnable()) {
			
			Logger.logMain("6TICK: human location");
			final ArrayList<Human> residents = SimUtils.getObjectsAllRandom(Human.class);
			Logger.logMain("- Run Human.stepLocation");
			for (final Human resident: residents) {
				resident.stepLocation();
			}

			Logger.logMain("------------------------------------------------------------------------------");
			Logger.logMain("End of this step");
		}
		
		// checks whether to save the population to a file
		int tick = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		savePopulation(tick);
	}
		
	private void savePopulation(int tick) {
		
		int stopYear = RepastParam.getPopGenTickLimit();
		Logger.logDebug("stopYear/ " + stopYear);
		boolean saveToFile = RepastParam.getPopGenToFile();
		Logger.logDebug("savePopulation, stopYear : " + stopYear + ", saveToFile : " + saveToFile);

		if (saveToFile && (tick == Constants.TICKS_PER_YEAR * stopYear)) {
			String fileName = RepastParam.getPopGenFileName();
			Logger.logMain("------------------------------------------------------------------------------");
			Logger.logMain(stopYear + " years have passed, save population in file: ./output/" + fileName + ".txt");
			savePopulation("./output", fileName);
			RunEnvironment.getInstance().pauseRun();
			Logger.logMain("------------------------------------------------------------------------------");
		}
	}
	
	public void savePopulation(String filePath, String fileName) {
		// Data humans
		List<String> dataHumans = new ArrayList<String>();
		dataHumans.add("%0:id,money,wage,livingplaceId,groups,norms,valueThresholds");
		ArrayList<Human> residents = SimUtils.getObjectsAll(Human.class);
		for (Human resident : residents) {
			dataHumans.add(resident.getHumanVarsAsString());
		}
		
		// Data network		
		List<String> dataWaterTank = getDataWaterTank();
		
		List<String> data = new ArrayList<String>();
		data.addAll(dataHumans);
		data.addAll(dataWaterTank);
		
		writeToFile(filePath + "/" + fileName + ".txt", data);
	}

	public void writeToFile(String filePathAndName, List<String> data) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePathAndName, "UTF-8");
			for (String datum : data) {
				writer.println(datum);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<String> getDataWaterTank() {
		List<String> data = new ArrayList<String>();
		data.add("%4:value1,level1,threshold1,value2,level2,threshold2,value etc.");
		for (Human resident : SimUtils.getObjectsAll(Human.class)) {
			data.add(resident.getId() + "," + resident.getValuedDecisionMaker().getThresholds());
		}
		return data;
	}
	
}