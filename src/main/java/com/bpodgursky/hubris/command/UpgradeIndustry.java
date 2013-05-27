package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UpgradeIndustry extends GameRequest {

	public final Integer star;
	
	public UpgradeIndustry(Integer player, String userName, Long game, Integer star) {
		super(RequestType.UpgradeIndustry, player, userName, game);

		this.star = star;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", upgradeStarOrder(star));
	}

	private final String upgradeStarOrder(Integer star) throws TransformerFactoryConfigurationError, TransformerException {
    Document doc = docBuilder.newDocument();
	
    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element tech = doc.createElement("star");
    
    kind.appendChild(doc.createTextNode("upgrade_industry"));
    tech.appendChild(doc.createTextNode(Integer.toString(star)));
    
    root.appendChild(kind);
    root.appendChild(tech);
    
    return asString(doc);
	}
}
