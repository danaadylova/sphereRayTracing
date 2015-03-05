
public class Sphere {

	private vector center;
	private double radius;
	
	public Sphere() {
		vector cent = new vector (0,0,0);
		this.center = cent;
		this.radius = 0;
	}
	
	public Sphere(vector cent, double rad) {
		this.center = cent;
		this.radius = rad;
	}
	
	public vector getCenter() {
		return center;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setCenter(vector cen) {
		center = cen;
	}
	
	public void setRadius(double rad) {
		radius = rad;
	}
}
