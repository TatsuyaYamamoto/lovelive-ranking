package net.sokontokoro_factory.api.util.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import net.sokontokoro_factory.api.util.validation.constraint.CompatibleGameValidator;

@Documented
@Constraint(validatedBy = {CompatibleGameValidator.class })
//@Target(ElementType.FIELD)
public @interface CompatibleGame {}