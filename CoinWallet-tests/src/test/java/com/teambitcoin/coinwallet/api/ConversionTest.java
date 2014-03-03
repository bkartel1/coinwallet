package com.teambitcoin.coinwallet.api;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import com.teambitcoin.coinwallet.api.Conversion;
import com.teambitcoin.coinwallet.models.User;
import java.util.Arrays;

/**
 * @author Michael Williams
 *
 */
public class ConversionTest {

	private static final double DUMMY_RATE = 200;
	private static final String DUMMY_NAME = "DUD";
	private static final String DUMMY_SYMBOL = "$";
	private static final double DELTA_BTC = 0.00000001;
	private static final double DELTA_REAL = 0.01;

	private Conversion conversion;

	@Before
	public void setUp() throws Exception {
		conversion = new Conversion();
		conversion.addCurrency(new Conversion.Currency(DUMMY_NAME, DUMMY_RATE, DUMMY_SYMBOL));
        }

	@Test
	public final void testToBTC() throws Exception {
		double testValue = 400.00;
		double expected = testValue/DUMMY_RATE;
		double actual = conversion.toBTC(testValue, DUMMY_NAME);
		assertEquals(expected, actual, DELTA_BTC);
	}

	@Test
	public final void testToMoney() throws Exception {
		double testValue = 2.24235324;
		double expected = testValue*DUMMY_RATE;
		double actual = conversion.toMoney(testValue, DUMMY_NAME);
		assertEquals(expected, actual, DELTA_REAL);
	}

	@Test
	public final void testGetCurrencyList() throws Exception {
		String[] currencies = conversion.getCurrencyList();
		int result = Arrays.binarySearch(currencies, DUMMY_NAME);
		assertTrue(result >= 0);
	}

	@Test
        public final void testSupportsDefault() throws Exception {
                String[] currencies = conversion.getCurrencyList();
		String defaultCurrency = User.getDefaultCurrency();
                int result = Arrays.binarySearch(currencies, defaultCurrency);
                assertTrue(result >= 0);
        }
}
