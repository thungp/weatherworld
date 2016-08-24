/*
 *	https://processing.org/examples/texturesphere.html
 *  Adapted by Daniel Vance for VerletWeatherWorld
 */
import processing.core.*;

public class ParticleSphere {
	int ptsW, ptsH;
	
	PApplet p;
	PImage img;

	int numPointsW;
	int numPointsH_2pi; 
	int numPointsH;

	float[] coorX;
	float[] coorY;
	float[] coorZ;
	float[] multXZ;
	
	ParticleSphere(){
	}
	
	ParticleSphere(PApplet p, int ptsW, int ptsH, PImage img){
		this.p = p;
		this.img = img;
		this.ptsW = ptsW;
		this.ptsH = ptsH;
		initializeSphere(this.ptsW, this.ptsH);
	}

	public void initializeSphere(int numPtsW, int numPtsH_2pi) {

		// The number of points around the width and height
		numPointsW=numPtsW+1;
		numPointsH_2pi=numPtsH_2pi;  // How many actual pts around the sphere (not just from top to bottom)
		numPointsH=p.ceil((float)numPointsH_2pi/2)+1;  // How many pts from top to bottom (abs(....) b/c of the possibility of an odd numPointsH_2pi)

		coorX=new float[numPointsW];   // All the x-coor in a horizontal circle radius 1
		coorY=new float[numPointsH];   // All the y-coor in a vertical circle radius 1
		coorZ=new float[numPointsW];   // All the z-coor in a horizontal circle radius 1
		multXZ=new float[numPointsH];  // The radius of each horizontal circle (that you will multiply with coorX and coorZ)

		for (int i=0; i<numPointsW ;i++) {  // For all the points around the width
			float thetaW=i*2*p.PI/(numPointsW-1);
			coorX[i]=p.sin(thetaW);
			coorZ[i]=p.cos(thetaW);
		}

		for (int i=0; i<numPointsH; i++) {  // For all points from top to bottom
			if ((int)(numPointsH_2pi/2) != (float)numPointsH_2pi/2 && i==numPointsH-1) {  // If the numPointsH_2pi is odd and it is at the last pt
				float thetaH=(i-1)*2*p.PI/(numPointsH_2pi);
				coorY[i]=p.cos(p.PI+thetaH); 
				multXZ[i]=0;
			} 
			else {
				//The numPointsH_2pi and 2 below allows there to be a flat bottom if the numPointsH is odd
				float thetaH=i*2*p.PI/(numPointsH_2pi);

				//PI+ below makes the top always the point instead of the bottom.
				coorY[i]=p.cos(p.PI+thetaH); 
				multXZ[i]=p.sin(thetaH);
			}
		}
	}


	public void textureSphere(float rx, float ry, float rz) { 
		// These are so we can map certain parts of the image on to the shape 
		float changeU=img.width/(float)(numPointsW-1); 
		float changeV=img.height/(float)(numPointsH-1); 
		float u=0;  // Width variable for the texture
		float v=0;  // Height variable for the texture

		p.beginShape(p.TRIANGLE_STRIP);
		p.texture(img);
		for (int i=0; i<(numPointsH-1); i++) {  // For all the rings but top and bottom
			// Goes into the array here instead of loop to save time
			float coory=coorY[i];
			float cooryPlus=coorY[i+1];

			float multxz=multXZ[i];
			float multxzPlus=multXZ[i+1];

			for (int j=0; j<numPointsW; j++) { // For all the pts in the ring
				p.normal(-coorX[j]*multxz, -coory, -coorZ[j]*multxz);
				p.vertex(coorX[j]*multxz*rx, coory*ry, coorZ[j]*multxz*rz, u, v);
				p.normal(-coorX[j]*multxzPlus, -cooryPlus, -coorZ[j]*multxzPlus);
				p.vertex(coorX[j]*multxzPlus*rx, cooryPlus*ry, coorZ[j]*multxzPlus*rz, u, v+changeV);
				u+=changeU;
			}
			v+=changeV;
			u=0;
		}
		p.endShape();
	}
}
