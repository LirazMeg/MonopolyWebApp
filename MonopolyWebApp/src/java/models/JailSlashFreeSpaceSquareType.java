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
public class JailSlashFreeSpaceSquareType extends SquareBase {

    @Override
    public void stepOnMe(Player pleyer) {
        pleyer.setIsOnJailOrFreePass(true);
    }

    @Override
    public String toString() {
        return "jail/free space square";
    }

    @Override
    public String getInfo() {
        return this.toString();
    }

    @Override
    public String getImageName() {
        return "JailSlashFreeSpace";
    }

}
