
package org.coffeemips.HBDMIPS;
import java.util.HashMap;

import org.coffeemips.Assembler.Assembler;
import org.coffeemips.Assembler.Instruction;
import java.util.Map;
/**
 * This class represents <b>Instruction Fetch<b> Stage.
 * @author HDB
 */
public class IF {
        private String filePath;
	private HashMap<Integer, Instruction> ins_mem;
        private HashMap<Integer, Instruction> ins_cache;
	int PC = 0;
        String ins = null;
	IF_ID ifid;        
        
        /**
         * Fetch the number of Instruction currently in IF stage.
         * @return PC - current Program Counter. 
         */
	public int getPC() {
		return PC;
	}

        
        /**
         * Set the number of instruction that goes to the IF Stage.
         * @param pc 
         */
	public void setPC(int pc) {
		this.PC = pc;
	}
        
        
        /**
         * set path of the file which should be assembled in InstructionFetch stage.
         * @param filePath 
         */
        public void setfilePath(String filePath){
            this.filePath = filePath;
        }
        
        /**
         * get path of the file which has been assembled in InstructionFetch stage.
         * @return filePath
         */
        public String getfilePath(){
            return this.filePath;
        }


	public IF(IF_ID ifid,String filePath) {
		this.ifid = ifid;
                this.filePath = filePath;
                Assembler assemble = new Assembler();
                assemble.setModeBit(true);
                ins_mem =new HashMap<Integer, Instruction>(assemble.assembleFile(filePath));
                assemble.setModeBit(false);
            for (Map.Entry<Integer, Instruction> entrySet : ins_mem.entrySet()) {
                Integer key = entrySet.getKey();
                Instruction value = entrySet.getValue();
                System.out.print(value.getAddress()+" : "+ value.getInstruction()+"\n");
                
            }
	}
        
        
        /**
         * Do the job of InstructionFetch stage.
         * This includes:
         *  1- Save <b>Instruction</b> in InstructionFetch/InstructionDecode 
         *     Pipeline Register.
         *  2- Increment of PC by 1.
         *  3- Save the new <b>PC</b> in IF/ID Pipeline Register. 
         */
	public void action(boolean mode) {
                if(mode)
                    ifid.setIns(ins_mem.get(PC).getInstruction());
                else
                    ifid.setIns(ins_cache.get(PC).getInstruction());
                
		PC++;
		ifid.setPC(PC);
	}
        
        
        /**
         * Note that <code>getInstruction()</code> in the scope of the method
         * belongs to Instruction class of the Assembler Package.
         * @return Instruction - 6bit function of the instruction code
         * as string .
         */
        public String getInstruction(){
            return getIns_mem().get(this.PC-1).getInstruction();
        }

    /**
     * @return the ins_mem
     */
    public HashMap<Integer, Instruction> getIns_mem() {
        return ins_mem;
    }

    /**
     * @param ins_mem the ins_mem to set
     */
    public void setIns_mem(HashMap<Integer, Instruction> ins_mem) {
        this.ins_mem = ins_mem;
    }

    /**
     * @return the ins_cache
     */
    public HashMap<Integer, Instruction> getIns_cache() {
        return ins_cache;
    }

    /**
     * @param ins_cache the ins_cache to set
     */
    public void setIns_cache(HashMap<Integer, Instruction> ins_cache) {
        this.ins_cache = ins_cache;
    }
}

