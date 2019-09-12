package property;

import java.awt.Color;

public enum PropertyColor {
	
	HOUSE(2, new Color(230, 210, 175)), 
	SOCIAL_CARE(3, new Color(200, 200, 200))
	;
	
	private final double valueLayerIndex;
	private final Color color;
	
	PropertyColor(double index, Color color) {
		this.valueLayerIndex = index + 5; // + 5, since everything below 5 are value layer colors for non-buildings
		this.color = color;
	}
	
	public double getValueLayerIndex() {
		return valueLayerIndex;
	}
	
	public Color getColor() {
		return color;
	}
}
