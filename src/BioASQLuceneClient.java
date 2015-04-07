import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BioASQLuceneClient {
	public static void main(String[] args) throws IOException, JSONException {
		System.out.println(Math.log(0));
		Map<String, String> id2queryMap = new HashMap<String, String>();
		Scanner qin = new Scanner(new File("gs_1-10.query"));
		while(qin.hasNext()) {
			String line = qin.nextLine();
			id2queryMap.put(line.split(":")[0], line.split(":")[1].trim());
		}
		
		String serverHostname = new String ("128.2.190.25");

		if (args.length > 0)
			serverHostname = args[0];
		System.out.println ("Attemping to connect to host " +
				serverHostname + " on port 10008.");

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			echoSocket = new Socket(serverHostname, 10008);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + serverHostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for "
					+ "the connection to: " + serverHostname);
			System.exit(1);
		}


		System.out.println ("Type Message (\"Bye.\" to quit)");
		
		List<String> resultBuf = new LinkedList<String>();
		
		for(Entry<String, String> e: id2queryMap.entrySet()) {
			String qid = e.getKey();
			
			String q = e.getValue();
			
			out.println(q);
			String lineFromSever = in.readLine();
			JSONObject jsobj = new JSONObject(lineFromSever);
			System.out.println("echo: " + lineFromSever);
			
			JSONArray docnoList = (JSONArray) jsobj.get("docno_list");
			JSONArray scoreList = (JSONArray) jsobj.get("score_list");
			
			
			
			for(int i=0; i<docnoList.length(); i++) {
				System.out.println(qid+" "+(docnoList.get(i)+" "+scoreList.get(i)));
				resultBuf.add(String.format("%s Q0 %s %d %f %s", qid, docnoList.get(i), i+1, (double)scoreList.get(i), "LTR"));
			}
			
		}
		


		out.close();
		in.close();

		echoSocket.close();
		
		
		
		PrintWriter outResult = new PrintWriter(new BufferedWriter(new FileWriter("resultFromServer", false)));
		for(String s: resultBuf) {
			outResult.println(s);
		}

		outResult.close();
	}
}
