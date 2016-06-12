/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;

/**
 *
 * @author Liraz
 */
public abstract class Player {

    private final static int NUM_OF_JAIL_SQUARE = 9;
    private String name;
    protected long amount;
    protected int squreNum;
    private boolean isNeedToQuit;
    private boolean isInJail;
    private boolean isInParking;
    private boolean isOnJailOrFreePass;
    private boolean isNeedToTakeSupriesCard;
    private boolean isNeedToTakeWarrentCard;
    private ArrayList<AssetType> myAssets;
    private boolean isPlayerNeedToPay;
    private boolean isPlayerCanBuy;
    private boolean isHaveGetOutOfJailCard;
    private models.GetOutOfJailCard getOutOfJailCard;
    private boolean resign;
    private boolean isPassStartSqure;

    public Player(String name) {
        this.name = name;
        this.amount = 1500;
        this.squreNum = 0;
        this.isInJail = false;
        this.isInParking = false;
        this.myAssets = new ArrayList<>();
        this.isNeedToQuit = false;
        this.isPlayerNeedToPay = false;
        this.isPlayerCanBuy = false;
        this.isOnJailOrFreePass = false;
        this.isNeedToTakeSupriesCard = false;
        this.isNeedToTakeWarrentCard = false;
        this.isHaveGetOutOfJailCard = false;
        this.getOutOfJailCard = null;
        this.resign = false;
        this.isPassStartSqure = false;
    }

    public void setResign(boolean resign) {
        this.resign = resign;
    }

    public void setIsOnJailOrFreePass(boolean isOnJailOrFreePass) {
        this.isOnJailOrFreePass = isOnJailOrFreePass;
    }

    abstract public boolean doYouWantToPurchaseHouse(CityType square, long price);

    abstract public boolean doYouWantToPurchase(AssetType square, long price);

    public void setIsHaveGetOutOfJailCard(boolean isHaveGetOutOfJailCard) {
        this.isHaveGetOutOfJailCard = isHaveGetOutOfJailCard;
    }

    public boolean isIsHaveGetOutOfJailCard() {
        return isHaveGetOutOfJailCard;
    }

    public boolean isIsNeedToTakeSupriesCard() {
        return isNeedToTakeSupriesCard;
    }

    public boolean isIsNeedToTakeWarrentCard() {
        return isNeedToTakeWarrentCard;
    }

    public void setIsNeedToQuit(boolean isNeedToQuit) {
        this.isNeedToQuit = isNeedToQuit;
    }

    public void setIsNeedToTakeSupriesCard(boolean isNeedToTakeSupriesCard) {
        this.isNeedToTakeSupriesCard = isNeedToTakeSupriesCard;
    }

    public void setIsNeedToTakeWarrentCard(boolean isNeedToTakeWarrentCard) {
        this.isNeedToTakeWarrentCard = isNeedToTakeWarrentCard;
    }

    public void setIsPlayerNeedToPay(boolean isPlayerNeedToPay) {
        this.isPlayerNeedToPay = isPlayerNeedToPay;
    }

    public void setIsPlayerCanBuy(boolean isPlayerCanBuyCity) {
        this.isPlayerCanBuy = isPlayerCanBuyCity;
    }

    public boolean isIsPlayerCanBuySquare() {
        return this.isPlayerCanBuy;
    }

    public void setMyAssets(ArrayList<AssetType> myAssets) {
        this.myAssets = myAssets;
    }

    public ArrayList<AssetType> getMyAssets() {
        return myAssets;
    }

    public String getName() {
        return name;
    }

    public long getAmount() {
        return amount;
    }

    public int getSqureNum() {
        return squreNum;
    }

    public boolean isInJail() {
        boolean res = false;
        if (this.isInJail) {
            this.isInJail = false;
            res = true;
        }
        return res;
    }

    public boolean isInParking() {
        boolean res = false;
        if (this.isInParking) {
            this.isInParking = false;
            res = true;
        }
        return res;
    }

    public void setIsInJail(boolean isInJail) {
        this.isInJail = isInJail;
    }

    public void setIsInParking(boolean isInParking) {
        this.isInParking = isInParking;
    }

    public void addToAmount(long amountToAdd) {
        this.amount += amountToAdd;
    }

    public void move(int numberOfSteps, boolean isCanPasByStart) {
        this.squreNum = this.squreNum + numberOfSteps;
        if (this.squreNum > 36 && isCanPasByStart) {//// the player pass on start square
            addToAmount(200);
            this.isPassStartSqure = true;

        }
        this.squreNum = this.squreNum % 36;
    }

    public boolean isQuit() {
        return (this.amount == 0) || this.isNeedToQuit;
    }

    public void purchase(AssetType asset, long price) {
        this.amount = (int) (this.amount - price);
        this.addToMyAsetse(asset);

    }

    public void goTOJail() {
        this.squreNum = NUM_OF_JAIL_SQUARE;
        this.isInJail = true;
        //ConsolUI.msgPlayerInJail(player.getName());
    }

    public void isNeetToGoJail(boolean isNeedToGoJail) {

        this.isInJail = isNeedToGoJail;
    }

    void stayInParking() {
        this.isInParking = true;
    }

    public void setDoesPlayerNeedToPay(boolean doesPlayerNeedToPay) {
        this.isPlayerNeedToPay = doesPlayerNeedToPay;
    }

    public boolean isDoesPlayerNeedToPay() {
        return this.isPlayerNeedToPay;
    }

    public boolean isPlayerHaveTheMany(long price) {
        boolean result = false;
        if (this.amount >= price) {
            result = true;
        }
        return result;
    }

    public boolean pay(Player owner, long price) {
        boolean haveEnoughMony = false;
        if (this.isPlayerHaveTheMany(price)) {
            owner.addToAmount(price);
            this.amount = (int) (this.amount - price);
            haveEnoughMony = true;

        } else {
            owner.addToAmount(this.amount);
            haveEnoughMony = false;
            this.amount = 0;
            this.isNeedToQuit = true;
        }
        return haveEnoughMony;
    }
// return is player have the many

    public boolean payToTreasury(long sum) {
        if (this.isPlayerHaveTheMany(sum)) {
            this.amount = (int) (this.amount - sum);

        } else {
            this.amount = 0;
            this.isNeedToQuit = true;

        }
        return this.isPlayerHaveTheMany(sum);
    }

    public void setUpFlages() {
        this.isPlayerCanBuy = false;
        this.isPlayerNeedToPay = false;
        this.isNeedToTakeWarrentCard = false;
        this.isNeedToTakeSupriesCard = false;
    }

    private void addToMyAsetse(AssetType asset) {
        this.myAssets.add(asset);
    }

    int getNumOfCitiesFromCountrythatPlayerOwned(String cuntryName) {
        int numOfcities = 0;
        for (AssetType asset : myAssets) {
            if (asset.getClass().equals(CityType.class)) {
                CityType city = (CityType) asset;
                if (city.getCuntryName().equals(cuntryName)) {
                    numOfcities++;
                }
            }
        }
        return numOfcities;
    }

    public models.GetOutOfJailCard getCardGetOutFromJail() {
        return this.getOutOfJailCard;
    }

    public void setIsHaveGetOutOfJailCard(boolean isHaveGetHoutOfJailCard, GetOutOfJailCard getOutOfJailCard) {
        this.getOutOfJailCard = getOutOfJailCard;
        this.isHaveGetOutOfJailCard = isHaveGetHoutOfJailCard;

    }

    @Override
    public String toString() {
        return this.getName() + "\namount: " + this.amount;

    }

    public boolean isResign() {
        return this.resign;
    }

    abstract public boolean isHumen();

    public boolean isPassStartSqure() {
        boolean res = false;
        if (this.isPassStartSqure) {
            res = true;
            this.isPassStartSqure = false;
        }
        return res;
    }

}
