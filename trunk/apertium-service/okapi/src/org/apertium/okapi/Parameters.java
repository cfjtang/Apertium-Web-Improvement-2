package org.apertium.okapi;

import net.sf.okapi.common.*;
import net.sf.okapi.common.uidescription.*;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	static final String SERVER = "server";
	
	private String server;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public Parameters (String initialData) {
		fromString(initialData);
	}
	
	public String getServer () {
		return server;
	}

	public void setServer (String server) {
		this.server = server;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		server = buffer.getString(SERVER, server);
	}

	public void reset () {
		server = "http://localhost:6173/RPC2";
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(SERVER, server);
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(SERVER, "Server URL:", "Full URL of the server");
		return desc;
	}

	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Apertium XML-RPC MT Connector Settings");
		desc.addTextInputPart(paramsDesc.get(SERVER));
		return desc;
	}

}
