package com.e2call.rest.status;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import com.e2call.db.*;
import java.util.logging.*;


/**
 * This is the root path of the REST api service.
 * In the web.xml file we specify that /api/* needs to be in the URL to get to this class.
 * 
 * This is the first version.
 * 
 * Example how to get to the root of this api resource:
 * http://localhost:8080/com.e2call/api/v1/status
 * @author FEDE
 */
@Path("V1/status")
public class V1_status {
    
    private static final String api_version = "1";

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String returnTitle(){
        return "<p>e2Call Java REST api</p>";
    }
    
    @Path("/version")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String returnVersion(){
        return "<p>Version: " + api_version + "</p>";
    }
    
    @Path("/database")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String returnDatabaseStatus() throws SQLException{
        
        PreparedStatement query = null;
        String myString = null;
        String returnString = null;
        Connection conn = null;
        
        try{
            conn = MysqlManager.GetMysqlConn();
            query = conn.prepareStatement("SELECT NOW() AS datetime");
            ResultSet rs = query.executeQuery();
            
            while(rs.next()){
                myString = rs.getString(1);
            }
            
            query.close(); //close connection
            
            returnString = "<p>Database Status </p>"+
                    "<p>Database Date/Time return: "+myString+"</p>";
        }
        catch(Exception e){
            return "Error occurred!";
        }
        finally{
            if(conn != null) conn.close();
        }
        return returnString;
    }
}
