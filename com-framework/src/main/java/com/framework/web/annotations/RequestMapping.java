package com.framework.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.framework.web.enums.RequestMethod;
import com.framework.web.enums.ResponseContentType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })

public @interface RequestMapping {
	
	String[] value() default {};

	RequestMethod[] method() default {};

	ResponseContentType produces() default ResponseContentType.JSON;
	
}
