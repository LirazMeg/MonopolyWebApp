/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Player;
import ws.monopoly.DuplicateGameName_Exception;
import ws.monopoly.GameDoesNotExists_Exception;
import ws.monopoly.InvalidParameters_Exception;

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

        if (playersIdAsGameName.containsKey(playerId)) {
            gameName = playersIdAsGameName.get(playerId);
            if (gamesContainer.containsKey(gameName)) {
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
        return "monopoly_config";
    }

    public java.lang.String getBoardXML() {
        return "monopoly_config";
    }

    public void createGame(int computerizedPlayers, int humanPlayers, java.lang.String name){
        MonopolyWS currentGame;
        try {
            if (gamesContainer.containsKey(name)) {
                throw new DuplicateGameName_Exception("Game with same name allready exists.", null);
            } else if (name.equals("")) {
                throw new InvalidParameters_Exception("Invalid input(name)", null);
            } else {
                currentGame = new MonopolyWS();
                currentGame.createGame(computerizedPlayers, humanPlayers, name);
                gamesContainer.put(name, currentGame);
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
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void buy(int arg0, int arg1, boolean arg2) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void resign(int playerId) throws ws.monopoly.InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.util.List<ws.monopoly.PlayerDetails> getPlayersDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
