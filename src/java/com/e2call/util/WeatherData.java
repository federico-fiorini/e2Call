package com.e2call.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author FEDE
 */
public class WeatherData {
    
    private GPS gps;
    private String name;
    private String description;
    private Float temperature;
    private Float humidity;
    private Float visibility;
    private Float pressure;
    private Float cloudcover;
    private Float precipitationMM;
    private Float wind_speed;

    public WeatherData(GPS gps){
        this.gps = gps;
    }
    
    /**
     * Updates all the weather attributes of the class
     * This method use the "worldweatheronline" api for get the weather data
     * Documentation here: https://developer.worldweatheronline.com/page/documentation
     * @throws Exception 
     */
    public void getWeatherData() throws Exception{
        String key = "6e6809611b146d24a0a561c9a08eb0ec0378a3dd";
        String url = "http://api.worldweatheronline.com/free/v1/weather.ashx?q="+gps.getLatitude()
                + "%2C"+gps.getLongitude() + "&format=xml&num_of_days=1&key="+key;        
        URL xml = new URL(url);
        File f = new File("weather_"+gps.getLatitude()+"_"+gps.getLongitude()+".xml");
        FileUtils.copyURLToFile(xml, f);
        
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;
        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(f);

            //get the root element
            Element docEle = dom.getDocumentElement();
            
            //get a nodelist of  elements
            NodeList nl_curr = docEle.getElementsByTagName("current_condition");
            if(nl_curr != null && nl_curr.getLength() > 0) {
                for(int i = 0 ; i < nl_curr.getLength();i++) {
                    //get the current condition element
                    Element curr = (Element)nl_curr.item(i);
                    name = XMLParser.getTextValue(curr,"weatherDesc");
                    description = name;
                    temperature = XMLParser.getFloatValue(curr,"temp_C");
                    humidity = XMLParser.getFloatValue(curr,"humidity");
                    visibility = XMLParser.getFloatValue(curr,"visibility");
                    pressure = XMLParser.getFloatValue(curr,"pressure");
                    cloudcover = XMLParser.getFloatValue(curr,"cloudcover");
                    precipitationMM = XMLParser.getFloatValue(curr,"precipMM");
                    wind_speed = XMLParser.getFloatValue(curr,"windspeedKmph");
                }
            }
            
            f.delete();
            
        }catch(  ParserConfigurationException | SAXException | IOException pce) {
                pce.printStackTrace();
        }
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the temperature
     */
    public Float getTemperature() {
        return temperature;
    }

    /**
     * @return the humidity
     */
    public Float getHumidity() {
        return humidity;
    }

    /**
     * @return the visibility
     */
    public Float getVisibility() {
        return visibility;
    }

    /**
     * @return the pressure
     */
    public Float getPressure() {
        return pressure;
    }

    /**
     * @return the cloudcover
     */
    public Float getCloudcover() {
        return cloudcover;
    }

    /**
     * @return the precipitationMM
     */
    public Float getPrecipitationMM() {
        return precipitationMM;
    }

    /**
     * @return the wind_speed
     */
    public Float getWind_speed() {
        return wind_speed;
    }
    
}
