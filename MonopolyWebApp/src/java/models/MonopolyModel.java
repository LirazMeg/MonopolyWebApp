/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Liraz
 */
public class MonopolyModel {

    private Board board;
    private Assets assets;
    private Surpries surpries;
    private Warrant warrant;


    public MonopolyModel() {
    }

    public MonopolyModel(Board board, Assets assets, Surpries surpries, Warrant warrant) {
        this.board = board;
        this.assets = assets;
        this.surpries = surpries;
        this.warrant = warrant;
    }

    public Board getBoard() {
        return board;
    }

    public Assets getAssets() {
        return assets;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    public void setSurpries(Surpries surpries) {
        this.surpries = surpries;
    }

    public void setWarrant(Warrant warrant) {
        this.warrant = warrant;
    }

    public void removePlayerrFromTheGame(Player currentPlayer) {
        for (int i = 0; i < this.board.BOARD_SIZE; i++) {
            if (this.board.getSqureBaseBySqureNum(i).getClass().equals(SquareType.class)) {
                SquareType currentSquare = (SquareType) this.board.getSqureBaseBySqureNum(i);
                if (currentSquare.getType().equals(SquareType.Type.CITY)
                        || currentSquare.getType().equals(SquareType.Type.TRANSPORTATION)
                        || currentSquare.getType().equals(SquareType.Type.UTILITY)) {
                   
                    if (currentSquare.getAsset().isIsHaveOwner()) {
                        if (currentSquare.getAsset().getOwner().equals(currentPlayer)) {
                            currentSquare.getAsset().setDoNotHaveOwner();
                        }
                    }
                }
            }
        }
    }

    public void shuffelCards() {
        this.surpries.shuffel();
        this.warrant.shuffel();

    }

    public Surpries getSurpries() {
        return surpries;
    }

    public Warrant getWarrant() {
        return warrant;
    }

//========================Surpries==============
    public static class Surpries {

        private List<models.CardBase> surpries;

        public Surpries(List<models.CardBase> baseCardLst) {
            this.surpries = baseCardLst;
        }

        private void shuffel() {
            Collections.shuffle(this.surpries);
        }

        public models.CardBase getCard() {// take the first card, remove the card from the list 
            models.CardBase card = this.surpries.get(0);;
            if (this.surpries.get(0).getClass().equals(GotoCard.class)) {
                card = (models.GotoCard) this.surpries.get(0);
            }
//            } else {
//                card = this.surpries.get(0);
//            }
            this.surpries.remove(0);
            return card;
        }

        public void addCardToSurpriseList(models.CardBase card) {
            this.surpries.add(card);

        }
    }
//======================Warrant===========================

    public static class Warrant {

        private List<models.CardBase> warrant;

        public Warrant(List<models.CardBase> cardBaseLst) {
            this.warrant = cardBaseLst;
        }

        private void shuffel() {
            Collections.shuffle(this.warrant);
        }

        public models.CardBase getCard() {
            // take the first card, remove the card from the list 
            models.CardBase card = warrant.get(0);
            warrant.remove(0);
            return card;

        }

        public void addCardToWarrantCardList(models.CardBase card) {
            this.warrant.add(card);
        }
    }

    //=============Assets===============
    public static class Assets {

        private List<Country> countries;
        private Utilities utilities;
        private Transportations transportations;

        public Assets(List<Country> countries, Utilities utilities, Transportations transportations) {
            this.countries = countries;
            this.utilities = utilities;
            this.transportations = transportations;
        }

        public void setCountries(List<Country> countries) {
            this.countries = countries;
        }

        public void setUtilities(Utilities utilities) {
            this.utilities = utilities;
        }

        public void setTransportations(Transportations transportations) {
            this.transportations = transportations;
        }

        public List<Country> getCountries() {
            return countries;
        }

        public Utilities getUtilities() {
            return utilities;
        }

        public Transportations getTransportations() {
            return transportations;
        }

        public boolean checkIfPlayerOwnedTheCountry(String cuntryName, Player currentPlayer) {
            boolean isPlayerOwnTheCountry = false;
            int numOfCitiesInCountry;
            numOfCitiesInCountry = getNumOfCitiesInCuntry(cuntryName);

            if (numOfCitiesInCountry == currentPlayer.getNumOfCitiesFromCountrythatPlayerOwned(cuntryName)) {
                isPlayerOwnTheCountry = true;
            }
            return isPlayerOwnTheCountry;
        }

        private int getNumOfCitiesInCuntry(String cuntryName) {
            int numOfCitiesRes = 0;
            for (Country country : countries) {
                if (country.name.equals(cuntryName)) {
                    numOfCitiesRes = country.citys.size();
                }
            }
            return numOfCitiesRes;
        }

        private void removeFromCountries(Player currentPlayer) {
            for (Country country : this.countries) {
                removePlayerFromCities(country.citys, currentPlayer);
            }
        }

        private void removePlayerFromUtilits(Player currentPlayer) {
            for (SimpleAssetType asset : this.utilities.utility) {
                if (asset.isIsHaveOwner()) {
                    if (asset.getOwner().equals(currentPlayer)) {
                        asset.setDoNotHaveOwner();
                    }
                }
            }
        }

        private void removePlayerFromTransportion(Player currentPlayer) {
            for (SimpleAssetType asset : this.transportations.transport) {
                if (asset.isIsHaveOwner()) {
                    if (asset.getOwner().equals(currentPlayer)) {
                        asset.setDoNotHaveOwner();
                    }
                }
            }
        }

        private void removePlayerFromCities(List<CityType> cites, Player currentPlayer) {
            for (CityType city : cites) {
                if (city.isIsHaveOwner()) {
                    if (city.getOwner().equals(currentPlayer)) {
                        city.setDoNotHaveOwner();
                    }
                }
            }
        }

    }
//==============Country===============

    public static class Country {

        String name;
        List<CityType> citys;

        public Country(String name, List<CityType> citys) {
            this.name = name;
            this.citys = citys;
        }

        public String getName() {
            return name;
        }

        public List<CityType> getCitys() {
            return citys;
        }

    }
//===================Board========================

    public static class Board {

        public final static int NUM_OF_JAIL_SQUARE = 10;
        public final static int BOARD_SIZE = 36;
        protected List<models.SquareBase> content;

        private int numOfCurrentSurpriseSqure;

        public Board(List<models.SquareBase> content) {
            this.content = content;
        }

        public List<models.SquareBase> getContent() {
            return content;
        }

        public void setContent(List<models.SquareBase> content) {
            this.content = content;
        }

        public models.SquareBase getSqureBaseBySqureNum(int squreNum) {
            return this.content.get(squreNum);
        }

        public int getNumberOfStepstToSquareByType(int squreNum, String squareBaseName) {
            int i = squreNum;
            int numberOfSteps = 0;
            boolean isFound = false;
            while (!isFound) {
                i = (i + 1) % this.content.size();
//check if we are on the square we want to get to
                models.SquareBase squreToCheck = this.getSqureBaseBySqureNum(i);
                if (squreToCheck.toString().equals(squareBaseName)) {
                    isFound = true;
                }
                numberOfSteps++;
            }

            return numberOfSteps;
        }

        public int getNumberOfStepstToJailSquare(int squreNum) {
            int numOfSteps = 0;
            if (squreNum < NUM_OF_JAIL_SQUARE) {

                numOfSteps = NUM_OF_JAIL_SQUARE - squreNum + 1;
            } else {
                numOfSteps = BOARD_SIZE - squreNum + NUM_OF_JAIL_SQUARE;
            }

            return numOfSteps;
        }

        public int getNextSquareNumAfterWarrantCard(int squreNum, int numOfStepToWarrant) {
            return (squreNum + numOfStepToWarrant) % BOARD_SIZE;
        }

    }
//===============Transportation==================

    public static class Transportations {

        long stayCost;
        List<SimpleAssetType> transport;

        public Transportations(long stayCost, List<SimpleAssetType> transport) {
            this.stayCost = stayCost;
            this.transport = transport;
        }

        public void setStayCost(long stayCost) {
            this.stayCost = stayCost;
        }

        public void setTransport(List<SimpleAssetType> transport) {
            this.transport = transport;
        }

        public long getStayCost() {
            return stayCost;
        }

        public List<SimpleAssetType> getTransport() {
            return transport;
        }

    }
//===========Utilities=================

    public static class Utilities {

        long stayCost;
        List<SimpleAssetType> utility;

        public Utilities(long stayCost, List<SimpleAssetType> utility) {
            this.stayCost = stayCost;
            this.utility = utility;
        }

        public long getStayCost() {
            return stayCost;
        }

        public List<SimpleAssetType> getUtility() {
            return utility;
        }

        public void setStayCost(long stayCost) {
            this.stayCost = stayCost;
        }

        public void setUtility(List<SimpleAssetType> utility) {
            this.utility = utility;
        }

    }
}
