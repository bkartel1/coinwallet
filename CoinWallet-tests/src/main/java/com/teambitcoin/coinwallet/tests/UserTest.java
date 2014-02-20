package com.teambitcoin.coinwallet.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import android.test.AndroidTestCase;

import com.teambitcoin.coinwallet.models.User;
import com.teambitcoin.coinwallet.models.UserWrapper;
import com.teambitcoin.coinwallet.models.Database;
import com.teambitcoin.coinwallet.api.Account;

/**
 * @author Michael Williams
 *
 */
public class UserTest extends AndroidTestCase {

	private User testuser;

	/**
	 * Test method for {@link com.teambitcoin.coinwallet.models.User#create(String, String, String, String, boolean)}.
	 */
	//@Test
	public final void testCreate() throws Exception{
		User bob = User.create("bob", "1234567890", "question", "answer", true);// returns null, as "bob" is not a valid username
		User alice = User.create("alice@wonder.land", "jabberwock", "Follow the white rabbit?", "yes", true);// returns User object, as "alice@wonder.land" is a valid username
		User aliceAgain = User.create("alice@wonder.land", "jabberwock", "Follow the white rabbit?", "yes", true);// returns null, as "alice@wonder.land" already exists
		User mtler = User.create("5146669090", "1234567890", "question", "answer", true);// returns User object, as "5146669090" is a valid (Monteal) phone number
		assertNull(bob);
		assertNotNull(alice);
		assertNull(aliceAgain);
		assertNotNull(mtler);
	}

	protected void setUp() throws Exception {
		Database.initializeDatabase(this.getContext());
		if(testuser==null){
			testuser=User.create("tester@test.dom", "testpassword", "Is the answer to this question is false?", "True!", true);
		}
	}

	/**
	 * Test method for {@link com.teambitcoin.coinwallet.models.User#generateAccount()}.
	 */
	//@Test
	public final void testGenerateAccount() throws Exception {
		Account acc = new UserWrapper().getUserAccount(testuser);
		assertTrue(acc.getUsername().equals("tester@test.dom"));
		assertTrue(acc.getPassword().equals("testpassword"));
		assertTrue(acc.getGuid().equals("tester@test.dom"));
	}
}
