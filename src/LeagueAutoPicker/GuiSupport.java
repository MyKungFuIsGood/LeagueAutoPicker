package LeagueAutoPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

public class GuiSupport {
	// takes a directory path and returns an array of strings of hero names
	public static String[] getHeroNames( String path ) {
		File dir = new File(path);
		File[] listOfFiles = dir.listFiles();
		
		List<String> heroFileNames = new ArrayList<String>();
        
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().indexOf("square") > 0)) {
				heroFileNames.add(listOfFiles[i].getName());
			}
		}
		
		String[] heroNames = new String[heroFileNames.size()];
		for ( int i = 0; i < heroNames.length; i++) {
			heroNames[i] = heroFileNames.get(i).split("_")[0];
		}
		return heroNames;
	}
	
	// takes a directory path and returns list of strings of hero FILE names
	public static List<String> getHeroFileNames( String path ) {
		File dir = new File(path);
		File[] listOfFiles = dir.listFiles();
		
		List<String> heroFileNames = new ArrayList<String>();
        
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().indexOf("square") > 0)) {
				heroFileNames.add(listOfFiles[i].getName());
			}
		}
		
		return heroFileNames;
	}
}
