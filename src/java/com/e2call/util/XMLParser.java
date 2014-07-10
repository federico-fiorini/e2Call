package com.e2call.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Contains methods for parse XML files
 * @author FEDE
 */
public class XMLParser {
    /**
    * Takes a xml element and the tag name, look for the tag and get
    * the text content
    * @param ele
    * @param tagName
    * @return 
    */
    public static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
                Element el = (Element)nl.item(0);
                textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;   
    }

    /**
     * Calls getTextValue and returns a int value
     * @param ele
     * @param tagName
     * @return 
     */
    public static int getIntValue(Element ele, String tagName) {
        return Integer.parseInt(getTextValue(ele,tagName));
    }
    
    /**
     * Calls getTextValue and returns a Float value
     * @param ele
     * @param tagName
     * @return 
     */
    public static Float getFloatValue(Element ele, String tagName) {
        return Float.parseFloat(getTextValue(ele,tagName));
    }
    
    /**
     * Calls getTextValue and returns a Double value
     * @param ele
     * @param tagName
     * @return 
     */
    public static Double getDoubleValue(Element ele, String tagName) {
        return Double.parseDouble(getTextValue(ele,tagName));
    }
}
