package com.framework.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.framework.web.enums.RequestMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })

public @interface RequestParam {

	String value();

	boolean required() default true;
	
	RequestMethod method() default RequestMethod.GET;
	
	String defaultValue() default "";

}
