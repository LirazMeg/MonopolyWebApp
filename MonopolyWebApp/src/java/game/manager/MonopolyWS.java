/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.manager;

import game.wsService.UtilitiesWS;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import models.AssetType;
import models.CardBase;
import models.CityType;
import models.GotoCard;
import models.MonetaryCard;
import models.MonopolyModel;
import models.Player;
import models.SimpleAssetType;
import models.SquareBase;
import models.SquareType;
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

    public void addEvents(EventType type, String playerName, int timeoutCount) {
        events.add(UtilitiesWS.createEvent(events.size(), type, playerName, timeoutCount));
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
            //creat all the cumputers players
            for (int i = 0; i < details.getComputerizedPlayers(); i++) {
                spesificGame.addPlayerToGame(CUMPUTER_PLAYER + (i + 1), false);
            }
            setGameDetails(GameStatus.ACTIVE);
            events.clear();
            initNewGame();
            initCurrentPlayerInSpecificGame();
            addEvents(EventType.GAME_START, spesificGame.getCurrentPlayer().getName(), ZERO);
            addEvents(EventType.PLAYER_TURN, spesificGame.getCurrentPlayer().getName(), ZERO);
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

    public List<PlayerDetails> getPlayersDetailsWS() {
        List<PlayerDetails> detailsList = new ArrayList<>();
        PlayerDetails res;

        for (Player currentPlayer : spesificGame.getPlayers()) {
            res = UtilitiesWS.getPlayerDetails(currentPlayer);
            detailsList.add(res);
        }
        return detailsList;
    }

    private void initCurrentPlayerInSpecificGame() {
        spesificGame.setCurrentPlayer(this.spesificGame.getPlayers().get(this.spesificGame.getPleyerIndex()));
    }

    private void timing() {
        this.timer.cancel();
        this.timer = new Timer(true);
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    actionMethod();
                } catch (Exception ex) {
                    Logger.getLogger(MonopolyWS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, TIME, TIME);
    }

    private void actionMethod() throws Exception {
        spesificGame.getCurrentPlayer().setResign(true);
        removePlayerThatResignFromList();
        // sendToClient("Clear");
    }

    //todo
    public void removePlayerThatResignFromList() throws Exception {
        int lastIndex = this.spesificGame.getPleyerIndex();
        if (getGameDetails().getStatus().equals(GameStatus.WAITING)) {
            spesificGame.removePlayerThatResignFromList();
        } else {// in case of active game
            addEvents(EventType.PLAYER_RESIGNED, this.spesificGame.getCurrentPlayer().getName(), ZERO);
            this.spesificGame.nextPlayerTurn();
            if (!this.spesificGame.checkIfIsGameOver()) { // while the game is going - more ten one player
                addEvents(EventType.GAME_WINNER, this.spesificGame.getWinnerName(), ZERO);
                addEvents(EventType.GAME_OVER, this.spesificGame.getCurrentPlayer().getName(), ZERO);
            } else {

                doIterion();

            }

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

    private void doIterion() throws Exception {
        Player currPlayer = this.spesificGame.getCurrentPlayer();
        boolean isGameOver = this.spesificGame.checkIfIsGameOver();

        while (!isGameOver) {
            addEvents(EventType.PLAYER_TURN, currPlayer.getName(), ZERO);

            if (!currPlayer.isInParking()) {
                int[] diecResult = this.spesificGame.rollTheDice();
                addEventsDiseRoll(EventType.DICE_ROLL, currPlayer.getName(), diecResult);
                if (currPlayer.isInJail()) {
                    if ((diecResult[0] == diecResult[1])) { // if the dice reaut is double
                        currPlayer.setIsInJail(false); // update
                        addEventsMove(EventType.MOVE, currPlayer.getName(), currPlayer.getSqureNum(), true, "You Can Get Out From Jail In Your Next Turn!", ZERO);
                    } else if (currPlayer.isIsHaveGetOutOfJailCard()) {
                        addEventsWitheMsg(EventType.PLAYER_USED_GET_OUT_OF_JAIL_CARD, currPlayer.getName(), "You Use Your Get Out Of Jail Card", ZERO);
                        currentPlayerHaveGetOutCard(currPlayer);
                        makeMove(diecResult[0] + diecResult[1], true, currPlayer);
                    } else {
                        addEventsMove(EventType.MOVE, currPlayer.getName(), currPlayer.getSqureNum(), true, "You Can't Get Out From Jail! Wait One More Turn To Get One More Chanse!", ZERO);
                    }
                } else {
                    makeMove(diecResult[0] + diecResult[1], true, currPlayer);
                }

            }
            this.spesificGame.handelPlayerPresence(currPlayer); // cheacke if player is still in the game - or remove player from game list
            this.spesificGame.nextPlayerTurn();// continue to next player
            isGameOver = this.spesificGame.checkIfIsGameOver();
        }
    }

    public long getStayCostForAsset(SquareType square) {
        long stayCost = square.getAsset().getStaycost();
        if (square.getType().equals(SquareType.Type.TRANSPORTATION)) {
            if (isCurrentPlayerOwneAllTransportioes(square.getAsset().getOwner())) {
                stayCost = this.spesificGame.getMonopolyGame().getAssets().getTransportations().getStayCost();
            }
        } else if (square.getType().equals(SquareType.Type.UTILITY)) {
            if (isCurrentPlayerOwneAllUtilities(square.getAsset().getOwner())) {
                stayCost = this.spesificGame.getMonopolyGame().getAssets().getUtilities().getStayCost();
            }
        }
        return stayCost;
    }

    private boolean isCurrentPlayerOwneAllTransportioes(Player owner) {
        int numOfTransportionForPleyer = 0;
        int numberOfTransportiones = this.spesificGame.getMonopolyGame().getAssets().getTransportations().getTransport().size();
        boolean result = false;

        ArrayList<AssetType> playerAssets = owner.getMyAssets();
        List<SimpleAssetType> transportionAssets = this.spesificGame.getMonopolyGame().getAssets().getTransportations().getTransport();

        for (AssetType playerAsset : playerAssets) {
            if (playerAsset.getClass().equals(SimpleAssetType.class)) {// if the player asset is SimpeleAssetType
                SimpleAssetType simpeleAssetPlayer = (SimpleAssetType) playerAsset;// do castimg
                for (SimpleAssetType simpelAsset : transportionAssets) {// chech if the simpel asset is a transportion
                    if (simpelAsset.equals(simpeleAssetPlayer)) {
                        numOfTransportionForPleyer++;;
                    }
                }
            }

        }
        if (numOfTransportionForPleyer == numberOfTransportiones) {
            result = true;
        }
        return result;
    }

    private boolean isCurrentPlayerOwneAllUtilities(Player owner) {
        int numOfUtilitisForPleyer = 0;
        int numberOfUtilitis = this.spesificGame.getMonopolyGame().getAssets().getUtilities().getUtility().size();
        boolean result = false;

        ArrayList<AssetType> playerAssets = owner.getMyAssets();
        List<SimpleAssetType> utilitisAssets = this.spesificGame.getMonopolyGame().getAssets().getUtilities().getUtility();

        for (AssetType playerAsset : playerAssets) {
            if (playerAsset.getClass().equals(SimpleAssetType.class)) {// if the player asset is SimpeleAssetType
                SimpleAssetType simpeleAssetPlayer = (SimpleAssetType) playerAsset;// do castimg
                for (SimpleAssetType simpelAsset : utilitisAssets) {// chech if the simpel asset is a transportion
                    if (simpelAsset.equals(simpeleAssetPlayer)) {
                        numOfUtilitisForPleyer++;;
                    }
                }
            }

        }
        if (numOfUtilitisForPleyer == numberOfUtilitis) {
            result = true;
        }
        return result;
    }

    public void makeMove(int numOfSteps, boolean isCanPasStart, Player currentPlayer) throws Exception {
        currentPlayer.move(numOfSteps, isCanPasStart); //cheng player squreNum
        SquareBase currentSqure = this.spesificGame.getMonopolyGame().getBoard().getSqureBaseBySqureNum(currentPlayer.getSqureNum());
        addEventsMove(EventType.MOVE, currentPlayer.getName(), currentPlayer.getSqureNum(), false, "", ZERO);

        currentSqure.stepOnMe(currentPlayer); //the square after movment 

        if (currentPlayer.getSqureNum() == ZERO) {
            addEventsWitheMsg(EventType.LANDED_ON_START_SQUARE, currentPlayer.getName(), "You Just Stepped On Start Squar ,You Get 400 Nis", ZERO);
        }
        if (currentPlayer.isPassStartSqure()) {
            addEventsWitheMsg(EventType.PASSED_START_SQUARE, currentPlayer.getName(), "You Just Stepped By Start Squar , You Get 200 Nis , Enjoy!", ZERO);
        }

        if (currentSqure.getClass().equals(SquareType.class)) {
            SquareType currentSquareType = (SquareType) currentSqure;

            if (currentPlayer.isDoesPlayerNeedToPay()) {// current player isn't the owner and the squre has a owner
                //pay to owner
                long stayCost = getStayCostForAsset(currentSquareType);
                currentPlayer.pay(currentSquareType.getAsset().getOwner(), stayCost);
                boolean paymemtFromUser = true;
                String paymentToPlayerName = currentSquareType.getAsset().getOwner().getName();
                addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), paymemtFromUser, paymentToPlayerName, (int) stayCost, ZERO);
            } else if (currentPlayer.isIsPlayerCanBuySquare()) { //has the option to buy 
                if (currentPlayer.isPlayerHaveTheMany(currentSquareType.getAsset().getCost())) {
                    buyingAssetOffer(currentSquareType, this.spesificGame.getCurrentPlayer().getSqureNum());
                } else {
                    // ConsolUI.msgCantBuy();
                }
            } else if (currentPlayer.isIsNeedToTakeSupriesCard()) {
                CardBase card = this.spesificGame.getMonopolyGame().getSurpries().getCard();
                addEventsWitheMsg(EventType.SURPRISE_CARD, currentPlayer.getName(), card.getText(), ZERO);
                actionSurpriseCard(card);
            } else if (currentPlayer.isIsNeedToTakeWarrentCard()) {
                CardBase card = this.spesificGame.getMonopolyGame().getWarrant().getCard();
                addEventsWitheMsg(EventType.WARRANT_CARD, currentPlayer.getName(), card.getText(), ZERO);
                actionWarrantCard(card);
            }
            currentPlayer.setUpFlages();
        }
    }

    public boolean checkIfPlayerCanBuyHouse(SquareType square) {
        boolean result = false;
        CityType city = (CityType) square.getAsset();//casting
        if (this.spesificGame.getCurrentPlayer().isPlayerHaveTheMany(city.getHouseCost())) {
            if (this.spesificGame.getMonopolyGame().getAssets().checkIfPlayerOwnedTheCountry(city.getCuntryName(), this.spesificGame.getCurrentPlayer())) {
                result = true;
            }
        }
        return result;
    }

    public void currentPlayerHaveGetOutCard(Player currentPlayer) {
        currentPlayer.setIsInJail(false); // update
        this.spesificGame.getMonopolyGame().getSurpries().addCardToSurpriseList(currentPlayer.getCardGetOutFromJail());
        currentPlayer.setIsHaveGetOutOfJailCard(false, null);
    }

    private void addEventsDiseRoll(EventType type, String playerName, int[] diceRes) {
        Event eventDiceRes = UtilitiesWS.createEvent(events.size(), type, playerName, ZERO);
        eventDiceRes.setFirstDiceResult(diceRes[0]);
        eventDiceRes.setSecondDiceResult(diceRes[1]);
        events.add(eventDiceRes);
    }

    private void addEventsWitheMsg(EventType type, String playerName, String msg, int timeoutCounter) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timeoutCounter);
        eventToAdd.setEventMessage(msg);
        this.events.add(eventToAdd);
    }

    private void addEventsMove(EventType type, String playerName, int squreNum, boolean isInJail, String msg, int timoutCount) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timoutCount);
        eventToAdd.setNextBoardSquareID(squreNum);
        eventToAdd.setPlayerMove(isInJail);
        eventToAdd.setEventMessage(msg);
        this.events.add(eventToAdd);
    }

    private void addEventsPayment(EventType type, String playerName, boolean paymemtFromUser, String paymentToPlayerName, int amount, int timountCount) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timountCount);
        eventToAdd.setPaymentAmount(amount);
        eventToAdd.setPaymentToPlayerName(paymentToPlayerName);
        eventToAdd.setPaymemtFromUser(paymemtFromUser);
        this.events.add(eventToAdd);
    }

    private void addEventsPropmtPlayerToBuy(EventType type, String playerName, String msg, int squreNum, int timeoutCounter) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timeoutCounter);
        eventToAdd.setEventMessage(msg);
        eventToAdd.setBoardSquareID(squreNum);
        this.events.add(eventToAdd);
    }

    public void buyHouse(CityType squareCity, Player player) {
        player.purchase(squareCity, squareCity.getHouseCost());
        squareCity.addToCounterOfHouse();

    }

    public boolean checkIfPlayerCanBuyCity(SquareType square) {
        boolean canBuyCity = false;
        CityType city = (CityType) square.getAsset();
        if (!city.doYouHaveOwner()) {
            canBuyCity = true;
        }
        return canBuyCity;
    }

    public void buyCity(SquareType square, Player player) {
        CityType city = (CityType) square.getAsset();
        player.purchase(city, city.getCost());
        city.setHaveOwner(player);
    }

    private void buyTrnsportionOrUtility(SquareType square, int squareNum, Player player) {
        SquareType squreType = (SquareType) this.spesificGame.getMonopolyGame().getBoard().getSqureBaseBySqureNum(squareNum);
        squreType.getAsset().setHaveOwner(player);
        player.purchase(square.getAsset(), square.getAsset().getCost());
    }

    public void buyingAssetOffer(SquareType square, int squreNum) {// in this case can buy only house
        boolean canBuy = false;
        boolean wantToBuy = false;
        Player currentPlayer = this.spesificGame.getCurrentPlayer();

        switch (square.getType()) {
            case CITY:
                CityType citySquar = (CityType) square.getAsset();
                if (checkIfPlayerCanBuyHouse(square)) {
                    if (currentPlayer.isHumen()) {
                        String msg = "  Do You Want To Buy House Number " + citySquar.getCounterOfHouse() + 1 + " (price " + citySquar.getHouseCost() + ", you have: " + currentPlayer.getAmount() + ") ?";
                        addEventsPropmtPlayerToBuy(EventType.PROPMPT_PLAYER_TO_BY_HOUSE, currentPlayer.getName(), msg, currentPlayer.getSqureNum(), -1);
                        timing();
                    } else {
                        buyHouse(citySquar, currentPlayer);
                        citySquar.setCounterOfHouse(citySquar.getCounterOfHouse() + 1);
                    }
                } else if (checkIfPlayerCanBuyCity(square)) {
                    if (currentPlayer.isHumen()) {
                        String msg = "Do You Want To Buy " + citySquar.getName() + " (price " + citySquar.getCost() + ", your amount: " + currentPlayer.getAmount() + ")?";
                        addEventsPropmtPlayerToBuy(EventType.PROPMT_PLAYER_TO_BY_ASSET, currentPlayer.getName(), msg, currentPlayer.getSqureNum(), -1);
                        timing();
                    } else {
                        buyCity(square, currentPlayer);
                    }
                }
                break;

            case UTILITY:
            case TRANSPORTATION:
                SimpleAssetType assetSquar = (SimpleAssetType) square.getAsset();
                if (currentPlayer.isHumen()) {
                    String msg = "Do You Want To Buy " + assetSquar.getName() + " (price " + assetSquar.getCost() + ", your amount: " + currentPlayer.getAmount() + ")?";
                    addEventsPropmtPlayerToBuy(EventType.PROPMT_PLAYER_TO_BY_ASSET, currentPlayer.getName(), msg, currentPlayer.getSqureNum(), -1);
                    timing();
                } else {
                    buyTrnsportionOrUtility(square, currentPlayer.getSqureNum(), currentPlayer);
                }

                break;
        }

    }

    private void substractFromAllPlayersAmount(long sum) {
        boolean paymemtFromUser = false;
        for (Player player : this.spesificGame.getPlayers()) {
            if (!player.equals(this.spesificGame.getCurrentPlayer())) {
                player.pay(this.spesificGame.getCurrentPlayer(), sum);
                addEventsPayment(EventType.PAYMENT, this.spesificGame.getCurrentPlayer().getName(), paymemtFromUser, player.getName(), (int) sum, ZERO);
                this.spesificGame.handelPlayerPresence(player);
            }
        }
    }

    private void actionMonoteryCardFromSurpeiseCards(long sum, MonetaryCard.Who type) {
        boolean paymemtFromUser = false;
        Player currentPlayer = this.spesificGame.getCurrentPlayer();
        if (type == MonetaryCard.Who.PLAYERS) {
            substractFromAllPlayersAmount(sum);
        } else if (type == MonetaryCard.Who.TREASURY) {
            this.spesificGame.getCurrentPlayer().addToAmount(sum);
            addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), false, currentPlayer.getName(), (int) sum, ZERO);
        }
    }

    public void actionSurpriseCard(CardBase card) throws Exception {
        Player currentPlayer = this.spesificGame.getCurrentPlayer();

        if (card.getClass().equals(models.MonetaryCard.class)) {
            models.MonetaryCard monoteryCard = (models.MonetaryCard) card;
            actionMonoteryCardFromSurpeiseCards(monoteryCard.getSum(), monoteryCard.getType());
        } else if (card.getClass().equals(models.GetOutOfJailCard.class)) {
            addEvents(EventType.GO_TO_JAIL, currentPlayer.getName(), ZERO);
            currentPlayer.setIsHaveGetOutOfJailCard(true, (models.GetOutOfJailCard) card);
        } else if (card.getClass().equals(models.GotoCard.class)) {
            models.GotoCard gotoCard = (models.GotoCard) card;
            actionGoToCard(gotoCard.getType());
        }
        if (card.getClass().equals(models.GotoCard.class) || card.getClass().equals(models.MonetaryCard.class)) {
            this.spesificGame.getMonopolyGame().getSurpries().addCardToSurpriseList(card);
        }
    }

    public int actionGoToCard(GotoCard.To type) throws Exception {
        Player currentPlayer = this.spesificGame.getCurrentPlayer();
        boolean isCanPasByStart = true;
        int numOfSteps = 0;
        int numNextOfQquare = 0;

        if (type.equals(models.GotoCard.To.START)) {
            numOfSteps = this.spesificGame.getMonopolyGame().getBoard().getNumberOfStepstToSquareByType(
                    currentPlayer.getSqureNum(), new models.StartSquare().toString());
        } else if (type.equals(models.GotoCard.To.NEXT_SURPRISE)) {
            numOfSteps = this.spesificGame.getMonopolyGame().getBoard().getNumberOfStepstToSquareByType(
                    currentPlayer.getSqureNum(), new models.SquareType("SURPRISE").toString());
        } else if (type.equals(models.GotoCard.To.JAIL)) {
            isCanPasByStart = false;
            numOfSteps = this.spesificGame.getMonopolyGame().getBoard().getNumberOfStepstToSquareByType(
                    currentPlayer.getSqureNum(), new models.JailSlashFreeSpaceSquareType().toString());

        } else if (type.equals(models.GotoCard.To.NEXT_WARRANT)) {
            isCanPasByStart = false;
            numOfSteps = this.spesificGame.getMonopolyGame().getBoard().getNumberOfStepstToSquareByType(
                    currentPlayer.getSqureNum(), new models.SquareType("WARRANT").toString());
        }
        makeMove(numOfSteps, isCanPasByStart, currentPlayer);
        return numOfSteps;
    }

    private void actionWarrantCard(CardBase card) throws Exception {
        if (card.getClass().equals(models.MonetaryCard.class)) {
            models.MonetaryCard monoteryCard = (models.MonetaryCard) card;
            //   ConsolUI.msgMonoteryCard(monoteryCard.getText(), monoteryCard.getSum());
            actionMonoteryCardFromWarrantCards(monoteryCard.getSum(), monoteryCard.getType());
        } else if (card.getClass().equals(models.GotoCard.class)) {
            models.GotoCard gotoCard = (models.GotoCard) card;
            actionGoToCard(gotoCard.getType());
        }
        this.spesificGame.getMonopolyGame().getWarrant().addCardToWarrantCardList(card);
    }

    public void actionMonoteryCardFromWarrantCards(long sum, MonetaryCard.Who type) {
        long totalSubstract = 0;
        Player currentPlayer = this.spesificGame.getCurrentPlayer();
        boolean pymentFromUser = true;
        String paymentToPlayerName = "";
        if (type.equals(MonetaryCard.Who.TREASURY)) {
            this.spesificGame.getCurrentPlayer().payToTreasury(sum);
            //when player need to pay to truasury paymentToPlayerName= empty string
            addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), pymentFromUser, paymentToPlayerName, (int) sum, ZERO);
        } else if (type.equals(MonetaryCard.Who.PLAYERS)) {
            payToAllPlayers(sum);
        }
    }

    private void payToAllPlayers(long sum) {
        boolean pymentFromUser = true;
        for (Player player : this.spesificGame.getPlayers()) {
            if (!player.equals(this.spesificGame.getCurrentPlayer())) {
                this.spesificGame.getCurrentPlayer().pay(player, sum);
                addEventsPayment(EventType.PAYMENT, this.spesificGame.getCurrentPlayer().getName(), pymentFromUser, player.getName(), (int) sum, ZERO);
            }
        }
    }

    int getEventListSize() {
        return this.events.size();
    }

    void buy(Player player, Event event) {
        SquareType square = (SquareType) this.spesificGame.getMonopolyGame().getBoard().getSqureBaseBySqureNum(event.getBoardSquareID());

        switch (event.getType()) {
            case PROPMT_PLAYER_TO_BY_ASSET:
                buyAsset(player, square, event.getBoardSquareID());
                break;
            case PROPMPT_PLAYER_TO_BY_HOUSE:
                buyHouse((CityType) square.getAsset(), player);
                break;
            default:
                throw new AssertionError(event.getType().name());

        }
    }

    private void buyAsset(Player player, SquareType square, int squreNum) {
        switch (square.getType()) {
            case CITY:
                buyCity(square, player);
                break;
            case UTILITY:
            case TRANSPORTATION:
                buyTrnsportionOrUtility(square, squreNum, player);
                break;
            default:
                throw new AssertionError(square.getType().name());
        }
    }

}
