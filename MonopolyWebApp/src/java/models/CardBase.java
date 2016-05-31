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
public abstract class CardBase {

    private long num;
    private String text;

    public CardBase(long num, String text) {
        this.num = num;
        this.text = text;
    }

    public long getNum() {
        return num;
    }

    public String getText() {
        return text;
    }

}
