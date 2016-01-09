package net.sokontokoro_factory.api.game.resource;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class ScoreTest extends JerseyTest{

//	@Test
	public void test() {
		String actual = target("/test").queryParam("name", "world").request().get(String.class);
		assertThat(actual,is("hoge"));
	}

}
