import processing.core.*;
import processing.data.JSONObject;
import processing.opengl.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import org.multiply.processing.TimedEventGenerator;

/*


Assignment: Final Project
Author: Peter Thung and Daniel Vance
Based off of Verlet World



Change Log:
1. (PT) Migrated weather API code from NarutoGame2Server
2. (PT) Exposed cloudCoveragePercentage into City class and converted to a percentage.
3. (PT) Added city name/wind/direction Text overlay.
4. (PT) Removed references in the menu to the Verlet worms, rays, blockies, spiders and associated classes
5. (PT) Added exception handling code to the call to external weather data.
 */

public class VerletWeatherWorld extends PApplet {

	///////////////////////////////////////////////////
	//DEFINE
	///////////////////////////////////////////////////	
	/*
	 * 
	 * Weather API Variables
	 */
	static final int CITY_CHG_PERIODICITY_IN_SECONDS = 10;
	static final int WEATHER_UPDATE_IN_SECONDS = 20;
	/*
	{"_id":5516233,"name":"Amarillo","country":"US","coord":{"lon":-101.831299,"lat":35.222}}
	{"_id":4058076,"name":"Dallas County","country":"US","coord":{"lon":-87.083321,"lat":32.333469}}
	{"_id":1848313,"name":"Yokosuka","country":"JP","coord":{"lon":139.667221,"lat":35.283611}}
	{"_id":4720131,"name":"Portland","country":"US","coord":{"lon":-97.323883,"lat":27.877251}}
	{"_id":5601538,"name":"Moscow","country":"US","coord":{"lon":-117.000168,"lat":46.732391}}
	 */
	static final String CITY_LIST = "5516233,4058076,1848313,4720131,5601538";
	TimedEventGenerator weatherRefreshEventGenerator;
	TimedEventGenerator cityChangeEventGenerator;
	CityList cityList;
	float pctCloudCoverage = 0.0f;

	ParticleSystem ps;
	PImage img;
	PVector wind;

	int gravity = 1;

	//int bgCol = 0xffddddff;
	int bgCol = 0x00000000;
	Menu menu;
	Cage cage;

	int sphereCount = 100;
	VerletSphere[] spheres = new VerletSphere[sphereCount];
	
	
	int cloudCount = 20;
	ParticleSystem[] clouds = new ParticleSystem[cloudCount];

	public void setup() {
		//size(1024, 768, P3D); 

		// initialize weather
		cityList = new CityList(this);
		City ca = cityList.getNextCity();
		ca = cityList.getNextCity();	

		weatherRefreshEventGenerator = new TimedEventGenerator(
				this, "onWeatherRefreshTimerEvent", true);
		weatherRefreshEventGenerator.setIntervalMs(1000 * WEATHER_UPDATE_IN_SECONDS);

		cityChangeEventGenerator = new TimedEventGenerator(
				this, "onCityChangeTimerEvent", true);

		cityChangeEventGenerator.setIntervalMs(1000 * CITY_CHG_PERIODICITY_IN_SECONDS);

		// Cloud Particle System Variables
		img = this.loadImage("texture.png");	  
		wind = new PVector(0, 0, 0);

		cage = new Cage(new PVector(width*1.075f, height*1.07f, 700));

		String[] labels = {
				 "spheres", "clouds"
		};
		int[] states = {
				//offState, onState, overState, pressState
				0xffeebfbb, bgCol, 0xffeeffef, 0xffffaa66
		};
		menu = new Menu(Layout.TOP, new Dimension(width, 23), labels, states, ButtonType.RECT);
		//menu = new Menu(Layout.LEFT, new Dimension(65, height/4), labels, states, ButtonType.ROUNDED_RECT);
		//menu = new Menu(Layout.BOTTOM, new Dimension(width, 23), labels, states, ButtonType.RECT);
		//menu = new Menu(Layout.RIGHT, new Dimension(65, height), labels, states, ButtonType.RECT);
		// How to change corner radius through explicit casting
		//RoundedRectButton rb = (RoundedRectButton)(menu.buttons[0]);
		//rb.cornerRadius = 12;

		

		// spheres
		for (int i=0; i<spheres.length; i++) {
			float sz = random(20, 55);		  
			spheres[i] = new VerletSphere(new PVector(sz, sz, sz), random(.003f, .1f));
			spheres[i].push(new PVector (random(-100.01f, 100.01f), random(-100.01f, 100.01f), random(-100.01f, 100.01f)));
		}
		
		// clouds
		for (int i = 0; i < clouds.length; i++) {
			clouds[i] = new ParticleSystem(this, 3, new PVector(width / 4 -random(i * 15), height / 4 - random(i * 15), random(700/2)), img);
		}
	}

	public void onWeatherRefreshTimerEvent() {
		System.out.println("Got a onWeatherRefreshTimerEvent!");
		// list of city IDs
		//JSONObject json = loadJSONObject("http://api.openweathermap.org/data/2.5/group?id=4058076,1848313,4720131&units=metric&mode=json&appid=cbc1fc23414e03a198fb37afa9c5bbd8");
		try {
			JSONObject json = loadJSONObject("http://api.openweathermap.org/data/2.5/group?id=" + CITY_LIST + "&units=metric&mode=json&appid=cbc1fc23414e03a198fb37afa9c5bbd8");		
			saveJSONObject(json, "cachedWeather.json");
		} catch (Exception e) {
			System.out.println("Error in retreiving weather payload: " + e.getMessage());
			
		}
		if(cityList != null) {
			cityList.initializeCityList();
		}
	}


	public void onCityChangeTimerEvent() {
		//System.out.println("Got a onCityChangeTimerEvent!");
		if(cityList != null) {
			cityList. getNextCity();
		}
	}

	public void draw() {
		background(bgCol);
		
		menu.display();

		if(cityList != null) {
			String cityName = cityList.getCurrentCity().getName();
			int windDirection = cityList.getCurrentCity().getWindDirection();
			float windSpeed = cityList.getCurrentCity().getWindSpeed();
			pctCloudCoverage = cityList.getCurrentCity().getPercentCloudCoverage();
			NumberFormat defaultFormat = NumberFormat.getPercentInstance();
			defaultFormat.setMinimumFractionDigits(1);
			//System.out.println("Percent format: " + defaultFormat.format(pctCloudCoverage));
			wind = cityList.getCurrentCity().getWindVector();
			text("City: " + cityName + " Wind Direction/Speed: " + windDirection + "/" + windSpeed + " Cloud Percentage(" + defaultFormat.format(pctCloudCoverage) + ")" , (float)(0.1 * width), (float) (height * 0.95));
		}
		translate(width/2, height/2, -550);
		
		if(keyPressed){
			if(key == ' '){
				rotateY(frameCount*PI/680);
				//rotateX(frameCount*PI/280);
				//grotateZ(frameCount*PI/580);
			}
		}
		cage.display();


		if (menu.getSelected() == "spheres") {
			for (VerletSphere s : spheres) {
				//System.out.println("spheres menu item selected");
				s.verlet();
				s.display();
			}

		} else if (menu.getSelected() == "clouds"){
			for(int i = 0; i <= 20 * pctCloudCoverage; i++) {
				clouds[i].applyForce(wind.div(10));
				clouds[i].run();
				for (int j = 0; j < 2; j++) {
					clouds[i].addParticle();
				}
			}

		} 
	}
	class Cage {

		PVector span;

		Cage() {
		}

		Cage(PVector span) {
			this.span = span;
		}

		public void display() {
			noFill();
			strokeWeight(1.5f);
			stroke(255, 245, 245);
			box(span.x, span.y, span.z);
		}
	}
	abstract class Component {
		PVector position;
		Dimension dimension;
		String label;
		int labelCol, labelTextCol;
		int offState, onState, overState, pressState;
		int[] states = {
				offState, onState, overState, pressState
		};
		boolean hasBorder = false;
		boolean isSelected = false;
		int mouseClickCount=0;

		Component() {
		}

		Component(PVector position, Dimension dimension, 
				String label, int[] states) {
			this.position = position;
			this.dimension = dimension;
			this.label = label;
			labelCol = states[0];
			labelTextCol = 0xffffffff;
			offState = states[0];
			onState = states[1];
			overState = states[2];
			pressState = states[3];
			this.states = states;
		}

		//concrete method
		public void setHasBorder(boolean hasBorder){
			this.hasBorder = hasBorder;
		}

		// implement in subclasses
		public abstract boolean isHit();
		public abstract void display();
	}
	class Dimension {
		float w, h;

		Dimension(){
		}

		Dimension(float w, float h){
			this.w = w;
			this.h = h;
		}

	}
	class Menu {

		Layout menuPosition;
		ButtonType buttonType;
		String[] labels;
		int[] states;
		Dimension dimension;
		Component[] buttons;

		Menu() {
		}

		Menu(Layout menuPosition, Dimension dimension, String[] labels, int[] states) {
			this.menuPosition = menuPosition;
			this.dimension = dimension;
			this.labels = labels;
			this.states = states;
			buttons = new Component[labels.length];

			generate();
		}

		Menu(Layout menuPosition, Dimension dimension, String[] labels, int[] states, ButtonType buttonType) {
			this.menuPosition = menuPosition;
			this.dimension = dimension;
			this.labels = labels;
			this.states = states;
			this.buttonType = buttonType;
			buttons = new Component[labels.length];

			generate();
		}


		public void generate() {

			float btnW, btnH;
			if (menuPosition == Layout.TOP || menuPosition == Layout.BOTTOM) {
				btnW = dimension.w/buttons.length;
				btnH = dimension.h;
			} else {

				// left or right position
				btnW = dimension.w;
				btnH = dimension.h/buttons.length;
			}

			for (int i=0; i<buttons.length; i++) {
				PVector pos;
				Dimension dim;
				switch (menuPosition) {
				case TOP:
					pos = new PVector(btnW * i, 0);
					dim = new Dimension(btnW, btnH);
					break; 
				case BOTTOM:
					pos = new PVector(btnW * i, height-btnH);
					dim = new Dimension(btnW, btnH); 
					break;
				case LEFT:
					pos = new PVector(0, btnH * i);
					dim = new Dimension(btnW, btnH); 
					break;
				case RIGHT:
					pos = new PVector(width-btnW, btnH * i);
					dim = new Dimension(btnW, btnH); 
					break;
				default: // top
				pos = new PVector(btnW * i, 0);
				dim = new Dimension(btnW, btnH);
				}

				switch (buttonType) {
				case RECT:
					buttons[i] = new RectButton(pos, dim, labels[i], states);
					break;
				case ROUNDED_RECT:
					buttons[i] = new RoundedRectButton(pos, dim, labels[i], states, 6);
					break;
				default:
					buttons[i] = new RectButton(pos, dim, labels[i], states);
				}
			}
		}


		public void display() {
			for (int i=0; i<buttons.length; i++) {
				buttons[i].display();
			}

			createMenuEvents();
		}

		public void createMenuEvents() {
			for (int i=0; i<buttons.length; i++) {
				// pressed
				if (buttons[i].isHit() && mousePressed) {
					select(i);
					buttons[i].labelCol = states[3];
					// mouse over
				} else if (buttons[i].isHit() && !buttons[i].isSelected) {
					buttons[i].labelCol = states[2];
					buttons[i].labelTextCol = 0xff766676;
					// selected
				} else if (buttons[i].isSelected) {
					buttons[i].labelCol = states[1];
					buttons[i].labelTextCol = 0xff766676;
					// default
				} else {
					buttons[i].labelCol = states[0];
					buttons[i].labelTextCol = 0xffffffff;
				}
			}
		}
		public void select(int isSelectedID) {
			for (int i=0; i<buttons.length; i++) {
				if (i==isSelectedID) {
					buttons[i].isSelected = true;
				} else {
					buttons[i].isSelected = false;
				}
			}
		}

		public String getSelected() {
			String btn;
			for (int i=0; i<buttons.length; i++) {
				if (buttons[i].isSelected) {
					return buttons[i].label;
				}
			}
			return "";
		}
	}
	class RectButton extends Component {
		PFont font;

		RectButton(){
		}

		RectButton(PVector position, Dimension dimension, 
				String label, int[] states) {
			super(position, dimension, label, states);
			font = loadFont("ArialMT-22.vlw");
			textFont(font, 15);
		}

		public boolean isHit() {
			if (mouseX >= position.x && mouseX <= position.x + dimension.w &&
					mouseY >= position.y && mouseY <= position.y + dimension.h) {
				return true;
			}
			return false;
		}

		public void display() {
			if (hasBorder) {
				stroke(100);
			} else {
				noStroke();
			}
			fill(labelCol);
			rect(position.x, position.y, dimension.w, dimension.h);

			fill(labelTextCol);
			float tw = textWidth(label);
			textAlign(LEFT, CENTER);
			text(label, position.x +(dimension.w-tw)/2.0f, position.y + dimension.h/2);
		}
	}
	class RoundedRectButton extends RectButton {
		float cornerRadius = 6;

		RoundedRectButton(){
		}

		RoundedRectButton(PVector position, Dimension dimension, 
				String label, int[] states, float cornerRadius) {
			super(position, dimension, label, states);
			this.cornerRadius = cornerRadius;
		}

		public void display() {
			if (hasBorder) {
				stroke(100);
			} else {
				noStroke();
			}
			fill(labelCol);
			rect(position.x, position.y, dimension.w, dimension.h, cornerRadius);

			fill(labelTextCol);
			float tw = textWidth(label);
			textAlign(LEFT, CENTER);
			text(label, position.x +(dimension.w-tw)/2.0f, position.y + dimension.h/2);
		}
	}
	class VerletBall {

		PVector pos, posOld;

		VerletBall() {
		}

		VerletBall(PVector pos) {
			this.pos = pos;
			this.posOld  = new PVector(pos.x, pos.y, pos.z);
		}

		public void verlet() {
			PVector posTemp = new PVector(pos.x, pos.y, pos.z);
			pos.x += (pos.x-posOld.x);
			pos.y += (pos.y-posOld.y);
			pos.z += (pos.z-posOld.z);
			posOld.set(posTemp);

		}

	}

	class VerletSphere extends VerletObj {

		PVector sz;

		VerletSphere() {
		}

		VerletSphere(PVector sz, float stiffness) {
			super(stiffness);
			balls = new VerletBall[1];
			sphereDetail(3);
			this.sz = sz;

			// Only Ball
			balls[0] = new VerletBall(new PVector(-sz.x/2, -sz.y/2, sz.z/2)); //LF

		}

		public void display() {

			noFill();
			beginShape();
			for (VerletBall b : balls) {
				pushMatrix();
				translate(b.pos.x, b.pos.y, b.pos.z);
				stroke(255, 75);
				box(5);
				popMatrix();
			}
		}
	}


	abstract class VerletObj {

		VerletBall[] balls;
		ArrayList<VerletStick> sticks = new ArrayList<VerletStick>();
		float stiffness;

		VerletObj() {
		}

		VerletObj(float stiffness) {
			this.stiffness = stiffness;
		}



		public void push(PVector push) {
			balls[0].pos.add(push);
		}


		public void verlet() {
			for (VerletBall b : balls) {
				b.verlet();
			}

			for (VerletStick s : sticks) {
				s.constrainLen();
			}

			collide();
		}

		public void collide() {
			float jolt = 3.0f;
			for (VerletBall b : balls) {
				if (b.pos.x > width/2) {
					b.pos.x = width/2;
					b.pos.x -= jolt;
				} else if (b.pos.x < -width/2) {
					b.pos.x = -width/2;
					b.pos.x += jolt;
				}

				if (b.pos.y > height/2) {
					b.pos.y = height/2;
					b.pos.y -= jolt;
				} else if (b.pos.y < -height/2) {
					b.pos.y = -height/2;
					b.pos.y += jolt;
				}

				if (b.pos.z > 250) {
					b.pos.z = 250;
					b.pos.z -= jolt;
				} else if (b.pos.z < -250) {
					b.pos.z = -250;
					b.pos.z += jolt;
				}
			}
		}
	}


	class Vec2 {
		float x, y;

		Vec2() {
		}
		Vec2(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	class VerletStick {

		VerletBall b1, b2;
		float stiffness;

		PVector vecOrig;
		float len;
		Vec2 bias;
		boolean isVisible;

		VerletStick() {
		}

		VerletStick(VerletBall b1, VerletBall b2, float stiffness, boolean isVisible) {
			this.b1 = b1;
			this.b2 = b2;
			this.stiffness = stiffness;
			bias = new Vec2(.5f, .5f);
			this.isVisible = isVisible;
			vecOrig  = new PVector(b2.pos.x-b1.pos.x, b2.pos.y-b1.pos.y, b2.pos.z-b1.pos.z);
			len = dist(b1.pos.x, b1.pos.y, b1.pos.z, b2.pos.x, b2.pos.y, b2.pos.z);
		}

		VerletStick(VerletBall b1, VerletBall b2, float stiffness, Vec2 bias, boolean isVisible) {
			this.b1 = b1;
			this.b2 = b2;
			this.stiffness = stiffness;
			this.bias = bias;
			this.isVisible = isVisible;
			vecOrig  = new PVector(b2.pos.x-b1.pos.x, b2.pos.y-b1.pos.y, b2.pos.z-b1.pos.z);
			len = dist(b1.pos.x, b1.pos.y, b1.pos.z, b2.pos.x, b2.pos.y, b2.pos.z);
		}


		// constrainVal needs to be changed for anchors
		public void constrainLen() {
			for (int i=0; i<2; i++) {
				PVector delta = new PVector(b2.pos.x-b1.pos.x, b2.pos.y-b1.pos.y, b2.pos.z-b1.pos.z);
				float deltaLength = delta.mag();
				float difference = ((deltaLength - len) / deltaLength);
				b1.pos.x += delta.x * (bias.x * stiffness * difference);
				b1.pos.y += delta.y * (bias.x * stiffness * difference);
				b1.pos.z += delta.z * (bias.x * stiffness * difference);
				b2.pos.x -= delta.x * (bias.y * stiffness * difference);
				b2.pos.y -= delta.y * (bias.y * stiffness * difference);
				b2.pos.z -= delta.z * (bias.y * stiffness * difference);
			}
		}



		public void display() {
			if (isVisible) {
				//	      beginShape();
				//	      vertex(b1.pos.x, b1.pos.y, b1.pos.z);
				//	      //curveVertex(b1.pos.x, b1.pos.y, b1.pos.z);
				//	      //curveVertex(b2.pos.x, b2.pos.y, b2.pos.z);
				//	      vertex(b2.pos.x, b2.pos.y, b2.pos.z);
				//	      endShape();
				line(b1.pos.x, b1.pos.y, b1.pos.z, b2.pos.x, b2.pos.y, b2.pos.z);
			}
		}
	}
	class VerletWorm extends VerletObj {

		float len = 175.0f;
		int joints = 12;
		float thickness;
		float[] body;

		VerletWorm() {
		}

		VerletWorm(float len, int joints, float stiffness, float thickness) {
			super(stiffness);
			this.len = len;
			this.joints = joints;
			this.thickness = thickness;
			balls = new VerletBall[joints];
			PVector randomBirthPos = new PVector(random(-len/2, len/2), random(-height/2.5f, height/2.5f), random(-150, 150));
			float randomBodyVector = random(TWO_PI);
			body = new float[joints];
			float bodyGap = PI/joints;
			for (int i=0; i<joints; i++) {
				balls[i] = new VerletBall(new PVector(randomBirthPos.x+cos(randomBodyVector)*i*(len/(joints-1)), 
						randomBirthPos.y+sin(randomBodyVector)*i*(len/(joints-1)), 
						randomBirthPos.z));

				if (i>0) {
					sticks.add(new VerletStick(balls[i-1], balls[i], stiffness, true));
				}
				body[i] = abs(sin(bodyGap*i)*16);
			}
		}

		public void display() {
			// spine
			stroke(65, 75, 155);
			strokeWeight(thickness);
			for (VerletStick s : sticks) {
				s.display();
			}
			//body
			stroke(125, 135, 190);
			strokeWeight(.75f);
			for (int i=0; i<joints; i++) {
				pushMatrix();
				translate(balls[i].pos.x, balls[i].pos.y, balls[i].pos.z);
				fill(125, 135, 190, 25);
				//noFill();
				ellipse(0, 0, body[i], body[i]);
				popMatrix();
			}
		}
	}
	public void settings() {  
		size(1024, 768, P3D); 
	}

	public static void main(String[] passedArgs) {
		PApplet.main(new String[] { "VerletWeatherWorld" });
	}

}
