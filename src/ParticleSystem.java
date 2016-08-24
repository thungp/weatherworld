/**
 * Smoke Particle System
 * by Daniel Shiffman.
 *
 * Adapted by Daniel Vance for VerletWeatherWorld
 */

import processing.core.*;
import java.util.ArrayList;

class ParticleSystem {
	PApplet p;
	ArrayList<Particle> particles;    // An arraylist for all the particles
	PVector origin;                   // An origin point for where particles are birthed
	PImage img;

	ParticleSystem(PApplet p, int num, PVector v, PImage img_) {
		this.p = p;
		particles = new ArrayList<Particle>();              // Initialize the arraylist
		origin = v;		                                    // Store the origin point
		img = img_;
		for (int i = 0; i < num; i++) {
			particles.add(new Particle(p, origin, img));         // Add "num" amount of particles to the arraylist
		}
	}

	public void run() {
		for (int i = particles.size()-1; i >= 0; i--) {
			Particle p = particles.get(i);
			p.run();
			if (p.isDead()) {
				particles.remove(i);
			}
		}
	}

	// Method to add a force vector to all particles currently in the system
	public void applyForce(PVector dir) {
		// Enhanced loop!!!
		for (Particle p: particles) {
			p.applyForce(dir);
		}

	}  

	public void addParticle() {
		particles.add(new Particle(p, origin, img));
	}

}