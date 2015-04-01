
public class vector {

	private double x;
	private double y;
	private double z; 
	
	public vector(){
		x = 0;
		y = 0;
		z = 0;
	}
	
	public vector(double setx, double sety, double setz){
		this.x = setx;
		this.y = sety;
		this.z = setz;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setX(double setx) {
		x = setx;
	}
	
	public void setY(double sety) {
		y = sety;
	}
	
	public void setZ(double setz) {
		z = setz;
	}
	
	public vector addVector(vector vec) {
		return new vector(this.x+vec.x, this.y+vec.y, this.z+vec.z);
	}
	
	public vector subtractVector(vector vec) {
		return new vector(this.x-vec.x, this.y-vec.y, this.z-vec.z);
	}
	
	public double cos(vector vec) {
		return dotProduct(this, vec)/(this.vectorLength()*vec.vectorLength());
	}
	
	/** This method returns length of a vector
	 * 
	 * @param vec
	 * @return
	 */
	public double vectorLength() {
		return Math.sqrt(this.getX()*this.getX()+this.getY()*this.getY()+this.getZ()*this.getZ());
	}
	
	/** This method finds skalar result of two vector multiplication
	 * 
	 * @param one
	 * @param two
	 * @return double
	 */
	public static double dotProduct (vector one, vector two) {
		return (one.getX()*two.getX()+one.getY()*two.getY()+one.getZ()*two.getZ());
	}
	
	public static vector crossProduct(vector a, vector b) {
		return new vector(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x);
	}
	
	/** Gets random vector for a sphere. The origin is the center of the sphere
	 * 
	 * @param radius is a sphere
	 * @return
	 */
	public static vector randomPointInSphere(double radius) {
		double x,y,z;
		boolean repeat = true;
		vector answer = null;
		while (repeat) {
			if (Math.random()>0.5)
				x = Math.random()*radius;
			else
				x = 0-Math.random()*radius;
			if (Math.random()>0.5)
				y = Math.random()*radius;
			else 
				y = 0-Math.random()*radius;
			if (Math.random()>0.5)
				z = Math.random()*radius;
			else 
				z = 0-Math.random()*radius;
			answer = new vector(x, y, z);
			if (answer.vectorLength()<=radius)
				repeat = false;
		}
		return answer;
	}
	
	public static vector randomDirectionUp(vector point, vector normal) {
		vector add = randomPointInSphere(1);
		vector smallSphereRandom = point.addVector(normal).addVector(add);
		return smallSphereRandom.subtractVector(point);
	}
	
	public vector normalize() {
		return new vector(this.getX()/this.vectorLength(), this.getY()/this.vectorLength(), this.getZ()/this.vectorLength());
	}
	
	public static void main(String[] args) {
//		for (int i = 0; i<1000; i++) {
//			vector origin = new vector();
//			vector normal = new vector (0,0,1);
//			vector direction = randomDirectionUp(origin, normal);
//			if (direction.getZ()<0)
//				System.out.println(direction.getX()+" "+ direction.getY()+" "+direction.getZ());
//		}
	}

}