package property;
import repast.simphony.space.grid.GridPoint;
import saf.v3d.scene.VSpatial;

/**
* Social care houses people without a home that are to young for
* the elderly care. And it gives social benefits to people with
* no/too little income.
*
* @author Maarten Jensen
* @since 2018-02-20
*/
public class SocialCare extends Property {
	
	public SocialCare(int id, GridPoint location) {
		super(id, location, 11, 8, PropertyColor.SOCIAL_CARE);
		addToValueLayer();
	}
	
	
	@Override
	public VSpatial getSpatial() {
		
		return spatialImagesOwned.get(true);
	}

	@Override
	public String getName() {
		return "SocialCare [" + getId() + "]";
	}
	
	@Override
	public String getLabel() {
		return "Social care [" + getId() + "]";
	}	
}