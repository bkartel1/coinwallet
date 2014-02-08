/**
 * 
 */
package com.teambitcoin.coinwallet.api;

import static org.junit.Assert.*;

import org.junit.Test;

import com.teambitcoin.coinwallet.models.User;

/**
 * @author Michael Williams
 *
 */
public class UserTest {

	/**
	 * Test method for {@link com.teambitcoin.coinwallet.models.User#getUsername()}.
	 */
//	@Test
//	public final void testGetUsername() {
//		fail("Not yet implemented"); // TODO
//	}

	/**
	 * Test method for {@link com.teambitcoin.coinwallet.models.User#generateAccount()}.
	 */
//	@Test
//	public final void testGenerateAccount() {
//		fail("Not yet implemented"); // TODO
//	}

	/**
	 * Test method for {@link com.teambitcoin.coinwallet.models.User#getSQLInitQuery()}.
	 */
//	@Test
//	public final void testGetSQLInitQuery() {
//		fail("Not yet implemented"); // TODO
//	}

	/**
	 * Test method for {@link com.teambitcoin.coinwallet.models.User#isValidUsername(java.lang.String)}.
	 */
	@Test
	public final void testIsValidUsername() {
		String phoneOne = "5146621018";
		String phoneTwo = "514-662-1018";
		String phoneThree = "+1-514-662-1018";
		String email = "draringi@draringi.net";
		String bad = "bob";
		assertTrue(User.isValidUsername(phoneOne));
		assertTrue(User.isValidUsername(phoneTwo));
		assertTrue(User.isValidUsername(phoneThree));
		assertTrue(User.isValidUsername(email));
		assertFalse(User.isValidUsername(bad));
	}

}
