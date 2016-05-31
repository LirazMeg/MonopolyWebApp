/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;


/**
 *
 * @author Liraz
 */
public class SquareType extends SquareBase {

    private Type type;
    private AssetType asset;

    @Override
    public String getImageName() {
        String toReturn = "";
        switch (this.type) {
            case CITY:
                CityType city = (CityType) this.asset;
                toReturn = city.getCuntryName();
                break;
            case UTILITY:
                toReturn = this.getAsset().getName();
                break;
            case TRANSPORTATION:
                toReturn = this.getAsset().getName();
                break;
            case SURPRISE:
                toReturn = "Surprise";
                break;
            case WARRANT:
                toReturn = "Warrant";
                break;
        }
        return toReturn;
    }

  
    public enum Type {
        CITY, UTILITY, TRANSPORTATION, SURPRISE, WARRANT
    }

    public AssetType getAsset() {
        return asset;
    }

    public SquareType(String type) {
        switch (type) {
            case "CITY":
                this.type = Type.CITY;
                break;
            case "UTILITY":
                this.type = Type.UTILITY;
                break;
            case "TRANSPORTATION":
                this.type = Type.TRANSPORTATION;
                break;
            case "SURPRISE":
                this.type = Type.SURPRISE;
                break;
            case "WARRANT":
                this.type = Type.WARRANT;
                break;
        }

    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setAsset(AssetType asset) {
        this.asset = asset;
    }

    public Type getType() {
        return type;
    }

    public String getNameType() {
        return type.toString();

    }

    @Override
    public void stepOnMe(Player pleyer) {
        switch (this.type) {
            case CITY:
            case UTILITY:
            case TRANSPORTATION:
                stepOnAssetSquare(pleyer);
                break;
            case SURPRISE:
                pleyer.setIsNeedToTakeSupriesCard(true);
                break;
            case WARRANT:
                pleyer.setIsNeedToTakeWarrentCard(true);
                break;
        }
    }

    private void stepOnAssetSquare(Player pleyer) {
        if (this.asset.isIsHaveOwner()) { // this asset has some owner
            if (this.asset.getOwner().equals(pleyer)) { // the owner is the current player
                if (this.type.equals(Type.CITY)) {
                    pleyer.setIsPlayerCanBuy(true);
                }
            } else {
                pleyer.setDoesPlayerNeedToPay(true);
            }
        } else {
            pleyer.setIsPlayerCanBuy(true);
        }
    }

    @Override
    public String toString() {
        String toReturn = "";
        switch (this.type) {
            case CITY:
                CityType city = (CityType) this.asset;
                toReturn = city.getCuntryName() + ", " + city.getName();
                break;
            case UTILITY:
            case TRANSPORTATION:
                SimpleAssetType simpleAsset = (SimpleAssetType) this.asset;
                toReturn = simpleAsset.getName();
                break;
            case SURPRISE:
                toReturn = "take surprise card";
                break;
            case WARRANT:
                toReturn = "take warrant card";
                break;
        }
        return toReturn;
    }

    @Override
    public String getInfo() {
        String toReturn = "";
        switch (this.type) {
            case CITY:
                toReturn = getCitySquerInfo();
                break;
            case UTILITY:
                toReturn = "Utility,\n" + this.toString();
                break;
            case TRANSPORTATION:
                toReturn = "Transportion,\n" + this.toString();
                break;
            case SURPRISE:
                toReturn = "Surprise,\n" + this.toString();
                break;
            case WARRANT:
                toReturn = "Warrant,\n" + this.toString();
                break;
        }
        return toReturn;
    }

    private String getCitySquerInfo() {
        String toReturn = "City Square,\n";
        CityType city = (CityType) this.asset;
        toReturn = this.toString() + "\nOwner:";
        if (this.getAsset().doYouHaveOwner()) {
            toReturn += this.getAsset().getOwner().getName() + "\n";
        } else {
            toReturn += "-\n";
        }
        toReturn += "Number of Build Houses:" + city.getCounterOfHouse() + "\n";
        toReturn += "Cost: " + city.getCost() + "\n";
        toReturn += "House Cost: " + city.getHouseCost() + "\n";
        toReturn += "Stay Cost: " + city.getStayCost() + "\n";
        toReturn += "Stay Cost for 1 House: " + city.getStayCost1() + "\n";
        toReturn += "Stay Cost for 2 House: " + city.getStayCost2() + "\n";
        toReturn += "Stay Cost for 3 House: " + city.getStayCost3() + "\n";
        return toReturn;
    }
}
