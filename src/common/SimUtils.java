package common;

import java.util.ArrayList;
import java.util.Random;
import property.House;
import property.Property;
import builder.DataCollector;
import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.SimUtilities;
import repast.simphony.valueLayer.GridValueLayer;

public class SimUtils {
// Initialize variables
	private static final Random random = new Random();
	private static Context<Object> masterContext = null;
	
	private static int newPropertyId = 0;
	public static Context<Object> getContext() {

		if (masterContext == null)  {
			Logger.logError("SimUtils.getContext(): context returned null");
		}
		return masterContext;
	}
	
	@SuppressWarnings("unchecked")
	public static ContinuousSpace<Object> getSpace() {
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) getContext().getProjection(Constants.ID_SPACE);
		if (space == null)  {
			Logger.logError("SimUtils.getSpace(): space returned null");
		}
		return space;
	}
	
	public static Property getPropertyByIdByOwnerId(int ownerId) {
		
		ArrayList<Property> properties = SimUtils.getObjectsAllRandom(Property.class);
		for (Property property : properties) {
			if (property.getOwnerId() == ownerId) {
				return property;
			}
		}
		return null;
	}
	
	
	public static void resetPropertyId() {
		newPropertyId = 0;
	}
		
	@SuppressWarnings("unchecked")
	public static Grid<Object> getGrid() {
		Grid<Object> grid = (Grid<Object>) getContext().getProjection(Constants.ID_GRID);
		if (grid == null)  {
			Logger.logError("SimUtils.getGrid(): grid returned null");
		}
		return grid;
	}

	/**
	 * Same as getObjectsAll but uses SimUtilities.shuffle to randomize
	 * the ArrayList of objects
	 * @param clazz (e.g. use as input Human.class)
	 * @return an ArrayList of objects from the given class
	 */
	public static <T> ArrayList<T> getObjectsAllRandom(Class<T> clazz) {
		
		ArrayList<T> objectList = getObjectsAll(clazz);
		SimUtilities.shuffle(objectList, RandomHelper.getUniform());
		return objectList;
	}
	
	public static DataCollector getDataCollector() {
		return getObjectsAll(DataCollector.class).get(0);
	}
	
	/**
	 * Retrieves all the objects within the master context based on the given class.
	 * @param clazz (e.g. use as input Human.class)
	 * @return an ArrayList of objects from the given class
	 */
	public static <T> ArrayList<T> getObjectsAll(Class<T> clazz) {
		
		@SuppressWarnings("unchecked")
		final Iterable<T> objects = (Iterable<T>) getContext().getObjects(clazz);
		final ArrayList<T> objectList = new ArrayList<T>();
		for (final T object : objects) {
			objectList.add(object);
		}
		return objectList;
	}

	public static GridValueLayer getValueLayer() {
		
		GridValueLayer valueLayer = (GridValueLayer) getContext().getValueLayer(Constants.ID_VALUE_LAYER);
		if (valueLayer == null)  {
			Logger.logError("SimUtils.getValueLayer(): valueLayer returned null");
		}
		return valueLayer;
	}

	public static House getHouseById(int id) {
		
		ArrayList<House> properties = SimUtils.getObjectsAllRandom(House.class);
		for (House property : properties) {
			if (property.getId() == id) {
				return property;
			}
		}
		return null;
	}
	
	public static void setContext(Context<Object> masterContext) {
		SimUtils.masterContext = masterContext;
	}
	
	/**
	 * This returns a new Id. ++ is used after the variable to make sure
	 * the current newHumanId is returned
	 * @return a new unused id for a resident
	 */
	public static int getNewPropertyId() {
		return newPropertyId++;
	}

	/**
	 * Get all available property
	 * @param <T> class
	 * @return
	 */
	public static <T> ArrayList<T> getHousesAvailableAll(Class<T> clazz) {
		
		@SuppressWarnings("unchecked")
		final Iterable<T> properties = (Iterable<T>) getContext().getObjects(clazz);
		final ArrayList<T> propertyList = new ArrayList<T>();
		for (final T property : properties) {
			if (((House) property).getAvailable()) {
				propertyList.add(property);
			}
		}
		return propertyList;
	}
	
	public static <T> ArrayList<T> getHousesAvailableAllRandom(Class<T> clazz) {
		
		ArrayList<T> propertyList = getHousesAvailableAll(clazz);
		SimUtilities.shuffle(propertyList, RandomHelper.getUniform());
		return propertyList;
	}


	public static Property getPropertyById(int id) {
		
		ArrayList<Property> properties = SimUtils.getObjectsAllRandom(Property.class);
		for (Property property : properties) {
			if (property.getId() == id) {
				return property;
			}
		}
		return null;
	}
}
