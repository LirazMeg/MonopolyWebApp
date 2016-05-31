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
public class StartSquare extends SquareBase {

    final static int START_BONUS = 400;

    public StartSquare() {
    }

    @Override
    public void stepOnMe(Player pleyer) {

        pleyer.addToAmount(START_BONUS);
        //ConsolUI.msgStepOnStartSqure();
    }

    @Override
    public String toString() {
        return "start square";
    }

    @Override
    public String getInfo() {
        return toString();
    }

    @Override
    public String getImageName() {
        return "Start";
    }

}
