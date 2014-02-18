package com.teambitcoin.coinwallet;

//import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
//import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.*;
import com.teambitcoin.coinwallet.models.*;

public class ForgotPassword extends Activity {
	String username;// username variable to hold the input
	String secretQuestionDb;// variable to hold the secret question query from
							// database
	String answer;// answer variable to the input
	String DbAnswer;// variable to hold answer query from database
	String DbPassword;// variable to hold password query from database

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_main);

		// added this text when hit done after the user entered username
		final EditText editText1 = (EditText) findViewById(R.id.username_field);// had
																				// to
																				// make
																				// it
																				// final
																				// in
																				// order
																				// to
																				// work...

		editText1.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Context context = getApplicationContext();
					CharSequence text = "Successfully entered username";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					username = editText1.getText().toString();

					CharSequence usernamePrint = "Welcome " + username
							+ " your question will appear shortly";
					int durationUsername = Toast.LENGTH_LONG;
					Toast toastUsername = Toast.makeText(context,
							usernamePrint, durationUsername);
					toastUsername.show();

					// method to query the secret question from database
					// according to username entered...
					// this method return a string with the question
					querySecretQuestion(username);
					handled = true;
				}
				return handled;
			}
		});

		// added this text when hit done after the user entered answer
		final EditText editText2 = (EditText) findViewById(R.id.password_field);

		editText2.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {

					Context context = getApplicationContext();
					CharSequence text = "Successfully answered question";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();

					answer = editText2.getText().toString();// set the variable
															// answer to be the
															// inputted answer
															// from user...
					handled = true;

				}
				return handled;
			}
		});

		// adding the button object

		Button button = (Button) findViewById(R.id.login_button);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Context context = getApplicationContext();
				CharSequence text = "Checking answer...";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				validateAnswer(answer);// added this method
				// call the method which will check if the answer entered
				// correspond
				// to the right answer to that member's secret question
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public String querySecretQuestion(String username) {
		if (User.getUser(username) == null) {
			Context context = getApplicationContext();
			secretQuestionDb = null;
			CharSequence secretQuestion = "No Secret Question associated with the inputted username";// put
																										// the
																										// secret
																										// question
																										// here
			int duration2 = Toast.LENGTH_LONG;// stay on screen longer
			Toast toast2 = Toast.makeText(context, secretQuestion, duration2);
			toast2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, -40);
			toast2.show();

			return secretQuestionDb;
		}
		if (User.getUser(username).getQuestion() == null) {
			Context context = getApplicationContext();
			secretQuestionDb = null;

			CharSequence secretQuestion = "No Secret Question associated with the inputted username";// put
																										// the
																										// secret
																										// question
																										// here

			int duration2 = Toast.LENGTH_LONG;// stay on screen longer
			Toast toast2 = Toast.makeText(context, secretQuestion, duration2);
			toast2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, -40);
			toast2.show();

			return secretQuestionDb;
		} else {
			Context context = getApplicationContext();
			secretQuestionDb = User.getUser(username).getQuestion();

			CharSequence secretQuestion = "Secret question: "
					+ secretQuestionDb;// put the secret question here

			int duration2 = Toast.LENGTH_LONG;// stay on screen longer
			Toast toast2 = Toast.makeText(context, secretQuestion, duration2);
			toast2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, -40);
			toast2.show();

			return secretQuestionDb;
		}
		// take the inputted username and go to the database first check if that
		// username exist
		// if not output the entered username does not exist

		// if the username exist query the secret question associated with it
		// and return it

	}

	public String validateAnswer(String answer) {
		if (User.getUser(username) == null) {
			Context context = getApplicationContext();
			DbPassword = null;

			CharSequence secretAnswer = "Could not find any user with the given username";

			int duration2 = Toast.LENGTH_LONG;// stay on screen longer
			Toast toast2 = Toast.makeText(context, secretAnswer, duration2);
			toast2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, -40);
			toast2.show();
			return DbPassword;
		}
		if (User.getUser(username).recoverPassword(answer) == null) {// if the
																		// password
																		// is
																		// null,
																		// wrong
																		// answer
																		// inputted
																		// no
																		// password
																		// shown
			DbPassword = null;

			Context context = getApplicationContext();
			CharSequence text = "Wrong answer to question, please try again";// wrong
																				// answer
																				// no
																				// password
																				// shown
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, -40);
			toast.show();
			return DbPassword;

		} else {// if correct answer shown, show password to user
			DbPassword = User.getUser(username).recoverPassword(answer);

			Context context = getApplicationContext();
			CharSequence text = "Password: " + DbPassword;// set to password
															// query from
															// database
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, -40);
			toast.show();
			return DbPassword;
		}
		// query from database the answer associated to the user's secret
		// question, if the inputted answer
		// equals the one saved in the database output the user recorded
		// password else print WRONG ANSWER...
	}

}