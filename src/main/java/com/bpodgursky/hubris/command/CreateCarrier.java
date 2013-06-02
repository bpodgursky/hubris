package com.bpodgursky.hubris.command;

import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CreateCarrier extends GameRequest {

  public final Integer star;
  public final Integer strength;

  public CreateCarrier(Integer player, String userName, Long game, Integer star, Integer strength) {
    super(RequestType.CreateCarrier, player, userName, game);

    this.star = star;
    this.strength = strength;
  }

  @Override
  protected void addRequestParams(Map<String, String> params) throws Exception {
    params.put("order", createCarrierOrder(star, strength));
  }

  private final String createCarrierOrder(Integer starID, Integer strength) throws TransformerFactoryConfigurationError, TransformerException {
    Document doc = docBuilder.newDocument();

    Element root = doc.createElement("order");
    doc.appendChild(root);

    Element kind = doc.createElement("kind");
    Element star = doc.createElement("star");
    Element size = doc.createElement("strength");
    Element name = doc.createElement("name");

    kind.appendChild(doc.createTextNode("new_fleet"));
    star.appendChild(doc.createTextNode(Integer.toString(starID)));
    size.appendChild(doc.createTextNode(Integer.toString(strength)));
    name.appendChild(doc.createTextNode("fleet_" + System.currentTimeMillis()));

    root.appendChild(kind);
    root.appendChild(star);
    root.appendChild(size);
    root.appendChild(name);

    return asString(doc);
  }

  @Override
  public String toString() {
    return "CreateCarrier{" +
        "star=" + star +
        ", strength=" + strength +
        '}';
  }
}
