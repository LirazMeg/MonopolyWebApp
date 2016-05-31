/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author efrat
 */
public class CityType extends AssetType {

    private String cuntryName;
    private long numberOfCitiesInCountry;
    private long houseCost;
    private long stayCost1;
    private long stayCost2;
    private long stayCost3;
    private long counterOfHouse;

    public CityType(long houseCost, long stayCost1, long stayCost2, long stayCost3, String name, long cost, long staycost, String countryNamr) {
        super(name, cost, staycost);
        this.houseCost = houseCost;
        this.stayCost1 = stayCost1;
        this.stayCost2 = stayCost2;
        this.stayCost3 = stayCost3;
        this.cuntryName = countryNamr;
        this.numberOfCitiesInCountry = 1;
        this.counterOfHouse = 0;
    }

    public void setCounterOfHouse(long counterOfHouse) {
        this.counterOfHouse = counterOfHouse;
    }

    public void addToCounterOfHouse() {
        this.counterOfHouse++;
    }

    public long getCounterOfHouse() {
        return counterOfHouse;
    }

    public void setNumberOfCitiesInCountry(int numberOfCitiesInCountry) {
        this.numberOfCitiesInCountry = numberOfCitiesInCountry;
    }

    public long getNumberOfCitiesInCountry() {
        return numberOfCitiesInCountry;
    }

    public long getHouseCost() {
        return houseCost;
    }

    public long getStayCost1() {
        return stayCost1;
    }

    public long getStayCost2() {
        return stayCost2;
    }

    public long getStayCost3() {
        return stayCost3;
    }

    public String getCuntryName() {
        return cuntryName;
    }

    public void addOneToCounter() {
        this.counterOfHouse++;
    }

    public long getStayCost() {
        long stayCost = super.getStaycost();
        if (this.counterOfHouse == 1) {
            stayCost = this.stayCost1;
        } else if (this.counterOfHouse == 2) {
            stayCost = this.stayCost2;
        } else if (this.counterOfHouse == 3) {
            stayCost = this.stayCost3;
        }
        return stayCost;
    }
}
