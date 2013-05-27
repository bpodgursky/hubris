package com.bpodgursky.hubris.universe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;


public class GameState {

  public final Map<Integer, Player> playersByID;
  public final Map<Integer, Star> starsByID;
  public final Map<Integer, Fleet> fleetsByID;
  public final Map<String, Tech> techStatesByName;
  public final Alliance alliance;
  public final Game gameData;

  public GameState(Game gameData, Collection<Player> players, Collection<Star> stars, Collection<Fleet> fleets,
                   Collection<Tech> techs, Alliance alliance) {
    this.playersByID = new HashMap<Integer, Player>();

    for (Player p : players) {
      playersByID.put(p.id, p);
    }

    this.starsByID = new HashMap<Integer, Star>();

    for (Star s : stars) {
      starsByID.put(s.id, s);
    }

    this.fleetsByID = new HashMap<Integer, Fleet>();

    for (Fleet f : fleets) {
      fleetsByID.put(f.id, f);
    }

    this.techStatesByName = new HashMap<String, Tech>();

    for (Tech t : techs) {
      techStatesByName.put(t.researchName, t);
    }

    this.alliance = alliance;
    this.gameData = gameData;
  }

  public GameState apply(GameStateDelta delta) {

    Map<Integer, Player> playersByID = new HashMap<Integer, Player>(this.playersByID);
    Map<Integer, Star> starsByID = new HashMap<Integer, Star>(this.starsByID);
    Map<Integer, Fleet> fleetsByID = new HashMap<Integer, Fleet>(this.fleetsByID);
    Map<String, Tech> techStatesByName = new HashMap<String, Tech>(this.techStatesByName);

    for (Player p : delta.getPlayers()) {
      playersByID.put(p.id, p);
    }

    for (Star s : delta.getStars()) {
      starsByID.put(s.id, s);
    }

    for (Fleet f : delta.getFleets()) {
      fleetsByID.put(f.id, f);
    }

    for (Tech t : delta.getTechs()) {
      techStatesByName.put(t.researchName, t);
    }

    Alliance alliance = delta.getAlliance();
    Game gameData = this.gameData;

    return new GameState(gameData, playersByID.values(), starsByID.values(), fleetsByID.values(), techStatesByName.values(), alliance);
  }

  public String toString() {
    return new Gson().toJson(this);
  }

  private transient static final Map<Integer, String> colorsByID = new HashMap<Integer, String>();

  static {
    colorsByID.put(-1, "#778899");
    colorsByID.put(0, "red");
    colorsByID.put(1, "blue");
    colorsByID.put(2, "green");
    colorsByID.put(3, "brown");
    colorsByID.put(4, "#ADD8E6");
    colorsByID.put(5, "blueviolet");
    colorsByID.put(6, "#D2691E");
    colorsByID.put(7, "#00008B");
    colorsByID.put(8, "darkorange");
    colorsByID.put(9, "coral");
    colorsByID.put(10, "crimson");
    colorsByID.put(11, "black");
    colorsByID.put(12, "gold");

  }

  public void writeGnuPlot(String epsName) throws IOException, InterruptedException {
    StringBuilder builder = new StringBuilder();
    builder.append("set terminal postscript eps enhanced color \"Times-Roman\" 3\n");
    builder.append("set output \"" + epsName + "\"\n");

    String root = UUID.randomUUID().toString().replaceAll("-", "");
    String tmpStarRoot = "tmp_star_data_" + root;
    String tmpFleetRoot = "tmp_fleet_data_" + root;

    Map<Integer, FileWriter> playerStarFiles = new HashMap<Integer, FileWriter>();
    Map<Integer, FileWriter> playerFleetFiles = new HashMap<Integer, FileWriter>();

    for (Integer player : playersByID.keySet()) {
      playerStarFiles.put(player, new FileWriter(tmpStarRoot + "_" + player));
      playerFleetFiles.put(player, new FileWriter(tmpFleetRoot + "_" + player));
    }
    playerStarFiles.put(-1, new FileWriter(tmpStarRoot + "_none"));
    playerFleetFiles.put(-1, new FileWriter(tmpFleetRoot + "_none"));

    for (Star s : starsByID.values()) {
      String label = (s.fleets == null ? "" : s.fleets + "-") + "[" +
          (s.economy == null ? "" : s.economy + ",") +
          (s.industry == null ? "" : s.industry + ",") +
          (s.science == null ? "" : s.science) + "]" +
          (s.resources == null ? "" : "-" + s.resources);

      playerStarFiles.get(s.playerNumber).append(s.x + "\t" + (-s.y) + "\t" + label + "\n");
    }

    for (Map.Entry<Integer, FileWriter> entry : playerStarFiles.entrySet()) {
      builder.append("set style line " + (entry.getKey() + 10) + " lt rgb \"" + colorsByID.get(entry.getKey()) + "\" lw 3 pt 6\n");
    }

    builder.append("set pointsize .1\n");
    builder.append("set nokey\n");
    builder.append("unset border\n");
    builder.append("unset xtics\n");
    builder.append("unset ytics\n");
    builder.append("plot ");

    List<String> plots = new ArrayList<String>();
    for (Map.Entry<Integer, FileWriter> entry : playerStarFiles.entrySet()) {
      entry.getValue().close();
      plots.add("'" + tmpStarRoot + "_" + (entry.getKey() == -1 ? "none" : entry.getKey()) + "' using 1:2:3 with labels left offset -5,-1 point ls " + (entry.getKey() + 10));
    }

    builder.append(StringUtils.join(plots, ", \\\n"));
    builder.append("\n");

    FileWriter plotFile = new FileWriter("tmp_figure_" + root + ".gnu");
    plotFile.write(builder.toString());
    plotFile.close();

    Process p = Runtime.getRuntime().exec("gnuplot tmp_figure_" + root + ".gnu");
    p.waitFor();

    for (Map.Entry<Integer, FileWriter> entry : playerFleetFiles.entrySet()) {
      new File(tmpStarRoot + "_" + entry.getKey()).delete();
      new File(tmpFleetRoot + "_" + entry.getKey()).delete();
    }
    new File("tmp_figure_" + root + ".gnu").delete();
    new File(tmpStarRoot + "_none").delete();
    new File(tmpFleetRoot + "_none").delete();
  }

  public Game getGameData() {
    return gameData;
  }
}
