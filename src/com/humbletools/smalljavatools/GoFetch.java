package com.humbletools.smalljavatools;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is a small program able to download files from urls to a path on
 * disk. You can specify a url pattern with a number range and a path on disk.
 * 
 * @author lmadeuf
 * 
 */
public class GoFetch {

	/**
	 * Command line example.
	 */
	private static final String ARG_EXAMPLE = "EXAMPLE : (java) GoFetch --url http://www.website.com/img[X].jpg --target C:/folder/a/ --range 1-6";

	/**
	 * Enum holding all the possible arguments for this program.
	 * 
	 * @author lmadeuf
	 */
	private enum Argument {

		URL("-u", "--url", "-u/--url		url argument, the url of the doxuments to download. Replace one integer occurence with '[X]', wich will be replaced by the numbers from the specified range."),
		TARGET("-t", "--target", "-t/--target		target argument, the value is a folder path on disk, required to write the files."),
		RANGE("-r", "--range", "-r/--range		range argument, requires a specific syntax value 'n1:n2' where n1 and n2 are two positive integers >= 0 and n1 >= n2."),
		HELP("-h", "--help", "-h/--help		help argument without value, will display the help of the program and shut it down."),
		SIMULATION("-s", "--simul", "-s/--simul		simul argument, no value required. If present, the files will not really be downloaded or written to disk, it will fake everything for debug/demonstration purposes.");

		/**
		 * The short version of the argument name.
		 */
		private String shortLabel;

		/**
		 * The long version of the argument name.
		 */
		private String longLabel;
		
		/**
		 * The line explaining whet the argument is for and how to use it. 
		 */
		private String helpLine;

		private Argument(final String shortLabel, final String longLabel, final String helpLine) {
			this.shortLabel = shortLabel;
			this.longLabel = longLabel;
			this.helpLine = helpLine;
		}

		/**
		 * Resolves a correct Argument enum value from it's string representation.
		 * @param argline the argument from the command line to be recognised.
		 * @return the Argument enum value found, if a match was detected.
		 */
		private static Argument resolve(final String argline) {
			Argument result = null;
			for (final Argument arg : Argument.values()) {
				if (arg.shortLabel.equals(argline) || arg.longLabel.equals(argline)) {
					result = arg;
					break;
				}
			}
			return result;
		}
		
		/**
		 * Retrieves the help text from the argument enum value.
		 */
		public String getHelpLine(){
			return helpLine;
		}
	}

	/**
	 * Where the magic happens.
	 * @param args the command line from the user.
	 */
	public static void main(final String[] args) {
		final Map<Argument, String> arguments = resolveCommandLine(args);
		if (arguments != null) {
			if(!arguments.containsKey(Argument.HELP)){
				final List<String> urlList = buildUrlList(arguments);
				if (urlList != null) {
					for(final String url : urlList){
						try {
							boolean simulation = arguments.containsKey(Argument.SIMULATION);
							final byte[] data = download(new URL(url), simulation);
							writeToDisk(data, buildTargetFileName(arguments.get(Argument.TARGET), url), simulation);
						} catch (final IOException e) {
							e.printStackTrace();
							break;
						}
					}
				}
			} else {
				printHelp();
			}
		}
	}

	/**
	 * Builds the full filepath of the target file by getting the filename from the compiled
	 * url and the filepath from the target path.
	 * @param target target file path
	 * @param url the dowload url
	 * @return the String representation of the target filename
	 */
	private static String buildTargetFileName(final String target, final String url) {
		final StringBuilder builder = new StringBuilder();
		builder.append(target);
		if(!target.endsWith("/") && target.contains("/")){
			builder.append("/");
		} else if(!target.endsWith("\\") && target.contains("\\")){
			builder.append("\\");
		}
		final String[] split = url.split("/");
		builder.append(split[split.length-1]);
		return builder.toString();
	}

	/**
	 * Builds the list of urls to fetch from the parsed command line.
	 */
	private static List<String> buildUrlList(final Map<Argument, String> args) {
		List<String> result = null;
		if (args != null) {
			result = new ArrayList<String>();
			final String[] range = args.get(Argument.RANGE).split("-");
			final int from = Integer.parseInt(range[0]);
			final int to = Integer.parseInt(range[1]);
			for(int i = from; i<=to; i++){
				result.add(args.get(Argument.URL).replaceFirst("\\[X\\]", String.valueOf(i)));
			}
		}
		return result;
	}

	/**
	 * Resolves the command line and builds a map with all the arguments found.
	 * 
	 * @param args the command line arguments.
	 * @return the map built.
	 */
	private static Map<Argument, String> resolveCommandLine(final String[] args) {
		Map<Argument, String> result = null;
		if (args == null || args.length == 0) {
			System.out.println("No arguments found on command line.");
			printHelp();
		} else {
			result = new HashMap<Argument, String>();
			final Iterator<String> it = Arrays.asList(args).iterator();
			while (it.hasNext()) {
				final String arg = (String) it.next();
				final Argument argument = Argument.resolve(arg);
				if (argument != null) {
					if(Argument.HELP.equals(argument) || Argument.SIMULATION.equals(argument)){
						result.put(argument, "");
					}else{
						result.put(argument, (String) it.next());
					}
				} else {
					System.out.println(String.format("This argument was not recognised : %s", arg));
					printHelp();
					result = null;
					break;
				}
			}
			if(result!=null){
				if(!result.containsKey(Argument.HELP) 
						&& (!result.containsKey(Argument.RANGE) || !result.containsKey(Argument.URL) || !result.containsKey(Argument.TARGET))){
					System.out.println("Incorrect number of arguments. At least -r/--range, -u/--url, -t/--target must be used.");
					printHelp();
					result = null;
				}
			}
		}
		return result;
	}

	/**
	 * Prints out the help contents.
	 */
	private static void printHelp() {
		System.out.println("\nHELP\n");
		for (final Argument arg : Argument.values()) {
			System.out.println(arg.getHelpLine());
		}
		System.out.println();
		System.out.println(ARG_EXAMPLE);
	}

	/**
	 * Writes the data array to the path name specified.
	 * 
	 * @param data
	 *            the byte[] containing the data of the downloaded file.
	 * @param fullPathName
	 *            the path name on disk, ended by the file name.
	 * @param simulation 
	 * @throws IOException
	 *             If an IOException is thrown by FileOutputStream (opening,
	 *             writing, closing).
	 */
	private static void writeToDisk(final byte[] data, final String fullPathName, final boolean simulation)
			throws IOException {
		if (fullPathName == null || fullPathName.length() == 0) {
			throw new IllegalArgumentException("fullPathName null or empty");
		}
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("data null or empty");
		}
		System.out.println(String.format("Writing to : %s", fullPathName));
		if(!simulation){
			final FileOutputStream out = new FileOutputStream(fullPathName);
			try {
				out.write(data);
			} finally {
				out.close();
			}
		}
	}

	/**
	 * Downloads a file as a byte array from any url. This snippet has been
	 * taken from albertb on stackoverflow.
	 * 
	 * @param url
	 *            the url to download a file from.
	 * @param simulation if true, the file will not really be downloaded
	 * @return byte[] the raw data
	 * @throws IOException
	 *             if less bytes have been received than expected, or if read()
	 *             from {@link java.io.InputStream} throws it, or if
	 *             openConnection() from {@link java.net.URL} throws it.
	 */
	private static byte[] download(final URL url, final boolean simulation) throws IOException {
		byte[] data = null;
		System.out.println(String.format("Downloading from : %s", url));
		if(!simulation){
			final URLConnection uc = url.openConnection();
			final int len = uc.getContentLength();
			final InputStream is = new BufferedInputStream(uc.getInputStream());
			try {
				data = new byte[len];
				int offset = 0;
				while (offset < len) {
					int read = is.read(data, offset, data.length - offset);
					if (read < 0) {
						break;
					}
					offset += read;
				}
				if (offset < len) {
					throw new IOException(String.format("Read %d bytes; expected %d", offset, len));
				}
			} finally {
				is.close();
			}
		} else {
			data = new byte[1];
		}
		return data;
	}
}
