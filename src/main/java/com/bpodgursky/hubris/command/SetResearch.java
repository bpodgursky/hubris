package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetResearch extends GameRequest {

	public final String research;
	
	public SetResearch(Integer player, String userName, Long game, String research) {
		super(RequestType.SetResearch, player, userName, game);

		this.research = research;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", setResearchOrder(research));
	}
	
	private final String setResearchOrder(String researchName) throws TransformerFactoryConfigurationError, TransformerException {
    Document doc = docBuilder.newDocument();
	
    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element tech = doc.createElement("tech");
    
    kind.appendChild(doc.createTextNode("research"));
    tech.appendChild(doc.createTextNode(researchName));
    
    root.appendChild(kind);
    root.appendChild(tech);
    
    return asString(doc);
	}
}
