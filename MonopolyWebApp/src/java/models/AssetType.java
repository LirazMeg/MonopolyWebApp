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
public abstract class AssetType {

    protected String name;
    private long cost;
    private long staycost;
    private Player owner;
    private boolean isHaveOwner;

    public AssetType(String name, long cost, long staycost) {
        this.name = name;
        this.cost = cost;
        this.staycost = staycost;
        this.owner = null;
        this.isHaveOwner = false;

    }

    public boolean isIsHaveOwner() {
        return this.isHaveOwner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public void setStaycost(long staycost) {
        this.staycost = staycost;
    }

    public void setHaveOwner(Player owner) {
        this.owner = owner;
        this.isHaveOwner = true;
    }

    public void setDoNotHaveOwner() {
        this.owner = null;
        this.isHaveOwner = false;
    }

    public String getName() {
        return this.name;
    }

    public long getCost() {
        return cost;
    }

    public long getStaycost() {
        return staycost;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean doYouHaveOwner() {
        boolean isHaveOwner = false;

        if (this.owner != null) {
            isHaveOwner = true;
        }
        return isHaveOwner;
    }
}
