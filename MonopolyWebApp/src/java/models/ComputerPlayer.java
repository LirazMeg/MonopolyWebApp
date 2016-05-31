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
public class ComputerPlayer extends Player {

    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public boolean doYouWantToPurchaseHouse(CityType square, long price) {

        boolean buy = false;
        if (square.getCost() <= this.amount) {
            buy = true;
           // ConsolUI.computerPerchasAHouse(price, square.getName(), square.getCounterOfHouse() + 1);
        }
        return buy;

    }

    @Override
    public boolean doYouWantToPurchase(AssetType square, long price) {
        boolean buy = false;
        if (square.getCost() <= this.amount) {
            buy = true;
            //ConsolUI.computerPerchas(this.getName(),price, square.getName());
        }

        return buy;
    }

}
