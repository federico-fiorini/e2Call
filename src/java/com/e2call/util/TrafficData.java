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
public class TrafficData {
    
    private String traffic_name;
    private String traffic_description;
    private String roadcondition_name;
    private String roadcondition_description;
    private LocationData location;
    
    public TrafficData(LocationData location){        
        this.traffic_name = "Data not available";
        this.traffic_description = "Data not available";
        this.roadcondition_name = "Data not available";
        this.roadcondition_description = "Data not available";
        this.location = location;
    }
    
    /**
     * Updates all the traffic attributes of the class
     * This method use the Microsoft's Bing Maps REST Services api for get the traffic data
     * Documentation here: http://msdn.microsoft.com/en-us/library/hh441726
     * @throws Exception 
     */
    public void getTrafficData() throws Exception{
        GPS bottom_right = new GPS(location.getGps().getLatitude() - 0.1 , location.getGps().getLongitude() + 0.1, location.getGps().getAltitude());
        GPS top_left = new GPS(location.getGps().getLatitude() + 0.1 , location.getGps().getLongitude() - 0.1, location.getGps().getAltitude());
        
        String key = "AmXrjfxfrRkLti9dXdCvydfvN5i7nxKMmo8hB19w1W1wpQ6npP0JydIRrsUegcJB";
        String url = "http://dev.virtualearth.net/REST/V1/Traffic/Incidents/" +
                bottom_right.getLatitude() + "," + bottom_right.getLongitude() + "," + 
                top_left.getLatitude() + "," + top_left.getLongitude() + "?o=xml&key=" + key;
        URL xml = new URL(url);
        File f = new File("traffic"+location.getGps().getLatitude()+"_"+location.getGps().getLongitude()+".xml");
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
            //get a nodelist of resourceSets
            NodeList res_sets = docEle.getElementsByTagName("ResourceSets");
            if(res_sets != null && res_sets.getLength() > 0) {                
                //get the resourceSets element
                Element resource_sets = (Element)res_sets.item(0);
                //get a nodelist of resourceSet
                NodeList res_set = resource_sets.getElementsByTagName("ResourceSet");
                if(res_set != null && res_set.getLength() > 0) {
                    for(int i = 0 ; i < res_set.getLength();i++) {
                        //get the resourceSet element
                        Element resource_set = (Element)res_set.item(i);
                        //get a nodelist of Resources
                        NodeList resources = resource_set.getElementsByTagName("Resources");
                        if(resources != null && resources.getLength() > 0) {
                            for(int k = 0 ; k < resources.getLength();k++) {
                                //get the Resources element
                                Element resource = (Element)resources.item(k);
                                //get a nodelist of TrafficIncident
                                NodeList incidents = resource.getElementsByTagName("TrafficIncident");
                                if(incidents != null && incidents.getLength() > 0) {
                                    for(int j = 0 ; j < incidents.getLength();j++) {
                                        //get the TrafficIncident element
                                        Element incident = (Element)incidents.item(j);
                                        //get a nodelist of TrafficIncident
                                        NodeList points = incident.getElementsByTagName("Point");
                                        boolean same = false;
                                        if(points != null && points.getLength() > 0) {
                                            //get the resourceSets element
                                            Element point = (Element)points.item(0);
                                            Double latitude = XMLParser.getDoubleValue(point,"Latitude");
                                            Double longitude = XMLParser.getDoubleValue(point,"Longitude");
                                            GPS incident_gps = new GPS(latitude, longitude, 0);
                                            //create a Location Data for the incident found
                                            LocationData location_incident = new LocationData(incident_gps);
                                            location_incident.getLocationData();
                                            System.out.println("LOCATION INCIDENT: " + location_incident.getRoadName());
                                            System.out.println("LOCATION: " + location.getRoadName());
                                            //if equals: the incident found is the same of the input incident
                                            //and i get the information about the road                                            
                                            if(location_incident.getRoadName().equals(location.getRoadName())){
                                                same = true;
                                            }
                                        }
                                        
                                        //if is not the same, I check with ToPoint
                                        if (!same){
                                            points = incident.getElementsByTagName("ToPoint");
                                            if(points != null && points.getLength() > 0) {
                                                //get the resourceSets element
                                                Element point = (Element)points.item(0);
                                                Double latitude = XMLParser.getDoubleValue(point,"Latitude");
                                                Double longitude = XMLParser.getDoubleValue(point,"Longitude");
                                                GPS incident_gps = new GPS(latitude, longitude, 0);
                                                //create a Location Data for the incident found
                                                LocationData location_incident = new LocationData(incident_gps);
                                                location_incident.getLocationData();                                                
                                                //if equals: the incident found is the same of the input incident
                                                //and i get the information about the road                                            
                                                if(location_incident.getRoadName().equals(location.getRoadName())){
                                                    same = true;
                                                }
                                            }
                                        }
                                        
                                        if(same){
                                            String road_closed = XMLParser.getTextValue(incident,"RoadClosed");
                                            String description = XMLParser.getTextValue(incident,"Description");
                                            if(road_closed.equals("true")) {
                                                roadcondition_name = "Closed";
                                            }
                                            else {
                                                roadcondition_name = "Open";
                                            }
                                            String [] d = description.split("-");
                                            roadcondition_description = d[d.length-1];                                            
                                        }                                                                                     
                                    }
                                }                                
                            }
                        }
                    }
                }
            }
            
            f.delete();
            
        }catch(  ParserConfigurationException | SAXException | IOException pce) {
                pce.printStackTrace();
        }
    }

    /**
     * @return the traffic_name
     */
    public String getTraffic_name() {
        return traffic_name;
    }

    /**
     * @return the traffic_description
     */
    public String getTraffic_description() {
        return traffic_description;
    }

    /**
     * @return the roadcondition_name
     */
    public String getRoadcondition_name() {
        return roadcondition_name;
    }

    /**
     * @return the roadcondition_description
     */
    public String getRoadcondition_description() {
        return roadcondition_description;
    }
}
