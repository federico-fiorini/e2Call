/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e2call.rest.update;

import java.sql.SQLException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author FEDE
 */
@Path("update")
public class UpdateForm {
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String showForm() throws SQLException{
        String page_content = 
        "<div>\n" +
"		<input id='log' type='text' />\n" +
"		<button id='update_log' onclick='updateLog()' type='button'>UPDATE LOG</button><br/><br/>\n" +
"		<button id='update_all' onclick='updateAllLog()' type='button'>UPDATE ALL</button>\n" +
"	</div>\n" +
"	<script type='text/javascript'>\n" +
"		function updateLog(){\n" +
"			var log = document.getElementById('log');\n" +
"			if(log.value != \"\")\n" +
"				execute('PUT','V1/context/'+log.value);			\n" +
"			else				\n" +
"			alert(\"Would you please enter some id?\")		\n" +
"		}\n" +
"\n" +
"		function updateAllLog(){\n" +
"			execute('PUT','V1/context');			\n" +
"		}\n" +
"\n" +
"		function execute(method, url){ \n" +
"			var xmlhttp = new XMLHttpRequest(); \n" +
"			xmlhttp.open(method,url,false) \n" +
"			xmlhttp.send(null); \n" +
"		}\n" +
"	</script>";
        return page_content;
    }
}
