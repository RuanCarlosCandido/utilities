package org.util;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class GeneralPurposesUtil {

	static final PrintStream printStreamOriginal = System.out;

	private ObjectMapper mapper = new ObjectMapper();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

	/**
	 * @param <T>
	 * @param json
	 * @return Clazz instance
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String json, Class<?> clazz) {
		try {
			Object o = mapper.readValue(json, clazz);
			return (T) o;
		} catch (IOException e) {
			logger.warn(
					"Cannot deserialize JSON: {} \nto {} instance. \nException catch at {} deserialize(String json, Class<?> clazz) method \nreason: {}: ",
					json, clazz, this.getClass(), e.getMessage());

		}
		return null;
	}

	public String serialize(Object obj) {

		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.warn(
					"Cannot serialize Object: {}.\nException catch at {} serialize(String json, Class<?> clazz) method \nreason: {}: ",
					obj, this.getClass(), e.getMessage());

		}
		return null;

	}
}
