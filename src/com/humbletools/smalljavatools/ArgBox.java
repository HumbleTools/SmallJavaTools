package com.humbletools.smalljavatools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ArgBox is an abstract class able to statically manage your program arguments.
 * It's purpose is to be the simplest and most dynamic argument manager
 * possible, so you can focus on your program and not argument management.
 */
public abstract class ArgBox {

	private class Argument {
		private String	name;
		private String	shortCall;
		private String	longCall;
		private String	helpLine;

		public Argument(final String argName, final String shortCall, final String longCall, final String helpLine, final Integer minOccur, final Integer maxOccur) {
			// TODO
		}

		public String getName() {
			return name;
		}
		public void setName(final String name) {
			this.name = name;
		}
		public String getShortCall() {
			return shortCall;
		}
		public void setShortCall(final String shortCall) {
			this.shortCall = shortCall;
		}
		public String getLongCall() {
			return longCall;
		}
		public void setLongCall(final String longCall) {
			this.longCall = longCall;
		}
		public String getHelpLine() {
			return helpLine;
		}
		public void setHelpLine(final String helpLine) {
			this.helpLine = helpLine;
		}
		public Integer getMinOccur() {
			return minOccur;
		}
		public void setMinOccur(final Integer minOccur) {
			this.minOccur = minOccur;
		}
		public Integer getMaxOccur() {
			return maxOccur;
		}
		public void setMaxOccur(final Integer maxOccur) {
			this.maxOccur = maxOccur;
		}
		private Integer	minOccur;
		private Integer	maxOccur;
	}

	/**
	 * The map that will contain the parsed command line. Keys are the argument
	 * names and values are... the values.
	 */
	private static Map<String, String>	parsedArguments;

	/**
	 * Set containing all the arguments created by the working program with the
	 * register methods.
	 */
	private static Set<Argument>		registeredArguments;

	/**
	 * Builds the help so the program using ArgBox can print it as it wishes,
	 * where it wishes.
	 */
	public static String getHelpString() {
		// TODO
		return null;
	}

	public static Map<String, String> resolveCommandLine(final String... args) {
		parsedArguments = new HashMap<String, String>();
		// TODO
		return parsedArguments;
	}

	public static void register(final String argName, final String shortCall, final String longCall, final String helpLine) {
		register(argName, shortCall, longCall, helpLine, null, null);
	}

	public static void register(final String argName, final String shortCall, final String longCall, final String helpLine, final Integer minOccur, final Integer maxOccur) {
		// TODO
	}
}
