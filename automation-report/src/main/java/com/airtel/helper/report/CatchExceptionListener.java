package com.airtel.helper.report;

import java.lang.reflect.Method;

import org.testng.ITestListener;
import org.testng.ITestResult;

import com.airtel.helper.exception.CatchException;
import com.airtel.helper.exception.GenericException;
import com.airtel.helper.logging.AirtelLoggingMgr;

/**
 * TestNG listener for {@link CatchException}: on failure, logs framework
 * exception details (error code + message) via {@link ReportHelper}.
 */
public class CatchExceptionListener implements ITestListener {

	private static final org.apache.logging.log4j.Logger LOGGER =
			AirtelLoggingMgr.getLogger(CatchExceptionListener.class);

	@Override
	public void onTestFailure(ITestResult result) {
		CatchException annotation = resolveAnnotation(result);
		if (annotation == null) {
			return;
		}

		Throwable throwable = unwrap(result.getThrowable());
		if (throwable == null) {
			return;
		}

		GenericException frameworkException = findGenericException(throwable);
		if (frameworkException != null) {
			reportFrameworkException(frameworkException);
			return;
		}

		if (annotation.includeAll()) {
			reportGenericFailure(throwable);
		}
	}

	private static CatchException resolveAnnotation(ITestResult result) {
		Method method = result.getMethod() != null ? result.getMethod().getConstructorOrMethod().getMethod() : null;
		if (method != null) {
			CatchException onMethod = method.getAnnotation(CatchException.class);
			if (onMethod != null) {
				return onMethod;
			}
		}
		Object instance = result.getInstance();
		if (instance != null) {
			Class<?> type = instance.getClass();
			while (type != null && type != Object.class) {
				CatchException onClass = type.getAnnotation(CatchException.class);
				if (onClass != null) {
					return onClass;
				}
				type = type.getSuperclass();
			}
		}
		return null;
	}

	private static Throwable unwrap(Throwable throwable) {
		Throwable current = throwable;
		while (current != null && current.getCause() != null
				&& (current instanceof java.lang.reflect.InvocationTargetException
						|| current instanceof java.lang.reflect.UndeclaredThrowableException
						|| current.getClass().getName().contains("UndeclaredThrowable"))) {
			current = current.getCause();
		}
		return current;
	}

	private static GenericException findGenericException(Throwable throwable) {
		Throwable current = throwable;
		while (current != null) {
			if (current instanceof GenericException) {
				return (GenericException) current;
			}
			current = current.getCause();
		}
		return null;
	}

	private static void reportFrameworkException(GenericException exception) {
		String errorCode = exception.getErrorCode() != null ? exception.getErrorCode() : "N/A";
		String message = exception.getMessage() != null ? exception.getMessage() : exception.toString();
		try {
			ReportHelper.logValidationFailure(
					exception.getClass().getSimpleName(),
					"no framework exception",
					"[" + errorCode + "] " + message,
					"FrameworkException");
			ReportHelper.reporterLogging(false, "Caught framework exception [" + errorCode + "]: " + message);
		} catch (Exception e) {
			LOGGER.error("Unable to report framework exception [{}]: {}", errorCode, message, e);
		}
	}

	private static void reportGenericFailure(Throwable throwable) {
		String message = throwable.getMessage() != null ? throwable.getMessage() : throwable.toString();
		try {
			ReportHelper.logValidationFailure(
					throwable.getClass().getSimpleName(),
					"no exception",
					message,
					"Exception");
			ReportHelper.reporterLogging(false, "Caught exception: " + message);
		} catch (Exception e) {
			LOGGER.error("Unable to report exception: {}", message, e);
		}
	}
}
