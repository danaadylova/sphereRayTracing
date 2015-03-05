
public class color {

	private double r;
	private double g;
	private double b; 
	
	public color(){
		r = 0;
		g = 0;
		b = 0;
	}
	
	public color(double setRed, double setGreen, double setBlue){
		this.r = setRed;
		this.g = setGreen;
		this.b = setBlue;
	}
	
	public double getRed() {
		return r;
	}
	
	public double getGreen() {
		return g;
	}
	
	public double getBlue() {
		return b;
	}
	
	public void setRed(double red) {
		r = red;
	}
	
	public void setGreen(double green) {
		g = green;
	}
	
	public void setBlue(double blue) {
		b = blue;
	}
}
