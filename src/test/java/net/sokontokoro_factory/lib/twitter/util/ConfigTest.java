package net.sokontokoro_factory.lib.twitter.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ResourceBundle;

import org.junit.Test;

public class ConfigTest {

	@Test
	public void Resourceファイルを取得できる(){
		ResourceBundle rb = Config.getResouce();
		String actual = rb.getBaseBundleName();
		assertThat(actual, is("twitterConfig"));
	}

	@Test
	public void keyからStringValueを返す() {
		String actual = Config.getString("test.signature");
		assertThat(actual, is("ykA6blBRBATy6fYgQP3uXXTqh4s%3D"));
	}
	@Test
	public void keyからintValueを返す() {
		int actual = Config.getInt("test.id");
		assertThat(actual, is(298062670));
	}	
}
