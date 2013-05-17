package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.bpodgursky.hubris.universe.GameStateDelta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ClearAllFleetPaths extends GameRequest<GameStateDelta> {

	public final Integer fleet;
	
	public ClearAllFleetPaths(Integer player, String userName, Long game, Integer fleet) {
		super(RequestType.ClearAllFleetPaths, player, userName, game);

		this.fleet = fleet;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", clearFleetPathOrder(fleet));
	}
	
	private final String clearFleetPathOrder(Integer fleetID) throws TransformerFactoryConfigurationError, TransformerException{
    Document doc = docBuilder.newDocument();
		
    Element root = doc.createElement("order");
    doc.appendChild(root);
    
    Element kind = doc.createElement("kind");
    Element fleet = doc.createElement("fleet");
    
    kind.appendChild(doc.createTextNode("clear_fleet_path"));
    fleet.appendChild(doc.createTextNode(Integer.toString(fleetID)));
    
    root.appendChild(kind);
    root.appendChild(fleet);
    
    return asString(doc);
	}

}
