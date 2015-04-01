/* Dana Adylova
 * 02/04/2015
 * This class has methods that are used to produce a spheres ray traced image, including shades.
*/
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class shades {
	
	/** Gets a hitpoint (three coordinates) of a vector and a sphere if they intersect
	 * 
	 * @param origin vector of the ray that hits sphere
	 * @param ray - the ray itself
	 * @param sph - the sphere that the ray hits/misses
	 * @return hitpoint vector
	 */
	public static vector hitPoint(vector origin, vector ray, ColoredSphere sph) { // check if there is smth in between for shades
		vector q = sph.getCenter().subtractVector(origin);
		double lengthRay = ray.vectorLength();
		double qLength = q.vectorLength();
		double cos = ray.cos(q);
		double d = qLength*Math.sqrt(1-cos*cos);
		if (cos<0 || d>=sph.getRadius())
			return null;
		double t = (Math.sqrt(qLength*qLength - d*d) - Math.sqrt(sph.getRadius()*sph.getRadius() - d*d))/lengthRay;
		if (t<=0.000000001)
			t = 0;
		vector hit = new vector (t*ray.getX()+origin.getX(), t*ray.getY()+origin.getY(), t*ray.getZ()+origin.getZ());
		return hit;
	}
	
	/** Gets a hitpoint if the checkSphere is the closest hit sphere. If not, returns null
	 * 
	 * @param origin
	 * @param ray
	 * @param spheres
	 * @param checkSphereIndex
	 * @param currentSphereIndex
	 * @return
	 */
	public static vector hitPoint(vector origin, vector ray, ColoredSphere[] spheres, int checkSphereIndex, int currentSphereIndex) {
		int whichSphereHits = getSphereIndexIfRayHitsSphere(origin, ray, spheres, currentSphereIndex);
		if (whichSphereHits != checkSphereIndex) 
			return null;
		else {
			ColoredSphere sph = spheres[checkSphereIndex];
			vector q = sph.getCenter().subtractVector(origin);
			double lengthRay = ray.vectorLength();
			double qLength = q.vectorLength();
			double cos = ray.cos(q);
			double d = qLength*Math.sqrt(1-cos*cos);
			if (cos<0 || d>=sph.getRadius())
				return null;
			double t = (Math.sqrt(qLength*qLength - d*d) - Math.sqrt(sph.getRadius()*sph.getRadius() - d*d))/lengthRay;
			if (t<=0.000000001)
				t = 0;
			vector hit = new vector (t*ray.getX()+origin.getX(), t*ray.getY()+origin.getY(), t*ray.getZ()+origin.getZ());
			return hit;
		}
	}
	
	/** How many times random ray reaches light sphere compared to (how many time it reaches it AND hits some sphere in between)
	 * 
	 * @param ray
	 * @param spheres - sphere system
	 * @param currentSphereIndex - index of the sphere that gets hit
	 * @return
	 */
	public static double getShadeIndex (vector hitpoint, ColoredSphere[] spheres, ColoredSphere[] lightSpheres, int currentSphereIndex) {

		if (hitpoint == null)
			return 0;
		int hit = 0;
		int samples = 100;
		for (int i = 0; i<lightSpheres.length; i++) {
			ColoredSphere lightSphere = lightSpheres[i];
			for (int k = 0; k<samples; k++) {
				vector randomPointInSphere = vector.randomPointInSphere(lightSphere.getRadius());
				vector fromHitpoinToLightSphere = lightSphere.getCenter().subtractVector(hitpoint);
				vector hitpointRandomPoint = fromHitpoinToLightSphere.addVector(randomPointInSphere).normalize();
				if (hitPoint(hitpoint, hitpointRandomPoint, spheres, spheres.length - lightSpheres.length +i, currentSphereIndex) == null)
					hit = hit+1;
			}
		}
		return (1.0*hit)/(1.0*samples*lightSpheres.length);
	}
	
	/** Returns normal vector
	 * 
	 * @param ray that hits sphere at some point
	 * @param sph that gets hit by the ray
	 * @return normal to the sphere at the hitpoint
	 */
	public static vector normal(vector ray, ColoredSphere sph) {
		vector origin = new vector (0,0,0);
		vector hit = hitPoint(origin, ray,sph);
		if (hit == null){
			return null;
		}
		vector norm = hit.subtractVector(sph.getCenter()).normalize();
		return norm;
	}
	
	/** Returns index of a closest sphere if ray hits any sphere. -1 if ray hits void
	 * 
	 * @param origin of ray
	 * @param ray - ray itself
	 * @param spheres - 
	 * @param currentSphereIndex
	 * @return
	 */
	public static int getSphereIndexIfRayHitsSphere(vector origin, vector ray, ColoredSphere[] spheres, int currentSphereIndex){
		int index = -1;
		for (int i = 0; i<spheres.length; i++) {
			if (i!= currentSphereIndex) {
				if (hitPoint(origin, ray, spheres[i])!=null) {
					if (index<0) {
						index = i;
					}
					else {
						vector previous = hitPoint(origin, ray, spheres[index]);
						vector current = hitPoint(origin, ray, spheres[i]);
						vector originPrevious = previous.subtractVector(origin);
						vector originCurrent = current.subtractVector(origin);
						if (originCurrent.vectorLength() < originPrevious.vectorLength()) {
							index = i;
						}
					}
				}
			}
		}
		return index;
	}
	
	/** Returns the color
	 * 
	 * @param ray
	 * @param spheres - sphere system
	 * @param fromWhichSphereRayGoes - index of the sphere that gets hit
	 * @return the color that gets reflected
	 */
	public static color getColor(vector origin, vector ray, ColoredSphere[] spheres, ColoredSphere[] lightSpheres,int fromWhichSphereRayGoes, int depth) {
		color rayColor = new color(0,0,0); // Reflected color that will be returned
		int whichSphere = getSphereIndexIfRayHitsSphere(origin, ray, spheres, fromWhichSphereRayGoes); // gets index of the sphere that the ray hits. -1 if it is void
		if (whichSphere == -1){ // ray hits void
			rayColor.setRed(0);
			rayColor.setGreen(0.2*(1.0-ray.getY()/ray.vectorLength()));
			rayColor.setBlue(0.1);
		}
		else { // ray hits some sphere
			ColoredSphere currentSphere = spheres[whichSphere]; // the sphere that the ray hits
			if (currentSphere.isLightSphere()) {// if it is a light sphere
				rayColor.setRed(1); rayColor.setGreen(1); rayColor.setBlue(1);
			}
			else { // if sphere is not a light sphere
				vector hitpoint = hitPoint(origin, ray, currentSphere);
				vector normal = normal(hitpoint, currentSphere);
				double cosToLight = normal.cos(lightSpheres[0].getCenter().subtractVector(hitpoint));
				if (depth<10) { // if depth is ok
					double shadeIndex = 1.0 - getShadeIndex(hitpoint, spheres, lightSpheres, whichSphere);
					if (currentSphere.isMirror()) { // If this is a mirror. Mapped sphere cannot be a mirror
						double c1 = hitpoint.getX()*normal.getX()+hitpoint.getY()*normal.getY()+hitpoint.getZ()*normal.getZ();
						vector reflected = new vector(hitpoint.getX()-2.0*c1*normal.getX(), hitpoint.getY()-2.0*c1*normal.getY(), hitpoint.getZ()-2.0*c1*normal.getZ());
						color reflColor = getColor(hitpoint, reflected, spheres, lightSpheres,  whichSphere, depth+1); // R*reflected
						rayColor = new color(currentSphere.getPicColor(null).getRed()*reflColor.getRed(),
								currentSphere.getPicColor(null).getGreen()*reflColor.getGreen(), 
								currentSphere.getPicColor(null).getBlue()*reflColor.getBlue());
					}
					else { // if this is not a mirror. Perfect diffusion
							if (cosToLight>0) {
								vector random = vector.randomDirectionUp(hitpoint, normal);
								color randomRayColor = getColor(hitpoint, random, spheres, lightSpheres, whichSphere, depth+1);
								rayColor = new color(shadeIndex*cosToLight*currentSphere.getPicColor(normal).getRed()*(1.0 - randomRayColor.getRed()), 
										shadeIndex*cosToLight*currentSphere.getPicColor(normal).getGreen()*(1.0 - randomRayColor.getGreen()),
										shadeIndex*cosToLight*currentSphere.getPicColor(normal).getBlue()*(1.0 - randomRayColor.getBlue()));
							}
							else 
								rayColor = new color();
						
					}
				}
				else { // if depth is too big
					if (cosToLight>0) {
						rayColor = new color(currentSphere.getPicColor(null).getRed()*cosToLight,
								currentSphere.getPicColor(null).getGreen()*cosToLight, 
								currentSphere.getPicColor(null).getBlue()*cosToLight);
					}
					else
						rayColor = new color();
				}
			}
		}
		return rayColor;
	}
	
	public static void sphereRayTracingShades(BufferedImage image, ColoredSphere[] spheres, ColoredSphere[] lightSpheres){
		int width = image.getWidth();
		int height = image.getHeight();
		vector origin = new vector(0,0,0);
		try {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					double red = 0;
					double green = 0;
					double blue = 0;
					int sample = 1;
					for (int n = 0; n<sample; n++) {
						vector ray = new vector(-1.0+2.0*j/((double)width), 1.0-2.0*i/((double)height), -1.0);
						color rayColor = getColor(origin, ray, spheres, lightSpheres, -1, 0); // color that will be displayed
						red = red+rayColor.getRed(); green = green+rayColor.getGreen(); blue = blue+rayColor.getBlue();
					}
					int r = (int)(red*255.0/sample); int g = (int)(green*255.0/sample); int b = (int)(blue*255.0/sample);
				    int rgb = ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
				    image.setRGB(j,i,rgb);
			      }
			    }
			ImageIO.write(image, "jpg", new File("/Users/danaadylova/Desktop/graphics.jpg"));
		} catch (IOException e) {
			System.out.println("Exception occured :" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		BufferedImage image = new BufferedImage (2000, 2000, BufferedImage.TYPE_INT_RGB);
		
		BufferedImage imgStripes = null;
		try {
			imgStripes = ImageIO.read(new File("/Users/danaadylova/Desktop/debugging.png"));
			} catch (IOException e) {
				System.out.println("The image was not loaded.");
			}
		vector oneCenter = new vector(1, 0, -2);
		color oneColor = new color(1.0, 0.5, 0.5);
		ColoredSphere one = new ColoredSphere(oneCenter, oneColor, 0.5, false, false, imgStripes);
		
		vector twoCenter = new vector(0, 1, -3.5);
		color twoColor = new color(1,1,1);
		ColoredSphere two = new ColoredSphere(twoCenter, twoColor, 1.3, true, false, null); // the copper mirror
		
		vector threeCenter = new vector(-1, 0, -2);
		color threeColor = new color(0.5, 0.5, 1);
		BufferedImage imgGlobe = null;
		try {
			imgGlobe = ImageIO.read(new File("/Users/danaadylova/Desktop/world32k.jpg"));
			} catch (IOException e) {
				System.out.println("The image was not loaded.");
			}
		ColoredSphere three = new ColoredSphere(threeCenter, threeColor, 0.5, false, false, imgGlobe); // the globe
		
		vector fourCenter = new vector(0, -105, 0);
		color fourColor = new color(184.0/255.0, 115.0/255.0, 51.0/255.0);
		ColoredSphere four = new ColoredSphere(fourCenter, fourColor, 102.5, false, false, null); // the large sphere
		
		vector fiveCenter = new vector(0, 8, -1);
		ColoredSphere five = new ColoredSphere(fiveCenter, oneColor, 1.0, false, true, null);

		
		ColoredSphere[] spheres = new ColoredSphere[5];
		spheres[0]=one; 
		spheres[1]=two;
		spheres[2]=three;
		spheres[3]=four;
		spheres[4]=five;
		
		
		ColoredSphere[] lightSpheres = new ColoredSphere[1];
		lightSpheres[0] = five;
//		System.out.println("Console");
		sphereRayTracingShades(image, spheres, lightSpheres);
		}
	
	
}