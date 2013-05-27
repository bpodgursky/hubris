package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetNextResearch extends GameRequest {

	public final String research;
	
	public SetNextResearch(Integer player, String userName, Long game, String researchName) {
		super(RequestType.SetNextResearch, player, userName, game);
	
		this.research = researchName;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", setNextResearchOrder(research));
	}
	
	private final String setNextResearchOrder(String researchName) throws TransformerFactoryConfigurationError, TransformerException {
    Document doc = docBuilder.newDocument();
	
    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element tech = doc.createElement("tech");
    
    kind.appendChild(doc.createTextNode("research_next"));
    tech.appendChild(doc.createTextNode(researchName));
    
    root.appendChild(kind);
    root.appendChild(tech);
    
    return asString(doc);
	}
}
