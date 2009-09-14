
import java.io.*;
import java.util.*;

public class CleanTranslate {
	
	 public void doTheTango(String sourceFileName){
		 String strTmp = "", sTmp[], sTmp1[];
		 HashMap wordlist = new HashMap();
		 try {
			 FileReader f1 = new FileReader(sourceFileName);
	         BufferedReader fr1 = new BufferedReader(f1);
	         while ((strTmp = fr1.readLine()) != null) {
	        	 sTmp = strTmp.split("\t");
	        	 if(sTmp.length > 2){
	        		 //System.out.println("EKO: " + sTmp[0] + "KKK"+sTmp[1]);
	        		 wordlist.put(sTmp[1], sTmp[2]);
	        	 }
	        	 else
	        		 System.err.println("JEBA");
	            }
	      }catch (Exception e) {
	    	  System.err.println("Error "+ e);
	      }
	      try {
		         BufferedReader fr1 = new BufferedReader(new InputStreamReader(System.in));
		         while ((strTmp = fr1.readLine()) != null) {
		        	 sTmp = strTmp.split(" ");
		        	 for(int i = 0; i < sTmp.length; i++){
		        		 String result = sTmp[i], tmp;
		        		 if(sTmp[i].indexOf("/") != -1){
		        			 sTmp1 = sTmp[i].split("/");
		        			 if(sTmp1.length > 0){
							 result = sTmp1[0];
		        				 int value = -1;
		        				 for(int j = 0; j < sTmp1.length; j++){
		        					 tmp = (String)wordlist.get(sTmp1[j]);
		        					 if(tmp != null){
		        						 if(Integer.parseInt(tmp) > value){
		        							 result = sTmp1[j];
		        							 value = Integer.parseInt(tmp);
		        						 }
		        					 }
		        				 }
		        			 }
		        			 System.out.print(result + " ");
		        		 }
		        		 else{
		        			 sTmp[i] = sTmp[i].replaceAll("[#]", "");
		        			 sTmp[i] = sTmp[i].replaceAll("[*]", "");
		        			 sTmp[i] = sTmp[i].replaceAll("\\\\", "");
		        			 sTmp[i] = sTmp[i].replaceAll("@.\\[\\]", "");
		        			 sTmp[i] = sTmp[i].replaceAll("\\[", "");
		        			 sTmp[i] = sTmp[i].replaceAll("\\]", "");
		        			 sTmp[i] = sTmp[i].replaceAll("@", "");
		        			 System.out.print(sTmp[i] + " ");
		        		 }
		        	 }
		        	 System.out.println();
		         }
		      }catch (Exception e) {
		    	  e.printStackTrace();
		      }
	 }
	
	public static void main(String a[]){
		CleanTranslate pp = new CleanTranslate();
        if(a.length == 1){
        	pp.doTheTango(a[0]);
        }
        else
            System.err.println("Usage: java CleanTranslate wordlist");
	}
}

