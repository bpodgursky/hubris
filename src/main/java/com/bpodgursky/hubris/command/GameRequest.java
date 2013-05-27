package com.bpodgursky.hubris.command;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

public abstract class GameRequest {

  public final Integer playerNumber;
  public final String userName;
  public final Long gameNumber;
  public final RequestType requestType;

  protected static final DocumentBuilder docBuilder;

  static {
    try {
      DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
      docBuilder = dbfac.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static enum RequestType {
    GetEvents {
      public String type() {
        return "return_event_messages";
      }
    },

    GetMessageComments {
      public String type() {
        return "return_game_message_comments";
      }
    },

    SendMessage {
      public String type() {
        return "create_game_message";
      }
    },

    SendMessageComment {
      public String type() {
        return "create_game_message_comment";
      }
    },

    GetMessages {
      public String type() {
        return "return_game_messages";
      }
    },

    SendCash {
      public String type() {
        return "game_order";
      }
    },

    UpgradeEcon {
      public String type() {
        return "game_order";
      }
    },

    UpgradeScience {
      public String type() {
        return "game_order";
      }
    },

    UpgradeIndustry {
      public String type() {
        return "game_order";
      }
    },

    SetNextResearch {
      public String type() {
        return "game_order";
      }
    },

    SetResearch {
      public String type() {
        return "game_order";
      }
    },

    TransferShips {
      public String type() {
        return "game_order";
      }
    },

    CreateCarrier {
      public String type() {
        return "game_order";
      }
    },

    SetGarrison {
      public String type() {
        return "game_order";
      }
    },

    GetState {
      public String type() {
        return "game_order";
      }
    },

    FleetWaypoint {
      public String type() {
        return "game_order";
      }
    },

    ClearLastFleetPath {
      public String type() {
        return "game_order";
      }
    },

    ClearAllFleetPaths {
      public String type() {
        return "game_order";
      }
    },

    SendTech {
      public String type() {
        return "game_order";
      }
    };

    public abstract String type();
  }


  public GameRequest(RequestType requestType, Integer player, String userName, Long game) {
    this.requestType = requestType;
    this.playerNumber = player;
    this.userName = userName;
    this.gameNumber = game;
  }


  public static GameRequest fromRequestParams(String request) {
    //	TODO do

    return null;
  }

  public String toRequestParams() throws Exception {
    Map<String, String> allParams = new HashMap<String, String>();
    addRequestParams(allParams);

    allParams.put("see_all", "");
    allParams.put("player_number", Integer.toString(playerNumber));
    allParams.put("user", userName);
    allParams.put("game_number", Long.toString(gameNumber));
    allParams.put("request_type", requestType.type());

    List<String> params = new ArrayList<String>();
    for (Map.Entry<String, String> entry : allParams.entrySet()) {
      params.add(URLEncoder.encode(entry.getKey(), "utf-8") + "=" + URLEncoder.encode(entry.getValue(), "utf-8"));
    }


//    return "player%5Fnumber=0&order=&game%5Fnumber=45546246&user=Bpodgursky%40gmail%2Ecom&see%5Fall=&request%5Ftype=game%5Forder"

    return StringUtils.join(params, "&");
  }

  protected final String asString(Document doc) throws TransformerFactoryConfigurationError, TransformerException {
    Transformer serializer = TransformerFactory.newInstance().newTransformer();
    serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.transform(new DOMSource(doc), new StreamResult(out));

    return out.toString();
  }

  protected abstract void addRequestParams(Map<String, String> params) throws Exception;

  public Integer getPlayerNumber() {
    return playerNumber;
  }

  public String getUserName() {
    return userName;
  }

  public Long getGameNumber() {
    return gameNumber;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public static DocumentBuilder getDocBuilder() {
    return docBuilder;
  }
}
