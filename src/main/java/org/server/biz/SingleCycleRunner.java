package org.server.biz;

import org.coffeemips.Assembler.Assembler;
import org.coffeemips.Assembler.Instruction;
import org.coffeemips.GUI.Computer;
import org.coffeemips.GUI.Monitor;
import org.coffeemips.memory.SegmentDefragmenter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mostafa on 6/27/2016.
 */
public class SingleCycleRunner {

    private int      count=0;
    private int      lineOfInstructions;
    private String   filePath = null;
    private Computer computer ;
    private Monitor  monitor;
    private HashMap<Integer, SegmentDefragmenter> programs;
    private SegmentDefragmenter segmentDefragmenter;
    private ArrayList<String> starr;
    //start of address
    private int start_address ;
    /**
     * Constructor of class
     * new Computer
     */
    public SingleCycleRunner(){
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

    private void assembleCode(){
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
                    instruction_object.put("address",assembled.get(code_number).getInstruction());
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
            }
        }
    }

}
