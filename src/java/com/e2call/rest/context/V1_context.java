package com.e2call.rest.context;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import com.e2call.db.*;
import com.e2call.util.GPS;
import java.util.ArrayList;
/**
 *
 * @author FEDE
 */
@Path("V1/context")
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
        ArrayList<String> log_ids = new ArrayList<String>();
        String returnMessage = null;
        Connection conn = null;
        int count = 0;
        int errors = 0;
        String status = "";
        
        try{
            conn = MysqlManager.GetMysqlConn();
            query = conn.prepareStatement("SELECT vehicleDataID, latGPS, lonGPS, heightGPS "
                                        + "FROM VehicleData "
                                        + "WHERE vehicleSegmentID IS NULL");//select all car log without context
            ResultSet rs = query.executeQuery();
                        
            while(rs.next()){
                log_ids.add(rs.getString("vehicleDataID"));
            }            
            query.close(); //close connection
            rs.close();
            conn.close();
            
            for(int i = 0; i<log_ids.size(); i++){
                status = VehicleData.updateContext(log_ids.get(i));//for everyone call the updateContext method
                if (status.equals("Ok")) count+=1; else errors+=1;
            }
            
            returnMessage = "<p>Updated " + count + " logs, with " + errors+" errors.</p>";
        }
        catch(Exception e){
            e.printStackTrace();
            return "<p>Updated " + count + " logs, but then Error occurred: " + status;
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
        
        String status;
        
        status = VehicleData.updateContext(log_id);//call updateContext method
        if (status.equals("Ok")) return "<p>Updated log with id " + log_id + ".</p>";
        else return status;
        
    }
}
