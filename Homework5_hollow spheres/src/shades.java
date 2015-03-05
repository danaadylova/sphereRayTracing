/* Dana Adylova
 * 02/04/2015
 * This class has methods that are used to produce a spheres ray traced image, including shades.
*/
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class shades {
	
//	public static boolean originRayHitSphere(vector ray, Sphere sph) {
//		double lengthRay = vector.vectorLength(ray);
//		double centerLength = vector.vectorLength(sph.getCenter());
//		double cos = (ray.getX()*sph.getCenter().getX()+ray.getY()*sph.getCenter().getY()+ray.getZ()*sph.getCenter().getZ())/(lengthRay*centerLength);
//		double d;
//		if (cos>=0)
//			d = centerLength*Math.sqrt(1-cos*cos);
//		else
//			return false;
//		if (d>=sph.getRadius()) {
//			return false;
//		}
//		return true;
//	}
	
	public static vector hitPoint(vector origin, vector ray, Sphere sph) {
		vector q = new vector (-origin.getX()+sph.getCenter().getX(), -origin.getY()+sph.getCenter().getY(), -origin.getZ()+sph.getCenter().getZ());
		double lengthRay = vector.vectorLength(ray);
		double qLength = vector.vectorLength(q);
		double cos = (ray.getX()*q.getX()+ray.getY()*q.getY()+ray.getZ()*q.getZ())/(lengthRay*qLength);
		double d = qLength*Math.sqrt(1-cos*cos);
		if (cos<0){
			return null;
		}
		if (d>=sph.getRadius()) {
			return null;
		}
		double t = (Math.sqrt(qLength*qLength - d*d) - Math.sqrt(sph.getRadius()*sph.getRadius() - d*d))/lengthRay;
		if (t<=0.000000001)
			t = 0;
		vector hit = new vector (t*ray.getX()+origin.getX(), t*ray.getY()+origin.getY(), t*ray.getZ()+origin.getZ());
		return hit;
	}
	
	public static boolean sphereInFront(vector ray, Object[] spheres, int currentSphereIndex) {
		Sphere currentSphere = (Sphere)spheres[currentSphereIndex];
		vector origin = new vector (0,0,0);
		vector currentHit = hitPoint(origin, ray, currentSphere);
		if (currentHit == null)
			return false;
		double currentHitLength = vector.vectorLength(currentHit);
		for (int i = 0; i<4; i++) {
			if (i!= currentSphereIndex) {
				Sphere checkSphere = (Sphere) spheres[i];
				if (normal(ray, checkSphere)!=null) {
					vector hitPoint = hitPoint(origin, ray, checkSphere);
					double hitLength = vector.vectorLength(hitPoint);
					if (hitLength<currentHitLength)  // DOES NOT INCLUDE SPHERES THAT ARE NOT SEEN
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean sphereUp (vector ray, Object[] spheres, int currentSphereIndex) {
		Sphere currentSphere = (Sphere)spheres[currentSphereIndex];
		vector origin = new vector (0,0,0);
		vector hitPoint = hitPoint(origin, ray, currentSphere);
		if (hitPoint == null)
			return false;
		vector n = new vector(0, 1, 0);
		for (int i = 0; i<4; i++) {
			if (i!=currentSphereIndex) {
				Sphere checkSphere = (Sphere) spheres[i];
				// hitSph is a vector that starts at normal start and ends at the checking sphere center
				vector hitSph = new vector(checkSphere.getCenter().getX()-hitPoint.getX(), checkSphere.getCenter().getY()-hitPoint.getY(), checkSphere.getCenter().getZ()-hitPoint.getZ());
				double hitSphLength = vector.vectorLength(hitSph);
				double cos = (hitSph.getX()*n.getX()+hitSph.getY()*n.getY()+hitSph.getZ()*n.getZ())/(hitSphLength);
				double d;
				if (cos>=0){
					d = hitSphLength*Math.sqrt(1-cos*cos);
					if (d < checkSphere.getRadius())
						return true;
				}
			}
		}
		return false;
	}
	
	public static vector normal(vector ray, Sphere sph) {
		vector origin = new vector (0,0,0);
		vector hit = hitPoint(origin, ray,sph);
		if (hit == null){
			return null;
		}
		vector norm = new vector(hit.getX()-sph.getCenter().getX(), hit.getY()-sph.getCenter().getY(), hit.getZ()-sph.getCenter().getZ());
		double normLength = vector.vectorLength(norm);
		norm.setX(norm.getX()/normLength);
		norm.setY(norm.getY()/normLength);
		norm.setZ(norm.getZ()/normLength);
		return norm;
	}
	
	public static color getMirroredColor(vector ray, Object[] spheres, int currentSphereIndex) {
		color rayColor = new color(0,0,0); // Reflected color that will be returned
		vector origin = new vector (0,0,0);
		vector hitpoint = hitPoint(origin, ray, (Sphere)spheres[currentSphereIndex]);
		if (hitpoint==null){
			System.out.println("Hitpoint");
			return null;
		}
		vector normal = normal(ray, (Sphere)spheres[currentSphereIndex]);
		double c1 = hitpoint.getX()*normal.getX()+hitpoint.getY()*normal.getY()+hitpoint.getZ()*normal.getZ();
		vector reflected = new vector(hitpoint.getX()-2.0*c1*normal.getX(), hitpoint.getY()-2.0*c1*normal.getY(), hitpoint.getZ()-2.0*c1*normal.getZ());
		
		vector mirroredPoint = null;
		int mirroredSphereIndex = -1;
		
		for (int i=0; i<spheres.length; i++){ // Determines if reflected ray hits sphere, and which sphere
			if (i!=currentSphereIndex) {
				if (hitPoint(hitpoint, reflected, (Sphere)spheres[i]) != null) {
					if (mirroredPoint == null){
						mirroredPoint = hitPoint(hitpoint, reflected, (Sphere)spheres[i]);
						mirroredSphereIndex = i;
					}
					else if (vector.vectorLength(mirroredPoint) > vector.vectorLength(hitPoint(hitpoint, reflected, (Sphere)spheres[i]))) {
						mirroredPoint = hitPoint(hitpoint, reflected, (Sphere)spheres[i]);
						mirroredSphereIndex = i;
					}
				}
			}
		}
		
		if (mirroredPoint != null) {
			ColoredSphere mirroredSphere = (ColoredSphere)spheres[mirroredSphereIndex];
			if (mirroredSphere.isLightSphere() == false) { // reflected ray hits colored sphere
				double mirroredPointLength = vector.vectorLength(mirroredPoint);
				vector mirroredPointNormalized = new vector(mirroredPoint.getX()/mirroredPointLength, mirroredPoint.getY()/mirroredPointLength, mirroredPoint.getZ()/mirroredPointLength);
				boolean isSphereUp = sphereUp(mirroredPointNormalized, spheres, mirroredSphereIndex);
				if (!isSphereUp) { // if reflected ray hits some sphere and does not hit shaded region
					vector reflectedNormalNotNormalized = new vector(mirroredPoint.getX()-mirroredSphere.getCenter().getX(), 
							mirroredPoint.getY()-mirroredSphere.getCenter().getY(), 
							mirroredPoint.getZ()-mirroredSphere.getCenter().getZ());//normal(mirroredPointNormalized, mirroredSphere);
					double reflectedNormalNotNormalizedLength = vector.vectorLength(reflectedNormalNotNormalized);
					vector reflectedNormal = new vector(reflectedNormalNotNormalized.getX()/reflectedNormalNotNormalizedLength, 
							reflectedNormalNotNormalized.getY()/reflectedNormalNotNormalizedLength, 
							reflectedNormalNotNormalized.getZ()/reflectedNormalNotNormalizedLength);
					if (reflectedNormal.getY()>0) {
						rayColor.setBlue(mirroredSphere.getColor().getBlue()*255.0*reflectedNormal.getY());
						rayColor.setGreen(mirroredSphere.getColor().getGreen()*255.0*reflectedNormal.getY());
						rayColor.setRed(mirroredSphere.getColor().getRed()*255.0*reflectedNormal.getY());
					}
				}
			}
			else { // reflected ray hits light sphere
				rayColor.setBlue(255.0);
				rayColor.setGreen(255.0);
				rayColor.setRed(255.0);
			}
			
		}
		else { // reflected ray hits void
			rayColor.setRed(0);
			rayColor.setGreen(0.2*(1-normal.getY())*255.0);
			rayColor.setBlue(0.1*255.0);
		}
		return rayColor;
	}

	public static int getHitSphere(vector origin, vector ray, Object[] spheres, int currentSphereIndex){
		int index = -1;
		for (int i = 0; i<spheres.length; i++) {
			if (i!= currentSphereIndex) {
				if (hitPoint(origin, ray, (Sphere)spheres[i])!=null) {
					if (index<0) {
						index = i;
					}
					else {
						vector previous = hitPoint(origin, ray, (Sphere)spheres[index]);
						vector New = hitPoint(origin, ray, (Sphere)spheres[i]);
						vector originPrev = new vector(previous.getX() - origin.getX(),
								previous.getY() - origin.getY(), previous.getZ() - origin.getZ());
						vector originN = new vector(New.getX() - origin.getX(),
								New.getY() - origin.getY(), New.getZ() - origin.getZ());
						double originPrevious = vector.vectorLength(originPrev);
						double originNew = vector.vectorLength(originN);
						if (originNew<originPrevious) {
							index = i;
						}
					}
				}
			}
		}
		return index;
	}
	
	public static color getRefractedColor(vector ray, Object[] spheres, int currentSphereIndex, double refractionIndex){
		color rayColor = new color(0,0,0); // Reflected color that will be returned
		vector origin = new vector (0,0,0);
		vector hitpoint = hitPoint(origin, ray, (Sphere)spheres[currentSphereIndex]);
		if (hitpoint==null){
			System.out.println("Hitpoint");
			return null;
		}
		vector normal = normal(ray, (Sphere)spheres[currentSphereIndex]);
		double c1 = hitpoint.getX()*normal.getX()+hitpoint.getY()*normal.getY()+hitpoint.getZ()*normal.getZ();
		double c2 = Math.sqrt(1- refractionIndex*refractionIndex*(1 - c1*c1));
		vector refracted = new vector(refractionIndex*hitpoint.getX()+(refractionIndex*c1-c2)*normal.getX(),
				refractionIndex*hitpoint.getY()+(refractionIndex*c1-c2)*normal.getY(),
				refractionIndex*hitpoint.getZ()+(refractionIndex*c1-c2)*normal.getZ()); //Rr = (n * V) + (n * c1 - c2) * N
		
//		while ()
		return rayColor;
	}
	
	public static void sphereRayTracingShades(BufferedImage image, Object[] spheres){
		int width = image.getWidth();
		int height = image.getHeight();
		try {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					vector ray = new vector(-1.0+2.0*j/((double)width), 1.0-2.0*i/((double)height), -1.0);
					color rayColor = new color(0,0,0); // color that will be displayed
					int whichSphere = 
//					boolean hitsSomeSphere = false;
//					for (int l=0; l<spheres.length; l++){
//						if (originRayHitSphere(ray,(Sphere)spheres[l])) {
//							hitsSomeSphere = true;
//						}
//					}
					
					if (hitsSomeSphere) {
						for (int k = 0; k < spheres.length; k++) {
							ColoredSphere currentSphere = (ColoredSphere)spheres[k]; // the sphere that ray hits
							if (originRayHitSphere(ray,currentSphere)){ // if this ray hits sphere
								
								if (currentSphere.isLightSphere() == false) {
									if (!sphereInFront(ray,spheres,k) && (currentSphere.getFactor()<1.0)) { // If there is no sphere in front and this is not a mirror
										if (!sphereUp(ray, spheres, k)) { // if there is no sphere up
											vector norm = normal(ray, currentSphere);
											if (norm.getY()>0) {
												rayColor.setRed(currentSphere.getColor().getRed()*norm.getY()*255.0);
												rayColor.setGreen(currentSphere.getColor().getGreen()*norm.getY()*255.0);
												rayColor.setBlue(currentSphere.getColor().getBlue()*norm.getY()*255.0);
											}
											else {
												rayColor.setRed(0);
												rayColor.setBlue(0);
												rayColor.setGreen(0);
											}
										}
										else { // if there is sphere up
											rayColor.setRed(0);
											rayColor.setBlue(0);
											rayColor.setGreen(0);
										}
									}
									else if (!sphereInFront(ray,spheres,k) && (currentSphere.getFactor() == 1.0)) { // if there is no sphere in front and it is a mirror
										rayColor = getMirroredColor(ray, spheres, k);
									}
									else if (!sphereInFront(ray,spheres,k) && currentSphere.isHollowSphere()) {
										rayColor = getRefractedColor(ray, spheres, k, 1.51);
									}
								}
								else if (currentSphere.isLightSphere() && !sphereInFront(ray,spheres,k)){ // Hits light sphere
									rayColor.setRed(255.0);
									rayColor.setGreen(255.0);
									rayColor.setBlue(255.0);
								}
							}
						}
					}
					else {
						rayColor.setRed(0);
						rayColor.setGreen(0.2*(1.0-ray.getY())*255.0);
						rayColor.setBlue(0.1*255.0);
					}
					int r = (int)rayColor.getRed();
					int g = (int)rayColor.getGreen();
					int b = (int)rayColor.getBlue();
				    int rgb = ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
				    image.setRGB(j,i,rgb);
			      }
			    }
			ImageIO.write(image, "jpg", new File("/Users/danaadylova/Desktop/sphereRayTracing.jpg"));
		} catch (IOException e) {
			System.out.println("Exception occured :" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		BufferedImage image = new BufferedImage (2000, 2000, BufferedImage.TYPE_INT_RGB);
		vector oneCenter = new vector(0, 0, -3);
		color oneColor = new color(1.0, 0.5, 0.5);
		ColoredSphere one = new ColoredSphere(oneCenter, oneColor, 1.0, 0.0, false);
		vector twoCenter = new vector(2, 0, -4);
		color twoColor = new color(0.5, 1, 0.5);
		ColoredSphere two = new ColoredSphere(twoCenter, twoColor, 1, 0.0, false);
		vector threeCenter = new vector(-2, 0, -3);
		color threeColor = new color(0.5, 0.5, 1);
		ColoredSphere three = new ColoredSphere(threeCenter, threeColor, 1.0, 0.0, false);
		vector fourCenter = new vector(0, -100, 0);
		color fourColor = new color(1, 1, 1);
		ColoredSphere four = new ColoredSphere(fourCenter, fourColor, 98.5, 0, false);
//		vector fiveCenter = new vector(0, 10, 0);
//		ColoredSphere five = new ColoredSphere(fiveCenter, oneColor, 1.0, 0, true);
		vector hollowCenter = new vector(0,0,-2);
		ColoredSphere hollowOne = new ColoredSphere(hollowCenter, oneColor, 1.0, 0.0, false);
		ColoredSphere hollowTwo = new ColoredSphere(hollowCenter, oneColor, 0.9, 0.0, false);
		hollowOne.setIsHollow(true);
		hollowTwo.setIsHollow(true);

		Object[] spheres = new Object[6];
		spheres[0]=one; 
		spheres[1]=two;
		spheres[2]=three;
		spheres[3]=four;
//		spheres[4]=five;
		spheres[4] = hollowOne;
		spheres[5] = hollowTwo;
		sphereRayTracingShades(image, spheres);
		}
	
	
}