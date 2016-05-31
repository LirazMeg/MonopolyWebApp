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
public class MonetaryCard extends CardBase {

    private long sum;
    private Who type;

    public enum Who {
        TREASURY, PLAYERS

    }

    public MonetaryCard(long sum, String type, String text, long num) {

        super(num, text);
        this.sum = sum;
        switch (type) {
            case "TREASURY":
                this.type = Who.TREASURY;
                break;
            case "PLAYERS":
                this.type = Who.PLAYERS;
                break;
        }
    }

    public long getSum() {
        return sum;
    }

    public Who getType() {
        return type;
    }
    

}
