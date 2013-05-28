package com.bpodgursky.hubris.response;

import com.bpodgursky.hubris.command.GameRequest;
import com.bpodgursky.hubris.notification.AIAdmin;
import com.bpodgursky.hubris.notification.CapturedSystem;
import com.bpodgursky.hubris.notification.CashReceived;
import com.bpodgursky.hubris.notification.CashSent;
import com.bpodgursky.hubris.notification.FleetCombat;
import com.bpodgursky.hubris.notification.GameNotification;
import com.bpodgursky.hubris.notification.Message;
import com.bpodgursky.hubris.notification.NotAiAdmin;
import com.bpodgursky.hubris.notification.Production;
import com.bpodgursky.hubris.notification.TechReceived;
import com.bpodgursky.hubris.notification.TechResearch;
import com.bpodgursky.hubris.notification.TechSent;
import com.bpodgursky.hubris.universe.Alliance;
import com.bpodgursky.hubris.universe.Comment;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.Game;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.bpodgursky.hubris.universe.Tech;
import com.bpodgursky.hubris.universe.TechType;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.notification.*;
import com.bpodgursky.hubris.universe.*;
import com.bpodgursky.hubris.universe.Comment;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseTransformer {
  public static final int NORMALIZED_SIZE = 1000;
  private static final DocumentBuilder docBuilder;

  static {
    try {
      DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
      docBuilder = dbfac.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static GameState parseUniverse(GetState originalRequest, String response) throws SAXException, IOException, TransformerException {
    Document doc = docBuilder.parse(new ByteArrayInputStream(response.getBytes()));

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(new StringWriter());
    transformer.transform(source, result);

    NodeList nodes = doc.getChildNodes();

    Element root = (Element) nodes.item(0);
    Element universe = (Element) root.getElementsByTagName("universe").item(0);

    NodeList children = universe.getChildNodes();

    Game game = parseGameNode(children.item(1));
    Alliance alliances = getAlliances(children.item(3));
    List<Player> players = getPlayers(children.item(5));
    StarClosure starClosure = getStars(children.item(7));
    List<Fleet> fleets = getFleets(children.item(9), starClosure);
    List<Tech> techs = getTechs(children.item(11));


    return new GameState(game, players, starClosure.stars, fleets, techs, alliances, originalRequest.getPlayerNumber());
  }

  private static Game parseGameNode(Node gameNode) {
    NamedNodeMap gameAttributes = gameNode.getAttributes();
    return new Game(Long.parseLong(gameAttributes.getNamedItem("gn").getNodeValue()),
        gameAttributes.getNamedItem("n").getNodeValue(),
        gameAttributes.getNamedItem("aa").getNodeValue(),
        gameAttributes.getNamedItem("go").getNodeValue(),
        gameAttributes.getNamedItem("hs").getNodeValue(),
        gameAttributes.getNamedItem("lt").getNodeValue(),
        gameAttributes.getNamedItem("mid").getNodeValue(),
        gameAttributes.getNamedItem("np").getNodeValue(),
        gameAttributes.getNamedItem("sfv").getNodeValue(),
        gameAttributes.getNamedItem("tr").getNodeValue(),
        gameAttributes.getNamedItem("tt").getNodeValue());
  }

  private static Alliance getAlliances(Node alliance) {
    NamedNodeMap allianceAttributes = alliance.getAttributes();
    return new Alliance(allianceAttributes.getNamedItem("a").getNodeValue());
  }

  private static List<Player> getPlayers(Node playerNodes) {
    NodeList playerList = playerNodes.getChildNodes();

    List<Player> players = new ArrayList<Player>();
    for (int i = 0; i < playerList.getLength(); i++) {
      Node playerNode = playerList.item(i);
      NamedNodeMap playerAttributes = playerNode.getAttributes();

      Player player = new Player(playerAttributes.getNamedItem("n").getNodeValue(),
          Integer.parseInt(playerAttributes.getNamedItem("id").getNodeValue()),
          Integer.parseInt(playerAttributes.getNamedItem("tecon").getNodeValue()),
          Integer.parseInt(playerAttributes.getNamedItem("tind").getNodeValue()),
          Integer.parseInt(playerAttributes.getNamedItem("tsci").getNodeValue()),
          Integer.parseInt(playerAttributes.getNamedItem("tstars").getNodeValue()),
          Integer.parseInt(playerAttributes.getNamedItem("tfleets").getNodeValue()),
          Integer.parseInt(playerAttributes.getNamedItem("thf").getNodeValue()),
          Integer.parseInt(playerAttributes.getNamedItem("tships").getNodeValue()),
          Boolean.parseBoolean(playerAttributes.getNamedItem("ai").getNodeValue()),
          playerAttributes.getNamedItem("al").getNodeValue(),
          Integer.parseInt(playerAttributes.getNamedItem("c").getNodeValue()),
          playerAttributes.getNamedItem("cr").getNodeValue(),
          playerAttributes.getNamedItem("crn").getNodeValue(),
          Integer.parseInt(playerAttributes.getNamedItem("fc").getNodeValue()),
          Double.parseDouble(playerAttributes.getNamedItem("fr").getNodeValue()),
          Double.parseDouble(playerAttributes.getNamedItem("fs").getNodeValue()),
          Double.parseDouble(playerAttributes.getNamedItem("sr").getNodeValue()));

      players.add(player);
    }

    return players;
  }

  private static StarClosure getStars(Node starsNode) {
    NodeList starsList = starsNode.getChildNodes();

    List<Star> stars = Lists.newArrayList();
    List<Integer> xvalues = Lists.newArrayList();
    List<Integer> yvalues = Lists.newArrayList();
    for (int i = 0; i < starsList.getLength(); i++) {
      Node starNode = starsList.item(i);
      NamedNodeMap starAttributes = starNode.getAttributes();

      Node econ = starAttributes.getNamedItem("e");
      Node eup = starAttributes.getNamedItem("eup");
      Node f = starAttributes.getNamedItem("f");
      Node industry = starAttributes.getNamedItem("i");
      Node iup = starAttributes.getNamedItem("iup");
      Node pn = starAttributes.getNamedItem("pn");
      Node s = starAttributes.getNamedItem("s");
      Node sup = starAttributes.getNamedItem("sup");
      Node g = starAttributes.getNamedItem("g");
      Node resources = starAttributes.getNamedItem("r");

      int x = Integer.parseInt(starAttributes.getNamedItem("x").getNodeValue());
      int y = Integer.parseInt(starAttributes.getNamedItem("y").getNodeValue());
      xvalues.add(x);
      yvalues.add(y);

      stars.add(new Star(starAttributes.getNamedItem("n").getNodeValue(),
          pn == null ? null : Integer.parseInt(pn.getNodeValue()),
          econ == null ? null : Integer.parseInt(econ.getNodeValue()),
          eup == null ? null : Integer.parseInt(eup.getNodeValue()),
          f == null ? null : Integer.parseInt(f.getNodeValue()),
          industry == null ? null : Integer.parseInt(industry.getNodeValue()),
          iup == null ? null : Integer.parseInt(iup.getNodeValue()),
          s == null ? null : Integer.parseInt(s.getNodeValue()),
          sup == null ? null : Integer.parseInt(sup.getNodeValue()),
          Integer.parseInt(starAttributes.getNamedItem("uid").getNodeValue()),
          x,
          y,
          g == null ? null : Integer.parseInt(g.getNodeValue()),
          resources == null ? null : Integer.parseInt(resources.getNodeValue())));
    }
    Range<Integer> xRange = Range.encloseAll(xvalues);
    Range<Integer> yRange = Range.encloseAll(yvalues);
    List<Star> normalizedStars = Lists.newArrayList();
    double xSpan = (xRange.upperEndpoint() - xRange.lowerEndpoint());
    double ySpan = (yRange.upperEndpoint() - yRange.lowerEndpoint());

    for (Star star : stars) {
      int normalizedX = (int)(((star.getX() - xRange.lowerEndpoint()) / xSpan) * NORMALIZED_SIZE);
      int normalizedY = (int)(((star.getY() - yRange.lowerEndpoint()) / ySpan) * NORMALIZED_SIZE);

      normalizedStars.add(new Star(star, normalizedX, normalizedY));
    }

    return new StarClosure(normalizedStars, xRange, yRange, xSpan, ySpan);
  }

  private static List<Fleet> getFleets(Node fleetsNode, StarClosure starClosure) {
    NodeList fleetList = fleetsNode.getChildNodes();

    List<Fleet> fleets = new ArrayList<Fleet>();
    for (int i = 0; i < fleetList.getLength(); i++) {
      Node fleetNode = fleetList.item(i);
      NamedNodeMap fleetAttributes = fleetNode.getAttributes();

      String destinations = fleetAttributes.getNamedItem("paths").getNodeValue();
      String[] destinationIDs = destinations.split(",");
      List<Integer> destStars = new ArrayList<Integer>();
      for (String s : destinationIDs) {
        if (!s.equals("")) {
          destStars.add(Integer.parseInt(s));
        }
      }

      int x = Integer.parseInt(fleetAttributes.getNamedItem("x").getNodeValue());
      int y = Integer.parseInt(fleetAttributes.getNamedItem("y").getNodeValue());

      x = (int)(((x - starClosure.xRange.lowerEndpoint()) / starClosure.xSpan)*NORMALIZED_SIZE);
      y = (int)(((y - starClosure.yRange.lowerEndpoint()) / starClosure.ySpan)*NORMALIZED_SIZE);

      Fleet fleet = new Fleet(fleetAttributes.getNamedItem("n").getNodeValue(),
        Integer.parseInt(fleetAttributes.getNamedItem("uid").getNodeValue()),
        Integer.parseInt(fleetAttributes.getNamedItem("pn").getNodeValue()),
        Integer.parseInt(fleetAttributes.getNamedItem("eta").getNodeValue()),
        Integer.parseInt(fleetAttributes.getNamedItem("neta").getNodeValue()),
        Integer.parseInt(fleetAttributes.getNamedItem("s").getNodeValue()),
        Integer.parseInt(fleetAttributes.getNamedItem("v").getNodeValue()),
        destStars,
        x,
        y,
        Integer.parseInt(fleetAttributes.getNamedItem("rt").getNodeValue()));

      fleets.add(fleet);
    }

    return fleets;
  }

  private static List<Tech> getTechs(Node techsNode) {
    NodeList techList = techsNode.getChildNodes();

    List<Tech> techs = new ArrayList<Tech>();
    for (int i = 0; i < techList.getLength(); i++) {
      Node techNode = techList.item(i);
      NamedNodeMap techAttributes = techNode.getAttributes();

      Integer level = Integer.parseInt(techAttributes.getNamedItem("l").getNodeValue());
      Integer increment = Integer.parseInt(techAttributes.getNamedItem("brr").getNodeValue());
      Integer currentPoints = Integer.parseInt(techAttributes.getNamedItem("cr").getNodeValue());
      Double v = Double.parseDouble(techAttributes.getNamedItem("v").getNodeValue());
      Double bv = Double.parseDouble(techAttributes.getNamedItem("bv").getNodeValue());
      Double sv = Double.parseDouble(techAttributes.getNamedItem("sv").getNodeValue());

      Tech tech = new Tech(
          techAttributes.getNamedItem("n").getNodeValue(),
          level, level * increment, currentPoints, v, bv, sv);

      techs.add(tech);
    }

    return techs;
  }

  public static List<Message> parseMessages(String messages) throws TransformerException, SAXException, IOException {
    Document doc = docBuilder.parse(new ByteArrayInputStream(messages.getBytes()));

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();
    StreamResult result = new StreamResult(new StringWriter());
    DOMSource source = new DOMSource(doc);
    transformer.transform(source, result);

    NodeList nodes = doc.getChildNodes();

    Element root = (Element) nodes.item(0);
    Element messagesNode = (Element) root.getElementsByTagName("messages").item(0);
    NodeList messageNodeList = messagesNode.getElementsByTagName("message");

    List<Message> messageList = new ArrayList<Message>();
    for (int i = 0; i < messageNodeList.getLength(); i++) {
      Element messageNode = (Element) messageNodeList.item(i);
      NodeList children = messageNode.getChildNodes();

      Integer from = null;
      List<Integer> to = new ArrayList<Integer>();
      Long timestamp = null;
      String key = null;
      String subject = null;
      String status = null;
      String body = null;

      for (int j = 0; j < children.getLength(); j++) {
        Node node = children.item(j);

        String nodeName = node.getNodeName();
        String content = node.getTextContent();
        if (nodeName.equals("key")) {
          key = content;
        } else if (nodeName.equals("fr")) {
          from = Integer.parseInt(content);
        } else if (nodeName.equals("to")) {
          to.add(Integer.parseInt(content));
        } else if (nodeName.equals("at")) {
          timestamp = (long) Double.parseDouble(content);
        } else if (nodeName.equals("subject")) {
          subject = content;
        } else if (nodeName.equals("body")) {
          body = content;
        } else if (nodeName.equals("status")) {
          status = content;
        }
      }

      messageList.add(new Message(from, to, timestamp, key, subject, body, status));
    }

    return messageList;
  }

  public static List<Comment> parseMessageComments(String comments) throws SAXException, IOException, TransformerException {
    Document doc = docBuilder.parse(new ByteArrayInputStream(comments.getBytes()));

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();
    StreamResult result = new StreamResult(new StringWriter());
    DOMSource source = new DOMSource(doc);
    transformer.transform(source, result);

    NodeList nodes = doc.getChildNodes();

    Element root = (Element) nodes.item(0);
    Element commentNode = (Element) root.getElementsByTagName("comments").item(0);
    NodeList commentNodeList = commentNode.getElementsByTagName("comment");

    List<Comment> commentList = new ArrayList<Comment>();
    for (int i = 0; i < commentNodeList.getLength(); i++) {
      Element messageNode = (Element) commentNodeList.item(i);
      NodeList children = messageNode.getChildNodes();

      Map<String, Node> nodeMap = nodesByName(children);

      commentList.add(new Comment(Integer.parseInt(nodeMap.get("fr").getTextContent()),
          (long) Double.parseDouble(nodeMap.get("at").getTextContent()),
          nodeMap.get("body").getTextContent()));
    }

    return commentList;
  }

  public static List<GameNotification> parseEventList(String events) throws Exception {
    Document doc = docBuilder.parse(new ByteArrayInputStream(events.getBytes()));

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();
    StreamResult result = new StreamResult(new StringWriter());
    DOMSource source = new DOMSource(doc);
    transformer.transform(source, result);

    NodeList nodes = doc.getChildNodes();

    Element root = (Element) nodes.item(0);
    Element eventsNode = (Element) root.getElementsByTagName("messages").item(0);
    NodeList eventNodeList = eventsNode.getElementsByTagName("message");

    List<GameNotification> eventList = new ArrayList<GameNotification>();

    for (int i = 0; i < eventNodeList.getLength(); i++) {
      Element eventNode = (Element) eventNodeList.item(i);
      NodeList children = eventNode.getChildNodes();

      Map<String, Node> nodeMap = nodesByName(children);

      String subject = nodeMap.get("subject").getTextContent();
      Node bodyData = nodeMap.get("body_data");

      NodeList dataElements = bodyData.getChildNodes();
      Map<String, Node> bodyNodeMap = nodesByName(dataElements);

      String key = nodeMap.get("key").getTextContent();
      Long time = (long) Double.parseDouble(nodeMap.get("at").getTextContent());

      if (subject.equals("new system")) {

        eventList.add(new CapturedSystem(key, time,
            Integer.parseInt(bodyNodeMap.get("s_uid").getTextContent()),
            Integer.parseInt(bodyNodeMap.get("f_uid").getTextContent())));

      } else if (subject.equals("gift_sent")) {

        String tech = bodyNodeMap.get("tech").getTextContent();
        if (tech.equals("")) {
          eventList.add(new CashSent(key, time,
              Integer.parseInt(bodyNodeMap.get("recip").getTextContent()),
              Integer.parseInt(bodyNodeMap.get("cash").getTextContent())));
        } else {
          eventList.add(new TechSent(key, time,
              TechType.fromGameId(tech),
              Integer.parseInt(bodyNodeMap.get("recip").getTextContent())));
        }

      } else if (subject.equals("gift_recieved")) {
        String tech = bodyNodeMap.get("tech").getTextContent();

        if (tech.equals("")) {
          eventList.add(new CashReceived(key, time,
              Integer.parseInt(bodyNodeMap.get("sender").getTextContent()),
              Integer.parseInt(bodyNodeMap.get("cash").getTextContent())));
        } else {
          eventList.add(new TechReceived(key, time,
              TechType.fromGameId(tech),
              Integer.parseInt(bodyNodeMap.get("sender").getTextContent())));
        }

      } else if (subject.equals("research level")) {

        eventList.add(new TechResearch(key, time,
            TechType.fromGameId(bodyNodeMap.get("n").getTextContent()),
            Integer.parseInt(bodyNodeMap.get("l").getTextContent())));

      } else if (subject.equals("production")) {

        eventList.add(new Production(key, time,
            Integer.parseInt(bodyNodeMap.get("cash").getTextContent())));
      } else if (subject.equals("fleet_combat")) {
        String[] combats = bodyNodeMap.get("combat_tech").getTextContent().split(",");
        String[] before = bodyNodeMap.get("ships_before").getTextContent().split(",");
        String[] after = bodyNodeMap.get("ships_after").getTextContent().split(",");
        Integer defender = Integer.parseInt(bodyNodeMap.get("home_defence").getTextContent());
        Integer star = Integer.parseInt(bodyNodeMap.get("home_defence").getTextContent());

        eventList.add(new FleetCombat(key, time,
            defender, intByIndex(before), intByIndex(after), intByIndex(combats), star));
      } else if (subject.equals("ai admin")) {

        String player = bodyNodeMap.get("p").getTextContent();
        eventList.add(new AIAdmin(key, time, Integer.parseInt(player)));

      } else if (subject.equals("ai admin no more")) {

        String player = bodyNodeMap.get("p").getTextContent();
        eventList.add(new NotAiAdmin(key, time, Integer.parseInt(player)));
      } else {
        throw new Exception("don't know how to handle event subject " + subject + "!");
      }
    }

    return eventList;
  }

  private static Map<Integer, Integer> intByIndex(String[] items) {

    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    for (int i = 0; i < items.length; i++) {
      map.put(i, Integer.parseInt(items[i]));
    }

    return map;
  }

  private static Map<String, Node> nodesByName(NodeList nodes) {

    Map<String, Node> textValues = new HashMap<String, Node>();
    for (int j = 0; j < nodes.getLength(); j++) {
      Node node = nodes.item(j);
      textValues.put(node.getNodeName(), node);
    }

    return textValues;
  }

  private static class StarClosure {
    private final List<Star> stars;
    private final Range<Integer> xRange;
    private final Range<Integer> yRange;
    private final double xSpan;
    private final double ySpan;

    private StarClosure(List<Star> stars, Range<Integer> xRange, Range<Integer> yRange, double xSpan, double ySpan) {
      this.stars = stars;
      this.xRange = xRange;
      this.yRange = yRange;
      this.xSpan = xSpan;
      this.ySpan = ySpan;
    }

    private List<Star> getStars() {
      return stars;
    }

    private Range<Integer> getxRange() {
      return xRange;
    }

    private Range<Integer> getyRange() {
      return yRange;
    }

    private double getxSpan() {
      return xSpan;
    }

    private double getySpan() {
      return ySpan;
    }
  }
}