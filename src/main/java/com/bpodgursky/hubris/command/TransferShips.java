package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TransferShips extends GameRequest {

  public final Integer loc1;
	public final Integer loc2;
	public final Integer loc1Final;
	public final Integer loc2Final;
	
	public TransferShips(Integer player, String userName, Long game, Integer loc1, Integer loc2, Integer loc1Final, Integer loc2Final) {
		super(RequestType.TransferShips, player, userName, game);

		this.loc1 = loc1;
		this.loc2 = loc2;
		this.loc1Final = loc1Final;
		this.loc2Final = loc2Final;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", transferShipsOrder(loc1, loc2, loc1Final, loc2Final));
	}
	
	private final String transferShipsOrder(Integer loc1, Integer loc2, Integer loc1Final, Integer loc2Final) throws TransformerFactoryConfigurationError, TransformerException{
    Document doc = docBuilder.newDocument();
		
    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element so1 = doc.createElement("so1_uid");
    Element so2 = doc.createElement("so2_uid");
    Element a1= doc.createElement("a1");
    Element a2= doc.createElement("a2");
    
    kind.appendChild(doc.createTextNode("transfer_strength"));
    so1.appendChild(doc.createTextNode(Integer.toString(loc1)));
    so2.appendChild(doc.createTextNode(Integer.toString(loc2)));

    a1.appendChild(doc.createTextNode(Integer.toString(loc1Final)));
    a2.appendChild(doc.createTextNode(Integer.toString(loc2Final)));
    
    root.appendChild(kind);
    root.appendChild(so1);
    root.appendChild(so2);
    root.appendChild(a1);
    root.appendChild(a2);
    
    return asString(doc);
	}
}
