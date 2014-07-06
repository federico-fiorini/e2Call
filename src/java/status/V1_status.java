package status;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/**
 * This is the root path of the REST api service.
 * In the web.xml file we specify that /api/* needs to be in the URL to get to this class.
 * 
 * This is the first version.
 * 
 * Example how to get to the root of this api resource:
 * http://localhost:8080/e2Call/api/v1/status/version
 * @author FEDE
 */
@Path("/v1/status")
public class V1_status {
    
    private static final String api_version = "1";
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String returnTitle(){
        return "<p>Java REST</p>";
    }
    
    @Path("/version")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String returnVersion(){
        return "<p>Version: " + api_version + "</p>";
    }
}
