import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
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
       float windSpeed = windJSONObj.getFloat("speed");
       int windDegrees = windJSONObj.getInt("deg");
       String cityName = city.getString("name");
       int dt = city.getInt("dt");
       City cityObj = new City(cityName, windSpeed, windDegrees, dt);
       cList.add(cityObj);
       //println(cityName + ", " + windSpeed + ", " + windDegrees + ", " + dt);
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