/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

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

    private MonopolyModel monopolyGame = null;
    private List<Player> players;
    private Player currentPlayer;
    private int pleyerIndex;
    private FileManager filesManager;
    final static int NUM_START_SQUARE = 1;
    final static int ONE = 1;
    boolean IsGameOver;
    private int numOfPlayers = 0;
    private int numOfHumanPlayers = 0;

    private String gameName;

    public MonopolyGame() throws JAXBException, FileNotFoundException, Exception {
        this.filesManager = new FileManager("monopoly_config", false, false);
        initiolaize();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
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

//    void run() throws Exception {
//        //  ConsolUI.msgNextTurnPlayer(this.currentPlayer.getName());
//        //showCurrentLocionOfPlayerOnBoard(true);
//        while (!this.IsGameOver) { // while the game is going - more ten one player
//
//            showCurrentLocionOfPlayerOnBoard(true);
//            if (!this.currentPlayer.isQuit()) {
//
//                if (!isPlayerInParking()) { // if current player standing on parking square
//                    int[] diecResult = rollTheDice();
//                    ConsolUI.showDiceResult(diecResult);
//                    if (this.currentPlayer.isInJail()) {// if player in jail
//                        if ((diecResult[0] == diecResult[1])) { // if the dice reaut is double
//                            this.currentPlayer.setIsInJail(false); // update
//                            //current player go out from jail;
//                            ConsolUI.msgPlayerGoOutFromJail();
//                        } else if (this.currentPlayer.isIsHaveGetOutOfJailCard()) {
//                            currentPlayerHaveGetOutCard();
//                            //if the current play have - get out of jail card te can play now
//                            makeMove(diecResult[0] + diecResult[1], true); // and make movment on player + smartBoard
//                        } else {// current player that in jail dosnt get doubel or dosent have get out of jail card
//                            ConsolUI.msgPlayerWillStyInJailOneMoreTurn();
//                        }
//                    } else {//player is not in jail
//                        makeMove(diecResult[0] + diecResult[1], true);
//                    }
//                } else { // player in parking
//                    ConsolUI.msgPlayerInParking();
//                }
//            }
//            handelPlayerPresence(this.currentPlayer); // cheacke if player is still in the game - or remove player from game list
//
//            nextPlayerTurn();// continue to next player
//            IsGameOver = checkIfIsGameOver();
//        }
//        if (IsGameOver) {
//            ConsolUI.showWinner(this.currentPlayer.getName());
//            if (ConsolUI.msgAnotherGame()) {
//                if (ConsolUI.msgUpLoadFile()) {
//                    String newXmlFile = ConsolUI.loadDest();
//                    this.filesController = new FileController(newXmlFile, true, false);
//                }
//                initiolaize();
//            }
//            ConsolUI.msgGoodBy();
//            exit(0);
//        }
//    }
//    private void showCurrentLocionOfPlayerOnBoard(boolean isStartOfTurn) throws Exception {
//        int numOfCurrentSquare = this.currentPlayer.getSqureNum();
//        SquareBase currentSqureForPlayer = this.monopolyGame.getBoard().getSqureBaseBySqureNum(numOfCurrentSquare);
//        String squaerStr = currentSqureForPlayer.toString();
//        String currentPlayerName = this.currentPlayer.getName();
//
//        if (currentSqureForPlayer.getClass().equals(models.SquareType.class)) {
//            SquareType squareType = (SquareType) currentSqureForPlayer;
//            switch (squareType.getType()) {
//                case CITY:
//                case UTILITY:
//                case TRANSPORTATION:
//                    squaerStr = squaerStr + " " + squareType.getAsset().getName();
//                    break;
//            }
//        }
//        if (isStartOfTurn) {
//            squaerStr = squaerStr + " ,Roll the dice.... (Press Enter To Continue Or 'E' To Quit Game)";
//            ConsolUI.showCurrentLocationOfPlayer(numOfCurrentSquare, squaerStr, currentPlayerName);
//            this.currentPlayer.setIsNeedToQuit(ConsolUI.continueOrQuit(this.currentPlayer));
//        } else {
//            // ConsolUI.showCurrentLocationOfPlayer(numOfCurrentSquare, squaerStr, currentPlayerName);
//        }
//    }
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

//    public void initPlayers() {
//        ArrayList<String> result;
//        //-------------------------------------------------------------
//        result = ConsolUI.getStartInfo();
//
//        int numPlayers = Integer.parseInt(result.get(0));
//        int numOfHuman = Integer.parseInt(result.get(1));
//        int numOfPlayersNames = 2;
//        //-------------------------------------------------------------
//
//        for (int i = 0; i < numOfHuman; i++) {
//            String name = result.get(numOfPlayersNames);
//            players.add(new HumanPlayer(name));
//            numOfPlayersNames++;
//        }
//
//        int numOfComputerPlayers = numPlayers - numOfHuman;
//        for (int i = 0; i < numOfComputerPlayers; i++) {
//            String name = "Computer player" + (i + 1);
//            players.add(new ComputerPlayer(name));
//        }
//    }
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
        if (this.players.size() == ONE) {
            result = true;
        }
        return result;
    }

    public void handelPlayerPresence(Player player) {
        if (player.isQuit()) {
            // update alll the assetes that the player owned
            monopolyGame.removePlayerrFromTheGame(player);
            this.players.remove(this.pleyerIndex);
            this.pleyerIndex = this.pleyerIndex - 1;
//            ConsolUI.msgPlayerIsOut(player.getName());
        }
    }

//    public void buyingAssetOffer(SquareType square, int squreNum) {// in this case can buy only house
//        boolean canBuy = false;
//        boolean wantToBuy = false;
//        String answer = "";
//
//        switch (square.getType()) {
//            case CITY:
//                CityType citySquar = (CityType) square.getAsset();
//                if (checkIfPlayerCanBuyHouse(square)) {
//                    wantToBuy = currentPlayer.doYouWantToPurchaseHouse(citySquar, citySquar.getHouseCost());
//                    if (wantToBuy) {
//                        buyHouse(citySquar);
//                        citySquar.setCounterOfHouse(citySquar.getCounterOfHouse() + 1);
//                        ConsolUI.tankYouForYourPurchase();
//                    }
//                } else if (checkIfPlayerCanBuyCity(square)) {
//                    wantToBuy = currentPlayer.doYouWantToPurchase(citySquar, citySquar.getCost());
//                    if (wantToBuy) {
//                        buyCity(square);
//                        ConsolUI.tankYouForYourPurchase();
//                    }
//                }
//                break;
//            case UTILITY:
//            case TRANSPORTATION:
//                SimpleAssetType assetSquar = (SimpleAssetType) square.getAsset();
//                wantToBuy = currentPlayer.doYouWantToPurchase(assetSquar, assetSquar.getCost());
//                if (wantToBuy) {
//
//                }
//                break;
//        }
//
//    }
//    public boolean checkIfPlayerCanBuyHouse(SquareType square) {
//        boolean result = false;
//        CityType city = (CityType) square.getAsset();//casting
//        if (currentPlayer.isPlayerHaveTheMany(city.getHouseCost())) {
//            if (monopolyGame.getAssets().checkIfPlayerOwnedTheCountry(city.getCuntryName(), currentPlayer)) {
//                result = true;
//            }
//        }
//        return result;
//    }
//    public void buyHouse(CityType squareCity) {
//        //CityType city = (CityType) squareCity.getAsset();
//        currentPlayer.purchase(squareCity, squareCity.getHouseCost());
//        squareCity.addToCounterOfHouse();
//
//    }
//    public void buyTrnsportionOrUtility(SquareType square, int squareNum) {
//        SquareType squreType = (SquareType) this.monopolyGame.getBoard().getSqureBaseBySqureNum(squareNum);
//        squreType.getAsset().setHaveOwner(this.currentPlayer);
//        currentPlayer.purchase(square.getAsset(), square.getAsset().getCost());
//    }
//    public void actionSurpriseCard(CardBase card) throws Exception {
//        if (card.getClass().equals(models.MonetaryCard.class)) {
//            models.MonetaryCard monoteryCard = (models.MonetaryCard) card;
//        } else if (card.getClass().equals(models.GetOutOfJailCard.class)) {
//            ConsolUI.msgCard(card.getText());
//            this.currentPlayer.setIsHaveGetOutOfJailCard(true, (models.GetOutOfJailCard) card);
//        } else if (card.getClass().equals(models.GotoCard.class)) {
//            ConsolUI.msgCard(card.getText());
//            models.GotoCard gotoCard = (models.GotoCard) card;
//            actionGoToCard(gotoCard.getType());
//        }
//        if (card.getClass().equals(models.GotoCard.class) || card.getClass().equals(models.MonetaryCard.class)) {
//            this.monopolyGame.getSurpries().addCardToSurpriseList(card);
//        }
//    }
//
//    public void actionMonoteryCardFromSurpeiseCards(long sum, MonetaryCard.Who type) {
//        if (type == MonetaryCard.Who.PLAYERS) {
//            substractFromAllPlayersAmount(sum);
//        } else {
//            this.currentPlayer.addToAmount(sum);
//        }
//    }
//    private void substractFromAllPlayersAmount(long sum) {
//        for (Player player : this.players) {
//            if (!player.equals(this.currentPlayer)) {
//                player.pay(this.currentPlayer, sum);
//                handelPlayerPresence(player);
//            }
//        }
//    }
//    public int actionGoToCard(GotoCard.To type) throws Exception {
//        boolean isCanPasByStart = true;
//        int numOfSteps = 0;
//        int numNextOfQquare = 0;
//        if (type.equals(models.GotoCard.To.START)) {
//            numOfSteps = this.monopolyGame.getBoard().getNumberOfStepstToSquareByType(
//                    this.currentPlayer.getSqureNum(), new models.StartSquare().toString());
//        } else if (type.equals(models.GotoCard.To.NEXT_SURPRISE)) {
//            numOfSteps = this.monopolyGame.getBoard().getNumberOfStepstToSquareByType(
//                    this.currentPlayer.getSqureNum(), new models.SquareType("SURPRISE").toString());
//        } else if (type.equals(models.GotoCard.To.JAIL)) {
//            isCanPasByStart = false;
//            numOfSteps = this.monopolyGame.getBoard().getNumberOfStepstToSquareByType(
//                    this.currentPlayer.getSqureNum(), new models.JailSlashFreeSpaceSquareType().toString());
//
//        } else if (type.equals(models.GotoCard.To.NEXT_WARRANT)) {
//            isCanPasByStart = false;
//            numOfSteps = this.monopolyGame.getBoard().getNumberOfStepstToSquareByType(
//                    this.currentPlayer.getSqureNum(), new models.SquareType("WARRANT").toString());
//        }
//        //   makeMove(numOfSteps, isCanPasByStart);
//        return numOfSteps;
//    }
//
//    private void actionWarrantCard(CardBase card) throws Exception {
//        if (card.getClass().equals(models.MonetaryCard.class)) {
//            models.MonetaryCard monoteryCard = (models.MonetaryCard) card;
//            ConsolUI.msgMonoteryCard(monoteryCard.getText(), monoteryCard.getSum());
//            actionMonoteryCardFromWarrantCards(monoteryCard.getSum(), monoteryCard.getType());
//        } else if (card.getClass().equals(models.GotoCard.class)) {
//            models.GotoCard gotoCard = (models.GotoCard) card;
//        }
//        this.monopolyGame.getWarrant().addCardToWarrantCardList(card);
//    }
//
//    public void actionMonoteryCardFromWarrantCards(long sum, MonetaryCard.Who type) {
//        long totalSubstract = 0;
//        if (type.equals(MonetaryCard.Who.TREASURY)) {
//            this.currentPlayer.payToTreasury(sum);
//        } else if (type.equals(MonetaryCard.Who.PLAYERS)) {
//            payToAllPlayers(sum);
//        }
//    }
//
//    public boolean checkIfPlayerCanBuyCity(SquareType square) {
//        boolean canBuyCity = false;
//        CityType city = (CityType) square.getAsset();
//        if (!city.doYouHaveOwner()) {
//            canBuyCity = true;
//        }
//        return canBuyCity;
//    }
//
//    public void buyCity(SquareType square) {
//        CityType city = (CityType) square.getAsset();
//        currentPlayer.purchase(city, city.getCost());
//        city.setHaveOwner(currentPlayer);
//    }
//
//    private void payToAllPlayers(long sum) {
//        for (Player player : this.players) {
//            if (!player.equals(this.currentPlayer)) {
//                this.currentPlayer.pay(player, sum);
//            }
//        }
//    }
//
//    public long getStayCostForAsset(SquareType square) {
//        long stayCost = square.getAsset().getStaycost();
//        if (square.getType().equals(SquareType.Type.TRANSPORTATION)) {
//            if (isCurrentPlayerOwneAllTransportioes(square.getAsset().getOwner())) {
//                stayCost = this.monopolyGame.getAssets().getTransportations().getStayCost();
//            }
//        } else if (square.getType().equals(SquareType.Type.UTILITY)) {
//            if (isCurrentPlayerOwneAllUtilities(square.getAsset().getOwner())) {
//                stayCost = this.monopolyGame.getAssets().getUtilities().getStayCost();
//            }
//        }
//        return stayCost;
//    }
//
//    private boolean isCurrentPlayerOwneAllTransportioes(Player owner) {
//        int numOfTransportionForPleyer = 0;
//        int numberOfTransportiones = this.monopolyGame.getAssets().getTransportations().getTransport().size();
//        boolean result = false;
//
//        ArrayList<AssetType> playerAssets = owner.getMyAssets();
//        List<SimpleAssetType> transportionAssets = this.monopolyGame.getAssets().getTransportations().getTransport();
//
//        for (AssetType playerAsset : playerAssets) {
//            if (playerAsset.getClass().equals(SimpleAssetType.class)) {// if the player asset is SimpeleAssetType
//                SimpleAssetType simpeleAssetPlayer = (SimpleAssetType) playerAsset;// do castimg
//                for (SimpleAssetType simpelAsset : transportionAssets) {// chech if the simpel asset is a transportion
//                    if (simpelAsset.equals(simpeleAssetPlayer)) {
//                        numOfTransportionForPleyer++;;
//                    }
//                }
//            }
//
//        }
//        if (numOfTransportionForPleyer == numberOfTransportiones) {
//            result = true;
//        }
//        return result;
//    }
//
//    private boolean isCurrentPlayerOwneAllUtilities(Player owner) {
//        int numOfUtilitisForPleyer = 0;
//        int numberOfUtilitis = this.monopolyGame.getAssets().getUtilities().getUtility().size();
//        boolean result = false;
//
//        ArrayList<AssetType> playerAssets = owner.getMyAssets();
//        List<SimpleAssetType> utilitisAssets = this.monopolyGame.getAssets().getUtilities().getUtility();
//
//        for (AssetType playerAsset : playerAssets) {
//            if (playerAsset.getClass().equals(SimpleAssetType.class)) {// if the player asset is SimpeleAssetType
//                SimpleAssetType simpeleAssetPlayer = (SimpleAssetType) playerAsset;// do castimg
//                for (SimpleAssetType simpelAsset : utilitisAssets) {// chech if the simpel asset is a transportion
//                    if (simpelAsset.equals(simpeleAssetPlayer)) {
//                        numOfUtilitisForPleyer++;;
//                    }
//                }
//            }
//
//        }
//        if (numOfUtilitisForPleyer == numberOfUtilitis) {
//            result = true;
//        }
//        return result;
//    }
//
//    public void currentPlayerHaveGetOutCard() {
//        this.currentPlayer.setIsInJail(false); // update
//        this.monopolyGame.getSurpries().addCardToSurpriseList(this.currentPlayer.getCardGetOutFromJail());
//        this.currentPlayer.setIsHaveGetOutOfJailCard(false, null);
//    }
//
//    public String getWinnerName() {
//
//        String name = this.players.get(0).getName();
//        long winnerAmount = this.players.get(0).getAmount();
//
//        for (Player winner : this.players) {
//
//            if (winner.getAmount() > winnerAmount) {
//                winnerAmount = winner.getAmount();
//                name = winner.getName();
//            }
//
//        }
//        return name;
//    }
    String getGameName() {
        return this.gameName;
    }
}
