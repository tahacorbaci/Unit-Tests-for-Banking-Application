package com.aric.samples.account.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Mehmet Taha Çorbacıoğlu - 150130017
 * @author Habib Beyatlı - 150120048
 */

public class AccountTest {
	private static final double DELTA = 1e-15; // to double values comparison
	private static double TEST_BALANCE = 43350.0;
	private static long TEST_ID = 1;
	private static long TEST_TCKN = 124;
	private static String TEST_FIRST_NAME = "Mehmet";
	private static String TEST_LAST_NAME = "Yar";
	
	Account madeupAccount = new Account();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setup(){
		madeupAccount.setBalance(TEST_BALANCE);
		madeupAccount.setId(TEST_ID);
		madeupAccount.setOwnerFirstName(TEST_FIRST_NAME);
		madeupAccount.setOwnerLastName(TEST_LAST_NAME);
		madeupAccount.setOwnerTckn(TEST_TCKN);
	}
	
	@Test
	public void testGetId(){
		assertEquals(TEST_ID,madeupAccount.getId());
	}
	
	@Test
	public void testGetBalance(){
		assertEquals(TEST_BALANCE,madeupAccount.getBalance(),DELTA);
	}
	
	@Test
	public void testGetOwnerTckn(){
		assertEquals(TEST_TCKN,madeupAccount.getOwnerTckn());
	}
	
	@Test
	public void testGetOwnerFirstName(){
		assertEquals(TEST_FIRST_NAME,madeupAccount.getOwnerFirstName());
	}

	@Test
	public void testGetOwnerLastName(){
		assertEquals(TEST_LAST_NAME,madeupAccount.getOwnerLastName());
	}
	
	@Test
	public void testSetOwnerTckn(){
		long newTCKN = 448;
		madeupAccount.setOwnerTckn(newTCKN);
		assertEquals(newTCKN,madeupAccount.getOwnerTckn());
	}
	
	@Test
	public void testSetId(){
		long newID = 852;
		madeupAccount.setId(newID);
		assertEquals(newID,madeupAccount.getId());
	}
	
	@Test
	public void testSetBalance(){
		double newBalance = 641.5;
		madeupAccount.setBalance(newBalance);
		assertEquals(newBalance,madeupAccount.getBalance(),DELTA);
	}
	
	@Test
	public void testSetOwnerFirstName(){
		String newFirstName = "Atakan";
		madeupAccount.setOwnerFirstName(newFirstName);
		assertEquals(newFirstName,madeupAccount.getOwnerFirstName());
	}
	
	@Test
	public void testSetOwnerLastName(){
		String newLastName = "Harani";
		madeupAccount.setOwnerLastName(newLastName);
		assertEquals(newLastName,madeupAccount.getOwnerLastName());
	}
	
	@Test
	public void testdepositSuccess(){
        double testAmount = 20d;
        madeupAccount.deposit(testAmount);
        assertEquals(testAmount + TEST_BALANCE,madeupAccount.getBalance(),DELTA);
	}

	@Test
	public void testdepositNegativeAmount(){
		double negativeAmount = -10d;
        thrown.expect(IllegalArgumentException.class); 
        thrown.expectMessage("Amount should be a positive value");//Expected message
        madeupAccount.deposit(negativeAmount);
	}
	
	@Test
	public void testwithdrawNegativeAmount(){
		double negativeAmount = -10d;
        thrown.expect(IllegalArgumentException.class); 
        thrown.expectMessage("Amount should be a positive value");//Expected message
        madeupAccount.withdraw(negativeAmount);
	}
	
	@Test
	public void testwithdrawSuccess(){
		double testAmount =  10000d;
        madeupAccount.withdraw(testAmount);
        assertEquals(TEST_BALANCE - testAmount,madeupAccount.getBalance(),DELTA);
	}

	@Test
	public void testwithdrawFail(){
		double testAmount = 30000d;
	    thrown.expect(IllegalArgumentException.class);
	    thrown.expectMessage("Amount cannot be greater than balance"); //Expected message
	    
	    //We understand that there is a security precaution which block withdrawing more than half of the balance. 
	    //If it happens, we will expect an exception. All in all, test will return success.
	    madeupAccount.withdraw(testAmount); 
	}
}
