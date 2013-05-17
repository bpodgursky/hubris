package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.bpodgursky.hubris.universe.GameStateDelta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetWaypoint extends GameRequest<GameStateDelta> {

	public final Integer fleet;
	public final Integer star;
	
	public SetWaypoint(Integer player, String userName, Long game, Integer fleet, Integer star) {
		super(RequestType.FleetWaypoint, player, userName, game);

		this.fleet = fleet;
		this.star = star;
	}

	@Override
	protected void addRequestParams(Map<String, String> params) throws Exception {
		params.put("order", fleetPathOrder(fleet, star));
	}

	private final String fleetPathOrder(Integer fleetNum, Integer starNum) throws TransformerFactoryConfigurationError, TransformerException{
    Document doc = docBuilder.newDocument();
		
    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element fleet = doc.createElement("fleet");
    Element star = doc.createElement("star");
    
    kind.appendChild(doc.createTextNode("add_fleet_path"));
    fleet.appendChild(doc.createTextNode(Integer.toString(fleetNum)));
    star.appendChild(doc.createTextNode(Integer.toString(starNum)));
    
    root.appendChild(kind);
    root.appendChild(fleet);
    root.appendChild(star);
    
    return asString(doc);
	}
}
