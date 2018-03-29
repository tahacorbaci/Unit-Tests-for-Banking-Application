package com.aric.samples.account.service;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;

import com.aric.samples.account.model.Account;
import com.aric.samples.account.repository.AccountRepository;

/**
 * @author Mehmet Taha Çorbacıoğlu - 150130017
 * @author Habib Beyatlı - 150120048
 */
public class AccountServiceTest {
	
	private static final double DELTA = 1e-15;
	private AccountService service = new AccountService();
	private AccountRepository accountRepository;
	
	public List<Account> accountList = new ArrayList<Account>();
	public Account account, account2;
	private static final String FIRST_NAME = "Mehmet";
    private static final String LAST_NAME = "Corbaci";
    private static final long Tckn = 123l;
    private static final double balance = 10000.0d;
    
	private static final String FIRST_NAME2 = "Habib";
    private static final String LAST_NAME2 = "Beyatli";
    private static final long Tckn2 = 1234l;
    private static final double balance2 = 20000.0d;
    
    @Before
    public void setup() throws Exception{
    	account = new Account();
    	account.setOwnerFirstName(FIRST_NAME);
    	account.setOwnerLastName(LAST_NAME);
    	account.setOwnerTckn(Tckn);
    	account.setBalance(balance);
    	account.setId(1L);
    	accountList.add(account);
    	
    	account2 = new Account();
    	account2.setOwnerFirstName(FIRST_NAME2);
    	account2.setOwnerLastName(LAST_NAME2);
    	account2.setOwnerTckn(Tckn2);
    	account2.setBalance(balance2);
    	account2.setId(2L);
    	
    	accountRepository = Mockito.mock(AccountRepository.class);
    	
        Field field = ReflectionUtils.findField(AccountService.class,"accountRepository");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field,service,accountRepository);
        
        Mockito.when(accountRepository.findByOwnerFirstNameAndOwnerLastName(FIRST_NAME, LAST_NAME)).thenReturn(accountList);
        Mockito.when(accountRepository.findOne(new Long(1L))).thenReturn(account);
        Mockito.when(accountRepository.findOne(new Long(2L))).thenReturn(account2);
        Mockito.when(accountRepository.save(account)).thenReturn(account);
    }
    
    @Test
    public void testFindAccountsByFirstNameAndLastName() throws Exception{
    	List<Account> accounts = service.findAccountsByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
    	
    	//Verify function calls
    	Mockito.verify(accountRepository).findByOwnerFirstNameAndOwnerLastName(FIRST_NAME, LAST_NAME);
    	
    	//Result Check
    	assertEquals(accounts.get(0), account);
    }
    
    @Test
    public void testDeposit() throws Exception{
    	Account temp = service.deposit(1L, 1200.0);
    	
    	//Verify function calls
    	Mockito.verify(accountRepository).findOne(1L); //Verify findOne function, submethod of the deposit, is called.
    	Mockito.verify(accountRepository).save(account); //Verify save function, submethod of the deposit, is called.
    	
    	//Result Check
    	assertEquals(balance + 1200.0, temp.getBalance(), DELTA);
    }
    
    @Test
    public void testTransfer() throws Exception{
    	Account sender = service.transfer(1L, 2L, 1200.0);
    	
    	//Verify function calls
    	Mockito.verify(accountRepository).findOne(1L);
    	Mockito.verify(accountRepository).findOne(2L);
    	Mockito.verify(accountRepository).save(account);
    	Mockito.verify(accountRepository).save(account2);
    	
    	//Result Check
    	assertEquals(balance - 1200.0, sender.getBalance(), DELTA);
    	// According to homework pdf file, errors found in the code will not be corrected. Tests to write about the wrong part are expected to fail.
    	assertEquals(balance2 + 1200.0, account2.getBalance(), DELTA); //We expect failure because function returns balance2+2*amount, it should be same as withdraw amount.
    }
    
}
