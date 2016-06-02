/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.awt.event.ActionListener;

/**
 *
 * @author Liraz
 */
public abstract class SquareBase {

    abstract public void stepOnMe(Player player);

    abstract public String getInfo();

    abstract public String getImageName();

   // abstract public int getSqureId();
}
