package net.sokontokoro_factory.api.game.service;

import java.util.Base64;
import java.util.UUID;

public class TokenService{
	public static String generate(){
		byte[] token_uuid = UUID.randomUUID().toString().getBytes();
		String token_base64 = Base64.getEncoder().encodeToString(token_uuid);

		return token_base64;
	}
}
