package org.server.service;


import com.codahale.metrics.annotation.Timed;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.server.biz.Main;

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
    private Main main;
    private boolean debugMode=false;


    @GET
    @Timed
    @Path("/testme")
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
                             @Context HttpServletResponse response) throws IOException {
        main=new Main();
        return main.runAssemble(inputStream,contentDispositionHeader).toString();
    }



    @GET
    @Timed
    @Path("/run")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRegistersOfLastFileUploaded(){
        if(!debugMode){
            if(main==null){
                return new JSONObject().put("error","cannot load your file").toString();
            }
            return main.runRun(main.getFilepath()).toString();
        }
        return new JSONObject().put("debugeMode","debug mode is enable please disable it").toString();
    }



    @GET
    @Timed
    @Path("/getregisterfile")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLastStateOfRegisters(){
        if(this.main.getLastRegistersState()!=null){
            return main.getLastRegistersState().toString();
        }
        return new JSONObject().put("registerLastState","[]").toString();
    }


    @GET
    @Timed
    @Path("/getmemory")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMemoryLastState(){
        if(main!=null){
            return new JSONObject().put("error","nothing executed").toString();
        }
        return main.getLastMemoryState().toString();
    }


    @GET
    @Timed
    @Path("/nextins")
    @Produces(MediaType.APPLICATION_JSON)
    public String runNextInstruction(){
        if(this.debugMode==true && this.main!=null){
            return this.main.runNextInstruction().toString();
        }
        return new JSONObject().put("error","debug mode is disable").toString();
    }



    @GET
    @Timed
    @Path("/debug/{mode}")
    @Produces(MediaType.APPLICATION_JSON)
    public String enableDebug(@PathParam("mode") String mode){
        if(mode.equals("enable")){
            if(this.debugMode==true){
                return new JSONObject().put("error","debug mode was enable").toString();
            }
            this.debugMode=true;
            return new JSONObject().put("success","debug mode is enable now").toString();
        }
        if (mode.equals("disable")){
            if(this.debugMode==false){
                return new JSONObject().put("error","debug mode was disable").toString();
            }
            this.debugMode=false;
            return new JSONObject().put("success","debug mode is disable now").toString();
        }
        return new JSONObject().put("error","wrong path /debug/{enable or disable}").toString();
    }



    @GET
    @Timed
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public String reset(){
        this.main=null;
        return  new JSONObject().put("success","system was reset reset").toString();
    }


}
