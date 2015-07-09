package com.humbletools.smalljavatools;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * CSVParser est un utilitaire permettant de lire un fichier CSV depuis un cheminFichier fourni. CSVParser se base sur la classe java.util.Scanner pour lire le
 * fichier et présenter ligne par ligne son contenu au code utilisant un CSVParser. CSVParser ne conservera pas de référence sur chaque ligne lue et il ne faut
 * pas garder chaque ligne en mémoire quand on utilise CSVParser : cela permet de ne pas saturer la mémoire et ainsi de lire des fichiers très lourds,
 * contrairement au com.atos.awl.pht.has.sara.batch.sigaes.tools.CsvReader qui ne supportera que de petits fichiers. L'encodage utilisé par défaut est
 * UTF-8.</br></br> Cas d'utilisation : </br> - Instancier un CSVParser via l'un des constructeurs</br> - Dans une boucle while (appeler hasNextLine())
 * récupérer chaque ligne sans conserver de référence en dehors de la boucle</br> - Quand on sort de la boucle, les ressources sont clôturées automatiquement
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
	 * Séparateur CSV par défaut correspondant à la locale fr
	 */
	public static final Character DEFAULT_CSV_SEPARATOR_FR = ';';

	/**
	 * Charset utilisé par défaut par un CSVParser s'il n'est pas spécifié au constructeur
	 */
	public static final String DEFAULT_CHARSET = "CP1252"; // CP1252 => plus connu sous le nom ANSI

	/**
	 * Crée un CSVParser avec le fichier spécifié par le filePath, le séparateur ';' par défaut et l'encodage par défaut DEFAULT_CHARSET
	 * 
	 * @throws FileNotFoundException si le fichier spécifié n'existe pas
	 */
	public CSVParser(final String filePath) throws FileNotFoundException {
		this(filePath, DEFAULT_CHARSET, DEFAULT_CSV_SEPARATOR_FR);
	}

	/**
	 * Crée un CSVParser avec le fichier spécifié par le filePath, le séparateur ';' par défaut et l'encodage charset
	 * 
	 * @throws FileNotFoundException si le fichier spécifié n'existe pas
	 */
	public CSVParser(final String filePath, final String charset) throws FileNotFoundException {
		this(filePath, charset, DEFAULT_CSV_SEPARATOR_FR);
	}

	/**
	 * Crée un CSVParser avec le fichier spécifié par le filePath, le séparateur csvSeparator et l'encodage par défaut DEFAULT_CHARSET
	 * 
	 * @throws FileNotFoundException si le fichier spécifié n'existe pas
	 */
	public CSVParser(final String filePath, final Character csvSeparator) throws FileNotFoundException {
		this(filePath, DEFAULT_CHARSET, csvSeparator);
	}

	/**
	 * Crée un CSVParser avec le fichier spécifié par le filePath, le séparateur csv csvSeparator et l'encodage charset
	 * 
	 * @throws FileNotFoundException si le fichier spécifié n'existe pas
	 */
	public CSVParser(final String filePath, final String charset, final Character csvSeparator) throws FileNotFoundException {
		if(charset==null){
			throw new IllegalArgumentException("L'encodage du fichier doit être spécifié !");
		}
		if(filePath == null){
			throw new IllegalArgumentException("L'argument filePath doit être spécifié !");
		}
		if (csvSeparator == null) {
			throw new IllegalArgumentException("L'argument csvSeparator soir être renseigné !");
		}
		this.filePath = filePath;
		this.charset = charset;
		separator = csvSeparator;
		inputStream = new FileInputStream(filePath);
		scanner = new Scanner(inputStream, charset);
		numberOfLinesRead = 0L;
	}

	/**
	 * Permet de lire la ligne suivante dans le fichier et de la retourner sous forme de String[]. Si on cherche à lire la première ligne et que le séparateur
	 * CSV est spécifié par le fichier, alors ce séparateur sera utilisé pour parser le fichier CSV. Dans ce cas, la méthode retournera directement la seconde
	 * ligne.
	 * 
	 * @throws IOException - Si une erreur I/O survient
	 * @throws IllegalStateException - Si le fichier ne contient pas de données
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
						throw new IllegalStateException("Le fichier CSV ne contient pas de données !");
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
	 * Renvoie true si le fichier lu contient encore une ligne après celle que l'on vient de lire. Renvoie false si on a atteint la fin du fichier. Cette
	 * méthode cloture automatiquement les ressources utilisées.
	 * 
	 * @throws IllegalStateException si les ressources sont fermées
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
	 * Retourne le chemin vers le fichier utilisé par ce CSVParser
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Retourne le charset utilisé par ce CSVParser
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Retourne le séparateur CSV utilisé par ce CSVParser
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
	 * Permet de comparer une ligne CSV avec un résultat attendu. On peut préciser si l'on souhaite trimmer les valeurs, si on veut gérer la casse et si l'on
	 * souhaite nettoyer la chaîne de caractères avec le pattern proposé avant de comparer.
	 * 
	 * @param expected les valeurs attendues
	 * @param values les valeurs lues
	 * @param trim mettre à vrai si on veut trimmer les chaînes avant comparaison
	 * @param putToLowercase mettre à vrai si l'on veut mettre à la casse basse avant comparaison
	 * @param removeUnsafeCharacters retire les caractères avec ce pattern regex [âäàêëéèöôùüûïîç&#@%',_\\t\\s]
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
						final String regexPattern = "[âäàêëéèöôùüûïîç&#@%',_\\t\\s]";
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
