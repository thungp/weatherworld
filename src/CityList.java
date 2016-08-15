import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class CityList {
  
   int currentCityIndex = -1;
   JSONObject json;
   List<City> cList = null;
   PApplet p;

   public CityList(PApplet p){
	  this.p = p;
      initializeCityList(); 
   }
   public City getNextCity() {
     if (cList != null) {
       currentCityIndex = (++currentCityIndex) % cList.size();
       return cList.get(currentCityIndex);     
     }  else {
       return null; 
     }     
   }
   
   public City getCurrentCity(){
     if(cList != null){
       return cList.get(currentCityIndex);
     } else {
      return null;
     
     } 
   }
   public void initializeCityList() {
     // eventually check to see if there is any change. 
     json = p.loadJSONObject("../cachedWeather.json");
     int count = json.getInt("cnt");
     System.out.println("count = " + count);
     JSONArray  cityList = json.getJSONArray("list");
     cList = new ArrayList<City>();
     for(int i = 0; i < count; i++) {
       JSONObject city =  cityList.getJSONObject(i);
       JSONObject windJSONObj = city.getJSONObject("wind");
       JSONObject cloudJSONObj = city.getJSONObject("clouds");
       float windSpeed = windJSONObj.getFloat("speed");
       int windDegrees = windJSONObj.getInt("deg");
       float percentCloudCoverage = (float) cloudJSONObj.getInt("all") ;
       percentCloudCoverage = percentCloudCoverage / 100;
       String cityName = city.getString("name");
       int dt = city.getInt("dt");
       City cityObj = new City(this.p, cityName, windSpeed, windDegrees, dt, percentCloudCoverage);
       cList.add(cityObj);
       System.out.println(cityName + ", " + windSpeed + ", " + windDegrees + ", " + dt + "percetnCloutCoverage " + percentCloudCoverage);
       PVector windVector = cityObj.getWindVector();
       System.out.println("WindVector (x, y, z, mag) " + windVector.x + ", " + windVector.y +  ", " + windVector.z + ",  " + windVector.mag() );
     }
     
   } 
   
   public int getCityListCount(){
      if(cList != null){
        return cList.size();
      } else {
        return 0;
      }
   }
  
}