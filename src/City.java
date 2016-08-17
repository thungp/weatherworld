import java.util.*;

import processing.core.PApplet;
import processing.core.PVector;

public class City {
  float windSpeed;
  int windDirection = 0; // degrees
  PVector windVector = null;
  PApplet p;
  String cityName;
  int dt; // time
  float percentCloudCoverage = 0.0f;
  
  public PApplet getP() {
		return p;
	}
	
	public void setP(PApplet p) {
		this.p = p;
	}

	public PVector getWindVector() {
		// calculate windVector
		// TODO eventually do error detection.
		windVector = PVector.fromAngle(p.radians(this.windDirection));
		windVector.setMag(this.windSpeed);
		return windVector;

	}
	

	

	  
	public float getPercentCloudCoverage() {
		return percentCloudCoverage;
	}
	
	public void setPercentCloudCoverage(float percentCloudCoverage) {
		this.percentCloudCoverage = percentCloudCoverage;
	}
	
	public String getName(){
	    return cityName;
	}
  
  public float getWindSpeed() {
    // if wind direction between 0-180 make wind speed positive for game else make it negative.
    if( windDirection > 0 && windDirection <= 180) {
      return windSpeed;
    } else {
      return windSpeed * (-1);
    }
  }
  
  private void updateWindVector() {
	  windVector = PVector.fromAngle(p.radians(this.windDirection));  
  }
  public int getWindDirection(){
    return windDirection;
  }
  
  
  public int getTime(){
    return dt;
  }
    
    
  /**
  * Copy constructor
  */
  public City(City aCity){
     this(aCity.getP(), aCity.getName(), aCity.getWindSpeed(), aCity.getWindDirection(), aCity.getTime(), aCity.getPercentCloudCoverage());
     //no defensive copies are created here, since 
    //there are no mutable object fields (String is immutable)
  }
  
  /**
  * regular constructor
  */
  public City(PApplet p, String name, float speed, int direction, int time, float percentCloudCoverage){
    this.p = p;
	this.cityName = name;
    this.windSpeed = speed;
    this.windDirection = direction;
    this.dt = time;
    this.percentCloudCoverage = percentCloudCoverage;
    
  }
  
}


