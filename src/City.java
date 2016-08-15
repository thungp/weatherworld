import java.util.*;

public class City {
  float windSpeed;
  int windDirection;
  String cityName;
  int dt; // time
  float percentCloudCoverage = 0.0f;
  
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
     this(aCity.getName(), aCity.getWindSpeed(), aCity.getWindDirection(), aCity.getTime(), aCity.getPercentCloudCoverage());
     //no defensive copies are created here, since 
    //there are no mutable object fields (String is immutable)
  }
  
  /**
  * regular constructor
  */
  public City(String name, float speed, int direction, int time, float percentCloudCoverage){
    this.cityName = name;
    this.windSpeed = speed;
    this.windDirection = direction;
    this.dt = time;
    this.percentCloudCoverage = percentCloudCoverage;
    
  }
  
}


