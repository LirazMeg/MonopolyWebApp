/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.ws;

import game.manager.GamesManagerWS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;

/**
 *
 * @author Liraz
 */
@WebService(serviceName = "MonopolyWebServiceService", portName = "MonopolyWebServicePort", endpointInterface = "ws.monopoly.MonopolyWebService", targetNamespace = "http://monopoly.ws/", wsdlLocation = "WEB-INF/wsdl/gameWS/MonopolyWebServiceService.wsdl")
public class gameWS {

    private GamesManagerWS manger = new GamesManagerWS();

    public java.util.List<ws.monopoly.Event> getEvents(int eventId, int playerId) throws ws.monopoly.InvalidParameters_Exception {
        return this.manger.getEvents(eventId, playerId);
    }

    public java.util.List<ws.monopoly.PlayerDetails> getPlayersDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        return this.manger.getPlayersDetails(gameName);
    }

    public java.util.List<java.lang.String> getWaitingGames() {
        return this.manger.getWaitingGames();
    }

    public void createGame(int computerizedPlayers, int humanPlayers, java.lang.String name) throws ws.monopoly.InvalidParameters_Exception, ws.monopoly.DuplicateGameName_Exception {
        this.manger.createGame(computerizedPlayers, humanPlayers, name);
    }

    public void resign(int playerId) throws ws.monopoly.InvalidParameters_Exception {
        try {
            this.manger.resign(playerId);
        } catch (Exception ex) {
            Logger.getLogger(gameWS.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    public int joinGame(java.lang.String gameName, java.lang.String playerName) throws ws.monopoly.GameDoesNotExists_Exception, ws.monopoly.InvalidParameters_Exception {
        return this.manger.joinGame(gameName, playerName);
    }

    public java.lang.String getBoardXML() {
        return this.manger.getBoardXML();
    }

    public ws.monopoly.PlayerDetails getPlayerDetails(int playerId) throws ws.monopoly.GameDoesNotExists_Exception, ws.monopoly.InvalidParameters_Exception {
        return this.getPlayerDetails(playerId);
    }

    public java.lang.String getBoardSchema() {
        return this.manger.getBoardSchema();
    }

    public ws.monopoly.GameDetails getGameDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        return this.manger.getGameDetails(gameName);
    }

    public void buy(int arg0, int arg1, boolean arg2) throws ws.monopoly.InvalidParameters_Exception {
        this.manger.buy(arg1, arg1, arg2);
    }

}
