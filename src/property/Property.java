package property;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import population.Human;
import common.HumanUtils;
import common.Logger;
import common.SimUtils;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.GridValueLayer;
import saf.v3d.scene.VSpatial;

/**
* The property class is inherited by all classes that
* can be owned by a human or by the village.
*
* @author Maarten Jensen
* @since 2018-02-20
*/
public class Property { 
	
	// Variable declaration (initialization in constructor)
	// Variable initialization
	protected Map<Boolean, VSpatial> spatialImagesOwned = new HashMap<Boolean, VSpatial>();
	//private boolean active = false;
	private int id = -1;
	private final GridPoint location;
	private int width;
	private int height;
	private int ownerId = -1;
	private PropertyColor propertyColor;

	public Property(int id, GridPoint location, int width, int height, PropertyColor propertyColor1) {
		this.id = id;
		this.location = location;
		this.propertyColor = propertyColor1;
		this.height = height;
		this.width = width;
		SimUtils.getContext().add(this);
		if (!SimUtils.getGrid().moveTo(this, location.getX(), location.getY())) {
			Logger.logError("Property could not be placed, coordinate: " + location);
		}
	}
	
	
	
	public int getId() {
		return id;
	}
	
	protected void addToValueLayer() {

		GridValueLayer valueLayer = SimUtils.getValueLayer();
		if (valueLayer == null) {
			Logger.logError("Error valueLayer is null");
			return ;
		}
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < height; j ++) {
				valueLayer.set(propertyColor.getValueLayerIndex(), location.getX() + i, location.getY() + j);
			}
		}
	}	
	
	public int getX() {
		return location.getX();
	}
	
	public int getY() {
		return location.getY();
	}
	
	protected void setDimensions(GridPoint gridPoint) {
		width = gridPoint.getX();
		height = gridPoint.getY();
	}	
	
	public GridPoint getLocation() {
		return location;
	}

	public Human getOwner() {
		
		if (ownerId >= 0) {
			return HumanUtils.getHumanById(ownerId);
		}
		return null;
	}

	
	public void setOwner(int ownerId) {
		if (this.ownerId == -1) {
			Logger.logDebug(getName() + " set ownerId:" + ownerId);
			this.ownerId = ownerId;
			return ;
		}
		Logger.logError("Property " + id + " Owner (" + this.ownerId + ") already set");
	}
	
	public int getOwnerId() {
		return ownerId;
	}
	
	public void removeOwner(int oldOwnerId) {
		if (ownerId == oldOwnerId) {
			ownerId = -1;
			return ;
		}
		Logger.logError("Property " + id + " Owner (" + ownerId + ") is not equal to remover:" + oldOwnerId);
	}
	
	public boolean getAvailable() {
		if (getOwner() == null) {
			return true;
		}
		return false;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public GridPoint getFreeLocation() {
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (HumanUtils.cellFreeOfHumans(new GridPoint(location.getX() + i, location.getY() + j))) {
					return new GridPoint(location.getX() + i, location.getY() + j);
				}
			}
		}
		return null;
	}
	
	public GridPoint getFreeLocationExcluded(Human humanExcluded) {
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (HumanUtils.cellFreeOfHumansExcluded(new GridPoint(location.getX() + i, location.getY() + j), humanExcluded)) {
					return new GridPoint(location.getX() + i, location.getY() + j);
				}
			}
		}
		return null;
	}
	
	public void setSpatials(HashMap<Boolean, VSpatial> spatialImages) {
		this.spatialImagesOwned = spatialImages;
	}
	
	public VSpatial getSpatial() {
		
		if (getOwner() == null) {
			return spatialImagesOwned.get(false);
		}
		return spatialImagesOwned.get(true);
	}
		
	public String getName() {
		return "Property";
	}
	
	public Color getColor() {
		return propertyColor.getColor();
	}
		
	public String toString() {
		return String.format("Property, location %s", SimUtils.getGrid().getLocation(this));
	}



	public String getLabel() {
		// TODO Auto-generated method stub
		return "Property";
	}
}