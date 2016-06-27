package org.server.service;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Mostafa on 6/27/2016.
 */
@Path("/execute")
public class Execute {

    @GET
    @Timed
    @Path("/compile")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCode(){
        System.out.println("request from a client");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("coffee","Have a good coffee with coffee mips");
        return jsonObject.toString();
    }
    @POST
    @Timed
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String fileUpload(@FormDataParam("file") InputStream inputStream,
                             @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
                             @Context HttpServletResponse response){
        String me=inputStream.toString();
        System.out.print(inputStream.toString());
        return new JSONObject().put("success","File successfully uploaded").toString();

    }

}
