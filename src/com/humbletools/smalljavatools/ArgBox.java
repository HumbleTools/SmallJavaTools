package com.humbletools.smalljavatools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * ArgBox is an abstract class able to statically manage your program arguments.
 * It's purpose is to be the simplest and most dynamic argument manager
 * possible, so you can focus on your program and not argument management.
 */
public abstract class ArgBox {

	private static class Argument {
		private final String argName;
		private final String shortCall;
		private final String longCall;
		private final String helpLine;
		private final Integer minOccur;
		private final Integer maxOccur;
		private final boolean valueNotRequired;

		public Argument(final String argName, final String shortCall, final String longCall, final String helpLine, final boolean valueNotRequired, final Integer minOccur, final Integer maxOccur) {
			this.argName = argName;
			this.shortCall = shortCall;
			this.longCall = longCall;
			this.helpLine = helpLine;
			this.valueNotRequired = valueNotRequired;
			this.minOccur = minOccur;
			this.maxOccur = maxOccur;
		}

		public String getArgName() {
			return argName;
		}
		public String getShortCall() {
			return shortCall;
		}
		public String getLongCall() {
			return longCall;
		}
		public String getHelpLine() {
			return helpLine;
		}
		public Integer getMinOccur() {
			return minOccur;
		}
		public Integer getMaxOccur() {
			return maxOccur;
		}
		public boolean isValueNotRequired() {
			return valueNotRequired;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((argName == null) ? 0 : argName.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Argument)) {
				return false;
			}
			final Argument other = (Argument) obj;
			if (argName == null) {
				if (other.argName != null) {
					return false;
				}
			} else if (!argName.equals(other.argName)) {
				return false;
			}
			return true;
		}

	}

	public static Set<String> registeredArgNames;
	public static Set<String> registeredShortCalls;
	public static Set<String> registeredLongCalls;

	/**
	 * The map that will contain the parsed command line. Keys are the argument
	 * names and values are... the values.
	 */
	private static Map<String, String> parsedArguments;

	/**
	 * Set containing all the arguments created by the working program with the
	 * register methods.
	 */
	private static Set<Argument> registeredArguments;

	/**
	 * Builds the help so the program using ArgBox can print it as it wishes,
	 * where it wishes.
	 */
	public static String getHelpString() {
		final StringBuilder helpBuilder = new StringBuilder("HELP\n\n");
		for (final Argument arg : registeredArguments) {
			helpBuilder.append(String.format("%1s$20s%2s/%3s$10s ", arg.getArgName(), arg.getShortCall(), arg.getLongCall()));
			helpBuilder.append(String.format("Must occur %1d to %2d times ", arg.getMinOccur(), arg.getMaxOccur()));
			helpBuilder.append(String.format("and must %1s be followed by a value.\n", arg.isValueNotRequired() ? "NOT" : ""));
			helpBuilder.append(arg.getHelpLine());
			helpBuilder.append("\n\n");
		}
		return helpBuilder.toString();
	}

	public static Map<String, String> resolveCommandLine(final String... args) {
		if ((args != null) && (args.length > 0)) {
			parsedArguments = new HashMap<String, String>();
			final Iterator<String> it = Arrays.asList(args).iterator();
			while (it.hasNext()) {
				final String arg = it.next();
				final Argument argument = resolveArgument(arg);
				if (argument != null) {
					if (!argument.isValueNotRequired() && it.hasNext()) {
						parsedArguments.put(argument.getArgName(), it.next());
					} else {
						parsedArguments.put(argument.getArgName(), null);
					}
				}
			}
		}
		return parsedArguments;
	}

	private static Argument resolveArgument(final String arg) {
		Argument result = null;
		for (final Argument argument : registeredArguments) {
			if (argument.getShortCall().equals(arg) || argument.getLongCall().equals(arg)) {
				result = argument;
				break;
			}
		}
		return result;
	}

	public static void register(final String argName, final String shortCall, final String longCall, final String helpLine) {
		register(argName, shortCall, longCall, helpLine, false, null, null);
	}

	public static void register(final String argName, final String shortCall, final String longCall, final String helpLine, final Boolean valueNotRequired) {
		register(argName, shortCall, longCall, helpLine, valueNotRequired, null, null);
	}

	public static void register(final String argName, final String shortCall, final String longCall, final String helpLine, final Boolean valueNotRequired, final Integer minOccur,
			final Integer maxOccur) {
		if (isBlank(argName) || isBlank(shortCall) || isBlank(longCall) || isBlank(helpLine) || (valueNotRequired == null)) {
			throw new IllegalArgumentException("Null or empty parameter : impossible to register argument !");
		}
		if (((minOccur != null) && (minOccur <= 0)) || ((maxOccur != null) && (maxOccur <= 0)) || ((minOccur != null) && (maxOccur != null) && (minOccur > maxOccur))) {
			throw new IllegalArgumentException(String.format("[%1s] minOccur and maxOccur must be both above zero and maxOccur must be superior or equal to minOccur !", argName));
		}
		if (!shortCall.startsWith("-")) {
			throw new IllegalArgumentException(String.format("[%1s] shortCall must start with '-' !", argName));
		}
		if (!longCall.startsWith("-")) {
			throw new IllegalArgumentException(String.format("[%1s] longCall must start with '--' !", argName));
		}
		if (registeredArgNames == null) {
			registeredArgNames = new HashSet<String>();
		}
		if (registeredShortCalls == null) {
			registeredShortCalls = new HashSet<String>();
		}
		if (registeredLongCalls == null) {
			registeredLongCalls = new HashSet<String>();
		}
		if (registeredArgNames.contains(argName)) {
			throw new IllegalArgumentException(String.format("An argument named %1s has already been registered !", argName));
		}
		if (registeredShortCalls.contains(argName)) {
			throw new IllegalArgumentException(String.format("An argument using the shortCall %1s has already been registered !", shortCall));
		}
		if (registeredLongCalls.contains(argName)) {
			throw new IllegalArgumentException(String.format("An argument using the longCall %1s has already been registered !", longCall));
		}
		registeredArgNames.add(argName);
		registeredShortCalls.add(shortCall);
		registeredLongCalls.add(longCall);
		registeredArguments.add(new Argument(argName, shortCall, longCall, helpLine, valueNotRequired, minOccur, maxOccur));
	}

	/**
	 * Returns true is the str string is null, empty or contains only
	 * whitespaces.
	 */
	private static boolean isBlank(final String str) {
		return (str == null) || str.trim().isEmpty();
	}
}
