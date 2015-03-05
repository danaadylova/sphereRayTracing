
public class ColoredSphere extends Sphere{

	private color color;
	private double reflectionFactor;
	private boolean isLightSphere;
	private boolean isHollowSphere; // not included in the constructor
	
	public ColoredSphere() {
		vector cen = new vector(0,0,0);
		this.setCenter(cen);
		this.setRadius(0);
		this.setFactor(0);
		this.isLightSphere = false;
		this.isHollowSphere = false;
	}
	
	public ColoredSphere(vector cen, color col, double rad, double reflFac, boolean isLight) {
		this.setCenter(cen);
		color = col;
		this.setRadius(rad);
		this.setFactor(reflFac);
		this.isLightSphere = isLight;
	}
	
	public color getColor() {
		return color;
	}
	
	public double getFactor() {
		return reflectionFactor;
	}
	
	public boolean isLightSphere() {
		return this.isLightSphere;
	}
	
	public boolean isHollowSphere() {
		return this.isHollowSphere;
	}
	
	public void setColor(color col) {
		color = col;
	}
	
	public void setFactor(double fac) {
		this.reflectionFactor = fac;
	}
	
	public void setIsLight(boolean isLight) {
		this.isLightSphere = isLight;
	}
	
	public void setIsHollow(boolean isHollow) {
		this.isHollowSphere = isHollow;
	}

}