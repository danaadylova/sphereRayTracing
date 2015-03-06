/* Dana Adylova
 * 02/04/2015
 * This class has methods that are used to produce a spheres ray traced image, including shades.
*/
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class shades {
	
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
	
	public static boolean sphereUp (vector ray, Object[] spheres, int currentSphereIndex) {
		Sphere currentSphere = (Sphere)spheres[currentSphereIndex];
		vector origin = new vector (0,0,0);
		vector hitpoint = hitPoint(origin, ray, currentSphere);
		if (hitpoint == null)
			return false;
		vector n = new vector(0, 1, 0);
		int whichSphere = getIndexIfRayHitsSphere(hitpoint, n, spheres, currentSphereIndex);
		if ((whichSphere>=0) && (whichSphere<spheres.length)){
			ColoredSphere HitSphere = (ColoredSphere)spheres[whichSphere];
			if (HitSphere.isLightSphere()) {
				return false;
			}
			else {
				return true;
			}
			
		}
		else
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
		
		int whichSphere = getIndexIfRayHitsSphere(hitpoint, reflected, spheres, currentSphereIndex);
		if (whichSphere == -1) {
			rayColor.setRed(0);
			rayColor.setGreen(0.2*(1-normal.getY())*255.0);
			rayColor.setBlue(0.1*255.0);
		}
		else if (whichSphere>=0 && whichSphere<spheres.length) {
			mirroredPoint = hitPoint(hitpoint, reflected, (Sphere)spheres[whichSphere]);
			ColoredSphere mirroredSphere = (ColoredSphere)spheres[whichSphere];
			if (mirroredSphere.isLightSphere() == false) { // reflected ray hits colored sphere
				double mirroredPointLength = vector.vectorLength(mirroredPoint);
				vector mirroredPointNormalized = new vector(mirroredPoint.getX()/mirroredPointLength, mirroredPoint.getY()/mirroredPointLength, mirroredPoint.getZ()/mirroredPointLength);
				boolean isSphereUp = sphereUp(mirroredPointNormalized, spheres, whichSphere);
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
		return rayColor;
	}

	public static int getIndexIfRayHitsSphere(vector origin, vector ray, Object[] spheres, int currentSphereIndex){
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
	
	/**public static color getRefractedColor(vector origin, vector ray, Object[] spheres, int hitSphereIndex, double refractionIndex){
		color rayColor = new color(0,0,0); // Reflected color that will be returned
		vector refracted = getRefractedVector(origin, ray, spheres, hitSphereIndex, refractionIndex);
		vector originRefracted = new vector (origin.getX()+ray.getX(), origin.getY()+ray.getY(), origin.getZ()+ray.getZ());
		vector hitPoint = hitPoint(origin, ray, (Sphere)spheres[hitSphereIndex]);
		vector normal = normal(hitPoint, (Sphere)spheres[hitSphereIndex]);
		int whichSphere = getIndexIfRayHitsSphere(originRefracted, refracted, spheres, hitSphereIndex);
		if (whichSphere == -1) { // hits void
			rayColor.setRed(0);
			rayColor.setGreen(0.2*(1-normal.getY())*255.0);
			rayColor.setBlue(0.1*255.0);
		}
		else if (whichSphere>=0 && whichSphere<spheres.length) { // hits some sphere
			vector refrPoint = hitPoint(originRefracted, refracted, (Sphere)spheres[whichSphere]);
			ColoredSphere refractedHitSphere = (ColoredSphere)spheres[whichSphere];
			if (refractedHitSphere.isLightSphere() == false) { // reflected ray hits colored sphere
				double refrPointLength = vector.vectorLength(refrPoint);
				vector refrPointNormalized = new vector(refrPoint.getX()/refrPointLength, 
						refrPoint.getY()/refrPointLength, refrPoint.getZ()/refrPointLength);
				boolean isSphereUp = sphereUp(refrPointNormalized, spheres, whichSphere);
				if (!isSphereUp) { // if refracted ray hits some sphere and does not hit shaded region
					vector refractedNormalNotNormalized = new vector(refrPoint.getX()-refractedHitSphere.getCenter().getX(), 
							refrPoint.getY()-refractedHitSphere.getCenter().getY(), 
							refrPoint.getZ()-refractedHitSphere.getCenter().getZ());//normal(mirroredPointNormalized, mirroredSphere);
					double reflectedNormalNotNormalizedLength = vector.vectorLength(refractedNormalNotNormalized);
					vector reflectedNormal = new vector(refractedNormalNotNormalized.getX()/reflectedNormalNotNormalizedLength, 
							refractedNormalNotNormalized.getY()/reflectedNormalNotNormalizedLength, 
							refractedNormalNotNormalized.getZ()/reflectedNormalNotNormalizedLength);
					if (reflectedNormal.getY()>0) {
						rayColor.setBlue(refractedHitSphere.getColor().getBlue()*255.0*reflectedNormal.getY());
						rayColor.setGreen(refractedHitSphere.getColor().getGreen()*255.0*reflectedNormal.getY());
						rayColor.setRed(refractedHitSphere.getColor().getRed()*255.0*reflectedNormal.getY());
					}
				}
			}
			else { // refracted ray hits light sphere
				rayColor.setBlue(255.0);
				rayColor.setGreen(255.0);
				rayColor.setRed(255.0);
			}
		}
		return rayColor;
	}
	
	public static vector getRefractedVector(vector origin, vector ray, Object[] spheres, int hitSphereIndex, double refractionIndex) {
		vector refracted = null;
		vector hitpoint = hitPoint(origin, ray, (Sphere)spheres[hitSphereIndex]);
		if (hitpoint==null){
			System.out.println("Hitpoint");
			return null;
		}
		vector normal = normal(ray, (Sphere)spheres[hitSphereIndex]);
		double c1 = hitpoint.getX()*normal.getX()+hitpoint.getY()*normal.getY()+hitpoint.getZ()*normal.getZ();
		double c2 = Math.sqrt(1- refractionIndex*refractionIndex*(1 - c1*c1));
		refracted = new vector(refractionIndex*hitpoint.getX()+(refractionIndex*c1-c2)*normal.getX(),
				refractionIndex*hitpoint.getY()+(refractionIndex*c1-c2)*normal.getY(),
				refractionIndex*hitpoint.getZ()+(refractionIndex*c1-c2)*normal.getZ()); //Rr = (n * V) + (n * c1 - c2) * N
		return refracted;
	}
	**/
	
	public static void sphereRayTracingShades(BufferedImage image, Object[] spheres){
		int width = image.getWidth();
		int height = image.getHeight();
		vector origin = new vector(0,0,0);
		try {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					vector ray = new vector(-1.0+2.0*j/((double)width), 1.0-2.0*i/((double)height), -1.0);
					color rayColor = new color(0,0,0); // color that will be displayed
					int whichSphere = getIndexIfRayHitsSphere(origin, ray, spheres, -1); // gets index of the sphere that the ray hits. -1 if it is void
					if (whichSphere == -1){ // ray hits void
						rayColor.setRed(0);
						rayColor.setGreen(0.2*(1.0-ray.getY())*255.0);
						rayColor.setBlue(0.1*255.0);
					}
					else if (whichSphere>=0 && whichSphere<spheres.length) { // ray hits some sphere
						ColoredSphere currentSphere = (ColoredSphere)spheres[whichSphere];
						if (!currentSphere.isLightSphere()) { // if it is not a light sphere
							if (currentSphere.getFactor()<1.0) { // If this is not a mirror
								if (!sphereUp(ray, spheres, whichSphere)) { // if there is no sphere up
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
							else if (currentSphere.getFactor() == 1.0) { // if it is a mirror
								rayColor = getMirroredColor(ray, spheres, whichSphere);
							}
						}
						else{ // Hits light sphere
							rayColor.setRed(255.0);
							rayColor.setGreen(255.0);
							rayColor.setBlue(255.0);
						}
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
		ColoredSphere one = new ColoredSphere(oneCenter, oneColor, 1.0, 1.0, false);
		vector twoCenter = new vector(2, 0, -4);
		color twoColor = new color(0.5, 1, 0.5);
		ColoredSphere two = new ColoredSphere(twoCenter, twoColor, 1, 0.0, false);
		vector threeCenter = new vector(-2, 0, -3);
		color threeColor = new color(0.5, 0.5, 1);
		ColoredSphere three = new ColoredSphere(threeCenter, threeColor, 1.0, 0.0, false);
		vector fourCenter = new vector(0, -100, 0);
		color fourColor = new color(1, 1, 1);
		ColoredSphere four = new ColoredSphere(fourCenter, fourColor, 98.5, 0, false);
		vector fiveCenter = new vector(0, 10, 0);
		ColoredSphere five = new ColoredSphere(fiveCenter, oneColor, 1.0, 0, true);


		Object[] spheres = new Object[5];
		spheres[0]=one; 
		spheres[1]=two;
		spheres[2]=three;
		spheres[3]=four;
		spheres[4]=five;
		sphereRayTracingShades(image, spheres);
		}
	
	
}