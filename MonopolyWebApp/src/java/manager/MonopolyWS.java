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
import java.util.TimerTask;
import javax.xml.bind.JAXBException;
import models.MonopolyModel;
import models.Player;
import ws.monopoly.Event;
import ws.monopoly.EventType;
import ws.monopoly.GameDetails;
import ws.monopoly.GameStatus;
import ws.monopoly.PlayerDetails;

/**
 *
 * @author Liraz
 */
class MonopolyWS {

    private static final String CUMPUTER_PLAYER = "CumputerPlayer";
    private MonopolyGame spesificGame;
    private boolean isFirstGamePlayed;
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
        // if the eventId contain n the list.size
        if (isContain) {
            resEventsList = this.events.subList(eventId, this.events.size());
        }
        if (eventId == ZERO) {
            resEventsList = this.events;
        }
        return resEventsList;
    }

    public void setGameDetails(GameStatus status) {
        details.setName(spesificGame.getGameName());
        details.setStatus(status);
        details.setHumanPlayers(spesificGame.getHumanPlayers());
        details.setComputerizedPlayers(spesificGame.getComputerizedPlayers());
        details.setJoinedHumanPlayers(spesificGame.getJoinNumber());
    }

    public void createGame(int computerizedPlayers, int humanPlayers, String name) {
        spesificGame.createGameWS(computerizedPlayers, humanPlayers, ZERO, name);
        setGameDetails(GameStatus.WAITING);
    }

    public void addEvents(EventType type, String playerName) {
        events.add(UtilitiesWS.createEvent(events.size(), type, playerName));
    }

    public GameDetails getGameDetails() {
        return this.details;
    }

    public boolean isGameWait() {
        return details.getStatus().equals(GameStatus.WAITING);
    }

    boolean isPlayerNameExist(String playerName) {

        boolean isExsist = false;
        for (Player currPlayer : this.spesificGame.getPlayers()) {
            if (currPlayer.getName().equals(playerName)) {
                isExsist = true;
                break;
            }
        }
        return isExsist;
    }

    Player addPlayerToGame(String playerName, boolean isHumen) {
        Player res = spesificGame.addPlayerToGame(playerName, isHumen);
        setGameDetails(GameStatus.WAITING);

        if (isGameFull()) {
            for (int i = 0; i < details.getComputerizedPlayers(); i++) {
                spesificGame.addPlayerToGame(CUMPUTER_PLAYER + (i + 1), false);
            }

            setGameDetails(GameStatus.ACTIVE);

            events.clear();
            initNewGame();
            initCurrentPlayerInSpecificGame();
            addEvents(EventType.GAME_START, spesificGame.getCurrentPlayer().getName());
            addEvents(EventType.PLAYER_TURN, spesificGame.getCurrentPlayer().getName());
            timing();
        }

        return res;
    }

    public boolean isGameFull() {
        return details.getJoinedHumanPlayers() == details.getHumanPlayers();
    }

    private void initNewGame() {
        this.isFirstGamePlayed = true;
        this.spesificGame.setNumOfPlayers(ZERO);
    }

    private void initCurrentPlayerInSpecificGame() {
        spesificGame.setCurrentPlayer(this.spesificGame.getPlayers().get(this.spesificGame.getPleyerIndex()));
    }

    private void timing() {
        this.timer.cancel();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                actionMethod();
            }
        }, TIME, TIME);
    }

    private void actionMethod() {
        spesificGame.getCurrentPlayer().setResign(true);

        removePlayerThatResignFromList();
        // sendToClient("Clear");
    }

    //todo
    public void removePlayerThatResignFromList() {

        int lastIndex = this.spesificGame.getPleyerIndex();
        if (getGameDetails().getStatus().equals(GameStatus.WAITING)) {
            spesificGame.removePlayerThatResignFromList();
        } else {
            addEvents(EventType.PLAYER_RESIGNED, spesificGame.getPlayer(this.spesificGame.getPleyerIndex()).getName());

//            if (doComputerIterations()) {
//                addEvents(EventType.GAME_WINNER, ZERO, spesificGame.getWinnerName());
//                addEvents(EventType.GAME_OVER, ZERO, spesificGame.getPlayer(lastIndex));
//            } else {
//                spesificGame.removeResignFromList();
//                if (currentPlayerNumber != Boots.ZERO) {
//                    currentPlayerNumber = (lastIndex) % spesificGame.getPlayersListSize();
//                }
//            }
            //     spesificGame.clearSavedReturnTiles();
        }

    }

    public List<PlayerDetails> getPlayersDetailsWS() {
        List<PlayerDetails> detailsList = new ArrayList<>();
        PlayerDetails res;

        for (Player currentPlayer : spesificGame.getPlayers()) {
            res = UtilitiesWS.getPlayerDetails(currentPlayer);
            detailsList.add(res);
        }
        return detailsList;
    }

}
