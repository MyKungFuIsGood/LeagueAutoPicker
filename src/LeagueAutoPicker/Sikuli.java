package LeagueAutoPicker;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopMouse;


public class Sikuli {
	// assume all sikuli functions return an array of [x,y] format, unless stated otherwise
	// also assume that if the location returned is 0,0 the find failed
	
	// finds a picture on the desktop and returns its center coord
	public static int[] findImg( String path ) {
		int[] location = {0,0};
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Target target = new ImageTarget(image);
		target.setMinScore(0.8); // 1 is perfect image match, 0 is blurry match
		ScreenRegion s = new DesktopScreenRegion();
		ScreenRegion r = s.find(target);
		
		if( r != null ) {
			location[0] = r.getCenter().getX();
			location[1] = r.getCenter().getY();
		}
		
		return location;
	}
}