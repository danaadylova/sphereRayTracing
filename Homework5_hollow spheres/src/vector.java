
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
	
	/** This method returns length of a vector
	 * 
	 * @param vec
	 * @return
	 */
	public static double vectorLength(vector vec) {
		return Math.sqrt(vec.getX()*vec.getX()+vec.getY()*vec.getY()+vec.getZ()*vec.getZ());
	}
	
	/** This method finds skalar result of two vector multiplication
	 * 
	 * @param one
	 * @param two
	 * @return double
	 */
	public static double multiplyVectors (vector one, vector two) {
		return (one.getX()*two.getX()+one.getY()*two.getY()+one.getZ()*two.getZ());
	}
	
	public static void main(String[] args) {
	}

}