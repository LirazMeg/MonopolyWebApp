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
public class GotoCard extends CardBase {

    private To type;

    public enum To {
        START, NEXT_SURPRISE, JAIL, NEXT_WARRANT
    }

    public GotoCard(String text, long num, String type) {
        super(num, text);
        switch (type) {
            case "START":
                this.type = To.START;
                break;
            case "NEXT_SURPRISE":
                this.type = To.NEXT_SURPRISE;
                break;
            case "JAIL":
                this.type = To.JAIL;
                break;
            case "NEXT_WARRANT":
                this.type = To.NEXT_WARRANT;
                break;
        }
    }

    public To getType() {
        return type;
    }

}
