/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.List;

/**
 *
 * @author efrat
 */
public class Assets {
    
    List<MonopolyModel.Country> Countries; 
    MonopolyModel.Utilities utilities;
    MonopolyModel.Transportations transportations;

    public Assets() {
        
    }

    public void setCountries(List<MonopolyModel.Country> Countries) {
        this.Countries = Countries;
    }

    public void setUtilities(MonopolyModel.Utilities utilities) {
        this.utilities = utilities;
    }

    public void setTransportations(MonopolyModel.Transportations transportations) {
        this.transportations = transportations;
    }

    public List<MonopolyModel.Country> getCountries() {
        return Countries;
    }

    public MonopolyModel.Utilities getUtilities() {
        return utilities;
    }

    public MonopolyModel.Transportations getTransportations() {
        return transportations;
    }
    
    
}
