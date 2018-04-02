package Agents;

public class PythonCaller {
	public static void main(String[] args) throws Exception{
		//System.out.println(System.getProperty("user.dir"));
		//System.out.println(System.getProperty("user.dir")+"\\src\\Agents");
		String tagName = "Test.PLC.Message1";
		ClientPython clientClass=new ClientPython();
		String result = clientClass.getValue(tagName);
		for(int i=0; i < 10; i++){
			result = clientClass.getValue(tagName);
			clientClass.doWrite(tagName, Integer.toString(i));
			System.out.println(result);
		}
	}
}
