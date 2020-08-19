package Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Customer extends Person {
    
    public Customer(
            int customerid, 
            String customerName, 
            int addressId, 
            int active, 
            Timestamp createDate, 
            String createdBy, 
            Timestamp lastUpdate, 
            String lastUpdateBy
    ) {
        this.customerid = customerid;
        this.customerName = customerName;
        this.addressId = addressId;
        this.active = active;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public int getCustomerid() {
        return customerid;
    }

    public void setCustomerid(int customerId) {
        this.customerid = customerid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
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
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getAttributesAsSQL() {
        List<String> list = Arrays.asList(
            Integer.toString(customerid),
            "'" + customerName + "'",
            Integer.toString(addressId),
            Integer.toString(active),
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
