import java.awt.image.BufferedImage;

public class ColoredSphere {

	private vector center;
	private double radius;
	private color color;
	private boolean isMirror;
	private boolean isLightSphere;
	private BufferedImage imageMapped;
	
	public ColoredSphere() {
		vector cen = new vector();
		this.center = cen;
		this.radius = 0;
		this.isMirror = false;
		this.isLightSphere = false;
		this.imageMapped = null;
	}
	
	public ColoredSphere(vector cen, color col, double rad, boolean reflFac, boolean isLight, BufferedImage image) {
		this.center = cen;
		this.radius = rad;
		this.color = col;
		this.isMirror = reflFac;
		this.isLightSphere = isLight;
		this.imageMapped = image;
	}
	
	public color getPicColor(vector normal) {
		if ((this.getImage() == null) || (normal == null))
			return this.color;
		else {
			color giveColor = new color();
			double width = this.imageMapped.getWidth();
			double height = this.imageMapped.getHeight();

			double phi = Math.acos(normal.getZ());
			double y = height*phi/Math.PI;
			
			double theta = Math.acos(normal.getX()/Math.sin(phi));
			double x = width*theta/(2.0*Math.PI);

			int clr =  this.imageMapped.getRGB((int)x, (int)y);
			int  red   = (clr & 0x00ff0000) >> 16;
			int  green = (clr & 0x0000ff00) >> 8;
			int  blue  =  clr & 0x000000ff;
			giveColor = new color(1.0*red/255.0, 1.0*green/255.0, 1.0*blue/255.0);
			return giveColor;
		}
	}
	
	
	
	public vector getCenter() {
		return center;
	}
	
	public double getRadius() {
		return this.radius;
	}
	
	public BufferedImage getImage() {
		if (this.imageMapped == null) 
			return null;
		else
			return this.imageMapped;
	}
	
	public boolean isMirror() {
		return isMirror;
	}
	
	public boolean isLightSphere() {
		return this.isLightSphere;
	}
	
	public void setCenter(vector cen) {
		center = cen;
	}
	
	public void setRadius(double rad) {
		radius = rad;
	}

	
	public void setColor(color col) {
		color = col;
	}
	
	public void setImageMapped(BufferedImage img) {
		this.imageMapped = img;
	}
	
	public void setIfMirror(boolean fac) {
		this.isMirror = fac;
	}
	
	public void setIsLight(boolean isLight) {
		this.isLightSphere = isLight;
	}
	
}
