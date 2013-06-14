package com.bpodgursky.hubris.universe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class GameState {

  public final Map<Integer, Player> playersByID;


  private final Map<Integer, Fleet> fleetsByID;
  private final Map<String, Fleet> fleetsByName;

  public final Alliance alliance;
  public final Game gameData;
  private final int playerId;

  private static class StarsRecord {

    public final Map<Integer, Star> starsByID;
    public final Map<String, Star> starsByName = Maps.newHashMap();


    private StarsRecord(Map<Integer, Star> starsByID) {
      this.starsByID = starsByID;

      for(Map.Entry<Integer, Star> entry: starsByID.entrySet()){
        starsByName.put(entry.getValue().getName(), entry.getValue());
      }
    }

    public Star get(String name){
      return starsByName.get(name);
    }

    public Star get(int id){
      return starsByID.get(id);
    }

    public Collection<Star> getAllStars(){
      return starsByID.values();
    }
  }

  private final StarsRecord currentStarData;
  private final StarsRecord historicStarData;

  public GameState(GameState previousState,
                   Game gameData,
                   Collection<Player> players,
                   Collection<Star> stars,
                   Collection<Fleet> fleets,
                   Alliance alliance, int playerId) {

    Map<Integer, Star> currentStars = Maps.newHashMap();
    Map<Integer, Star> historicStars = Maps.newHashMap();

    this.playersByID = Maps.newHashMap();
    for (Player p : players) {
      playersByID.put(p.getId(), p);
    }

    if(previousState != null){
      for(Star s: previousState.getAllStars(true)){
        historicStars.put(s.getId(), s);
      }
    }

    for(Star s: stars){
      currentStars.put(s.getId(), s);

      Star merged = merge(s, historicStars.get(s.getId()));

      historicStars.put(merged.getId(), merged);

    }

    currentStarData = new StarsRecord(currentStars);
    historicStarData = new StarsRecord(historicStars);

    this.fleetsByID = Maps.newHashMap();
    this.fleetsByName = Maps.newHashMap();

    for (Fleet f : fleets) {
      fleetsByID.put(f.id, f);
      fleetsByName.put(f.getName(), f);
    }

    this.alliance = alliance;
    this.gameData = gameData;
    this.playerId = playerId;
  }

  public List<Fleet> getAllFleets() {
    return Lists.newArrayList(fleetsByID.values());
  }

  public int getPlayerId(){
    return playerId;
  }

  public Fleet getFleet(int fleetId) {
    return fleetsByID.get(fleetId);
  }

  public Fleet getFleet(String name){
    return fleetsByName.get(name);
  }

  public boolean starIsVisible(int starId) {
    return starIsVisible(getStar(starId, false));
  }

  public static boolean starIsVisible(Star star){
    return star.getResources() != null;
  }

  public Star getStar(String starName, boolean useHistoric){
    if(useHistoric){
      return historicStarData.get(starName);
    }
    else{
      return currentStarData.get(starName);
    }
  }

  public Star getStar(int starId, boolean useHistoric){
    if(useHistoric){
      return historicStarData.get(starId);
    }
    else{
      return currentStarData.get(starId);
    }
  }

  public List<Player> getAllPlayers(){
    return Lists.newArrayList(playersByID.values());
  }

  public Collection<Star> getAllStars(boolean useHistoric){
    if(useHistoric){
      return historicStarData.getAllStars();
    }else{
      return currentStarData.getAllStars();
    }
  }

  public Player getPlayer(int playerId) {
    return playersByID.get(playerId);
  }

  private boolean equal(Integer int1, Integer int2){
    return int1 == null && int2 == null || int1 != null && int2 != null && int1.intValue() == int2.intValue();
  }

  private Star merge(Star current, Star lastVisible){

    if(lastVisible == null){
      return current;
    }

    if(starIsVisible(current)){
      return current;
    }

    Integer playerVisible = current.getPlayerNumber();
    Integer playerLast = lastVisible.getPlayerNumber();

    //  if the same player controls it, last state is the best guess
    if(equal(playerVisible, playerLast)){
      return lastVisible;
    } else {
      return new Star(current.getName(), current.getPlayerNumber(),
          null, null, null,
          lastVisible.getIndustry(), lastVisible.getIndustryUpgrade(),
          lastVisible.getScience(), lastVisible.getScienceUpgrade(),
          current.getId(),current.getCoords(), null, current.getResources(), Sets.<Integer>newHashSet());

    }
  }


  public String toString() {
    return new Gson().toJson(this);
  }

  public Map<Integer, Player> getPlayers(){
    return playersByID;
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

    for (Star s : currentStarData.getAllStars()) {
      String label = (s.ships == null ? "" : s.ships + "-") + "[" +
          (s.economy == null ? "" : s.economy + ",") +
          (s.industry == null ? "" : s.industry + ",") +
          (s.science == null ? "" : s.science) + "]" +
          (s.resources == null ? "" : "-" + s.resources);

      Coordinate coords = s.getCoords();

      playerStarFiles.get(s.playerNumber).append(coords.getX() + "\t" + (-coords.getY()) + "\t" + label + "\n");
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
