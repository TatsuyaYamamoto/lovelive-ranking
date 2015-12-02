package net.sokontokoro_factory.lib.twitter.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class ConfigTest {


	@Test
	public void keyからStringValueを返す() {
		String actual = Config.getString("testString");
		assertThat(actual, is("chunchun"));
	}
	@Test
	public void keyからintValueを返す() {
		int actual = Config.getInt("testInt");
		assertThat(actual, is(123));
	}	
}
