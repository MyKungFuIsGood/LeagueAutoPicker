package LeagueAutoPicker;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiSupport {
	// takes a directory path and returns an array of strings of available summoner spells
	public static String[] getSummonerSpells( String path ) {
		File dir = new File(path);
		File[] listOfFiles = dir.listFiles();
		
		String[] spells = new String[listOfFiles.length];
		for(int i = 0; i < listOfFiles.length; i++) {
			spells[i] = listOfFiles[i].getName().split("_")[0];
		}
		
		return spells;
	}
	
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
	
	public static void writeMsg(Robot bot, String msg, boolean...enter) {
		char[] arr = msg.toLowerCase().toCharArray();
		int length = arr.length;
		int i = 0;
		while( i < length ) {
			int kcode = (int) arr[i] - 32;
			
			if(kcode == 0) {
				kcode = KeyEvent.VK_SPACE;
			}
			bot.keyPress(kcode);
			bot.keyRelease(kcode);
			i++;
		}
		
		if(enter[0]) {
			bot.keyPress(KeyEvent.VK_ENTER);
			bot.keyRelease(KeyEvent.VK_ENTER);
		}
		
		// possible other method
		/*
		 for(int i = 0; i < string.length(); i++){
		    char c = Character.toUpperCase(s.charAt(i));
		    KeyEvent ke = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, (int)c, c);
		    //do whatever with key event
		}
		*/
	}
}
