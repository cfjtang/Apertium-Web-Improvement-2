import java.io.*;
import java.net.*;
import java.util.*;

import f00f.net.irc.martyr.*;
import f00f.net.irc.martyr.clientstate.*;
import f00f.net.irc.martyr.commands.*;
import f00f.net.irc.martyr.services.*;

import net.sf.okapi.common.*;
import net.sf.okapi.connectors.google.*;
import net.sf.okapi.lib.translation.*;

import org.apache.log4j.Logger;
import org.apertium.api.ApertiumXMLRPCClient;
import org.apertium.api.exceptions.ApertiumXMLRPCClientException;

public class Main {

	private IRCConnection connection;
	private AutoReconnect autoReconnect;

	private ClientState clientState;
	
	private String server;
	private int port;
	
	private boolean quit = false;

	private static Logger log = Logger.getLogger(Main.class);
	
	//private IQuery gconn = null;
	//private ApertiumXMLRPCClient aconn = null;
	
	public Main(String server, int port) throws MalformedURLException {
		this.server = server;
		this.port = port;
		
		clientState = new MainClientState(this);
		connection = new IRCConnection(clientState);
		
		new AutoResponder(connection );
		new AutoRegister(connection, "translator", "transl_tor", "tra_slator" );

		autoReconnect = new AutoReconnect(connection);

		new AutoJoin(connection, "#nectarine");
		new AutoJoin(connection, "#apertium");
		new AutoJoin(connection, "#gsoc-it");
		new AutoJoin(connection, "#linguistics");
		
		//gonn = new GoogleMTConnector();
		//aconn = new ApertiumXMLRPCClient(new URL("http://localhost:6173/RPC2"));
		
		new MessageMonitor(this);
	}

	public void connect() throws IOException, UnknownHostException {
		autoReconnect.go(server, port);
	}

	public boolean shouldQuit() {
		return quit;
	}
	
	public void incomingMessage(MessageCommand msg)	throws MalformedURLException, ApertiumXMLRPCClientException {
		String gcom = ".google";
		String acom = ".apertium";
		String scom = ".synthesise";
		String hcom = ".help";

		String message = msg.getMessage();

		boolean answer = false;
		String reply = "";
		
		StringTokenizer tokenizer = new StringTokenizer(message, " \t");

		if (tokenizer.hasMoreTokens()) {

			String cstr = tokenizer.nextToken().toLowerCase();

			if (tokenizer.countTokens() > 2) {

				String src = tokenizer.nextToken();
				String dest = tokenizer.nextToken();

				String query = "";
				while (tokenizer.hasMoreTokens()) {
					query += tokenizer.nextToken() + " ";
				}
				
				//IQuery gconn = new GoogleMTConnector();

				if (cstr.equals(gcom)) {
					answer = true;

					IQuery gconn = new GoogleMTConnector();

					gconn.setLanguages(LocaleId.fromString(src), LocaleId.fromString(dest));
					gconn.open();
					gconn.query(query);

					QueryResult res = gconn.next();
					reply = res.target.toString();

					gconn.close();
				} else if (cstr.equals(acom)) {
					answer = true;

					ApertiumXMLRPCClient aconn = new ApertiumXMLRPCClient(new URL("http://localhost:6173/RPC2"));

					reply = aconn.translate(query, src, dest).get("translation");
				} else if (cstr.equals(scom)) {
					answer = true;

					List<String> translations = new ArrayList<String>(2);

					IQuery gconn = new GoogleMTConnector();

					gconn.setLanguages(LocaleId.fromString(src), LocaleId.fromString(dest));
					gconn.open();
					gconn.query(query);

					QueryResult res = gconn.next();
					String greply = res.target.toString();

					gconn.close();

					ApertiumXMLRPCClient aconn = new ApertiumXMLRPCClient(new URL("http://localhost:6173/RPC2"));

					String areply = aconn.translate(query, src, dest).get("translation");

					translations.add(greply);
					translations.add(areply);

					reply = aconn.synthesise(translations, src, dest);
				} else if (cstr.equals(hcom)) {
					answer = true;

					reply = "example: .google|.apertium|.synthetise en es This is a test.";
				}
			}
		}

		if (msg.isPrivateToUs(clientState) && answer) {
			connection.sendCommand(new MessageCommand(msg.getSource(), reply));
		} else if (answer) {
			connection.sendCommand(new MessageCommand(msg.getDest(), reply));
		}
	}
	
	public IRCConnection getConnection() {
		return connection;
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ApertiumXMLRPCClientException {
		//Main m = new Main("irc.freenode.net", 6667);
		//m.connect();
		
		// ---
		
		/*
		ApertiumXMLRPCClient aconn = new ApertiumXMLRPCClient(new URL("http://localhost:6173/RPC2"));
		String areply = aconn.translate("prueba", "es", "en").get("translation");
		
		System.out.println(areply);
		*/
		
		// --
		
		IQuery gconn = new GoogleMTConnector();

		gconn.setLanguages(LocaleId.fromString("it"), LocaleId.fromString("en"));
		gconn.open();
		gconn.query("Salve a tutti.");

		QueryResult res = gconn.next();
	
		String greply = res.target.toString();

		gconn.close();
		
		System.out.println(greply);
	}
}
