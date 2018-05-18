package BinaryMipsConverter;

import java.util.*;

public class binaryInstruction {

    private static final Map<Integer,String> registers = new HashMap<>();
    private static final Map<Integer,String> opCodes = new HashMap<>();
    private static final Map<Integer,String> R_Types = new HashMap<>();

    private Map<String, Integer> controlSignals = new HashMap<>();
    private List<String> processorStages = new ArrayList<>();
    private String instruction;
    private String mipsCode;

    public binaryInstruction() throws Exception{
        Scanner input = new Scanner(System.in);
        System.out.println("Enter in a binary instruction (enter -1 to exit): ");
        String command = input.nextLine();

        if(command.length() == 32 && command.matches("[01]+")){
            this.instruction = command;
            initR_Types();
            initRegisters();
            initOpCodes();
            initControlSignals();
            initProcessorStages();

        }else if (command.equals("-1")){
            System.exit(0);
        }else{
            throw new Exception("Instruction must be 32 bits long and contain only 0s and 1s");
        }
    }

    private void initRegisters(){
        registers.put(0, "$zero");
        registers.put(1, "$at");
        registers.put(2, "$v0");
        registers.put(3, "$v1");
        registers.put(4, "$a0");
        registers.put(5, "$a1");
        registers.put(6, "$a2");
        registers.put(7, "$a3");
        registers.put(8, "$t0");
        registers.put(9, "$t1");
        registers.put(10, "$t2");
        registers.put(11, "$t3");
        registers.put(12, "$t4");
        registers.put(13, "$t5");
        registers.put(14, "$t6");
        registers.put(15, "$t7");
        registers.put(16, "$s0");
        registers.put(17, "$s1");
        registers.put(18, "$s2");
        registers.put(19, "$s3");
        registers.put(20, "$s4");
        registers.put(21, "$s5");
        registers.put(22, "$s6");
        registers.put(23, "$s7");
        registers.put(24, "$t8");
        registers.put(25, "$t9");
        registers.put(26, "$k0");
        registers.put(27, "$k1");
        registers.put(28, "$gp");
        registers.put(29, "$sp");
        registers.put(30, "$fp");
        registers.put(31, "$ra");
    }

    private void initOpCodes(){
        opCodes.put(2, "Jump");
        opCodes.put(4, "beq");
        opCodes.put(8, "addi");
        opCodes.put(35, "lw");
        opCodes.put(43, "sw");
    }

    private void initR_Types(){
        R_Types.put(32, "add");
        R_Types.put(34, "sub");
        R_Types.put(42, "slt");
    }

    private void initControlSignals(){
        this.controlSignals.put("RegDst", 0);
        this.controlSignals.put("ALUSrc", 0);
        this.controlSignals.put("Memto-Reg", 0);
        this.controlSignals.put("Reg-Write", 0);
        this.controlSignals.put("Mem-Read", 0);
        this.controlSignals.put("Mem-Write", 0);
        this.controlSignals.put("Branch", 0);
        this.controlSignals.put("ALUOp1", 0);
        this.controlSignals.put("ALUOp0", 0);
    }

    private void initProcessorStages(){
        this.processorStages.add("IF");
        this.processorStages.add("ID");
        this.processorStages.add("EX");
        this.processorStages.add("MEM");
        this.processorStages.add("WB");
    }

    private void setR_Type(){
        int RegRS = Integer.parseInt(this.instruction.substring(6, 11),2);
        int RegRT = Integer.parseInt(this.instruction.substring(11, 16),2);
        int RegRD = Integer.parseInt(this.instruction.substring(16, 21),2);
        int funct = Integer.parseInt(this.instruction.substring(26, 32),2);
        this.mipsCode = R_Types.get(funct) + " " + registers.get(RegRD) + ", " +
                registers.get(RegRS) + ", " + registers.get(RegRT);

        // update the control signals used
        this.controlSignals.replace("RegDst", this.controlSignals.get("RegDst") + 1);
        this.controlSignals.replace("Reg-Write", this.controlSignals.get("Reg-Write") + 1);
        this.controlSignals.replace("ALUOp1", this.controlSignals.get("ALUOp1") + 1);

        // update processor stages used
        this.processorStages.remove("MEM");
    }

    private void setLoad(){
        int RegRS = Integer.parseInt(this.instruction.substring(6, 11),2);
        int RegRT = Integer.parseInt(this.instruction.substring(11, 16),2);
        int address = Integer.parseInt(this.instruction.substring(17, 32),2);
        this.mipsCode = opCodes.get(35) + " " + registers.get(RegRT) + ", " + address + "(" + registers.get(RegRS) + ")";

        // update the control signals used
        this.controlSignals.replace("ALUSrc", this.controlSignals.get("ALUSrc") + 1);
        this.controlSignals.replace("Memto-Reg", this.controlSignals.get("Memto-Reg") + 1);
        this.controlSignals.replace("Reg-Write", this.controlSignals.get("Reg-Write") + 1);
        this.controlSignals.replace("Mem-Read", this.controlSignals.get("Mem-Read") + 1);

        // update processor stages used
        // all stages used
    }

    private void setStore(){
        int RegRS = Integer.parseInt(this.instruction.substring(6, 11),2);
        int RegRT = Integer.parseInt(this.instruction.substring(11, 16),2);
        int address = Integer.parseInt(this.instruction.substring(17, 32),2);
        this.mipsCode = opCodes.get(43) + " " + registers.get(RegRT) + ", " + address + "(" + registers.get(RegRS) + ")";

        // update the control signals used
        this.controlSignals.replace("ALUSrc", this.controlSignals.get("ALUSrc") + 1);
        this.controlSignals.replace("Mem-Write", this.controlSignals.get("Mem-Write") + 1);

        // update processor stages used
        this.processorStages.remove("WB");
    }

    private void setAddi(){
        int RegRS = Integer.parseInt(this.instruction.substring(6, 11),2);
        int RegRT = Integer.parseInt(this.instruction.substring(11, 16),2);
        int offset = Integer.parseInt(this.instruction.substring(17, 32),2);
        this.mipsCode = opCodes.get(8) + " " + registers.get(RegRT) + ", " + registers.get(RegRS) + ", " + offset;

        // update the control signals used
        this.controlSignals.replace("ALUSrc", this.controlSignals.get("ALUSrc") + 1);
        this.controlSignals.replace("Reg-Write", this.controlSignals.get("Reg-Write") + 1);

        // update processor stages used
        this.processorStages.remove("MEM");
    }

    private void setJump(){
        int address = Integer.parseInt(this.instruction.substring(25,32),2);
        address *= 4;
        this.mipsCode = opCodes.get(2) + " " + "address(" + address + ")";

        // add a control signal jump with 1
        this.controlSignals.put("Jump", 1);

        // update processor stages used
        this.processorStages.remove("EX");
        this.processorStages.remove("WB");
    }

    private void setBranch(){
        int RegRS = Integer.parseInt(this.instruction.substring(6, 11),2);
        int RegRT = Integer.parseInt(this.instruction.substring(11, 16),2);
        int offset = Integer.parseInt(this.instruction.substring(17, 32),2);
        this.mipsCode = opCodes.get(4) + " " + registers.get(RegRS) + ", " + registers.get(RegRT) + ", " + "branch address(" + offset + ")";

        // update the control signals used
        this.controlSignals.replace("Branch", this.controlSignals.get("Branch") + 1);
        this.controlSignals.replace("ALUOp0", this.controlSignals.get("ALUOp0") + 1);

        // update processor stages used
        this.processorStages.remove("MEM");
    }

    public void parseInstruction(){
        int opCode = Integer.parseInt(this.instruction.substring(0,6), 2);
        switch (opCode) {
            case 0: {
                setR_Type();
                System.out.println(Output());
                break;
            }
            case 2: {
                setJump();
                System.out.println(Output());
                break;
            }
            case 4: {
                setBranch();
                System.out.println(Output());
                break;
            }
            case 8: {
                setAddi();
                System.out.println(Output());
                break;
            }
            case 35: {
                setLoad();
                System.out.println(Output());
                break;
            }
            case 43: {
                setStore();
                System.out.println(Output());
                break;
            }
            default: {
                this.mipsCode = "Not valid instruction or instruction not yet supported";
            }
        }
    }

    public String getInstruction(){
        return this.instruction;
    }
    public String getMipsCode(){
        return this.mipsCode;
    }

    public String getControlSignals(){
        return this.controlSignals.toString();
    }

    public String getProcessorSteps(){
        return this.processorStages.toString();
    }

    public String Output(){
        return("BINARY INSTRUCTION: " + getInstruction() + "\n" +
                "MIPS CODE: " + getMipsCode() + "\n" +
                "CONTROL SIGNALS: " + getControlSignals() + "\n" +
                "PROCESSOR STAGES USED: " + getProcessorSteps() + "\n");

    }

}
