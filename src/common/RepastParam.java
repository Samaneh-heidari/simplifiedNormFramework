package common;

import repast.simphony.engine.environment.RunEnvironment;

public final class RepastParam {
	private static boolean popGenToFile = false;
	private static String popGenFileName = "none";
	private static int popGenTickLimit = 1;
	
	private static boolean popInitFromFile = false;
	private static String popInitFileName = "none";
	
	private static int pauseSimulationAt = -1;
	
	private static int valuePower1 = 50;
	private static int valueUniversalism1 = 50;
	
	private static int valuePower2 = 50;
	private static int valueUniversalism2 = 45;
	
	public static void setRepastParameters() {
		
		popGenToFile = RunEnvironment.getInstance().getParameters().getBoolean("popGenToFile");
		popGenFileName = RunEnvironment.getInstance().getParameters().getString("popGenFileName");
		popGenTickLimit = RunEnvironment.getInstance().getParameters().getInteger("popGenTickLimit");
		
		popInitFromFile = RunEnvironment.getInstance().getParameters().getBoolean("popInitFromFile");
		popInitFileName = RunEnvironment.getInstance().getParameters().getString("popInitFileName");
		
		pauseSimulationAt = RunEnvironment.getInstance().getParameters().getInteger("simPauseTick");
		
		valuePower1 = RunEnvironment.getInstance().getParameters().getInteger("valuePower");
		valueUniversalism1 = RunEnvironment.getInstance().getParameters().getInteger("valueUniversalism");
		
		valuePower2 = valuePower1;
		valueUniversalism2 = valueUniversalism1;
		Logger.logDebug("REPAST params : p,s,t,u " + valuePower1 + valueUniversalism1);
	}


	public static void setRepastParameters(boolean pGenToFile, String pGenFileName, int pGenTickLimit,
										   boolean pInitFromFile, String pInitFileName, int pPauseAtTick,
										   int pPower, int pUni, 
										   int pPower2, int pUni2) {
		popGenToFile = pGenToFile;
		popGenFileName = pGenFileName;
		popGenTickLimit = pGenTickLimit;
		
		popInitFromFile = pInitFromFile;
		popInitFileName = pInitFileName;
		
		pauseSimulationAt = pPauseAtTick;
		
		valuePower1 = pPower;
		valueUniversalism1 = pUni;
		
		valuePower2 = pPower2;
		valueUniversalism2 = pUni2;
	}

	public static boolean getPopGenToFile() {
		return popGenToFile;
	}
	
	public static boolean getPopInitFromFile() {
		return popInitFromFile;
	}

	public static String getPopGenFileName() {
		return popGenFileName;
	}

	public static String getPopInitFileName() {
		return popInitFileName;
	}
	
	public static int getPopGenTickLimit() {
		return popGenTickLimit;
	}


	public static int getSimulationPauseInt() {
		return pauseSimulationAt;
	}
	
}