package net.sokontokoro_factory.api.util.validation.constraint;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.sokontokoro_factory.api.util.Config;
import net.sokontokoro_factory.api.util.validation.CompatibleGame;

public class CompatibleGameValidator implements ConstraintValidator<CompatibleGame, String>{

	@Override
	public void initialize(CompatibleGame compatibleGame) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value == null){
			return true;
		}
		return isCompatibleGameValid(value, context);
	}
	
	static boolean isCompatibleGameValid(String value, ConstraintValidatorContext context) {
		
		String tmp = Config.getString("compatible.games");
		String[] compatibleGames = tmp.split(";", 0);
		
		return Arrays.asList(compatibleGames).contains(value);
	}

}
