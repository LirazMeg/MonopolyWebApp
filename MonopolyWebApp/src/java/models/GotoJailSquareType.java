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
public class GotoJailSquareType extends SquareBase {

    final static int NUM_OF_JAIL_SQUARE = 10;

    @Override
    public void stepOnMe(Player pleyer) {
        pleyer.goTOJail();

    }

    @Override
    public String toString() {
        return "go to jail square";
    }

    @Override
    public String getInfo() {
        return this.toString();
    }

    @Override
    public String getImageName() {
        return "GoToJail";
    }

}
