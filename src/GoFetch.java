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
	 * Enum holding all the possible arguments for this program.
	 * 
	 * @author lmadeuf
	 * 
	 */
	private enum Argument {

		URL("-u", "--url", "TODO help of url"),
		TARGET("-t", "--target", "TODO help of target"),
		RANGE("-r", "--range", "TODO help of range");

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
		}

		public static Argument resolve(final String argline) {
			Argument result = null;
			for (final Argument arg : Argument.values()) {
				if (arg.shortLabel.equals(argline) || arg.longLabel.equals(argline)) {
					result = arg;
					break;
				}
			}
			return result;
		}
		
		public String toString(){
			return helpLine;
		}
	}

	public static void main(final String[] args) {
		final Map<Argument, String> arguments = resolveCommandLine(args);
		if (arguments != null) {
			final List<String> urlList = buildUrlList(arguments);
			if (urlList != null) {
				for(final String url : urlList){
					try {
						final byte[] data = download(new URL(url));
						writeToDisk(data, buildTargetFileName(arguments.get(Argument.TARGET), url));
					} catch (final IOException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		}
	}

	private static String buildTargetFileName(String target, final String url) {
		final StringBuilder builder = new StringBuilder();
		builder.append(target);
		if(!target.endsWith("/") && target.contains("/")){
			builder.append("/");
		}
		if(!target.endsWith("\\") && target.contains("\\")){
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
	 * @param args
	 *            the command line arguments.
	 * @return the map built.
	 */
	private static Map<Argument, String> resolveCommandLine(final String[] args) {
		Map<Argument, String> result = null;
		if (args == null || args.length == 0 || args.length != 6) {
			System.out.println("No arguments specified. Please specify all of these : ");
			printHelp();
		} else {
			result = new HashMap<Argument, String>();
			final Iterator<String> it = Arrays.asList(args).iterator();
	
			while (it.hasNext()) {
				String arg = (String) it.next();
				final Argument argument = Argument.resolve(arg);
				if (argument != null) {
					result.put(argument, (String) it.next());
				} else {
					System.out.println("Wrong command line. Please specify all of these : ");
					printHelp();
					result = null;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Prints out the help contents.
	 */
	private static void printHelp() {
		for (final Argument arg : Argument.values()) {
			System.out.println(arg.toString());
		}
	}

	/**
	 * Writes the data array to the path name specified.
	 * 
	 * @param data
	 *            the byte[] containing the data of the downloaded file.
	 * @param fullPathName
	 *            the path name on disk, ended by the file name.
	 * @throws IOException
	 *             If an IOException is thrown by FileOutputStream (opening,
	 *             writing, closing).
	 */
	private static void writeToDisk(final byte[] data, final String fullPathName)
			throws IOException {
		if (fullPathName == null || fullPathName.length() == 0) {
			throw new IllegalArgumentException("fullPathName null or empty");
		}
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("data null or empty");
		}
		System.out.println(String.format("Writing to : %s", fullPathName));
		final FileOutputStream out = new FileOutputStream(fullPathName);
		try {
			out.write(data);
		} finally {
			out.close();
		}
	}

	/**
	 * Downloads a file as a byte array from any url. This snippet has been
	 * taken from albertb on stackoverflow.
	 * 
	 * @param url
	 *            the url to download a file from.
	 * @return byte[] the raw data
	 * @throws IOException
	 *             if less bytes have been received than expected, or if read()
	 *             from {@link java.io.InputStream} throws it, or if
	 *             openConnection() from {@link java.net.URL} throws it.
	 */
	private static byte[] download(final URL url) throws IOException {
		System.out.println(String.format("Downloading from : %s", url));
		final URLConnection uc = url.openConnection();
		final int len = uc.getContentLength();
		final InputStream is = new BufferedInputStream(uc.getInputStream());
		try {
			byte[] data = new byte[len];
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
			return data;
		} finally {
			is.close();
		}
	}
}
