package main.java.com.teambitcoin.coinwallet;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.teambitcoin.coinwallet.*;
import main.java.com.teambitcoin.coinwallet.models.User;


public class ForgotPasswordTest {
	
	@Test
	public void recoverPassword() throws Exception{
		String username = "tester";
		String password = "tests";
		String answer = "hyhy";
		User user = User.create(username, password, "Question", answer, true);
		String returnedPassword = user.recoverPassword(answer);
		assertEquals(password, returnedPassword);
	}
	
//	validatePasswordTest(){
//		String answerDb = "hyfr";
//		String password = "test1"
//		User u = User.create("tabtoub", password, "question", answerDb);
//		//create a user...but where do we set its question and answer (no setter methods in User.java)??
//		//i would set the answer to hyfr in the database and try if it succeed in validating the password...
//		
//		String inputAnswer = "hyfr";
//		//method from database that will query the right answer...
//		
//		assertTrue(u.validatePassword(inputAnswer).equals(User.(getUser(tabtoub)).getDecodedPassword()));
//		
//	}
	
//	validatePasswordTest2(){
//		User.create(tabtoub, test2);
//		//create a user...but where do we set its question and answer (no setter methods in User.java)??
//		//i would set the answer to hyfr in database and then try if it fails in validating the password...
//		String answerDb = hyfr;
//		String inputAnswer = yolo;
//		//method from database that will query the right answer...
//		
//		assertTrue(validatePassword(inputAnswer).equals(User.(getUser(tabtoub)).getDecodedPassword()));
//		
//	}
	
	

}
