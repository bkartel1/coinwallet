import com.teambitcoin.coinwallet.*;
import com.teambitcoin.coinwallet.models.User;

import org.junit.*;

public class ForgotPasswordTest {
	
	validatePasswordTest(){
		User.create(tabtoub, test1);
		//create a user...but where do we set its question and answer (no setter methods in User.java)??
		//i would set the answer to hyfr in the database and try if it succeed in validating the password...
		String answerDb = hyfr;
		String inputAnswer = hyfr;
		//method from database that will query the right answer...
		
		assertTrue(validatePassword(inputAnswer).equals(User.(getUser(tabtoub)).getDecodedPassword()));
		
	}
	
	validatePasswordTest2(){
		User.create(tabtoub, test2);
		//create a user...but where do we set its question and answer (no setter methods in User.java)??
		//i would set the answer to hyfr in database and then try if it fails in validating the password...
		String answerDb = hyfr;
		String inputAnswer = yolo;
		//method from database that will query the right answer...
		
		assertTrue(validatePassword(inputAnswer).equals(User.(getUser(tabtoub)).getDecodedPassword()));
		
	}
	
	

}
