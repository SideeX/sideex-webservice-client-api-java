package sideex;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		try {
        	//Connect to a SideeX WebService server
        	SideeXWebServiceClientAPI wsClient  = new SideeXWebServiceClientAPI("http://127.0.0.1:50000", ProtocalType.HTTP);
            File file = new File("testcast.zip");
            Map<String, File> fileParams = new HashMap<String, File>();
            fileParams.put(file.getName(), file);
        	
            String token = new JSONObject(wsClient.runTestSuite(fileParams)).getString("token"); // get the token
            boolean flag = false;
            
            //Check the execution state every 2 seconds
            while(!flag) {
            	//Get the current state
                String state = new JSONObject(wsClient.getState(token)).getJSONObject("webservice").getString("state");
                if(!state.equals("complete") && !state.equals("error")) {
                    System.out.println(state);
                    Thread.sleep(2000);
                }
                //If test is error
                else if(state.equals("error")) {
                	System.out.println(state);
                    flag = true;
                }
                //If test is complete
                else {
                    System.out.println(state);
                    Map<String, String> formData = new HashMap<String, String>();
                    formData.put("token", token);
                    formData.put("file", "reports.zip");
                    //Download the test report
                    wsClient.download(formData, "./reports.zip", 0);
                    
                    formData = new HashMap<String, String>();
                    formData.put("token", token);
                    //Download the logs
                    wsClient.download(formData, "./logs.zip", 1);
                    flag = true;
                }
//                //Delete the test case and report from the server
                System.out.println(wsClient.deleteReport(token));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
