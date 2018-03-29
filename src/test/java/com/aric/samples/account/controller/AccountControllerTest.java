package com.aric.samples.account.controller;

import java.util.ArrayList;
import java.util.List;
import org.json.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aric.samples.account.model.Account;
import com.aric.samples.account.service.AccountService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

/**
 * @author Mehmet Taha Çorbacıoğlu - 150130017
 * @author Habib Beyatlı - 150120048
 */

public class AccountControllerTest {
	
	@Autowired
    private MockMvc mockMvc;
	@MockBean
	private AccountService accountService;

	private static final double DELTA = 1e-15;
	public static List<Account> accountList = new ArrayList<Account>();
	public static Account account, account2;
	private static final String FIRST_NAME = "Mehmet";
    private static final String LAST_NAME = "Corbaci";
    private static final long Tckn = 123;
    private static final double balance = 10000.0d;
    
	private static final String FIRST_NAME2 = "Habib";
    private static final String LAST_NAME2 = "Beyatli";
    private static final long Tckn2 = 1234;
    private static final double balance2 = 20000.0d;
	
    @BeforeClass
    public static void firstSetup() throws Exception{
    	account = new Account();
    	account2 = new Account();
    	accountList.add(account);
    }
    
	@Before
	public void setup() throws Exception{
    	account.setOwnerFirstName(FIRST_NAME);
    	account.setOwnerLastName(LAST_NAME);
    	account.setOwnerTckn(Tckn);
    	account.setBalance(balance);
    	account.setId(1L);
    	
    	account2.setOwnerFirstName(FIRST_NAME2);
    	account2.setOwnerLastName(LAST_NAME2);
    	account2.setOwnerTckn(Tckn2);
    	account2.setBalance(balance2);
    	account2.setId(2L);
        
        Mockito.when(accountService.findAccountsByFirstNameAndLastName(FIRST_NAME, LAST_NAME)).thenReturn(accountList);
        Mockito.when(accountService.deposit(1l, 100.0)).thenReturn(account);
        Mockito.when(accountService.deposit(3l, 100.0)).thenReturn(null);
        Mockito.when(accountService.transfer(1L, 2L, 100.0)).thenReturn(account);
        
        mockMvc.perform(post("/account").content("{\"balance\": 10000.0,\"Tckn\": 123,\"ownerFirstName\": \"Mehmet\",\"ownerLastName\": \"Corbaci\"}"));
        
	}

	@Test
	public void testQueryMissedParameter() throws Exception {
		String message = mockMvc.perform(get("/query").param("firstName", FIRST_NAME)).andExpect(status().isBadRequest()).andReturn().getResolvedException().getMessage();
		assertEquals(message, "Required String parameter 'lastName' is not present");
	}

	@Test
	public void testQueryEmptyResult() throws Exception{
		MvcResult result = mockMvc.perform(get("/query").param("firstName", FIRST_NAME).param("lastName", LAST_NAME2)).andExpect(status().isOk()).andReturn();
		assertEquals("[]", result.getResponse().getContentAsString()); //Empty result return
	}

	@Test
	public void testQuerySuccess() throws Exception{
		MvcResult result = mockMvc.perform(get("/query").param("firstName", FIRST_NAME).param("lastName", LAST_NAME)).andExpect(status().isOk()).andReturn();
		String resultS = result.getResponse().getContentAsString();
		JSONObject json =  new JSONObject(resultS.substring(1, resultS.length())); //I take string between 1 to length, because it is a list.
		assertEquals(FIRST_NAME, json.get("ownerFirstName"));
		assertEquals(LAST_NAME, json.get("ownerLastName"));
		assertEquals(Tckn, Long.valueOf(json.getLong("ownerTckn")), DELTA);
		assertEquals(balance, json.get("balance"));
		assertEquals(1, json.get("id"));
	}

	@Test
	public void testDepositMissedParameter() throws Exception{
		String message = mockMvc.perform(get("/deposit").param("id", "1")).andExpect(status().isBadRequest()).andReturn().getResolvedException().getMessage();
		assertEquals(message, "Required Double parameter 'amount' is not present");
	}

	@Test
	public void testDepositEmptyResult() throws Exception{
		MvcResult result = mockMvc.perform(get("/deposit").param("id", "3").param("amount", "100.0")).andExpect(status().isOk()).andReturn();
		assertNull(result.getResponse().getContentType()); //Returns null
	}

	@Test
	public void testDepositSuccess() throws Exception{
		account.deposit(100.0);
		MvcResult result = mockMvc.perform(get("/deposit").param("id", "1").param("amount", "100.0")).andExpect(status().isOk()).andReturn();
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(balance+100.0, json.getDouble("balance"), DELTA);
	}

	@Test
	public void testTransferMissedParameter() throws Exception{
		String message = mockMvc.perform(get("/transfer").param("senderId", "1")).andExpect(status().isBadRequest()).andReturn().getResolvedException().getMessage();
		assertEquals(message, "Required Long parameter 'receiverId' is not present");
	}

	@Test
	public void testTransferEmptyResult() throws Exception{
		MvcResult result = mockMvc.perform(get("/transfer").param("senderId", "1").param("receiverId", "10").param("amount", "100.0")).andExpect(status().isOk()).andReturn();
		assertNull(result.getResponse().getContentType()); //Returns null
	}

	@Test
	public void testTransferSuccess() throws Exception{
		accountList.add(account2);
		account.withdraw(100.0);
		MvcResult result = mockMvc.perform(get("/transfer").param("senderId", "1").param("receiverId", "2").param("amount", "100.0")).andExpect(status().isOk()).andReturn();
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(balance-100.0, json.getDouble("balance"), DELTA);
	}
}
