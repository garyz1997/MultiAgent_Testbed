package Agents;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class testClientPython {
	public static void main(String[] args) throws FileNotFoundException{
		ClientPython opc=new ClientPython();
		opc.SimulatingTags();
		System.out.println(opc.getValue("Conv_N053:I.Data[3].1"));
	}
}