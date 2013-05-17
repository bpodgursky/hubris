package com.bpodgursky.hubris.command;

import com.bpodgursky.hubris.universe.GameStateDelta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.util.Map;

public class SendTech extends GameRequest<GameStateDelta> {

	public final String tech;
	public final Integer to;
	
	public SendTech(Integer player, String userName, Long game, String tech, Integer to) {
		super(RequestType.SendTech, player, userName, game);

		this.tech = tech;
		this.to = to;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", sendTechOrder());
		
	}
	
	private final String sendTechOrder() throws TransformerFactoryConfigurationError, TransformerException{
    Document doc = docBuilder.newDocument();

    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element to = doc.createElement("to");
    Element from= doc.createElement("fr");
    Element star= doc.createElement("star"); // why?
    Element fleet= doc.createElement("fleet"); // why?
    Element cash= doc.createElement("cash"); 
    Element tech = doc.createElement("tech");
    
    kind.appendChild(doc.createTextNode("player_gift"));
    from.appendChild(doc.createTextNode(Integer.toString(this.playerNumber)));
    to.appendChild(doc.createTextNode(Integer.toString(this.to)));
    star.appendChild(doc.createTextNode(Integer.toString(0)));
    fleet.appendChild(doc.createTextNode(Integer.toString(0)));
    cash.appendChild(doc.createTextNode(Integer.toString(0)));
    tech.appendChild(doc.createTextNode(this.tech));
    
    root.appendChild(kind);
    root.appendChild(to);
    root.appendChild(from);
    root.appendChild(star);
    root.appendChild(fleet);
    root.appendChild(cash);
    root.appendChild(tech);
    
    return asString(doc);
	}


}
