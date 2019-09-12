package visual;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;

import common.Constants;
import common.Logger;
import population.Human;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;

/**
* Changes human visual layout
*
* @author Maarten Jensen
* @since 2018-02-20
*/
public class HumanStyleOGL2D extends DefaultStyleOGL2D {
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		
	    if (agent instanceof Human) {
    		Human human = (Human) agent;
    		VSpatial newSpatial = human.getSpatialImage();

    		if (newSpatial != null) {
    			return newSpatial;
    		}
    		else {
    			loadSpatialImages(human);
    			newSpatial = human.getSpatialImage();
    			if (newSpatial != null) {
        			return newSpatial;
        		}
    			else {
    				Logger.logError("Getting spatial image for human" + human.getId() + " went wrong, status: " + human.getLivingPlaceId());
    				return shapeFactory.createRectangle(6, 6);
    			}
    		}
	    }
	    Logger.logError("Getting spatial went wrong since it is no human.");
	    return shapeFactory.createRectangle(6, 6);
	}
	
	public void loadSpatialImages(Human human) {
		
		
		HashMap<Integer, VSpatial> spatialImages = new HashMap<Integer, VSpatial>();
		spatialImages.put(0, createImageFromPath(Constants.ICON_HAPPY));
		spatialImages.put(1, createImageFromPath(Constants.ICON_SAD));
		human.setSpatialImages(spatialImages);
	}
	
	@Override
	public Color getColor(final Object agent) {
		
		if (agent instanceof Human) {
			final Human human = (Human) agent;
				return new Color(0x00, 0x00, 0xFF);
		}
		return super.getColor(agent);
	}
	
	@Override
	public String getLabel(Object object) {

		if (object instanceof Human) {
			final Human human = (Human) object;
			return human.getLabel();
		}
		
		return "Warning label not found for object";
	}
	
	@Override
	public Font getLabelFont(Object object) {
		
	    return Constants.FONT_SMALL;
	}
	
	@Override
	public Position getLabelPosition(Object object) {
	    return Position.NORTH;
	}
	
	private VSpatial createImageFromPath(String path) {
		try {
			return shapeFactory.createImage(path);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
