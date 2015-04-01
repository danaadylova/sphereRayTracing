
public class MirrorSphere extends ColoredSphere {
	
	private color R;
	
	public MirrorSphere() {
		color r = new color(1,1,1);
		this.R = r;
	}
	
	public MirrorSphere(color r) {
		this.R = r;
	}
	
	public color getR () {
		return R;
	}
	
	public void setR (color r) {
		this.R = r;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
