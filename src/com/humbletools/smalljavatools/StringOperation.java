package com.humbletools.smalljavatools;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* ==========================================================================================
 * repris depuis http://java.developpez.com/sources/?page=nombresDatesString#supprimmerAccents
 * ========================================================================================== */


/** Classe complementaire du J2SDK sur la manipulation de chaines de caractéres
 *  Permet nottament de supprimer les accents d'une chaine de caractères
 *
 *  Avantage :
 *   - Conserve la casse Majuscule / Minuscule
 *   - Transforme certains caractères spéciaux tel que ÷ en /
 *   - Gére le bi-caractères tels que Æ en AE
 *   - La transformation est efficace en determinant si le caractère est considéré avec accent ou non.
 *
 *
 *  Inconvenient :
 *   - la taille de la chaine résultat peut ne pas faire la même taille que la source. nottament a cause de Æ et æ
 *   - Certains caractères spéciaux sont transformé même si ce n'etait pas voulu.
 *   
 *   L'utilisation est simple :
 *   String chaine   = "Accés à la base";
 *   String chaine2 = StringOperation.sansAccent(chaine);  
 */
public abstract class StringOperation
{
	/** Index du 1er caractere accentué **/
	private static final int MIN = 192;
	/** Index du dernier caractere accentué **/
	private static final int MAX = 255;
	/** Index du premier chiffre**/
	private static final int MIN_NB = 48;
	/** Index du dernier chiffre**/
	private static final int MAX_NB = 57;
	/** Index de la premiere majuscule**/
	private static final int MIN_MAJ = 65;
	/**Index de la derniere majuscule**/
	private static final int MAX_MAJ = 90;
	/** Index de la premiere minuscule**/
	private static final int MIN_MIN = 97;
	/**Index de la derniere minuscule**/
	private static final int MAX_MIN = 122;
	/**Index de l'espace**/
	private static final int SPACE_VALUE = 32;
	/** Vecteur de correspondance entre accent / sans accent **/
	private static final Vector<String> map = initMap();
	/** Regexp : retours à la ligne */
	private static final String REGEXP_LINERET = "[\\r\\n]+";
	/** Chaine de caractères : simple espace */
	private static final String SPACE = " ";
	
	/** Initialisation du tableau de correspondance entre les caractéres accentués
	 * et leur homologues non accentués 
	 * @return
	 */
	private static Vector<String> initMap() {  
		Vector<String> Result         = new Vector<String>();
		java.lang.String car  = null;
		
		car = new java.lang.String("A");
		Result.add( car );            /* '\u00C0'   À   alt-0192  */ 
		Result.add( car );            /* '\u00C1'   Á   alt-0193  */
		Result.add( car );            /* '\u00C2'   Â   alt-0194  */
		Result.add( car );            /* '\u00C3'   Ã   alt-0195  */
		Result.add( car );            /* '\u00C4'   Ä   alt-0196  */
		Result.add( car );            /* '\u00C5'   Å   alt-0197  */
		car = new java.lang.String("AE");
		Result.add( car );            /* '\u00C6'   Æ   alt-0198  */
		car = new java.lang.String("C");
		Result.add( car );            /* '\u00C7'   Ç   alt-0199  */
		car = new java.lang.String("E");
		Result.add( car );            /* '\u00C8'   È   alt-0200  */
		Result.add( car );            /* '\u00C9'   É   alt-0201  */
		Result.add( car );            /* '\u00CA'   Ê   alt-0202  */
		Result.add( car );            /* '\u00CB'   Ë   alt-0203  */
		car = new java.lang.String("I");
		Result.add( car );            /* '\u00CC'   Ì   alt-0204  */
		Result.add( car );            /* '\u00CD'   Í   alt-0205  */
		Result.add( car );            /* '\u00CE'   Î   alt-0206  */
		Result.add( car );            /* '\u00CF'   Ï   alt-0207  */
		car = new java.lang.String("D");
		Result.add( car );            /* '\u00D0'   Ð   alt-0208  */
		car = new java.lang.String("N");
		Result.add( car );            /* '\u00D1'   Ñ   alt-0209  */
		car = new java.lang.String("O");
		Result.add( car );            /* '\u00D2'   Ò   alt-0210  */
		Result.add( car );            /* '\u00D3'   Ó   alt-0211  */
		Result.add( car );            /* '\u00D4'   Ô   alt-0212  */
		Result.add( car );            /* '\u00D5'   Õ   alt-0213  */
		Result.add( car );            /* '\u00D6'   Ö   alt-0214  */
		car = new java.lang.String("");
		Result.add( car );            /* '\u00D7'   ×   alt-0215  */
		car = new java.lang.String("0");
		Result.add( car );            /* '\u00D8'   Ø   alt-0216  */
		car = new java.lang.String("U");
		Result.add( car );            /* '\u00D9'   Ù   alt-0217  */
		Result.add( car );            /* '\u00DA'   Ú   alt-0218  */
		Result.add( car );            /* '\u00DB'   Û   alt-0219  */
		Result.add( car );            /* '\u00DC'   Ü   alt-0220  */
		car = new java.lang.String("Y");
		Result.add( car );            /* '\u00DD'   Ý   alt-0221  */
		car = new java.lang.String("p");
		Result.add( car );            /* '\u00DE'   Þ   alt-0222  */
		car = new java.lang.String("ss");
		Result.add( car );            /* '\u00DF'   ß   alt-0223  */
		car = new java.lang.String("a");
		Result.add( car );            /* '\u00E0'   à   alt-0224  */
		Result.add( car );            /* '\u00E1'   á   alt-0225  */
		Result.add( car );            /* '\u00E2'   â   alt-0226  */
		Result.add( car );            /* '\u00E3'   ã   alt-0227  */
		Result.add( car );            /* '\u00E4'   ä   alt-0228  */
		Result.add( car );            /* '\u00E5'   å   alt-0229  */
		car = new java.lang.String("ae");
		Result.add( car );            /* '\u00E6'   æ   alt-0230  */
		car = new java.lang.String("c");
		Result.add( car );            /* '\u00E7'   ç   alt-0231  */
		car = new java.lang.String("e");
		Result.add( car );            /* '\u00E8'   è   alt-0232  */
		Result.add( car );            /* '\u00E9'   é   alt-0233  */
		Result.add( car );            /* '\u00EA'   ê   alt-0234  */
		Result.add( car );            /* '\u00EB'   ë   alt-0235  */
		car = new java.lang.String("i");
		Result.add( car );            /* '\u00EC'   ì   alt-0236  */
		Result.add( car );            /* '\u00ED'   í   alt-0237  */
		Result.add( car );            /* '\u00EE'   î   alt-0238  */
		Result.add( car );            /* '\u00EF'   ï   alt-0239  */
		car = new java.lang.String("d");
		Result.add( car );            /* '\u00F0'   ð   alt-0240  */
		car = new java.lang.String("n");
		Result.add( car );            /* '\u00F1'   ñ   alt-0241  */
		car = new java.lang.String("o");
		Result.add( car );            /* '\u00F2'   ò   alt-0242  */
		Result.add( car );            /* '\u00F3'   ó   alt-0243  */
		Result.add( car );            /* '\u00F4'   ô   alt-0244  */
		Result.add( car );            /* '\u00F5'   õ   alt-0245  */
		Result.add( car );            /* '\u00F6'   ö   alt-0246  */
		car = new java.lang.String("");
		Result.add( car );            /* '\u00F7'   ÷   alt-0247  */
		car = new java.lang.String("o");
		Result.add( car );            /* '\u00F8'   ø   alt-0248  */
		car = new java.lang.String("u");
		Result.add( car );            /* '\u00F9'   ù   alt-0249  */
		Result.add( car );            /* '\u00FA'   ú   alt-0250  */
		Result.add( car );            /* '\u00FB'   û   alt-0251  */
		Result.add( car );            /* '\u00FC'   ü   alt-0252  */
		car = new java.lang.String("y");
		Result.add( car );            /* '\u00FD'   ý   alt-0253  */
		car = new java.lang.String("p");
		Result.add( car );            /* '\u00FE'   þ   alt-0254  */
		car = new java.lang.String("y");
		Result.add( car );            /* '\u00FF'   ÿ   alt-0255  */
		Result.add( car );            /* '\u00FF'       alt-0255  */
		
		return Result;
	}
	
	/** Transforme une chaine pouvant contenir des accents dans une version sans accent
	 *  @param chaine Chaine a convertir sans accent
	 *  @return Chaine dont les accents ont été supprimé
	 **/
	public static java.lang.String sansAccent(java.lang.String chaine) {  
		java.lang.StringBuffer Result  = new StringBuffer(chaine);
		
		for(int bcl = 0 ; bcl < Result.length() ; bcl++) { 
			int carVal = chaine.charAt(bcl);
			if( carVal >= MIN && carVal <= MAX ) {  // Remplacement
				java.lang.String newVal = (java.lang.String) map.get( carVal - MIN );
				Result.replace(bcl, bcl+1,newVal);
			}
			// Cas du caractere °
			if(carVal == 176) Result.replace(bcl, bcl+1, " ");
		}
		return Result.toString();
	}
	
	/** Transforme une chaine pouvant contenir des accents dans une version sans accents et sans espaces.
	 *  @param chaine Chaine a convertir sans accent et sans espaces
	 *  @return Chaine dont les accents et les espaces ont été supprimé
	 **/
	public static java.lang.String sansAccentNiEspaces(java.lang.String chaine) {  
		java.lang.StringBuffer Result  = new StringBuffer(chaine);
		
		for(int bcl = 0 ; bcl < Result.length() ; bcl++) { 
			int carVal = chaine.charAt(bcl);
			if( carVal >= MIN && carVal <= MAX ) {  // Remplacement
				java.lang.String newVal = (java.lang.String) map.get( carVal - MIN );
				Result.replace(bcl, bcl+1,newVal);
			}
			//Cas de l'espace
			if(carVal == SPACE_VALUE) Result.replace(bcl, bcl+1, "_");
		}
		return Result.toString();
	}


	public static String replaceAll(String ch, String replaceWhatPattern, String replaceWith) {
		Pattern pattern = Pattern.compile(replaceWhatPattern);
		Matcher matcher = pattern.matcher(ch);
		StringBuffer sb = new StringBuffer();
		while(matcher.find())
			matcher.appendReplacement(sb, replaceWith);
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	public static String firstCharUpperCase(String ch) {
		if (ch == null) return null;
		if (ch.length() == 0) return ch;
		if (ch.length() == 1) return ch.toUpperCase();
		return ch.substring(0, 1).toUpperCase() + ch.substring(1);
	}

	public static String firstCharLowerCase(String ch) {
		if (ch == null) return null;
		if (ch.length() == 0) return ch;
		if (ch.length() == 1) return ch.toLowerCase();
		return ch.substring(0, 1).toLowerCase() + ch.substring(1);
	}
	
	
	
	/**
	 * getSimpleClassName("pack1.pack2.MaClasse") renvoie "MaClasse".
	 * Ne renvoie pas null mais la chaîne vide si ch est null ou se termine par un point. 
	 */
	public static String getSimpleClassName(String ch) {
		if (ch == null) return "";
		int i = ch.lastIndexOf(".");
		if (i < 0) return ch;
		if (++i >= ch.length()) return "";
		return ch.substring(i);
	}
	
	/**
	 * getPackageName("pack1.pack2.MaClasse") renvoie "pack1.pack2".
	 * Ne renvoie pas null mais la chaîne vide si ch est null ou ne contient pas de point. 
	 */
	public static String getPackageName(String ch) {
		if (ch == null) return "";
		int i = ch.lastIndexOf(".");
		if (i < 0) return "";
		return ch.substring(0, i);
	}
	
	public static String removeNonAlphanumericOrUnderscoreOrDot(String ch) {
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9_\\.]");
		Matcher matcher = pattern.matcher(ch);
		StringBuffer sb = new StringBuffer();
		while(matcher.find())
			matcher.appendReplacement(sb, "_");
		matcher.appendTail(sb);
		return sb.toString();
	}

	// mantis 2281 : identifiant sans caracteres speciaux et espace
	public static String removeNonAlphanumeric(String ch) {
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
		Matcher matcher = pattern.matcher(ch);
		StringBuffer sb = new StringBuffer();
		while(matcher.find())
			matcher.appendReplacement(sb, " ");
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Renvoie la chaine de caractère avec les retours à la ligne remplacés par des espaces.<br/>
	 * Si la chaine en entrée est <code>null</code>, retourne une chaine vide (non-<code>null</code>)
	 * @param string la chaine à remplacer
	 * @return la chaine avec les retours à la ligne remplacés par des espaces
	 */
	public static String toSingleLine(String string) {
		if (string == null) return "";
		return string.replaceAll(REGEXP_LINERET, SPACE);
	}
	
	/**
	 * Changes first character of every word to upper case
	 */
	public static String firstLetterToUpperCase(String str)
	{
		Pattern spaces = Pattern.compile("\\s+[a-z]");       
		Matcher matcher = spaces.matcher(str);    
		StringBuilder capitalWordBuilder = new StringBuilder(str.substring(0,1).toUpperCase());
		int prevStart=1;
        while(matcher.find()) 
        {
            capitalWordBuilder.append(str.substring(prevStart, matcher.end()-1));
            capitalWordBuilder.append(str.substring(matcher.end()-1, matcher.end()).toUpperCase());
            prevStart = matcher.end();
        }   
        capitalWordBuilder.append(str.substring(prevStart, str.length()));
        return capitalWordBuilder.toString();
	}
}