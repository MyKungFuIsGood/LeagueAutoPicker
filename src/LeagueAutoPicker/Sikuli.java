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
		ScreenRegion s = new DesktopScreenRegion();
		ScreenRegion r = s.find(target);
		
		if( r != null ) {
			location[0] = r.getCenter().getX();
			location[1] = r.getCenter().getY();
		}
		
		return location;
	}
	
	public static int[] findHero( String path ) {
		int[] location = {0,0};
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		image = Sikuli.heroImageCrop(image);
		
		Target target = new ImageTarget(image);
		
		//ScreenRegion s = new DekstopScreenRegion();
		// smaller ScreenRegion means faster searches.
		ScreenRegion s = new DesktopScreenRegion(270, 215, 1380, 650);
		
		ScreenRegion r = s.find(target);
		
		if( r != null )  {
			location[0] = r.getCenter().getX();
			location[1] = r.getCenter().getY();
		}
		
		return location;
	}
	
	
	public static BufferedImage heroImageCrop( BufferedImage image ) {		
		// want to crop a rectangle to the right
		// cropping to the right makes sure that the hero free week icon does not disrupt the find process
		image = image.getSubimage(60, 15, 50, 90); 
		
		// resize image for desktop resolution
		// desktop is 1920 * 1200 for some reason on mac retina's
		// league's image sizes are twice what they need to be, don't know why
		int h = image.getHeight() / 2;
		int w = image.getWidth() / 2;
		BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = tmp.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(image, 0, 0, w, h, null);
		}
		finally {
			g.dispose();
		}
		image = tmp;
		
		return image;
	}
}
