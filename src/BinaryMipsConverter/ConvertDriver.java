package BinaryMipsConverter;


public class ConvertDriver {

    public static void main(String[] args) {
        System.out.println("_____Converting Binary to MIPS code_____");

        while(true){
            try{
                binaryInstruction test = new binaryInstruction();
                test.parseInstruction();
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
