/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import game.wsService.UtilitiesWS;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import javax.xml.bind.JAXBException;
import models.MonopolyModel;
import ws.monopoly.Event;
import ws.monopoly.EventType;
import ws.monopoly.GameDetails;
import ws.monopoly.GameStatus;

/**
 *
 * @author Liraz
 */
class MonopolyWS {

    private MonopolyGame spesificGame;
    private List<Event> events;
    private GameDetails details;
    private Timer timer;
    public static final int TIME = 60000;

    public static final int ZERO = 0;

    public MonopolyWS() throws JAXBException, FileNotFoundException, Exception {
        this.spesificGame = new MonopolyGame();
        this.events = new ArrayList<>();
        this.details = new GameDetails();
        this.timer = new Timer();

    }

    public List<Event> getEvents(int eventId) {
        List<Event> resEventsList = new ArrayList<>();
        boolean isContain = eventId >= 0 && eventId <= this.events.size();

        if (isContain) {
            resEventsList = this.events.subList(eventId, this.events.size());
        }

        if (eventId == ZERO) {
            resEventsList = this.events;
        }

        return resEventsList;
    }

    public void setGameDetails(GameStatus status, boolean isLoadedFromXml) {
        details.setName(spesificGame.getGameName());
        details.setStatus(status);
        details.setHumanPlayers(spesificGame.getHumanPlayers());
        details.setComputerizedPlayers(spesificGame.getComputerizedPlayers());
        details.setJoinedHumanPlayers(spesificGame.getJoinNumber());
    }

    public void createGame(int computerizedPlayers, int humanPlayers, String name) {
        spesificGame.createGameWS(computerizedPlayers, humanPlayers, ZERO, name);
        setGameDetails(GameStatus.WAITING, false);
    }

    public void addEvents(EventType type, int value, String playerName) {
        events.add(UtilitiesWS.createEvent(events.size(), type, value, playerName));
    }

    public GameDetails getGameDetails() {
        return this.details;
    }

    public boolean isGameWait() {
        return details.getStatus().equals(GameStatus.WAITING);
    }
}
