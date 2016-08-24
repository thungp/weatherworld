/**
 * Smoke Particle System
 * by Daniel Shiffman
 * 
 * Adapted by Daniel Vance for VerletWeatherWorld
 *
 */

import processing.core.*;

public class Particle {
	PApplet p;
	ParticleSphere pSphere;
	PVector loc;
	PVector vel;
	PVector acc;
	float lifespan;
	PImage img;

	Particle(PApplet p, PVector l,PImage img_) {
		this.p = p;
		acc = new PVector(0,0,0);
		float vx = p.randomGaussian() * 0.3f;
		float vy = p.randomGaussian() * 0.3f - 1.0f;
		float vz = p.randomGaussian() * 0.3f;
		vel = new PVector(vx, vy, vz);
		loc = l.copy();
		lifespan = 100.0f;
		img = img_;
		pSphere = new ParticleSphere(this.p, 30, 30, this.img);
	}

	void run() {
		update();
		render();
	}

	// Method to apply a force vector to the Particle object
	// Note we are ignoring "mass" here
	void applyForce(PVector f) {
		acc.add(f);
	}  

	// Method to update location
	void update() {
		vel.add(acc);
		loc.add(vel);
		lifespan -= 2.5;
		acc.mult(0); // clear Acceleration
	}

	// Method to display
	void render() {
		p.imageMode(p.CENTER);
		p.tint(255,lifespan);
		p.image(img,loc.x, loc.y);
		
		float theta = 0.0f;
		for(int i = 0; i < 90; i++){
			p.image(img,loc.x * p.cos(theta) * p.random(0.7f, 1.2f), loc.y * p.sin(theta) * p.random(0.7f, 1.5f) );
			theta += p.TWO_PI / 90;
		}
		
		// for 3D Orb
		p.translate(0, 0, 2);
		p.rotateX(7.0f);
		//p.rotateZ(1.1f); // For fun
		p.image(img,loc.x, loc.y);
		
//		pSphere.textureSphere(100.0f, 100.0f, 100.0f); // 3d Sphere
		
		// Drawing a circle instead
		// fill(255,lifespan);
		// noStroke();
		// ellipse(loc.x,loc.y,img.width,img.height);
	}

	// Is the particle still useful?
	boolean isDead() {
		if (lifespan <= 0.0) {
			return true;
		} else {
			return false;
		}
	}
}
