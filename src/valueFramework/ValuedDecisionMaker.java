package valueFramework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import population.Human;
import property.House;
import common.Constants;
import common.CustomizedMapUtils;
import common.Logger;

public class ValuedDecisionMaker {
	private Map<String, Integer> valueTrees;//Here we simplified using trees to only use the assigned threshold
	
	public ValuedDecisionMaker(String values) {
		valueTrees = new HashMap<String, Integer>();
		Logger.logDebug("ValuedDecisionMaker values " + values);
		valueTrees.put(AbstractValue.UNIVERSALISM.name(), new Integer(values.split(",")[0]));
		valueTrees.put(AbstractValue.POWER.name(), new Integer(values.split(",")[1]));				
	}
	
	public double getValueThreshold(AbstractValue abstractValue) {
		return getValueThresholdByName(abstractValue.name());
	}
	
	public double getValueThresholdByName(String abstractValue) {
		if (valueTrees.containsKey(abstractValue)) {
			return valueTrees.get(abstractValue);
		}
		return -1;
	}
	
	public String getThresholds() {
		String string = "";
		string += Math.round(getValueThreshold(AbstractValue.POWER)) + "," + 0 + ",";
		string += Math.round(getValueThreshold(AbstractValue.UNIVERSALISM)) + "," + 0 + ",";
		return string;
	}

	public Map<String, Double> sortValuesByThreshold() {

		Map<String, Double> allVals = new HashMap<String, Double>();
		for (String wtKey : valueTrees.keySet()) {
			double thresh = valueTrees.get(wtKey);
			allVals.put(wtKey, thresh);			
		}
		CustomizedMapUtils bvc = new CustomizedMapUtils(allVals);
	    TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
	    sorted_map.putAll(allVals);
	    return sorted_map;
	}
	
	public String getTheMostImportantValuesTitle() {
		Map<String, Double> sortedValueList = sortValuesByThreshold();
//		Logger.logDebug("sortedValuesByThre : " + sortedValueList.toString());
		for (Map.Entry<String, Double> entry : sortedValueList.entrySet()) {
			return entry.getKey()/*+";"+entry.getValue()*/;
		}
		return "";
	}

	public void setValueTreesFromData(List<String> data) {
		valueTrees.clear();
		for (int i = 1; i < data.size(); i += 2) {
			valueTrees.put(data.get(0), Integer.parseInt(data.get(1)));
		}
	}

	public House chooseHouse(Vector<ArrayList<House>> availablePropertiesSorted) {
		String importantVal = getTheMostImportantValuesTitle();
		for(ArrayList<House> houseArr : availablePropertiesSorted){
			for (House house : houseArr) {
				int housingIdx = Arrays.asList(Constants.GROUP_ORDERS_IN_CONTROLLED_SETTINGS_LIST).indexOf(house.getHouseType().name());
				String[] newPopulation = Constants.INITIAL_GROUPS_VALUED_SIZE[housingIdx].split(",");
				int valueIdx =  Arrays.asList(Constants.VALUE_ORDERS_IN_CONTROLLED_SETTINGS_LIST).indexOf(importantVal);
				if(newPopulation[valueIdx].equals("0")) 
					Logger.logError("there is no chance to live in group G" + housingIdx);
				newPopulation[valueIdx] = Integer.toString(new Integer(newPopulation[valueIdx])-1);
				Constants.INITIAL_GROUPS_VALUED_SIZE[housingIdx] = newPopulation[0];
				for(int i = 1; i < Constants.VALUE_ORDERS_IN_CONTROLLED_SETTINGS_LIST.length; i++)
					Constants.INITIAL_GROUPS_VALUED_SIZE[housingIdx] += "," + newPopulation[i];
				return house;
			}
		}
		return null;
	}

	public String getThresholdsAsString() {
		// TODO Auto-generated method stub
		return valueTrees.get(AbstractValue.UNIVERSALISM.name()) + "," + valueTrees.get(AbstractValue.POWER.name());
	}

	
}
