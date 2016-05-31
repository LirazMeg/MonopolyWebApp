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
public class ParkingSquareType extends SquareBase {

    @Override
    public void stepOnMe(Player pleyer) {

        pleyer.stayInParking();
    }

    @Override
    public String toString() {
        return "parking square";
    }

    @Override
    public String getInfo() {
        return this.toString();
    }

    @Override
    public String getImageName() {
        return "Parking";
    }

}
