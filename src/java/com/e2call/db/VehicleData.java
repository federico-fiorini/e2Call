package com.e2call.db;

import com.e2call.util.*;
import java.sql.*;

/**
 *
 * @author FEDE
 */
public class VehicleData {
    
    /**
     * This methods returns the GPS values of the car log.
     * @param id
     * @return GPS values of the car log
     */
    private static GPS getGPS(String id) throws SQLException{
        
        PreparedStatement query = null;
        Double latitude = null;
        Double longitude = null;
        Double altitude = null;
        GPS gps = null;
        Connection conn = null;
        
        try{
            conn = MysqlManager.GetMysqlConn();
            query = conn.prepareStatement("SELECT latGPS, lonGPS, heightGPS "
                                        + "FROM VehicleData "
                                        + "WHERE vehicleDataID = ?");
            query.setString(1, id);
            ResultSet rs = query.executeQuery();
            
            while(rs.next()){
                latitude = rs.getDouble("latGPS");
                longitude = rs.getDouble("lonGPS");
                altitude = rs.getDouble("heightGPS");
            }                                                
            
            query.close(); //close connection
            conn.close();
            
            if((latitude == 0.0) || (longitude == 0.0)) return null;
            gps = new GPS(latitude, longitude, altitude);
                        
        }
        catch(Exception e){
            return null;
        }
        finally{
            if(conn != null) conn.close();
        }
        return gps;
    }
    
    /**
     * 
     * @param log_id
     * @return
     * @throws SQLException 
     */
    public static String updateContext(String log_id) throws SQLException{
        
        String genericError = "<p>Not updated, error occurred! Check if input data are correct.</p>";
        String gpsError = "<p>Not updated, Error in GPS. Check if GPS values are correct.</p>";
        String geocodeLimitError = "<p>Not updated, error occurred! You have exceeded your daily request quota for geocode API.</p>";
        String noError = "Ok";
        
        GPS gps = getGPS(log_id);
        if(gps == null) return gpsError;
        
        
        //get location data
        LocationData location = new LocationData(gps);
        try {
            location.getLocationData();
        } catch (Exception ex) {
            System.out.println("Error in getLocationData");
            return genericError;
        }
        
        if (!location.isValid()) {
            System.out.println();
            return geocodeLimitError;
        }
        
        //get weather data
        WeatherData weather = new WeatherData(gps);        
        try {
            weather.getWeatherData();
        } catch (Exception ex) {
            System.out.println("Error in getWeatherData");
            return genericError;
        }
        
        //get traffic data
        TrafficData traffic = new TrafficData(location);
        try {
            traffic.getTrafficData();
        } catch (Exception ex) {
            System.out.println("Error in getTrafficData");
            return genericError;
        }
        
        
        try{  
            String id_location = insertLocation(location);
            String id_road_segment = insertRoadSegment(id_location, location.getRoadName(), gps);
            String id_road_condition = insertRoadCondition(traffic);
            String id_traffic = insertTraffic(traffic);
            String id_weather = insertWeather(weather);
            String id_vehicle_segment = insertVehicleSegment(id_traffic,id_road_segment,id_weather,id_road_condition,weather);
            updateVehicleData(log_id, id_vehicle_segment);   
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error in queries");
            return genericError;
        }

        return noError;
    }
    
    /**
     * Insert the location in the Location table, if it doesn't exist yet
     * 
     * @param conn
     * @param location
     * @return id of the location
     * @throws SQLException 
     */
    private static String insertLocation(LocationData location) throws Exception{
        
        String id_location = "";
        PreparedStatement query;
        Connection conn = MysqlManager.GetMysqlConn();
        try {
            query = conn.prepareStatement("SELECT locationID "
            + "FROM Location "
            + "WHERE country = ? AND region = ? AND province = ? AND municipality = ? AND city = ?");               
            query.setString(1, location.getCountry());
            query.setString(2, location.getRegion());
            query.setString(3, location.getProvince());
            query.setString(4, location.getMunicipality());
            query.setString(5, location.getCity());
            ResultSet rs = query.executeQuery();     
            
            while(rs.next()){                
                id_location = rs.getString("locationID");
            }       
                
            if (id_location.equals("")){
                query = conn.prepareStatement("INSERT INTO "
                + "Location(country, region, province, municipality, city) "
                + "VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);     

                query.setString(1, location.getCountry());
                query.setString(2, location.getRegion());
                query.setString(3, location.getProvince());
                query.setString(4, location.getMunicipality());
                query.setString(5, location.getCity());
                query.executeUpdate();     

                rs = query.getGeneratedKeys();
                if (rs.next()) {
                    id_location = Integer.toString(rs.getInt(1));                    
                }                
            }
            
            query.close();
            rs.close();
            conn.close();
        } finally {
            if(conn != null) conn.close();
        }  
        return id_location;
    }
    
    
    /**
     * Insert the road segment in the RoadSegment table, if it doesn't exist yet
     * 
     * @param conn
     * @param id_location
     * @param road_name
     * @param gps
     * @return id road segment
     * @throws SQLException 
     */
    private static String insertRoadSegment(String id_location, String road_name, GPS gps) throws Exception{
                
        String id_road_segment = "";
        PreparedStatement query;
        Connection conn = MysqlManager.GetMysqlConn();
        try {
            query = conn.prepareStatement("SELECT roadSegmentID "
            + "FROM RoadSegment "
            + "WHERE locationID = ? AND roadName = ?");  
            
            query.setString(1, id_location);
            query.setString(2, road_name);
            ResultSet rs = query.executeQuery();     
            
            while(rs.next()){                
                id_road_segment = rs.getString("roadSegmentID");
            }            
        
                
            if (id_road_segment.equals("")){
                query = conn.prepareStatement("INSERT INTO "
                + "RoadSegment(locationID, roadName, startLat, startLong) "
                + "VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);    

                query.setString(1, id_location);
                query.setString(2, road_name);
                query.setString(3, Double.toString(gps.getLatitude()));
                query.setString(4, Double.toString(gps.getLongitude()));
                query.executeUpdate();     

                rs = query.getGeneratedKeys();
                if (rs.next()) {
                    id_road_segment = Integer.toString(rs.getInt(1));                    
                }                
            }
            
            query.close();
            rs.close();
            conn.close();
        } finally {
            if(conn != null) conn.close();
        }  
        return id_road_segment;
    }
    
    /**
     * Insert the road condition data in the RoadCondition table, if they don't exist yet
     * @param conn
     * @param traffic
     * @return road condition id
     * @throws SQLException 
     */
    private static String insertRoadCondition(TrafficData traffic) throws Exception{
                
        String id_road_condition = "";
        PreparedStatement query;
        Connection conn = MysqlManager.GetMysqlConn();
        try{
            query = conn.prepareStatement("SELECT roadConditionID "
            + "FROM RoadCondition "
            + "WHERE name = ? AND description = ?");    
            
            query.setString(1, traffic.getRoadcondition_name());
            query.setString(2, traffic.getRoadcondition_description());
            ResultSet rs = query.executeQuery();     
            
            while(rs.next()){                
                id_road_condition = rs.getString("roadConditionID");
            }                    
                
            if (id_road_condition.equals("")){
                query = conn.prepareStatement("INSERT INTO "
                + "RoadCondition(name, description) "
                + "VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);   
                query.setString(1, traffic.getRoadcondition_name());
                query.setString(2, traffic.getRoadcondition_description());
                query.executeUpdate();     

                rs = query.getGeneratedKeys();
                if (rs.next()) {
                    id_road_condition = Integer.toString(rs.getInt(1));                    
                }
            }
            query.close();
            rs.close();
            conn.close();
        } finally {
            if(conn != null) conn.close();
        }
        return id_road_condition;
    }
    
    /**
     * Insert the traffic data in the Traffic table, if they don't exist yet
     * @param conn
     * @param traffic
     * @return traffic id
     * @throws SQLException 
     */
    private static String insertTraffic(TrafficData traffic) throws Exception{
                
        String id_traffic = "";
        PreparedStatement query;
        Connection conn = MysqlManager.GetMysqlConn();        
        try{
            query = conn.prepareStatement("SELECT trafficID "
            + "FROM Traffic "
            + "WHERE name = ? AND description = ?");    
            query.setString(1, traffic.getTraffic_name());
            query.setString(2, traffic.getTraffic_description());
            ResultSet rs = query.executeQuery();     
            
            while(rs.next()){                
                id_traffic = rs.getString("trafficID");
            }                    
                
            if (id_traffic.equals("")){
                query = conn.prepareStatement("INSERT INTO "
                + "Traffic(name, description) "
                + "VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);   
                query.setString(1, traffic.getTraffic_name());
                query.setString(2, traffic.getTraffic_description());
                query.executeUpdate();     

                rs = query.getGeneratedKeys();
                if (rs.next()) {
                    id_traffic = Integer.toString(rs.getInt(1));                    
                }                
            }
            query.close();
            rs.close();
            conn.close();
        } finally {
            if(conn != null) conn.close();
        }
        return id_traffic;
    }
    
    /**
     * Insert the weather data in the Weather table, if they don't exist yet
     * @param conn
     * @param weather
     * @return weather id
     * @throws SQLException 
     */
    private static String insertWeather(WeatherData weather) throws Exception{
                
        String id_weather = "";
        PreparedStatement query;
        Connection conn = MysqlManager.GetMysqlConn();
        try{ 
            query = conn.prepareStatement("SELECT weatherID "
            + "FROM Weather "
            + "WHERE name = ? AND description = ?");
            query.setString(1, weather.getName());
            query.setString(2, weather.getDescription());
            ResultSet rs = query.executeQuery();     
            
            while(rs.next()){                
                id_weather = rs.getString("weatherID");
            }                    
                
            if (id_weather.equals("")){
                query = conn.prepareStatement("INSERT INTO "
                + "Weather(name, description) "
                + "VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);    
                query.setString(1, weather.getName());
                query.setString(2, weather.getDescription());
                query.executeUpdate();     

                rs = query.getGeneratedKeys();
                if (rs.next()) {
                    id_weather = Integer.toString(rs.getInt(1));                    
                }
            }
            query.close();
            rs.close();
            conn.close();
        } finally {
            if(conn != null) conn.close();
        }
        return id_weather;
    }
    
    /**
     * Insert the vehicle segment data in the Vehicle_Segment table
     * @param conn
     * @param id_traffic
     * @param id_road_segment
     * @param id_weather
     * @param id_road_condition
     * @param weather
     * @return
     * @throws SQLException 
     */
    private static String insertVehicleSegment(String id_traffic, String id_road_segment, String id_weather, String id_road_condition, WeatherData weather) throws Exception{
                
        String id_vehicle_segment = "";
        PreparedStatement query;
        Connection conn = MysqlManager.GetMysqlConn();

        try{
            query = conn.prepareStatement("INSERT INTO "
            + "Vehicle_Segment(trafficID, roadSegmentID, weatherID, roadConditionID, weatherTemperature, weatherHumidity) "
            + "VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);   

            query.setString(1, id_traffic);
            query.setString(2, id_road_segment);
            query.setString(3, id_weather);
            query.setString(4, id_road_condition);
            query.setString(5, Float.toString(weather.getTemperature()));
            query.setString(6, Float.toString(weather.getHumidity()));
            query.executeUpdate();     

            ResultSet rs = query.getGeneratedKeys();
            if (rs.next()) {
                id_vehicle_segment = Integer.toString(rs.getInt(1));                    
            }
            query.close();
            rs.close();
            conn.close();
        } finally {
            if(conn != null) conn.close();
        }
                
        return id_vehicle_segment;
    }
    
    /**
     * Update the log record
     * @param conn
     * @param id_vehicle_data
     * @param id_vehicle_segment
     * @throws SQLException 
     */
    private static void updateVehicleData(String id_vehicle_data, String id_vehicle_segment) throws Exception{
        
        PreparedStatement query;
        Connection conn = MysqlManager.GetMysqlConn();
        
        try{
            query = conn.prepareStatement("UPDATE VehicleData "
                + "SET vehicleSegmentID = ? WHERE vehicleDataID = ?  ");  
            query.setString(1, id_vehicle_segment);
            query.setString(2, id_vehicle_data);
            query.executeUpdate();     
            
            query.close();
            conn.close();
        } finally {
            if(conn != null) conn.close();
        }
    }
}
