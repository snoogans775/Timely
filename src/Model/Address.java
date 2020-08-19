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
public class Address {
    
    Integer addressid;
    String address;
    String address2;
    Integer cityId;
    String postalCode;
    String phone;
    Timestamp createDate;
    String createdBy;
    Timestamp lastUpdate;
    String lastUpdateBy;
    
    //Empty constructor
    public Address() {};

    public Address(Integer addressid, String address, String address2, Integer cityId, String postalCode, String phone, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.addressid = addressid;
        this.address = address;
        this.address2 = address2;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public Integer getAddressid() {
        return addressid;
    }
    
    public void setAddressid(int addressid) {
        this.addressid = addressid;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
            //Integer.toString(addressid), //Id is AUTO_INCREMENT in the database
            "'" + address + "'",
            "'" + address2 + "'",
            Integer.toString(cityId),
            "'" + postalCode + "'",
            "'" + phone + "'",
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
