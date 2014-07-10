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
 *This class manage the data of a location: country, region, province, municipality, city, road name, gps coordinates
 * @author FEDE
 */
public class LocationData {
    
    private GPS gps;
    private String country;
    private String region;
    private String province;
    private String municipality;
    private String city; 
    private String roadName;
    
    public LocationData(GPS gps){
        this.gps = gps;
    }
    
    /**
     * Updates all the location attributes, using google geocode api
     * @return 
     */
    public void getLocationData() throws Exception{
        String google_geocode = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=";
        google_geocode += getGps().getLatitude() + "," + getGps().getLongitude();
        URL xml_geo = new URL(google_geocode);
        File f = new File("geocode_"+getGps().getLatitude()+"_"+getGps().getLongitude()+".xml");
        FileUtils.copyURLToFile(xml_geo, f);
        
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
            NodeList nl_res = docEle.getElementsByTagName("result");
            if(nl_res != null && nl_res.getLength() > 0) {
                for(int i = 0 ; i < nl_res.getLength();i++) {
                    //get the result element
                    Element el = (Element)nl_res.item(i);
                    String res_type = XMLParser.getTextValue(el,"type");

                    if(res_type.equals("street_address") || res_type.equals("route")){
                        NodeList nl_addr = docEle.getElementsByTagName("address_component");
                        if(nl_addr != null && nl_addr.getLength() > 0) {
                            for(int k = 0 ; k < nl_addr.getLength();k++) {
                                //get the result element
                                Element addr = (Element)nl_addr.item(k);
                                String addr_type = XMLParser.getTextValue(addr,"type");
                                if(addr_type.equals("route")) roadName = XMLParser.getTextValue(addr,"long_name");
                                if(addr_type.equals("locality")) city = XMLParser.getTextValue(addr,"long_name");
                                if(addr_type.equals("administrative_area_level_3")) municipality = XMLParser.getTextValue(addr,"long_name");
                                if(addr_type.equals("administrative_area_level_2")) province = XMLParser.getTextValue(addr,"long_name");
                                if(addr_type.equals("administrative_area_level_1")) region = XMLParser.getTextValue(addr,"long_name");
                                if(addr_type.equals("country")) country = XMLParser.getTextValue(addr,"long_name");
                            }
                            if (city == null) city = municipality;
                        }                        
                        break;
                    }
                }
            }
	
            f.delete();

        }catch(  ParserConfigurationException | SAXException | IOException pce) {
                pce.printStackTrace();
        }
        
    }

    /**
     * @return the gps
     */
    public GPS getGps() {
        return gps;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @return the province
     */
    public String getProvince() {
        return province;
    }

    /**
     * @return the municipality
     */
    public String getMunicipality() {
        return municipality;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the roadName
     */
    public String getRoadName() {
        return roadName;
    }
}
