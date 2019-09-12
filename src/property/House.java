package property;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import population.Human;
import common.Constants;
import common.HumanUtils;
import common.Logger;
import common.SimUtils;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.GridValueLayer;
import saf.v3d.scene.VSpatial;

public class House extends Property{
	HouseType houseType;
//	private int price;
//	private int maintenanceCost;
	//	private final PropertyColor propertyColor;
	// Variable initialization
	protected Map<Boolean, VSpatial> spatialImagesOwned = new HashMap<Boolean, VSpatial>();//houseType
	
	public House(int id, HouseType houseType, GridPoint location) {
		super(id, location, 4, 2, PropertyColor.HOUSE);		
		this.houseType = houseType;
		addGardenToValueLayer();
		addToValueLayer();
	}

	public HouseType getHouseType() {
		return houseType;
	}
	
	public void addGardenToValueLayer() {

		GridValueLayer valueLayer = SimUtils.getValueLayer();
		if (valueLayer == null) {
			Logger.logError("Error valueLayer is null");
			return ;
		}
		
		int width, height;
		width = Constants.LOCATION_DIMENTION;
		height = Constants.LOCATION_DIMENTION;
		
		/*switch(houseType) {
			case CHEAP:
				return;
			case STANDARD:
				width = 4;
				height = 3;
				break;
			case EXPENSIVE:
				width = 6;
				height = 5;
				break;
			default:
				width = 0;
				height = 0;
		}*/
//		Logger.logDebug(houseType + " width:" + width + ", height:" + height);
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < height; j ++) {
				valueLayer.set(RandomHelper.nextDoubleFromTo(3.90, 3.93), getX() + i, getY() + j);
			}
		}
	}
	
	
	public String getName() {
		return "House [" + getId() + "] " + houseType + ": " + getX() + ", " + getY();
	}
	
	@Override
	public String getLabel() {
		return houseType + " [" + getId() + "] " ;
	}
	
	public String toString() {
		return String.format("House (" + getId() + "), location %s", SimUtils.getGrid().getLocation(this) + ", houseType " + houseType.name());
	}
	
	

	public boolean getAvailable() {
		if (getOwner() == null) {
			return true;
		}
		return false;
	}
	
}
