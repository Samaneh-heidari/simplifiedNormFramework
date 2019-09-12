package visual;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;

import common.Constants;

import property.House;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;

/**
* Changes property visual layout
*
* @author Maarten Jensen
* @since 2018-02-20
*/
public class PropertyStyleOGL2D extends DefaultStyleOGL2D {

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (agent instanceof House) {
			// Create spatials
			if (spatial == null) {
	    	
	    		House property = (House) agent;
	    		HashMap<Boolean, VSpatial> spatialImages = new HashMap<Boolean, VSpatial>();
	    		spatialImages.put(false, createImageFromPath(Constants.ICON_NOT_OWNED));
		    	spatialImages.put(true, createImageFromPath(Constants.ICON_OWNED));
		    	property.setSpatials(spatialImages);
	    	}
			// Get spatial
			House property = (House) agent;
	    	if (property.getSpatial() != null) {
	    		return property.getSpatial();
	    	}
	    }
	    return shapeFactory.createRectangle(Constants.GRID_CELL_SIZE, Constants.GRID_CELL_SIZE);
	}

	@Override
	public Color getColor(final Object agent) {
		
		if (agent instanceof House) {
			return ((House) agent).getColor();
		}
		return super.getColor(agent);
	}


	
	@Override
	public String getLabel(Object object) {

		if (object instanceof House) {
			final House property = (House) object;
			return property.getLabel();
		}
		
		return "Warning label not found for object";
	}
	
	@Override
	public Position getLabelPosition(Object object) {
	    return Position.SOUTH_EAST;
	}
	
	@Override
	public Font getLabelFont(Object object) {
		
	    return Constants.FONT_SMALL;
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
