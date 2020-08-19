/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import DB.Session;
import Model.Address;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kevin
 */
public class AddCustomerControllerTest {
    
    public AddCustomerControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initSession method, of class AddCustomerController.
     */
    @Test
    public void testInitSession() throws Exception {
        System.out.println("initSession");
        Session session = null;
        AddCustomerController instance = new AddCustomerController();
        instance.initSession(session);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveButtonPushed method, of class AddCustomerController.
     */
    @Test
    public void testSaveButtonPushed() throws Exception {
        System.out.println("saveButtonPushed");
        ActionEvent event = null;
        AddCustomerController instance = new AddCustomerController();
        instance.saveButtonPushed(event);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cancelButtonPushed method, of class AddCustomerController.
     */
    @Test
    public void testCancelButtonPushed() throws Exception {
        System.out.println("cancelButtonPushed");
        ActionEvent event = null;
        AddCustomerController instance = new AddCustomerController();
        instance.cancelButtonPushed(event);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAddressFromInput method, of class AddCustomerController.
     */
    @Test
    public void testGetAddressFromInput() throws Exception {
        System.out.println("getAddressFromInput");
        AddCustomerController instance = new AddCustomerController();
        Address expResult = null;
        Address result = instance.getAddressFromInput();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllFields method, of class AddCustomerController.
     */
    @Test
    public void testGetAllFields() {
        System.out.println("getAllFields");
        AddCustomerController instance = new AddCustomerController();
        List<String> expResult = null;
        List<String> result = instance.getAllFields();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateCountryList method, of class AddCustomerController.
     */
    @Test
    public void testGenerateCountryList() throws Exception {
        System.out.println("generateCountryList");
        AddCustomerController instance = new AddCustomerController();
        ObservableList<String> expResult = null;
        ObservableList<String> result = instance.generateCountryList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initialize method, of class AddCustomerController.
     */
    @Test
    public void testInitialize() {
        System.out.println("initialize");
        URL url = null;
        ResourceBundle rb = null;
        AddCustomerController instance = new AddCustomerController();
        instance.initialize(url, rb);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
