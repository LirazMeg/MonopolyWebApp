/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.ws;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import manager.GamesManagerWS;
import ws.monopoly.DuplicateGameName_Exception;
import ws.monopoly.GameDoesNotExists_Exception;
import ws.monopoly.InvalidParameters_Exception;

/**
 *
 * @author Liraz
 */
@WebService(serviceName = "MonopolyWebServiceService", portName = "MonopolyWebServicePort", endpointInterface = "ws.monopoly.MonopolyWebService", targetNamespace = "http://monopoly.ws/", wsdlLocation = "WEB-INF/wsdl/MonopolyGameWS/MonopolyWebServiceService.wsdl")
public class MonopolyGameWS {

    GamesManagerWS manager = new GamesManagerWS();

    public java.util.List<ws.monopoly.Event> getEvents(int eventId, int playerId) throws InvalidParameters_Exception {
        return this.manager.getEvents(eventId, playerId);
    }

    public java.util.List<ws.monopoly.PlayerDetails> getPlayersDetails(java.lang.String gameName) throws GameDoesNotExists_Exception {
        return this.manager.getPlayersDetails(gameName);
    }

    public java.util.List<java.lang.String> getWaitingGames() {
        return this.manager.getWaitingGames();
    }

    public void createGame(int computerizedPlayers, int humanPlayers, java.lang.String name) throws InvalidParameters_Exception, DuplicateGameName_Exception {
        this.manager.createGame(computerizedPlayers, humanPlayers, name);
    }

    public void resign(int playerId) throws InvalidParameters_Exception {
        try {
            this.manager.resign(playerId);
        } catch (Exception ex) {
            Logger.getLogger(MonopolyGameWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int joinGame(java.lang.String gameName, java.lang.String playerName) throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        return this.manager.joinGame(gameName, playerName);
    }

    public java.lang.String getBoardXML() {
        return this.manager.getBoardXML();
    }

    public ws.monopoly.PlayerDetails getPlayerDetails(int playerId) throws InvalidParameters_Exception, GameDoesNotExists_Exception {
        return this.manager.getPlayerDetails(playerId);
    }

    public java.lang.String getBoardSchema() {
        return this.manager.getBoardSchema();
    }

    public ws.monopoly.GameDetails getGameDetails(java.lang.String gameName) throws GameDoesNotExists_Exception {
        return this.manager.getGameDetails(gameName);
    }

    public void buy(int arg0, int arg1, boolean arg2) throws InvalidParameters_Exception {
        this.manager.buy(arg0, arg1, arg2);
    }

}
