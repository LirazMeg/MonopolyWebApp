/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.manager;

import game.wsService.UtilitiesWS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Player;
import ws.monopoly.DuplicateGameName_Exception;
import ws.monopoly.Event;
import ws.monopoly.GameDoesNotExists_Exception;
import ws.monopoly.InvalidParameters_Exception;
import ws.monopoly.PlayerDetails;

/**
 *
 * @author Liraz
 */
public class GamesManagerWS {

    private final HashMap<String, MonopolyWS> gamesContainer = new HashMap<>();
    private final HashMap<Integer, Player> playersContainer = new HashMap<>();
    private final HashMap<Integer, String> playersIdAsGameName = new HashMap<>();
    Integer idPlayerCounter = 0;

    public java.util.List<ws.monopoly.Event> getEvents(int eventId, int playerId) throws ws.monopoly.InvalidParameters_Exception {
        String gameName;
        List<ws.monopoly.Event> events = new ArrayList<>();

        if (playersIdAsGameName.containsKey(playerId)) {//if the player exist
            gameName = playersIdAsGameName.get(playerId);
            if (gamesContainer.containsKey(gameName)) {// if the game eist
                events = gamesContainer.get(gameName).getEvents(eventId);
            } else {
                throw new InvalidParameters_Exception("Game: " + gameName + " is not exist .", null);
            }
        } else {
            throw new InvalidParameters_Exception("PlayerId: " + playerId + " is not exist .", null);
        }

        return events;
    }

    public java.lang.String getBoardSchema() {
        BufferedReader br = null;
        try {
            String xmlInput = "/resources/monopoly_config.xsd";
            InputStream xmlInputStream = GamesManagerWS.class.getResourceAsStream(xmlInput);
            br = new BufferedReader(new FileReader(new File(xmlInputStream.toString())));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GamesManagerWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GamesManagerWS.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(GamesManagerWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return br.toString();
    }

   public java.lang.String getBoardXML() {
        BufferedReader br = null;
        try {
            //String xmlFile = "/" + RESOURCES + "/monopoly_config.xml";
            URL url = GamesManagerWS.class.getResource("/resources/monopoly_config.xml");
            File input = new File(url.getPath());
            FileReader dir = new FileReader(input);
            br = new BufferedReader(dir);
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GamesManagerWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GamesManagerWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return br.toString();
    }

    public void createGame(int computerizedPlayers, int humanPlayers, java.lang.String name) {
        MonopolyWS currentGame;
        try {
            if (gamesContainer.containsKey(name)) {
                throw new DuplicateGameName_Exception("Game with same name allready exists.", null);
            } else if (name != null && "".equals(name)) {
                throw new InvalidParameters_Exception("Invalid input(name)", null);
            } else {
                currentGame = new MonopolyWS();
                currentGame.createGame(computerizedPlayers, humanPlayers, name);
                gamesContainer.put(name, currentGame);// add game to continer 
                System.out.println("Game Inserted");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + e.getStackTrace().toString());
        }

    }

    public ws.monopoly.GameDetails getGameDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        if (!gamesContainer.containsKey(gameName)) {
            throw new GameDoesNotExists_Exception("Game not exists.", null);
        } else {
            return gamesContainer.get(gameName).getGameDetails();
        }
    }

    public java.util.List<java.lang.String> getWaitingGames() {
        List<String> waitingGames = new ArrayList<>();
        for (Map.Entry<String, MonopolyWS> entry : gamesContainer.entrySet()) {
            if (entry.getValue().isGameWait()) {
                waitingGames.add(entry.getValue().getGameDetails().getName());
            }
        }
        return waitingGames;
    }

    public int joinGame(java.lang.String gameName, java.lang.String playerName) throws ws.monopoly.GameDoesNotExists_Exception, ws.monopoly.InvalidParameters_Exception {
        int resKeyID;
        MonopolyWS currentGame;

        if (!gamesContainer.containsKey(gameName)) {
            throw new GameDoesNotExists_Exception("Game is not exists.", null);
        } else {
            currentGame = gamesContainer.get(gameName);
            if (!currentGame.isGameWait()) {
                throw new InvalidParameters_Exception("Game status isn't wait.", null);
            } else if (currentGame.isPlayerNameExist(playerName)) {
                throw new InvalidParameters_Exception("The name " + playerName + " is already exist.", null);
            } else {
                playersContainer.put(idPlayerCounter, currentGame.addPlayerToGame(playerName, true));
                resKeyID = idPlayerCounter;
                playersIdAsGameName.put(resKeyID, gameName);
                idPlayerCounter++;
            }
        }

        return resKeyID;
    }

    public ws.monopoly.PlayerDetails getPlayerDetails(int playerId) throws ws.monopoly.InvalidParameters_Exception, ws.monopoly.GameDoesNotExists_Exception {
        PlayerDetails playerDetails;
        Player currPlayer;

        if (!playersContainer.containsKey(playerId)) {
            throw new GameDoesNotExists_Exception("Player is not exists.", null);
        } else {
            currPlayer = playersContainer.get(playerId);
            playerDetails = UtilitiesWS.getPlayerDetails(currPlayer);

        }
        return playerDetails;

    }

    public void buy(int playerID, int eventID, boolean buy) throws InvalidParameters_Exception {
        try {
            String gameName;
            MonopolyWS currGame;
            if (buy) {
                if (!this.playersContainer.containsKey(playerID)) {
                    throw new InvalidParameters_Exception("Player is not exists.", null);
                } else if (!this.playersIdAsGameName.containsKey(playerID)) {
                    throw new InvalidParameters_Exception("Game is not exists.", null);
                } else if (!this.gamesContainer.containsKey(this.playersIdAsGameName.get(playerID))) {
                    throw new InvalidParameters_Exception("Game is not exists.", null);
                } else {
                    gameName = this.playersIdAsGameName.get(playerID);
                    currGame = this.gamesContainer.get(gameName);
                    Player currPlayer = this.playersContainer.get(playerID);
                    int sizeOfEvents = currGame.getEventListSize();
                    
                    if (eventID > sizeOfEvents && eventID < 0) {
                        throw new InvalidParameters_Exception("Iligal event.", null);
                    } else {
                        List<Event> events = currGame.getEvents(eventID-1);
                        if (events.size() != 1) {
                            throw new InvalidParameters_Exception("Iligal event.", null);
                        } else {
                            currGame.buy(currPlayer, events.get(0));
                        }
                    }
                }
                // dosnt want to buy
            } else {
                //do nothing
            }
            
            gameName = this.playersIdAsGameName.get(playerID);
            currGame = this.gamesContainer.get(gameName);
            currGame.doIterion();
        } catch (Exception ex) {
            Logger.getLogger(GamesManagerWS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void resign(int playerId) throws ws.monopoly.InvalidParameters_Exception, Exception {
        String gameName;
        MonopolyWS currentGame;
        Player currentPlayer;
        if (!this.playersContainer.containsKey(playerId)) {
            throw new InvalidParameters_Exception("Player is not exists.", null);
        } else {
            currentPlayer = this.playersContainer.get(playerId);
            currentPlayer.setResign(true);
            gameName = this.playersIdAsGameName.get(playerId);
            currentGame = this.gamesContainer.get(gameName);
            currentGame.removePlayerThatResignFromList();
        }
    }

    public java.util.List<ws.monopoly.PlayerDetails> getPlayersDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        List<PlayerDetails> detailsList;
        MonopolyWS currGame;
        if (!gamesContainer.containsKey(gameName)) {
            throw new GameDoesNotExists_Exception("Game is not exists", null);
        } else {
            currGame = gamesContainer.get(gameName);
            detailsList = currGame.getPlayersDetailsWS();
        }
        return detailsList;
    }

}
