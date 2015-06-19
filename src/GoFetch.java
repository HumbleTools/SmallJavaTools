import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is a small program able to download files from urls, to a path on disk.
 * You can either specify a list of urls or a filename pattern with a number range and a base url.
 * 
 * @author lmadeuf
 *
 */
public class GoFetch {
	
	private enum arguments{
		URL("u", "url"),
		TARGET("t", "target"),
		URL_ARGUMENTS("ua", "urlarg"),
		
	}

	public static void main(final String[] args){
		if(args==null || args.length==0){
			System.out.println("No arguments specified. Please specify all of these : ");
			printHelp();
		}
		
	}
	
	/**
	 * Prints out the contents of the help.
	 */
	private static void printHelp() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Writes the data array to the path name specified.
	 * 
	 * @param data the byte[] containing the data of the downloaded file.
	 * @param fullPathName the path name on disk, ended by the file name.
	 * @throws IOException If an IOException is thrown by FileOutputStream (opening, writing, closing).
	 */
	private static void writeToDisk(final byte[] data, final String fullPathName) throws IOException{
		if(fullPathName==null || fullPathName.length()==0){
			throw new IllegalArgumentException("fullPathName null or empty");
		}
		if(data==null || data.length==0){
			throw new IllegalArgumentException("data null or empty");
		}
		final FileOutputStream out = new FileOutputStream(fullPathName);
		try {
		    out.write(data);
		} finally {
		    out.close();
		}
	}
	
	/**
	 * Downloads a file as a byte array from any url. This snippet has been taken from albertb on stackoverflow.
	 * 
	 * @param url the url to download a file from.
	 * @return byte[] the raw data
	 * @throws IOException if less bytes have been received than expected, or if read() from {@link java.io.InputStream} throws it, or if 
	 * openConnection() from {@link  java.net.URL} throws it.
	 */
	private static byte[] download(final URL url) throws IOException {
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
