/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.manager;

import java.io.FileNotFoundException;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import models.AssetType;
import models.CardBase;
import models.CityType;
import models.ComputerPlayer;
import models.GotoCard;
import models.HumanPlayer;
import models.MonetaryCard;
import models.MonopolyModel;
import models.Player;
import models.SimpleAssetType;
import models.SquareBase;
import models.SquareType;

/**
 *
 * @author Liraz
 */
public class MonopolyGame {

    final static int NUM_START_SQUARE = 1;
    final static int ONE = 1;
    final static int ZERO = 0;
    private MonopolyModel monopolyGame = null;
    private List<Player> players;
    private Player currentPlayer;
    private int pleyerIndex;
    private FileManager filesManager;
    boolean IsGameOver;
    private int numOfPlayers = 0;
    private int numOfHumanPlayers = 0;

    private String gameName;
    private int joinNumber;
    private int numComputerizedPlayers;

    public MonopolyGame() {
        try {
            this.filesManager = new FileManager("monopoly_config", false, false);
            initiolaize();
        } catch (Exception e) {
            System.out.println(e.getMessage() + e.getStackTrace().toString());
        }
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setPleyerIndex(int pleyerIndex) {
        this.pleyerIndex = pleyerIndex;
    }

    public MonopolyModel getMonopolyGame() {
        return monopolyGame;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public void setNumOfHumanPlayers(int numOfHumanPlayers) {
        this.numOfHumanPlayers = numOfHumanPlayers;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public int getNumOfHumanPlayers() {
        return numOfHumanPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayerToPlayersList(String playerName) {
        players.add(new HumanPlayer(playerName));
    }

    public void addComputerPlayerToPlayersList(String playerName) {
        players.add(new ComputerPlayer(playerName));
    }

    void initiolaize() throws Exception {
        this.pleyerIndex = 0;
        this.players = new ArrayList<>();
        initMonopoly();
        this.monopolyGame.shuffelCards();
    }

    public int getPleyerIndex() {
        return pleyerIndex;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setCurrentPlayerAccordingToIndex(int index) {
        Player currentPlayer = this.getPlayers().get(index);
        this.currentPlayer = currentPlayer;
    }

    public int[] rollTheDice() {
        int[] res = new int[2];
        res[0] = (int) ((Math.random() * (6 - 1 + 1)) + 1);
        res[1] = (int) ((Math.random() * (6 - 1 + 1)) + 1);

        return res;
    }

    private void initMonopoly() throws Exception {
        this.monopolyGame = this.filesManager.initalizeGameFromXMLFile();
    }

    public boolean isPlayerInParking() {
        boolean result = false;
        if (currentPlayer.isInParking()) {
            currentPlayer.setIsInParking(false);
            result = true;
        }
        return result;
    }

    public void nextPlayerTurn() {
        this.pleyerIndex = (this.pleyerIndex + 1) % this.players.size();
        this.currentPlayer = players.get(this.pleyerIndex);
    }

    public void removePlayerThatResignFromList() {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).isResign()) {
                handelPlayerPresence(this.players.get(i));
                joinNumber--;
                break;
            }
        }
    }

//    public void makeMove(int numOfSteps, boolean isCanPasStart) throws Exception {
//        this.currentPlayer.move(numOfSteps, isCanPasStart); //cheng player squreNum
//        //showCurrentLocionOfPlayerOnBoard(false);
//        SquareBase currentSqure = this.monopolyGame.getBoard().getSqureBaseBySqureNum(this.currentPlayer.getSqureNum());
//        currentSqure.stepOnMe(this.currentPlayer); //the square after movment 
//
//        if (currentSqure.getClass().equals(SquareType.class)) {
//            SquareType currentSquareType = (SquareType) currentSqure;
//
//            if (currentPlayer.isDoesPlayerNeedToPay()) {// current player isn't the owner and the squre has a owner
//                //pay to owner
//                long stayCost = getStayCostForAsset(currentSquareType);
//                currentPlayer.pay(currentSquareType.getAsset().getOwner(), stayCost);
//            } else if (currentPlayer.isIsPlayerCanBuySquare()) { //has the option to buy 
//                if (currentPlayer.isPlayerHaveTheMany(currentSquareType.getAsset().getCost())) {
//                    buyingAssetOffer(currentSquareType, this.currentPlayer.getSqureNum());
//                } else {
//                    ConsolUI.msgCantBuy();
//                }
//            } else if (currentPlayer.isIsNeedToTakeSupriesCard()) {
//                actionSurpriseCard(this.monopolyGame.getSurpries().getCard());
//            } else if (currentPlayer.isIsNeedToTakeWarrentCard()) {
//                actionWarrantCard(this.monopolyGame.getWarrant().getCard());
//            }
//            currentPlayer.setUpFlages();
//        }
//    }
    public boolean checkIfIsGameOver() {
        boolean result = false;
        if ((this.joinNumber == ONE && this.numComputerizedPlayers == ZERO) || (this.joinNumber == ZERO)) {
            result = true;
        }
        return result;
    }

    public String getWinnerName() {

        String name = this.players.get(0).getName();
        long winnerAmount = this.players.get(0).getAmount();

        for (Player winner : this.players) {

            if (winner.getAmount() > winnerAmount) {
                winnerAmount = winner.getAmount();
                name = winner.getName();
            }

        }
        return name;
    }

    public void handelPlayerPresence(Player player) {
        if (player.isResign() || player.isQuit()) {
            // update alll the assetes that the player owned
            monopolyGame.removePlayerrFromTheGame(player);
            this.players.remove(this.pleyerIndex);
            this.pleyerIndex = this.pleyerIndex - 1;
            if (player.isHumen()) {
                this.joinNumber--;
            }
        }
    }

    String getGameName() {
        return this.gameName;
    }

    int getHumanPlayers() {
        return this.numOfHumanPlayers;
    }

    int getComputerizedPlayers() {
        return this.numComputerizedPlayers;
    }

    int getJoinNumber() {
        return this.joinNumber;
    }

    void createGameWS(int computerizedPlayers, int humanPlayers, int joinNumber, String name) {
        this.numOfHumanPlayers = humanPlayers;
        this.numComputerizedPlayers = humanPlayers;
        this.joinNumber = joinNumber;
        this.gameName = name;
    }

    public Player addPlayerToGame(String playerName, boolean isHumen) {
        Player playerToAdd = null;
        if (isHumen) {
            playerToAdd = new HumanPlayer(playerName);
            this.joinNumber++;
        } else {
            playerToAdd = new ComputerPlayer(playerName);
        }
        this.players.add(playerToAdd);
        return playerToAdd;
    }

    public Player getPlayer(int index) {
        return this.players.get(index);
    }

  
    
}
