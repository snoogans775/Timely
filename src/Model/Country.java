/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author kevin
 */
public class Country {
    
    Integer countryid;
    String country;
    Timestamp createDate;
    String createdBy;
    Timestamp lastUpdate;
    String lastUpdateBy;
    
    //Empty constructor
    public Country() {};

    public Country(Integer countryid, String country, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.countryid = countryid;
        this.country = country;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public Integer getCountryid() {
        return countryid;
    }

    public void setCountryid(Integer countryid) {
        this.countryid = countryid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
    
    public String getAttributesAsSQL() {
        List<String> list = Arrays.asList(
            //Integer.toString(countryid), //Id is AUTO_INCREMENT in the database
            "'" + country + "'",
            "'" + createDate.toString() + "'",
            "'" + createdBy + "'",
            "'" + lastUpdate.toString() + "'",
            "'" + lastUpdateBy + "'"
        );
        
        StringBuilder sb = new StringBuilder();
        for( String str : list ) {
            sb = sb.append(str);
            sb =  sb.append(", ");
        }
        
        String result = sb.toString();
        
        //Clean up the final comma with a -2 substring
        result = result.substring(0, result.length() - 2);
        
        return result;
    }
}
