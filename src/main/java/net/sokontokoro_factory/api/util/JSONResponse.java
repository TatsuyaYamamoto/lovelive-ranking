package net.sokontokoro_factory.api.util;

public class JSONResponse {
	
	/* type */
	public static final String NOT_SELECTED = "not selected";
	public static final String NOT_AUTHORIZED = "not authorized";
	public static final String NOT_REGISTRATION = "not registration";
	public static final String INVALID_ACCOUNT = "invalid account";
	public static final String INVALID_FORM = "invalid form";
	public static final String INVALID_TOKEN = "invalid token";	
	public static final String INTERNAL_SERVER_ERROR = "internal server error";	
	
	public static String message(String status, String message){
		return "{\"status\":\"" + status + "\",\"message\":\"" + message + "\"}";
	}
}
