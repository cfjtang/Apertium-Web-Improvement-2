import java.io.*;
import java.net.*;
import java.util.Map;

import org.apache.xmlrpc.*;
import org.apache.xmlrpc.client.*;

import net.sf.okapi.common.*;
import net.sf.okapi.common.resource.*;
import net.sf.okapi.lib.translation.*;

public class ApertiumXMLRPCMTConnector implements IQuery {

	private Parameters params;
	
	private String srcLang;
	private String trgLang;
	
	private boolean hasNext;
	
	private QueryResult result;
	
	private HTMLCharacterEntities entities;
	
	private QueryUtil store;
	
	public ApertiumXMLRPCMTConnector () {
		params = new Parameters();

		entities = new HTMLCharacterEntities();
		entities.ensureInitialization(true);
		store = new QueryUtil();
	}
	
	public String getName () {
		return "Apertium MT";
	}

	public String getSettingsDisplay () {
		return "Server: "+params.getServer();
	}
	
	public void close () {
		hasNext = false;
	}

	public void export (String outputPath) {
		throw new UnsupportedOperationException();
	}

	public LocaleId getSourceLanguage () {
		return LocaleId.fromString(srcLang);
	}

	public LocaleId getTargetLanguage () {
		return LocaleId.fromString(trgLang);
	}

	public boolean hasNext () {
		return hasNext;
	}

	public QueryResult next () {
		if (hasNext) {
			hasNext = false;
			return result;
		}

		result = null;
		return result;
	}

	public void open () {

	}

	public int query (String plainText) {
		return query(new TextFragment(plainText));
	}

	@SuppressWarnings("unchecked")
	public int query (TextFragment frag) {
		result = null;
		hasNext = false;
		
		String plainText;
		
		if (frag.hasCode()) {
			plainText = store.toCodedHTML(frag);
		}
		else {
			plainText = frag.toString();
		}
		if (Util.isEmpty(plainText)) {
			return 0;
		}
		
		try {
			URL url = new URL(params.getServer());

			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(url);
			
	        config.setEnabledForExtensions(true);
	        config.setEnabledForExceptions(true);

	        config.setBasicEncoding("UTF-8");
			
	        XmlRpcClient client = new XmlRpcClient();
	        
			client.setTransportFactory(new XmlRpcSunHttpTransportFactory(client));
			client.setConfig(config);

			Object[] queryparams = new Object[] { plainText, srcLang, trgLang };
	        
			Object or = client.execute("translate", queryparams);
			Map<String, String> r = (Map<String, String>) or;
	        
			String transText = r.get("translation");
	        
	        result = new QueryResult();
	        
	        result.score = 95;
	        result.origin = Util.ORIGIN_MT;
        	result.source = frag;
        	
			if (frag.hasCode()) {
				result.target = new TextFragment(store.fromCodedHTML(transText, frag), frag.getCodes());
			}
			else {
				result.target = new TextFragment(store.fromCodedHTML(transText, frag));
			}
			
			hasNext = (result != null);
			
			return (hasNext ? 1 : 0);
			
		} catch (IOException e) {
			throw new RuntimeException("Error when querying.", e);
		} catch (XmlRpcException e) {
			throw new RuntimeException("Error when querying.", e);
		}
	}

	public void setLanguages (LocaleId sourceLocale, LocaleId targetLocale) {
		srcLang = toInternalCode(sourceLocale);
		trgLang = toInternalCode(targetLocale);
	}

	private String toInternalCode (LocaleId standardCode) {
		String lang = standardCode.getLanguage();
		String reg = standardCode.getRegion();
		if ( reg != null ) {
			// Temporary fix for the Aranese case (until we get real LocaleID)
			if ( reg.equals("aran") ) lang += "_aran";
			// Temporary fix for the Brazilian Portuguese case (until we get real LocaleID)
			if ( reg.equals("br") ) lang += "_BR";
		}
		return lang;
	}

	public IParameters getParameters () {
		return params;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	public void clearAttributes () {
		// Nothing to do
	}

	public void removeAttribute (String name) {
		// Nothing to do
	}

	public void setAttribute (String name,
		String value)
	{
		// Nothing to do
	}

}
