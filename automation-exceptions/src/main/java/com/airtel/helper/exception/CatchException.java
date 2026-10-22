package com.airtel.helper.exception;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method or class so that framework {@link GenericException}s
 * (and subclasses) are logged to the HTML / Sentinel report on failure.
 * <p>
 * Register {@code com.airtel.helper.report.CatchExceptionListener} once in the LOB
 * suite or base test:
 *
 * <pre>
 * {@code
 * @Listeners(CatchExceptionListener.class)
 * public class ClmBaseTest { ... }
 *
 * @CatchException
 * @Test
 * public void createOrder() { ... }
 * }
 * </pre>
 * The test still fails; this only adds structured reporting for the error code.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CatchException {

	/**
	 * When {@code true}, also report failures that are not {@link GenericException}.
	 * Default is framework exceptions only.
	 */
	boolean includeAll() default false;
}
