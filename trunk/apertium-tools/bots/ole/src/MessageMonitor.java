import java.net.MalformedURLException;

import org.apertium.api.exceptions.ApertiumXMLRPCClientException;

import f00f.net.irc.martyr.*;
import f00f.net.irc.martyr.commands.*;
import f00f.net.irc.martyr.replies.*;

public class MessageMonitor extends GenericAutoService {
	private Main m;

	public MessageMonitor(Main m) {
		super(m.getConnection());
		this.m = m;

		enable();
	}

	public void updateCommand(InCommand command) {
		if (command instanceof MessageCommand) {
			MessageCommand msg = (MessageCommand)command;
			try {
				m.incomingMessage(msg);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ApertiumXMLRPCClientException e) {
				e.printStackTrace();
			}
		}
	}

	protected void updateState(State state) {
		if(state == State.UNCONNECTED && m.shouldQuit())
			System.exit(0);
	}
}



