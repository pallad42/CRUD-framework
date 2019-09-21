package com.framework.web.utils;

import java.lang.annotation.Annotation;

public class SearchAnnotation {

	public static boolean deepSearch(Class<?> from, Class<?> annotation) {

		boolean findResult = false;

		Annotation[] annotations = from.getAnnotations();

		boolean repeat = false;

		//System.err.println(from.getSimpleName());
		
		for (Annotation ann : annotations) {

			//System.out.println(ann);
			
			if (ann.annotationType().equals(annotation)) {
				//System.err.println("FOUND!");
				return true;
			}

			if (ann.annotationType().equals(from)) {
				//System.out.println("REPEAT!");
				repeat = true;
			}

		}

		if (repeat) {
			return false;
		}

		for (Annotation ann : annotations) {
			if(deepSearch(ann.annotationType(), annotation)) {
				return true;
			}
		}

		return findResult;
	}

}
