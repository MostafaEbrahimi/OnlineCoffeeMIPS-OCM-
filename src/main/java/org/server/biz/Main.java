package org.server.biz;

import org.coffeemips.Assembler.Assembler;
import org.coffeemips.Assembler.Instruction;
import org.coffeemips.GUI.Computer;
import org.coffeemips.GUI.Monitor;
import org.coffeemips.HBDMIPS.Register_file;
import org.coffeemips.memory.SegmentDefragmenter;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Mostafa on 6/27/2016.
 */
public class Main {

    private int      count=0;
    private int      lineOfInstructions;
    private Computer computer ;
    private Monitor  monitor;
    private HashMap<Integer, SegmentDefragmenter> programs;
    private SegmentDefragmenter segmentDefragmenter;
    private ArrayList<String> starr;
    private FileServer fileServer=new FileServer();
    //start of address
    private int start_address ;
    private String filepath;
    private JSONArray lastInstructionState=null;
    private JSONArray lastRegistersState=null;
    private JSONArray lastMemoryState=null;
    private int counter=0;

    //TODO add Multiple file to the server
    private ArrayList<String> file_directory;

    /**
     * Constructor of class
     * new Computer
     */
    public Main(){
        this.computer=new Computer();
        this.monitor=new Monitor(computer.getAa().getMemory());
        this.programs=computer.getAa().getPrograms();

    }


    /**
    * this method just for now
    * it must be remove
    */
    public void freeMethod(){
        //computer.fix_memory_table(memoryTable);
        this.computer = new Computer();
        this.monitor  = new Monitor(computer.getAa().getMemory());
        this.programs = computer.getAa().getPrograms();
        this.segmentDefragmenter=programs.get(0);

        this.starr = segmentDefragmenter.getCode_seg();
        this.start_address=Integer.parseInt(segmentDefragmenter.getCode_seg_start_address(), 16);
        /*
        for (int i = 0; i < starr.size(); i++) {
            DefaultTableModel model = (DefaultTableModel) program1.getModel();
            model.addRow(new Object[]{Integer.toHexString(start_address+i*4),"",starr.get(i),""});
        }
        */

    }


    /**
     * assemble instruction file
     * must get from server and save
     * this file using file server
     * then assemble this codes and
     * return json array of these
     * instructions
     */
    private JSONArray assembleCode(String filePath){
        JSONArray instructions = new JSONArray();
        if(!filePath.isEmpty() && filePath!=null){
            Assembler assemble = new Assembler();
            assemble.setModeBit(true);
            HashMap<Integer,Instruction> assembled = new HashMap<>(assemble.assembleFile(filePath));
            assemble.setModeBit(false);
            this.lineOfInstructions = assembled.size();
            int code_number=0;
            for (int i = 0; i < lineOfInstructions; i++) {
                JSONObject instruction_object=new JSONObject();
                try{
                    instruction_object.put("instruction",assembled.get(code_number).getInstruction());
                    instruction_object.put("address",assembled.get(code_number).getAddress());
                    instruction_object.put("exception","");
                    code_number++;
                }
                catch(Exception e)
                {
                    instruction_object.put("instruction",assembled.get(code_number).getInstruction());
                    instruction_object.put("address","");
                    instruction_object.put("exception",e.getMessage());
                    throw e;
                }
                instructions.put(instruction_object);
            }
        }
        this.lastInstructionState=instructions;
        return instructions;
    }

    /**
     * this method called by service
     * execute and is business layer
     * of service Execute get input stream
     * and save it as file by it's name
     * then get it's file name and save it
     * in the root directory of the file server
     */
    public JSONArray runAssemble(InputStream is, FormDataContentDisposition fdcd){
        try {
            if (fdcd==null){
                filepath=fileServer.getRootPath().concat("\\").concat("test"+String.valueOf(counter)).concat(".txt");
                counter++;
            }
            else{
                filepath=fileServer.getRootPath().concat("\\").concat(fdcd.getFileName());
            }
            System.out.println("Save File: "+filepath);
            this.fileServer.saveFile(is,filepath);
            return assembleCode(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }



    //TODO add instruction
    public void runAllOfAction(){
        while (computer.getCurrentLineOfInstructions() < computer.getLineOfInstructions()) {
            computer.runSingleSycle();
        }
    }


    public JSONArray runRun(String filePath){
        if (computer.isRunable()) {
            computer.run_init(filePath,lineOfInstructions);
            return getRegisterFile();
        }
        return new JSONArray();
    }

    /**
     * Return all memory values as
     * an JsonArray Object
     * @return JSONArray
     */
    private JSONArray getMemoryValues(){
        JSONArray memory=new JSONArray();
        for (int i = 0; i < computer.getAa().getMemory().size(); i++) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("address",computer.getAa().parse8DigitHex(i));
            jsonObject.put("content",computer.getAa().getMemory().get(computer.getAa().parse8DigitHex(i)));
            memory.put(jsonObject);
        }
        this.lastMemoryState=memory;
        return memory;
    }

    /**
     * Return all data of registers
     * as an array
     * @return JSONArray
     */
    private JSONArray getRegisterFile(){
        JSONArray jsonArray=new JSONArray();
        Register_file regfile = computer.getStage_id().getRegfile();
        for (int i = 0; i < 32; i++) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("register_number",i);
            jsonObject.put("register_value",regfile.getRegfile(i));
            //TODO put register name here
            jsonObject.put("register_name","");
            jsonArray.put(jsonObject);
        }
        this.lastRegistersState=jsonArray;
        return jsonArray;
    }

    public JSONObject runNextInstruction(){
        if (computer.runSingleSycle()) {
            this.lastRegistersState=this.getRegisterFile();
            this.lastMemoryState=this.getMemoryValues();
            JSONObject register_memory=new JSONObject();
            register_memory.put("registers",this.getRegisterFile());
            register_memory.put("memory",this.getMemoryValues());
            return register_memory;
        }
        return new JSONObject().put("runSingleCycle",false);
    }

    public String getFilepath() {
        return filepath;
    }

    public JSONArray getLastInstructionState() {
        return lastInstructionState;
    }

    public JSONArray getLastRegistersState() {
        return lastRegistersState;
    }

    public JSONArray getLastMemoryState() {
        return lastMemoryState;
    }
}
