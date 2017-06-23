package com.bpodgursky.hubris.response;

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

import com.bpodgursky.hubris.command.GetState;
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
import com.bpodgursky.hubris.universe.Coordinate;
import com.bpodgursky.hubris.universe.Fleet;
import com.bpodgursky.hubris.universe.Game;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Player;
import com.bpodgursky.hubris.universe.Star;
import com.bpodgursky.hubris.universe.Tech;
import com.bpodgursky.hubris.universe.TechType;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResponseTransformer {
  private static final Logger LOG = LoggerFactory.getLogger(ResponseTransformer.class);

  public static final int AT_STAR_THRESHOLD = 1;

  private static final DocumentBuilder docBuilder;

  static {
    try {
      DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
      docBuilder = dbfac.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static GameState parseUniverse(GameState prevState, GetState originalRequest, String response) throws SAXException, IOException, TransformerException {

    try {
      Gson gson = new GsonBuilder()
          .registerTypeAdapter(GameState.class, new GameState.Deserializer(originalRequest.getGameNumber()))
          .registerTypeAdapter(Player.class, new Player.Deserializer())
          .registerTypeAdapter(Fleet.class, new Fleet.Deserializer())
          .registerTypeAdapter(Coordinate.class, new Coordinate.Deserializer())
          .registerTypeAdapter(Star.class, new Star.Deserializer())
          .registerTypeAdapter(Tech.class, new Tech.Deserializer())
          .create();
      return gson.fromJson(response, GameState.class);
//
//      TransformerFactory factory = TransformerFactory.newInstance();
//      Transformer transformer = factory.newTransformer();
//      DOMSource source = new DOMSource(doc);
//      StreamResult result = new StreamResult(new StringWriter());
//      transformer.transform(source, result);
//
//      NodeList nodes = doc.getChildNodes();
//
//      Element root = (Element) nodes.item(0);
//      Element universe = (Element) root.getElementsByTagName("universe").item(0);
//
//      NodeList children = universe.getChildNodes();
//
//      Game game = parseGameNode(children.item(1));
//
//      // Get techs before players so that we can set our player's tech state
//      List<Tech> techs = getTechs(children.item(11));
//
//      Alliance alliances = getAlliances(children.item(3));
//      List<Player> players = getPlayers(children.item(5), game.getMid(), techs);
//      StarClosure starClosure = getStars(children.item(7));
//      List<Fleet> fleets = getFleets(children.item(9), starClosure);
//
//      List<Star> starsWithFleets = Lists.newArrayListWithCapacity(starClosure.stars.size());
//      List<Fleet> fleetsWithStars = Lists.newArrayListWithCapacity(fleets.size());
//      Multimap<Integer, Integer> fleetsAtStars = HashMultimap.create();
//
//      // Find ships that are at stars. This isn't provided directly by the game and can be determined when a
//      // fleet doesn't have a star its destinations and is sufficiently close.
//      for (Fleet fleet : fleets) {
//        Star atStar = null;
//
//        for (Star star : starClosure.stars) {
//          double d = fleet.getCoords().distance(star.getCoords());
//
//          if ((fleet.getDestinations().isEmpty() || fleet.getDestinations().get(0).longValue() != star.getId()) && d < AT_STAR_THRESHOLD) {
//            atStar = star;
//            break;
//          }
//        }
//
//        Fleet fleetWithStar = new Fleet(fleet, atStar == null ? null : atStar.getId());
//        if (atStar != null) {
//          fleetsAtStars.put(atStar.getId(), fleetWithStar.getId());
//        }
//        fleetsWithStars.add(fleetWithStar);
//      }
//
//      // Create stars that have references to ships located at them
//      for (Star star : starClosure.stars) {
//        Collection<Integer> fleetsAtStar = fleetsAtStars.get(star.getId());
//        starsWithFleets.add(new Star(star, fleetsAtStar == null ? Sets.<Integer>newHashSet() : Sets.newHashSet(fleetsAtStar)));
//      }
//
//      return new GameState(prevState, game, players, starsWithFleets, fleetsWithStars, alliances, originalRequest.getPlayerNumber());
    } catch (Exception e) {
      LOG.info("Died parsing response: "+response);

      throw new RuntimeException(e);
    }

  }

  private static Game parseGameNode(Node gameNode) {
    NamedNodeMap gameAttributes = gameNode.getAttributes();
    return new Game(Long.parseLong(gameAttributes.getNamedItem("gn").getNodeValue()),
        gameAttributes.getNamedItem("n").getNodeValue(),
        gameAttributes.getNamedItem("aa").getNodeValue(),
        gameAttributes.getNamedItem("go").getNodeValue(),
        gameAttributes.getNamedItem("hs").getNodeValue(),
        gameAttributes.getNamedItem("lt").getNodeValue(),
        Integer.parseInt(gameAttributes.getNamedItem("mid").getNodeValue()),
        gameAttributes.getNamedItem("np").getNodeValue(),
        gameAttributes.getNamedItem("sfv").getNodeValue(),
        gameAttributes.getNamedItem("tr").getNodeValue(),
        gameAttributes.getNamedItem("tt").getNodeValue());
  }

  private static Alliance getAlliances(Node alliance) {
    NamedNodeMap allianceAttributes = alliance.getAttributes();
    return new Alliance(allianceAttributes.getNamedItem("a").getNodeValue());
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
              TechType.fromStringValue(tech),
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
              TechType.fromStringValue(tech),
              Integer.parseInt(bodyNodeMap.get("sender").getTextContent())));
        }

      } else if (subject.equals("research level")) {

        eventList.add(new TechResearch(key, time,
            TechType.fromStringValue(bodyNodeMap.get("n").getTextContent()),
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

    private StarClosure(List<Star> stars, Range<Integer> xRange, Range<Integer> yRange) {
      this.stars = stars;
      this.xRange = xRange;
      this.yRange = yRange;
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
  }
}
