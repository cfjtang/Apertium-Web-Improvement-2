import net.sf.okapi.common.*;
import net.sf.okapi.lib.translation.*;
import org.apertium.okapi.*;

public class TestCase {
	public static void main(String[] args) {
		IQuery conn = new ApertiumXMLRPCMTConnector();
		
		Parameters p = new Parameters();
		p.setServer("http://localhost:6173/RPC2");
		
		conn.setParameters(p);
		conn.setLanguages(LocaleId.fromString("en"), 
				LocaleId.fromString("es"));
	
		conn.open();
		
		String query = "This is a test for the machine" + 
				"translation service.";
		conn.query(query);
		
		QueryResult res = conn.next();
		System.out.println("source: " + res.source);
		System.out.println("target: " + res.target);
		
		conn.close();
	}
}
