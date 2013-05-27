package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetGarrison extends GameRequest {

	public final Integer star;
	public final Integer size;
	
	public SetGarrison(Integer player, String userName, Long game, Integer star, Integer size) {
		super(RequestType.SetGarrison, player, userName, game);

		this.star = star;
		this.size = size;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", setStarGarrison(star, size));
	}
	
	private final String setStarGarrison(Integer starID, Integer garrisonSize) throws TransformerFactoryConfigurationError, TransformerException{
    Document doc = docBuilder.newDocument();
		
    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element star = doc.createElement("star");
    Element size= doc.createElement("size");
    
    kind.appendChild(doc.createTextNode("set_garrison"));
    star.appendChild(doc.createTextNode(Integer.toString(starID)));
    size.appendChild(doc.createTextNode(Integer.toString(garrisonSize)));
    
    root.appendChild(kind);
    root.appendChild(star);
    root.appendChild(size);
    
    return asString(doc);
	}
}
