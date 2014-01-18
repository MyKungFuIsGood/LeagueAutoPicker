package LeagueAutoPicker;

import java.io.File;

public class LeagueVars {
	// champion post fix
	public final static String heroPostfix = "_square_0.png";
	
	// return league path
	private final static String macPrePath = "/Applications/League of Legends.app/Contents/LoL/RADS/projects/lol_air_client/releases/";
	private final static String macPostPath = "/deploy/bin/assets/images/champions/";
	
	private final static String winPrePath = "C:\\Riot Games\\League of Legends\\RADS\\projects\\lol_air_client\\releases\\";
	private final static String winPostPath = "\\deploy\\assets\\images\\champions\\";
	
	// finds the most recent patch file path for champion assests
	public static String getImageAssets() {
		String version = "0.0.0.0";
		String os = System.getProperty("os.name");
		String prePath = "";
		String postPath = "";
		
		if( os.toLowerCase().contains("win") ) {
			prePath = winPrePath;
			postPath = winPostPath;
		}
		else if ( os.toLowerCase().contains("mac") ) {
			prePath = macPrePath;
			postPath = macPostPath;
		}
		else {
			// alert that their operating system is not supported
		}
		
		File file = new File(prePath);
		String[] names = file.list();
		
		for(String name: names) {
			if(new File(prePath + name).isDirectory()) {
				// checks to see if name is greater than version, if it is version becomes name
				if( name.compareTo(version) == 1 ) { 
					version = name;
				}
			}
		}
		
		return prePath + version + postPath;
	}
}
