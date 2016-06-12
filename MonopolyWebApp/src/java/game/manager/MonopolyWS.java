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

    private void addEventsBought(EventType type, String playerName, int squreID, String msg, int timeoutCount) {
        Event event = UtilitiesWS.createEvent(events.size(), type, playerName, timeoutCount);
        event.setEventMessage(msg);
        event.setBoardSquareID(squreID);
        events.add(event);
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
            try {
                //creat all the cumputers players
                for (int i = 0; i < this.details.getComputerizedPlayers(); i++) {
                    spesificGame.addPlayerToGame(CUMPUTER_PLAYER + (i + 1), false);
                }
                setGameDetails(GameStatus.ACTIVE);
                events.clear();
                initNewGame();
                initCurrentPlayerInSpecificGame();
                addEvents(EventType.GAME_START, spesificGame.getCurrentPlayer().getName(), ZERO);
                doIterion();
            } catch (Exception ex) {
                Logger.getLogger(MonopolyWS.class.getName()).log(Level.SEVERE, null, ex);
                String exp = ex.getMessage();

            }
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
        // this.spesificGame.getCurrentPlayer().setResign(true);
        removePlayerThatResignFromList();
     
    }

    //todo
    public void removePlayerThatResignFromList() throws Exception {
        int lastIndex = this.spesificGame.getPleyerIndex() - 1;
        Player currPlayer = this.spesificGame.getPlayers().get(lastIndex);
        currPlayer.setResign(true);
        if (getGameDetails().getStatus().equals(GameStatus.WAITING)) {
            spesificGame.removePlayerThatResignFromList();
        } else {// in case of active game
            addEventsWitheMsg(EventType.PLAYER_RESIGNED, currPlayer.getName(), "You Resinged From Game", ZERO);
//            this.spesificGame.nextPlayerTurn();
//            addEvents(EventType.PLAYER_TURN, this.spesificGame.getCurrentPlayer().getName(), ZERO);
            if (!this.spesificGame.checkIfIsGameOver()) { // while the game is going - more ten one player
                addEventsWitheMsg(EventType.GAME_WINNER, this.spesificGame.getResignWinner(), this.spesificGame.getResignWinner() + " You Are The Winner !!!!!", ZERO);
                //        addEvents(EventType.GAME_OVER, this.spesificGame.getCurrentPlayer().getName(), ZERO);
            } else {
                doIterion();
            }
        }

    }

    public void doIterion() throws Exception {
        Player currPlayer = this.spesificGame.getCurrentPlayer();
        boolean isGameOver = this.spesificGame.checkIfIsGameOver();
        boolean isNeedToWait = false;
        while (!isGameOver && !isNeedToWait) {
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
                        isNeedToWait = makeMove(diecResult[0] + diecResult[1], true, currPlayer);

                    } else {
                        addEventsMove(EventType.MOVE, currPlayer.getName(), currPlayer.getSqureNum(), false, "You Can't Get Out From Jail! Wait One More Turn To Get One More Chanse!", ZERO);
                    }
                } else {
                    isNeedToWait = makeMove(diecResult[0] + diecResult[1], true, currPlayer);

                }

            } else {
                addEventsMove(EventType.MOVE, currPlayer.getName(), currPlayer.getSqureNum(), false, "You Are In Parkink,Please Wait One More Turn!", ZERO);

            }
            // cheacke if player is still in the game - or remove player from game list
            handelPlayerPresence(currPlayer);
            this.spesificGame.nextPlayerTurn();// continue to next player
            currPlayer = this.spesificGame.getCurrentPlayer();
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

    public boolean makeMove(int numOfSteps, boolean isCanPasStart, Player currentPlayer) throws Exception {
        boolean isNeedToWait = false;
        String msg = "";
        currentPlayer.move(numOfSteps, isCanPasStart); //cheng player squreNum
        SquareBase currentSqure = this.spesificGame.getMonopolyGame().getBoard().getSqureBaseBySqureNum(currentPlayer.getSqureNum());
        addEventsMove(EventType.MOVE, currentPlayer.getName(), currentPlayer.getSqureNum(), true, "", ZERO);

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
                String paymentToPlayerName = currentSquareType.getAsset().getOwner().getName();

                if (currentPlayer.pay(currentSquareType.getAsset().getOwner(), stayCost)) {
                    msg = "Just Pay To " + paymentToPlayerName + " " + stayCost + " Nis";
                } else {
                    stayCost = currentPlayer.getAmount();
                    msg = "Just Pay To " + paymentToPlayerName + " " + stayCost + " Nis";
                }

                addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), paymentToPlayerName, (-1) * (int) stayCost, ZERO, msg);
            } else if (currentPlayer.isIsPlayerCanBuySquare()) { //has the option to buy 
                if (currentPlayer.isPlayerHaveTheMany(currentSquareType.getAsset().getCost())) {
                    isNeedToWait = buyingAssetOffer(currentSquareType, this.spesificGame.getCurrentPlayer().getSqureNum());

                } else {

                }
            } else if (currentPlayer.isIsNeedToTakeSupriesCard()) {
                CardBase card = this.spesificGame.getMonopolyGame().getSurpries().getCard();
                actionSurpriseCard(card, currentPlayer);
            } else if (currentPlayer.isIsNeedToTakeWarrentCard()) {
                CardBase card = this.spesificGame.getMonopolyGame().getWarrant().getCard();
                actionWarrantCard(card, currentPlayer);
            }
            currentPlayer.setUpFlages();
        }
        return isNeedToWait;
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
        eventDiceRes.setEventMessage("Your Dice Result " + diceRes[0] + ", " + diceRes[1]);
        events.add(eventDiceRes);
    }

    private void addEventsWitheMsg(EventType type, String playerName, String msg, int timeoutCounter) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timeoutCounter);
        eventToAdd.setEventMessage(msg);
        this.events.add(eventToAdd);
    }

    private void addEventsMove(EventType type, String playerName, int squreNum, boolean isPlayerMove, String msg, int timoutCount) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timoutCount);
        eventToAdd.setNextBoardSquareID(squreNum);
        eventToAdd.setPlayerMove(isPlayerMove);
        eventToAdd.setEventMessage(msg);
        this.events.add(eventToAdd);
    }

    private void addEventsPayment(EventType type, String playerName, String paymentToPlayerName, int amount, int timountCount, String msg) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timountCount);
        eventToAdd.setPaymentAmount(amount);
        eventToAdd.setPaymentToPlayerName(paymentToPlayerName);
        //    eventToAdd.setPaymemtFromUser(paymemtFromUser);
        eventToAdd.setEventMessage(msg);
        this.events.add(eventToAdd);
    }

    private void addEventsPropmtPlayerToBuy(EventType type, String playerName, String msg, int squreNum, int timeoutCounter) {
        Event eventToAdd = UtilitiesWS.createEvent(events.size(), type, playerName, timeoutCounter);
        eventToAdd.setEventMessage(msg);
        eventToAdd.setBoardSquareID(squreNum);
        this.events.add(eventToAdd);
    }

    public void buyHouse(CityType squareCity, Player player, int squreID) {
        player.purchase(squareCity, squareCity.getHouseCost());
        squareCity.addToCounterOfHouse();
        String msg = "Just Bought House Number " + squareCity.getCounterOfHouse() + " In " + squareCity.getName() + ", " + squareCity.getCuntryName();
        addEventsBought(EventType.HOUSE_BOUGHT, player.getName(), squreID, msg, ZERO);

    }

    public boolean checkIfPlayerCanBuyCity(SquareType square) {
        boolean canBuyCity = false;
        CityType city = (CityType) square.getAsset();
        if (!city.doYouHaveOwner()) {
            canBuyCity = true;
        }
        return canBuyCity;
    }

    public String buyCity(SquareType square, Player player) {
        CityType city = (CityType) square.getAsset();
        player.purchase(city, city.getCost());
        city.setHaveOwner(player);
        return "Just Bought " + city.getName() + ", " + city.getCuntryName();

    }

    private String buyTrnsportionOrUtility(SquareType square, int squareNum, Player player) {
        SquareType squreType = (SquareType) this.spesificGame.getMonopolyGame().getBoard().getSqureBaseBySqureNum(squareNum);
        squreType.getAsset().setHaveOwner(player);
        player.purchase(square.getAsset(), square.getAsset().getCost());
        return "Just Bouth " + squreType.getAsset().getName();
    }

    public boolean buyingAssetOffer(SquareType square, int squreNum) {// in this case can buy only house
        boolean canBuy = false;
        boolean wantToBuy = false;
        boolean isNeedToWait = false;
        Player currentPlayer = this.spesificGame.getCurrentPlayer();

        switch (square.getType()) {
            case CITY:
                CityType citySquar = (CityType) square.getAsset();
                if (checkIfPlayerCanBuyHouse(square)) {
                    if (currentPlayer.isHumen()) {
                        String msg = "  Do You Want To Buy House Number " + citySquar.getCounterOfHouse() + 1 + " (price " + citySquar.getHouseCost() + ", you have: " + currentPlayer.getAmount() + ") ?";
                        addEventsPropmtPlayerToBuy(EventType.PROPMPT_PLAYER_TO_BY_HOUSE, currentPlayer.getName(), msg, currentPlayer.getSqureNum(), -1);
                        isNeedToWait = true;
                        timing();
                    } else {
                        buyHouse(citySquar, currentPlayer, squreNum);
                        citySquar.setCounterOfHouse(citySquar.getCounterOfHouse() + 1);
                        int cost = (int) citySquar.getHouseCost();
                        addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), "", (-1) * cost, ZERO, "You Just Pay To Treasury " + cost + "Nis.");
                    }
                } else if (checkIfPlayerCanBuyCity(square)) {
                    if (currentPlayer.isHumen()) {
                        String msg = "Do You Want To Buy " + citySquar.getName() + " (price " + citySquar.getCost() + ", your amount: " + currentPlayer.getAmount() + ")?";
                        addEventsPropmtPlayerToBuy(EventType.PROPMT_PLAYER_TO_BY_ASSET, currentPlayer.getName(), msg, currentPlayer.getSqureNum(), -1);
                        isNeedToWait = true;
                        timing();
                    } else {
                        String msg = buyCity(square, currentPlayer);
                        addEventsBought(EventType.ASSET_BOUGHT, currentPlayer.getName(), squreNum, msg, ZERO);
                        int cost = (int) citySquar.getCost();
                        addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), "", (-1) * cost, ZERO, "You Just Pay To Treasury " + cost + "Nis.");
                    }
                }
                break;

            case UTILITY:
            case TRANSPORTATION:
                SimpleAssetType assetSquar = (SimpleAssetType) square.getAsset();
                if (currentPlayer.isHumen()) {
                    String msg = "Do You Want To Buy " + assetSquar.getName() + " (price " + assetSquar.getCost() + ", your amount: " + currentPlayer.getAmount() + ")?";
                    addEventsPropmtPlayerToBuy(EventType.PROPMT_PLAYER_TO_BY_ASSET, currentPlayer.getName(), msg, currentPlayer.getSqureNum(), -1);
                    isNeedToWait = true;
                    timing();
                } else {
                    buyTrnsportionOrUtility(square, currentPlayer.getSqureNum(), currentPlayer);
                }

                break;
        }
        return isNeedToWait;
    }

    private void substractFromAllPlayersAmount(long sum) {
        for (Player player : this.spesificGame.getPlayers()) {
            if (!player.equals(this.spesificGame.getCurrentPlayer())) {
                player.pay(this.spesificGame.getCurrentPlayer(), sum);
                String msg = "You Got " + sum + " Nis From " + player.getName();
                addEventsPayment(EventType.PAYMENT, this.spesificGame.getCurrentPlayer().getName(), player.getName(), (int) sum, ZERO, msg);
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
            String msg = "You Got " + sum + "  Nis From Treasury.";
            addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), currentPlayer.getName(), (int) sum, ZERO, msg);
        }
    }

    public void actionSurpriseCard(CardBase card, Player currentPlayer) throws Exception {

        String msg = "Surpeise Card: ";

        if (card.getClass().equals(models.MonetaryCard.class)) {
            models.MonetaryCard monoteryCard = (models.MonetaryCard) card;
            msg += String.format(monoteryCard.getText(), monoteryCard.getSum());
            addEventsWitheMsg(EventType.SURPRISE_CARD, currentPlayer.getName(), msg, ZERO);
            actionMonoteryCardFromSurpeiseCards(monoteryCard.getSum(), monoteryCard.getType());
        } else if (card.getClass().equals(models.GetOutOfJailCard.class)) {
            msg += card.getText();
            addEventsWitheMsg(EventType.SURPRISE_CARD, currentPlayer.getName(), msg, ZERO);
            addEvents(EventType.GO_TO_JAIL, currentPlayer.getName(), ZERO);
            currentPlayer.setIsHaveGetOutOfJailCard(true, (models.GetOutOfJailCard) card);
        } else if (card.getClass().equals(models.GotoCard.class)) {
            models.GotoCard gotoCard = (models.GotoCard) card;
            msg += gotoCard.getText();
            addEventsWitheMsg(EventType.SURPRISE_CARD, currentPlayer.getName(), msg, ZERO);
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
            addEventsWitheMsg(EventType.GO_TO_JAIL, currentPlayer.getName(), "Go To Jail", ZERO);

        } else if (type.equals(models.GotoCard.To.NEXT_WARRANT)) {
            isCanPasByStart = false;
            numOfSteps = this.spesificGame.getMonopolyGame().getBoard().getNumberOfStepstToSquareByType(
                    currentPlayer.getSqureNum(), new models.SquareType("WARRANT").toString());
        }
        makeMove(numOfSteps, isCanPasByStart, currentPlayer);
        return numOfSteps;
    }

    private void actionWarrantCard(CardBase card, Player currentPlayer) throws Exception {
        String msg = "Warrant Card: ";
        if (card.getClass().equals(models.MonetaryCard.class)) {
            models.MonetaryCard monoteryCard = (models.MonetaryCard) card;
            msg += String.format(monoteryCard.getText(), monoteryCard.getSum());
            addEventsWitheMsg(EventType.WARRANT_CARD, currentPlayer.getName(), msg, ZERO);
            actionMonoteryCardFromWarrantCards(monoteryCard.getSum(), monoteryCard.getType());
        } else if (card.getClass().equals(models.GotoCard.class)) {
            models.GotoCard gotoCard = (models.GotoCard) card;
            msg += gotoCard.getText();
            addEventsWitheMsg(EventType.WARRANT_CARD, currentPlayer.getName(), msg, ZERO);
            actionGoToCard(gotoCard.getType());
        }
        this.spesificGame.getMonopolyGame().getWarrant().addCardToWarrantCardList(card);
    }

    public void actionMonoteryCardFromWarrantCards(long sum, MonetaryCard.Who type) {
        long totalSubstract = 0;
        Player currentPlayer = this.spesificGame.getCurrentPlayer();
        String paymentToPlayerName = "";
        if (type.equals(MonetaryCard.Who.TREASURY)) {
            this.spesificGame.getCurrentPlayer().payToTreasury(sum);
            String msg = "You Just Pay To Treasury " + sum + " Nis.";
            addEventsPayment(EventType.PAYMENT, currentPlayer.getName(), paymentToPlayerName, (-1) * (int) sum, ZERO, msg);
        } else if (type.equals(MonetaryCard.Who.PLAYERS)) {
            payToAllPlayers(sum);
        }
    }

    private void payToAllPlayers(long sum) {
        for (Player player : this.spesificGame.getPlayers()) {
            if (!player.equals(this.spesificGame.getCurrentPlayer())) {
                String msg = "Just Pay To " + player.getName() + " " + sum + " Nis.";
                this.spesificGame.getCurrentPlayer().pay(player, sum);
                addEventsPayment(EventType.PAYMENT, this.spesificGame.getCurrentPlayer().getName(), player.getName(), (-1) * (int) sum, ZERO, msg);
            }
        }
    }

    int getEventListSize() {
        return this.events.size();
    }

    void buy(Player player, Event event) {
        SquareType square = (SquareType) this.spesificGame.getMonopolyGame().getBoard().getSqureBaseBySqureNum(event.getBoardSquareID());
        int squreID = event.getBoardSquareID();
        int cost = 0;
        switch (event.getType()) {
            case PROPMT_PLAYER_TO_BY_ASSET:
                buyAsset(player, square, squreID);
                cost = (int) square.getAsset().getCost();
                break;
            case PROPMPT_PLAYER_TO_BY_HOUSE:
                CityType city = (CityType) square.getAsset();
                buyHouse(city, player, squreID);
                cost = (int) city.getHouseCost();
                break;
            default:
                throw new AssertionError(event.getType().name());
        }
        String msg = "You Just Pay To Treasury " + cost + " Nis";
        addEventsPayment(EventType.PAYMENT, player.getName(), "", (-1) * cost, ZERO, msg);
    }

    private void buyAsset(Player player, SquareType square, int squreNum) {
        String msg = "";
        switch (square.getType()) {
            case CITY:
                msg = buyCity(square, player);
                break;
            case UTILITY:
            case TRANSPORTATION:
                msg = buyTrnsportionOrUtility(square, squreNum, player);
                break;
            default:
                throw new AssertionError(square.getType().name());
        }
        addEventsBought(EventType.ASSET_BOUGHT, player.getName(), squreNum, msg, ZERO);
    }

    private void handelPlayerPresence(Player currPlayer) {
        if (currPlayer.isQuit() && !currPlayer.isResign()) {
            this.spesificGame.handelPlayerPresence(currPlayer);
            addEventsWitheMsg(EventType.PLAYER_LOST, currPlayer.getName(), "You Lost In The Game", ZERO);
        }
    }

}
