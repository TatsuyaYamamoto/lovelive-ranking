package net.sokontokoro_factory.lib.twitter.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class CalculationTest {

	@Test
	public void TwitterAPI用HmacSHA1化が出来る() throws InvalidKeyException, NoSuchAlgorithmException {
		String data = "d";
		String key = "k";
		int actual = ByteBuffer.wrap(Calculation.calcHmacSHA1(data, key)).getInt();
		int expected = 730915870;
		assertThat(actual, is(expected));
	}

}
