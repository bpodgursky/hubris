package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SendCash extends GameRequest {

	public final Integer destination;
	public final Integer amount;
	
	public SendCash(Integer player, String userName, Long game, Integer destination, Integer amount) {
		super(RequestType.SendCash, player, userName, game);
	
		this.destination = destination;
		this.amount = amount;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws TransformerFactoryConfigurationError, TransformerException {
		params.put("order", sendCashOrder(destination, amount));
	}
	
	private final String sendCashOrder(Integer destination, Integer amount) throws TransformerFactoryConfigurationError, TransformerException{
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
    to.appendChild(doc.createTextNode(Integer.toString(destination)));
    star.appendChild(doc.createTextNode(Integer.toString(0)));
    fleet.appendChild(doc.createTextNode(Integer.toString(0)));
    cash.appendChild(doc.createTextNode(Integer.toString(amount)));
    
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
