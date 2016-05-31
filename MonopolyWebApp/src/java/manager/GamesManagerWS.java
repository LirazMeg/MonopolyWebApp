/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.Player;
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

    public void createGame(int computerizedPlayers, int humanPlayers, java.lang.String name) throws ws.monopoly.DuplicateGameName_Exception, ws.monopoly.InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public ws.monopoly.GameDetails getGameDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.util.List<java.lang.String> getWaitingGames() {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public int joinGame(java.lang.String gameName, java.lang.String playerName) throws ws.monopoly.GameDoesNotExists_Exception, ws.monopoly.InvalidParameters_Exception {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
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
