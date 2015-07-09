package com.humbletools.smalljavatools;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * CSVParser est un utilitaire permettant de lire un fichier CSV depuis un cheminFichier fourni. CSVParser se base sur la classe java.util.Scanner pour lire le
 * fichier et pr�senter ligne par ligne son contenu au code utilisant un CSVParser. CSVParser ne conservera pas de r�f�rence sur chaque ligne lue et il ne faut
 * pas garder chaque ligne en m�moire quand on utilise CSVParser : cela permet de ne pas saturer la m�moire et ainsi de lire des fichiers tr�s lourds,
 * contrairement au com.atos.awl.pht.has.sara.batch.sigaes.tools.CsvReader qui ne supportera que de petits fichiers. L'encodage utilis� par d�faut est
 * UTF-8.</br></br> Cas d'utilisation : </br> - Instancier un CSVParser via l'un des constructeurs</br> - Dans une boucle while (appeler hasNextLine())
 * r�cup�rer chaque ligne sans conserver de r�f�rence en dehors de la boucle</br> - Quand on sort de la boucle, les ressources sont cl�tur�es automatiquement
 * 
 * @author lmadeuf - 06/2014
 */
public class CSVParser {


	private final FileInputStream inputStream;
	private final Scanner scanner;
	private final String filePath;
	private final String charset;

	private Character separator;
	private Long numberOfLinesRead;

	/**
	 * S�parateur CSV par d�faut correspondant � la locale fr
	 */
	public static final Character DEFAULT_CSV_SEPARATOR_FR = ';';

	/**
	 * Charset utilis� par d�faut par un CSVParser s'il n'est pas sp�cifi� au constructeur
	 */
	public static final String DEFAULT_CHARSET = "CP1252"; // CP1252 => plus connu sous le nom ANSI

	/**
	 * Cr�e un CSVParser avec le fichier sp�cifi� par le filePath, le s�parateur ';' par d�faut et l'encodage par d�faut DEFAULT_CHARSET
	 * 
	 * @throws FileNotFoundException si le fichier sp�cifi� n'existe pas
	 */
	public CSVParser(final String filePath) throws FileNotFoundException {
		this(filePath, DEFAULT_CHARSET, DEFAULT_CSV_SEPARATOR_FR);
	}

	/**
	 * Cr�e un CSVParser avec le fichier sp�cifi� par le filePath, le s�parateur ';' par d�faut et l'encodage charset
	 * 
	 * @throws FileNotFoundException si le fichier sp�cifi� n'existe pas
	 */
	public CSVParser(final String filePath, final String charset) throws FileNotFoundException {
		this(filePath, charset, DEFAULT_CSV_SEPARATOR_FR);
	}

	/**
	 * Cr�e un CSVParser avec le fichier sp�cifi� par le filePath, le s�parateur csvSeparator et l'encodage par d�faut DEFAULT_CHARSET
	 * 
	 * @throws FileNotFoundException si le fichier sp�cifi� n'existe pas
	 */
	public CSVParser(final String filePath, final Character csvSeparator) throws FileNotFoundException {
		this(filePath, DEFAULT_CHARSET, csvSeparator);
	}

	/**
	 * Cr�e un CSVParser avec le fichier sp�cifi� par le filePath, le s�parateur csv csvSeparator et l'encodage charset
	 * 
	 * @throws FileNotFoundException si le fichier sp�cifi� n'existe pas
	 */
	public CSVParser(final String filePath, final String charset, final Character csvSeparator) throws FileNotFoundException {
		if(charset==null){
			throw new IllegalArgumentException("L'encodage du fichier doit �tre sp�cifi� !");
		}
		if(filePath == null){
			throw new IllegalArgumentException("L'argument filePath doit �tre sp�cifi� !");
		}
		if (csvSeparator == null) {
			throw new IllegalArgumentException("L'argument csvSeparator soir �tre renseign� !");
		}
		this.filePath = filePath;
		this.charset = charset;
		separator = csvSeparator;
		inputStream = new FileInputStream(filePath);
		scanner = new Scanner(inputStream, charset);
		numberOfLinesRead = 0L;
	}

	/**
	 * Permet de lire la ligne suivante dans le fichier et de la retourner sous forme de String[]. Si on cherche � lire la premi�re ligne et que le s�parateur
	 * CSV est sp�cifi� par le fichier, alors ce s�parateur sera utilis� pour parser le fichier CSV. Dans ce cas, la m�thode retournera directement la seconde
	 * ligne.
	 * 
	 * @throws IOException - Si une erreur I/O survient
	 * @throws IllegalStateException - Si le fichier ne contient pas de donn�es
	 */
	public String[] getNextLineOfCells() throws IOException, IllegalStateException {
		String[] cellulesRetour = null;

		try {
			String ligneCSV = scanner.nextLine();
			numberOfLinesRead++;
			if (ligneCSV != null) {
				if ((numberOfLinesRead == 1L) && ligneCSV.contains("sep=") && (ligneCSV.length() == 5)) {
					separator = ligneCSV.charAt(4);
					if (scanner.hasNextLine()) {
						ligneCSV = scanner.nextLine();
						numberOfLinesRead++;
					} else {
						closeResources();
						throw new IllegalStateException("Le fichier CSV ne contient pas de donn�es !");
					}
				}
				cellulesRetour = ligneCSV.split(separator.toString());
			}
		} catch (final IllegalStateException ise) {
			closeResources();
			throw ise;
		} catch (final IOException ioe) {
			closeResources();
			throw ioe;
		}

		return cellulesRetour;
	}

	/**
	 * Renvoie true si le fichier lu contient encore une ligne apr�s celle que l'on vient de lire. Renvoie false si on a atteint la fin du fichier. Cette
	 * m�thode cloture automatiquement les ressources utilis�es.
	 * 
	 * @throws IllegalStateException si les ressources sont ferm�es
	 * @throws IOException si une erreur I/O survient
	 */
	public boolean hasNextLine() throws IOException {
		boolean hasNextLine = true;
		try {
			hasNextLine = scanner.hasNextLine();
			if (!hasNextLine) {
				closeResources();
			}
		} catch (final IllegalStateException ise) {
			closeResources();
			hasNextLine = false;
		} catch (final IOException ioe) {
			closeResources();
			throw ioe;
		}
		return hasNextLine;
	}

	private void closeResources() throws IOException {
		if (inputStream != null) {
			inputStream.close();
		}
		if (scanner != null) {
			scanner.close();
		}
	}

	/**
	 * Retourne le chemin vers le fichier utilis� par ce CSVParser
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Retourne le charset utilis� par ce CSVParser
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Retourne le s�parateur CSV utilis� par ce CSVParser
	 */
	public Character getSeparator() {
		return separator;
	}

	/**
	 * Retourne le nombre de lignes lues par le parseur
	 */
	public Long getNumberOfLinesRead() {
		return numberOfLinesRead;
	}

	/**
	 * Permet de comparer une ligne CSV avec un r�sultat attendu. On peut pr�ciser si l'on souhaite trimmer les valeurs, si on veut g�rer la casse et si l'on
	 * souhaite nettoyer la cha�ne de caract�res avec le pattern propos� avant de comparer.
	 * 
	 * @param expected les valeurs attendues
	 * @param values les valeurs lues
	 * @param trim mettre � vrai si on veut trimmer les cha�nes avant comparaison
	 * @param putToLowercase mettre � vrai si l'on veut mettre � la casse basse avant comparaison
	 * @param removeUnsafeCharacters retire les caract�res avec ce pattern regex [���������������&#@%',_\\t\\s]
	 * @return vrai si les tableaux contiennent des valeurs similaires, faux sinon
	 */
	public static boolean isValeursIdentiques(final String[] expected, final String[] values, final boolean trim, final boolean putToLowercase,
			final boolean removeUnsafeCharacters) {
		boolean isStructureCorrecte = true;
		if ((expected == null) || (values == null) || (expected.length != values.length)) {
			isStructureCorrecte = false;
		} else {
			for (int i = 0; i < expected.length; i++) {
				if ((expected[i] == null) || (values[i] == null)) {
					isStructureCorrecte = false;
					break;
				} else {
					String expectedValue = new String(expected[i]);
					String value = new String(values[i]);

					if (trim) {
						expectedValue = expectedValue.trim();
						value = value.trim();
					}
					if (putToLowercase) {
						expectedValue = expectedValue.toLowerCase();
						value = value.toLowerCase();
					}
					if (removeUnsafeCharacters) {
						final String regexPattern = "[���������������&#@%',_\\t\\s]";
						final String replacement = "";
						expectedValue = expectedValue.replaceAll(regexPattern, replacement);
						value = value.replaceAll(regexPattern, replacement);
					}
					if (!expectedValue.equalsIgnoreCase(value)) {
						isStructureCorrecte = false;
						break;
					}
				}
			}
		}
		return isStructureCorrecte;
	}

}
