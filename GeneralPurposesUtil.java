package org.util;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class GeneralPurposesUtil {

	static final PrintStream printStreamOriginal = System.out;

	/**
	 * 
	 * @param clazz      = class where the method is present
	 * @param methodName = method name to be tested
	 * @param parameters = Parameters to the method to be tested
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static long getMethodProcessingTime(Class<?> clazz, String methodName, Object[] parameters) {

		long start = 0;
		long end = 0;
		try {

			validation(clazz, methodName, parameters);

			DisablePrint();

			Class[] parametersTypes = new Class[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				Class<? extends Object> paramClazz = parameters[i].getClass();

				if (paramClazz.toString().equals("class java.util.ImmutableCollections$MapN"))
					parametersTypes[i] = Map.class;
				else
					parametersTypes[i] = paramClazz;
			}

			Method method = clazz.getMethod(methodName, parametersTypes);

			start = System.currentTimeMillis();

			// Testing Method
			for (int i = 0; i < 1000000; i++)
				method.invoke(App.class.getDeclaredConstructor().newInstance(), parameters);

			end = System.currentTimeMillis();

			EnablePrint();

			System.out.println("Method: " + methodName + " RESULT =\r" + ((end - start)) + " millisecond");

		} catch (Exception e) {
			EnablePrint();
			e.printStackTrace();
		}

		return (end - start);
	}

	/**
	 * @param clazz
	 * @param methodName
	 * @param parameters
	 * @throws Exception
	 */
	private static void validation(Class<?> clazz, String methodName, Object[] parameters) throws Exception {
		Objects.nonNull(clazz);
		Objects.nonNull(methodName);
		Objects.nonNull(parameters);
		List.of(parameters).forEach(Objects::nonNull);

		if (methodName.isEmpty())
			throw new Exception("methodName cannot be null");
	}

	/**
	 * Ex: "INPUT", Map.of("var1Name", var1Value, "var2Name", var2Value, "var3Name",
	 * var3Value)
	 * 
	 * @param initialMessage
	 * @param args
	 */
	public static void printFormattedInfo(String initialMessage, Map<String, ?> args) {
		StringBuilder sb = new StringBuilder(initialMessage).append(": ");
		for (String arg : args.keySet()) {
			sb.append(arg.trim()).append(": ").append(args.get(arg)).append(", ");
		}
		System.out.println(sb.deleteCharAt(sb.lastIndexOf(",")));
	}

	private static void DisablePrint() {
		System.setOut(new PrintMethods(new java.io.OutputStream() {
			@Override
			public void write(int b) {
			}
		}));
	}

	private static void EnablePrint() {
		// re-enable print
		System.setOut(printStreamOriginal);
	}
}
