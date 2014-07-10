package com.e2call.rest.context;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import com.e2call.db.*;
import com.e2call.util.GPS;
/**
 *
 * @author FEDE
 */
@Path("/context")
public class V1_context {
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String updateAllContext() throws SQLException{
        
        PreparedStatement query = null;
        String log_id = null;
        Double latitude = null;
        Double longitude = null;
        Double altitude = null;
        GPS gps = null;
        String returnMessage = null;
        Connection conn = null;
        int count = 0;
        int errors = 0;
        boolean no_error = true;
        
        try{
            conn = MysqlManager.GetMysqlConn();
            query = conn.prepareStatement("SELECT vehicleDataID, latGPS, lonGPS, heightGPS "
                                        + "FROM VehicleData "
                                        + "WHERE vehicleSegmentID IS NULL");//select all car log without context
            ResultSet rs = query.executeQuery();
            
            while(rs.next()){
                log_id = rs.getString("vehicleDataID");
                latitude = rs.getDouble("latGPS");
                longitude = rs.getDouble("lonGPS");
                altitude = rs.getDouble("heightGPS");
                gps = new GPS(latitude, longitude, altitude);
                no_error = VechicleData.updateContext(log_id, gps);//for everyone call the updateContext method
                if (no_error) count+=1; else errors+=1;
            }
            
            query.close(); //close connection
            
            returnMessage = "<p>Updated " + count + " logs, with " + errors+" errors.</p>";
        }
        catch(Exception e){            
            return "Error occurred!";
        }
        finally{
            if(conn != null) conn.close();
        }
        return returnMessage;
    }
    
    /**
     * 
     * @param log_id
     * @return
     * @throws SQLException 
     */
    @Path("/{log_id}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String updateContext(@PathParam("log_id") String log_id) throws SQLException{
        
        GPS gps = null;
        boolean no_error = true;
        
        no_error = VechicleData.updateContext(log_id, gps);//call updateContext method
        if (no_error) return "<p>Updated log with id " + log_id + ".</p>";
        else return "<p>Not updated, error occurred! Check if the inserted id is correct.</p>";
        
    }
}
