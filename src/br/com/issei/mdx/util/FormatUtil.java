package br.com.issei.mdx.util;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
/**
* Classe com serie de metodos estaticos de formatacao
* @author SYSGEN
*/

public class FormatUtil
{
    private static SimpleDateFormat formatFieldDate = null;
    private static SimpleDateFormat formatFieldTime = null;
    private static SimpleDateFormat formatFieldTimestamp = null;
	private static String           decimalCharacter = null;
	private static String           thousandCharacter = null;
	/*
	 * Getters e Setters
	 */
	public static SimpleDateFormat getFormatFieldDate()
	{
		return formatFieldDate;
	}
	public static SimpleDateFormat getFormatFieldTime()
	{
		return formatFieldTime;
	}
	public static SimpleDateFormat getFormatFieldTimestamp()
	{
		return formatFieldTimestamp;
	}
	public static String getDecimalCharacter()
	{
		return decimalCharacter;
	}
	public static String getThousandCharacter()
	{
		return thousandCharacter;
	}
	public static void setFormatFieldDate(SimpleDateFormat format)
	{
		formatFieldDate = format;
	}
	public static void setFormatFieldTime(SimpleDateFormat format)
	{
		formatFieldTime = format;
	}
	public static void setFormatFieldTimestamp(SimpleDateFormat format)
	{
		formatFieldTimestamp = format;
	}
	public static void setDecimalCharacter(String p)
	{
		decimalCharacter = p;
	}
	public static void setThousandCharacter(String p)
	{
		thousandCharacter = p;
	}

    /**
    * Transforma o parametro int recebido em String
    */
    public static String toString(int n)
    {
        return Integer.toString(n);
    }
    /**
    * Transforma o parametro Integer recebido em String
    */
    public static String toString(Integer n)
    {
        return n.toString();
    }

    /**
    * Transforma o parametro BigInteger recebido em String
    */
    public static String toString(BigInteger n)
    {
        return n.toString();
    }
    /**
    * Transforma o parametro Big decimal recebido em String
    */
    public static String toString(BigDecimal n)
    {
        return n.toString();
    }

    /**
    * Transforma o parametro String recebido em String
    * na realidade eh um metodo dummy (nao faz nada) usado pelo gerador.
    */
    public static String toString(String s)
    {
        return s;
    }

    /**
    * Transforma o parametro int recebido em String de tamanho "len"
    */
    public static String toString(int n, int len)
    {
        return fillLeftZeros(n, len);
    }
    /**
    * Transforma o parametro Integer recebido em String de tamanho "len"
    */
    public static String toString(Integer n, int len)
    {
        return fillLeftZeros(n.intValue(), len);
    }

    /**
    * Transforma o parametro BigInteger recebido em String de tamanho "len"
    */
    public static String toString(BigInteger n, int len)
    {
        return fillLeftZeros(n.intValue(), len);
    }
    /**
    * Transforma o parametro String recebido em String de tamanho "len"
    */
    public static String toString(String s, int len)
    {
        return fillRightBlanks(s, len);
    }
    /**
     * Repete o parametro String recebido em String com "n" repeticoes
     */
     public static String toStringRepeat(String s, int n)
     {
         if ((n<=0) || (s.length() <= 0))
         {
             return "";
         }
         StringBuffer sb = new StringBuffer(s);
         int count = 1;
         while (count < n)
         {
             sb.append(s);
             count++;
         }
         return sb.toString();         
     }

    /**
    * Retorna um numero em um String de determinado tamanho(len)
    * preenchido com zeros a esquerda.
    * Geralmente o metodo SgFormat.toString(int n , int len )
    * eh utilizado ao inves deste.
    */

    public static String fillLeftZeros(int n, int len)
    {

        StringBuffer sb = new StringBuffer(Integer.toString(n));
        while (sb.length() < len)
        {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    /**
    * Retorna um numero em um String de determinado tamanho(len)
    * preenchido com brancos a esquerda.
    */

    public static String fillLeftBlanks(int n, int len)
    {

        StringBuffer sb = new StringBuffer(Integer.toString(n));
        while (sb.length() < len)
        {
            sb.insert(0, " ");
        }
        return sb.toString();
    }

    /**
    * Retorna um String em um String de determinado tamanho(len)
    * preenchido com brancos a esquerda.
    */

    public static String fillLeftBlanks(String s, int len)
    {

        StringBuffer sb = new StringBuffer(s);
        while (sb.length() < len)
        {
            sb.insert(0, " ");
        }
        return sb.toString();
    } // public static String fillLeftBlanks(String s , int len )
    /**
     * Retorna um String em um String de determinado tamanho(len)
     * preenchido com caracteres a esquerda.
     */

     public static String fillLeftChars(String s, String c, int len)
     {

         StringBuffer sb = new StringBuffer(s);
         while (sb.length() < len)
         {
             sb.insert(0, c);
         }
         return sb.toString();
     } // public static String fillLeftChars(String s , int len )
    /**
    * Retorna um String em um String de determinado tamanho(len)
    * preenchido com brancos a direita.
    * Geralmente o metodo SgFormat.toString(String s , int len )
    * eh utilizado ao inves deste.
    */

    public static String fillRightBlanks(String s, int len)
    {
        
        StringBuffer sb = null;
        if (s == null)
        {
            sb = new StringBuffer("");
        }
        else
        {
            sb = new StringBuffer(s);
        }
        while (sb.length() < len)
        {
            sb.append(" ");
        }
        return sb.toString();
    } // public static String fillRightBlanks(String s ,int len )

    /**
    * Pega o indexador do String fornecido<br>
    * Exemplo: option[1], option[2], ... tfNmUsuario[10]....
    */
    public static int getIndexFromName(String p)
    {
        int nx = -1;
        if (p.indexOf("[") > 0)
        {
            String sx = p.substring(p.indexOf("[") + 1, p.indexOf("]"));
            try
            {
                nx = Integer.parseInt(sx.trim());
            }
            catch (NumberFormatException e)
            {
            }

        }
        return nx;
    }
    /**
    * Pega uma parametro numerico do String fornecido<br>
    * Exemplo: funcao(100)....
    */
    public static int getNumericParameterFromName(String p)
    {
        int nx = -1;
        if (p.indexOf("(") > 0)
        {
            String sx = p.substring(p.indexOf("(") + 1, p.indexOf(")"));
            try
            {
                nx = Integer.parseInt(sx.trim());
            }
            catch (NumberFormatException e)
            {
            }

        }
        return nx;
    }

    /**
    * Pega o nome do campo sem o Indexador<br>
    * Exemplo: option[1], option[2], ... tfNmUsuario[10]....
    */
    public static String getNameWithNoIndex(String p)
    {
        String sx = null;
        if (p.indexOf("[") > 0)
        {
            sx = p.substring(0, p.indexOf("[")).trim();
        }
        return sx;
    } // public static String getNameWithNoIndex( String p)

    /**
    * Separa um String baseado em um delimitador e retorna o elementos em um array de Strings<br>
    * Exemplo: val1;val2;val3;
    */
    public static String[] separate(String p, String del)
    {
        StringTokenizer tk = new StringTokenizer(p, del);

        String[] ret = new String[tk.countTokens()];
        int i = 0;

        while (tk.hasMoreTokens())
        {
            ret[i] = tk.nextToken();
            i++;
        }
        return ret;
    } // public static String[] separate( String p, String del)

    /**
    * Pega o sufixo numerico do String fornecido<br>
    * Exemplo: nome1, nome2, ... nomenNN....
    */
    public static int getSuffixNumber(String p)
    {

        char[] c = p.toCharArray();

        StringBuffer r = new StringBuffer("");

        int j = -1;

        for (int i = c.length - 1; i >= 0; i--)
        {
            if (Character.isDigit(c[i]))
            {
                r.insert(0, c[i]);
            }
            else
                break;
        }
        if (r.length() > 0)
        {
            return Integer.parseInt(r.toString());

        }
        return 0;

    }

    /**
    * Retorna a Data do sistema ignorando a hora minuto segundo.
    */
    public static java.sql.Date getCurrentDate()
    {
        java.util.Date dt = new Date();
        try
        {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            String ds = fmt.format(new java.util.Date(System.currentTimeMillis()));

            dt = fmt.parse(ds);

        }
        catch (java.text.ParseException e)
        {
			InfraUtil.err("ASPER: Erro em SgFormat.getCurrentDate = " + e.getMessage());
        }
        return new java.sql.Date(dt.getTime());
    }
    /**
     * Retorna a Data do sistema ignorando a hora minuto segundo.
     */
     public static String getCurrentDate(String pFormat)
     {
         try
         {
             java.util.Date dt = new Date();
             SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
             String ds = fmt.format(new java.util.Date(System.currentTimeMillis()));

             dt = fmt.parse(ds);
             
             SimpleDateFormat fmtResp = new SimpleDateFormat(pFormat);
             String resp = fmtResp.format(dt);
             
             return resp ;   

         }
         catch (java.text.ParseException e)
         {
            InfraUtil.err("ASPER: Erro em SgFormat.getCurrentDate = " + e.getMessage());
         }
         return "??DATE??";
     }    

    /**
    * Retorna um String com data recebida, formatado de acordo com a especificacao da
    * propriedade sysgen.date.format
    */
    public static String toString(java.util.Date pVal)
    {
        initDateFormats();
        if (pVal != null)
            return formatFieldDate.format(pVal);
        else
            return null;
    }
    /**
    * Retorna um String com data recebida, formatado de acordo com a especificacao da
    * propriedade sysgen.date.format
    */
    public static String toString(java.sql.Date pVal)
    {
        initDateFormats();
        if (pVal != null)
            return formatFieldDate.format(pVal);
        else
            return null;
    }
    /**
     * Retorna um String com data recebida, formatado de acordo com a especificacao da
     * propriedade sysgen.date.format
     */
     public static String toString(java.sql.Date pVal, String pFormat)
     {
         SimpleDateFormat fmtResp = new SimpleDateFormat(pFormat);
         String resp = fmtResp.format(pVal);
         
         return resp ;
     }    
    /**
    * Retorna um String com hora recebida, formatado de acordo com a especificacao da
    * propriedade sysgen.time.format
    */
    public static String toString(java.sql.Time pVal)
    {
        initDateFormats();
        if (pVal != null)
            return formatFieldTime.format(pVal);
        else
            return null;
    }
    /**
    * Retorna um String com hora recebida, formatado de acordo com a especificacao da
    * propriedade sysgen.timestamp.format
    */
    public static String toString(java.sql.Timestamp pVal)
    {
        initDateFormats();
        if (pVal != null)
            return formatFieldTimestamp.format(pVal);
        else
            return null;
    }
    /**
    * Retorna uma data a partir do String fornecido, deve vir
    * formatado de acordo com a especificacao da
    * propriedade sysgen.date.format
    */
    public static java.sql.Date parseDate(String pVal) throws java.text.ParseException
    {
        initDateFormats();
        if (pVal != null)
            return new java.sql.Date(formatFieldDate.parse(pVal).getTime());
        else
            return null;
    }
    /**
    * Retorna uma nora a partir do String fornecido, deve vir
    * formatado de acordo com a especificacao da
    * propriedade sysgen.time.format
    */
    public static java.sql.Time parseTime(String pVal) throws java.text.ParseException
    {
        initDateFormats();
        if (pVal != null)
            return new java.sql.Time(formatFieldTime.parse(pVal).getTime());
        else
            return null;
    }
    /**
    * Retorna uma nora a partir do String fornecido, deve vir
    * formatado de acordo com a especificacao da
    * propriedade sysgen.time.format
    */
    public static java.sql.Timestamp parseTimestamp(String pVal) throws java.text.ParseException
    {
        initDateFormats();
        if (pVal != null)
            return new java.sql.Timestamp(formatFieldTimestamp.parse(pVal).getTime());
        else
            return null;
    }
    /**
    * Inicializa os formatadores para datas e horas.
    */
    public static void initDateFormats()
    {

        if (formatFieldDate == null)
        {
            String sDatex = null;
            String sTimex = null;
            String sTimestampx = null;
            try
            {
                String sSysgenProperties = System.getProperty("sysgen.property.file");
                if (sSysgenProperties != null)
                {
                    Properties oProp = new Properties();
					java.io.InputStream oPropFile = null;
					ClassLoader loader = ClassLoaderUtil.getClassLoader(sSysgenProperties);
					oPropFile = loader.getResourceAsStream(sSysgenProperties);

                    //InputStream oPropFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(sSysgenProperties);
                    // InputStream oPropFile = SgFormat.class.getClassLoader().getResourceAsStream(sSysgenProperties);

                    if (oPropFile != null)
                    {
                        oProp.load(oPropFile);
                        oPropFile.close();

                        // Formatos de datas
                        sDatex = oProp.getProperty("sysgen.date.format");
                        sTimex = oProp.getProperty("sysgen.time.format");
                        sTimestampx = oProp.getProperty("sysgen.timestamp.format");
                    }
                }

            }
            catch (NullPointerException e)
            {
				InfraUtil.err("ASPER: Erro em SgFormat.initDateFormats = " + e.getMessage());
            }
            catch (Exception e)
            {
				InfraUtil.err("ASPER: Erro em SgFormat.initDateFormats = " + e.getMessage());
            }

            if (sDatex == null)
                formatFieldDate = new SimpleDateFormat("dd/MM/yyyy");
            else
                formatFieldDate = new SimpleDateFormat(sDatex);

            if (sTimex == null)
                formatFieldTime = new SimpleDateFormat("HH.mm.ss.SSS");
            else
                formatFieldTime = new SimpleDateFormat(sTimex);

            if (sTimestampx == null)
                formatFieldTimestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
            else
                formatFieldTimestamp = new SimpleDateFormat(sTimestampx);

        }

        return;

    } // public static void initDateFormats ()
	/**
	* Inicializa os formatadores para numeros. Carater decimal e separador de milhares.
	*/
	public static void initNumberFormats()
	{
        setDecimalCharacter(",");
		setThousandCharacter(".");

		return;

	} // public static void initNumberFormats ()
	
	
	/**
     * Calcula a diferenca em segundos entre duas datas
     * @param dt1
     * @param dt2
     * @return
     */
    public static long getDiffInSeconds(java.sql.Date dt1, java.sql.Date dt2)
    {

        try
        {

            Calendar oCal1 = Calendar.getInstance();
            Calendar oCal2 = Calendar.getInstance();

            oCal1.setTime(dt1);
            oCal2.setTime(dt2);
            long milliseconds1 = oCal1.getTimeInMillis();
            long milliseconds2 = oCal2.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;
            long diffSeconds = diff / (1000);
            return diffSeconds;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }	
	/**
     * Calcula a diferenca em minutos entre duas datas
     * @param dt1
     * @param dt2
     * @return
     */
    public static long getDiffInMinutes(java.sql.Date dt1, java.sql.Date dt2)
    {

        try
        {

            Calendar oCal1 = Calendar.getInstance();
            Calendar oCal2 = Calendar.getInstance();

            oCal1.setTime(dt1);
            oCal2.setTime(dt2);
            long milliseconds1 = oCal1.getTimeInMillis();
            long milliseconds2 = oCal2.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;
            long diffMinutes = diff / (60 * 1000);
            return diffMinutes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }	
	/**
     * Calcula a diferenca em horas entre duas datas
     * @param dt1
     * @param dt2
     * @return
     */
    public static int getDiffInHours(java.sql.Date dt1, java.sql.Date dt2)
    {

        try
        {

            Calendar oCal1 = Calendar.getInstance();
            Calendar oCal2 = Calendar.getInstance();

            oCal1.setTime(dt1);
            oCal2.setTime(dt2);
            long milliseconds1 = oCal1.getTimeInMillis();
            long milliseconds2 = oCal2.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;
            long diffHours = diff / (60 * 60 * 1000);
            Long ll= new Long(diffHours);
            return ll.intValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Calcula a diferenca em dias entre duas datas
     * @param dt1
     * @param dt2
     * @return
     */
    public static int getDiffInDays(java.sql.Date dt1, java.sql.Date dt2)
    {

        try
        {

            Calendar oCal1 = Calendar.getInstance();
            Calendar oCal2 = Calendar.getInstance();

            //*************************************************************************
            //** Inicio da rotina que acha dias corridos
            //*************************************************************************
            oCal1.setTime(dt1);
            oCal2.setTime(dt2);
            int totDaysDif = 0;

            // se for o mesmo ano pega os dias restantes do ano
            if (oCal1.get(Calendar.YEAR) == oCal2.get(Calendar.YEAR))
            {
                totDaysDif = oCal2.get(Calendar.DAY_OF_YEAR) - oCal1.get(Calendar.DAY_OF_YEAR);
            }

            // se forem anos diferentes
            else
            {
                Calendar oCal3 = Calendar.getInstance();
                int dif = oCal2.get(Calendar.YEAR) - oCal1.get(Calendar.YEAR);

                for (int i = 0; i <= dif; i++)
                {
                    oCal3.set(Calendar.YEAR, oCal1.get(Calendar.YEAR) + i);
                    // se for o primeiro ano pega os dias restantes do ano
                    if (i == 0)
                    {
                        totDaysDif = oCal3.getActualMaximum(Calendar.DAY_OF_YEAR) - oCal1.get(Calendar.DAY_OF_YEAR);
                        //SgInfra.out("totDaysDif " + totDaysDif );
                    }
                    // se for o ultimo ano pega os dias iniciais do no
                    else if (oCal2.get(Calendar.YEAR) == oCal3.get(Calendar.YEAR))
                    {
                        totDaysDif = totDaysDif + oCal2.get(Calendar.DAY_OF_YEAR);
                        //SgInfra.out("totDaysDif " + totDaysDif );
                    }
                    // se for um ano intermediario pega o numero de dias do ano
                    else
                    {
                        totDaysDif = totDaysDif + oCal3.getActualMaximum(Calendar.DAY_OF_YEAR);
                        //SgInfra.out("totDaysDif " + totDaysDif );
                    }
                }
                //          SgInfra.out("Anos diferentes "  );
            }

            //        SgInfra.out("Resultado " + totDaysDif );
            return totDaysDif;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Calcula a diferenca em meses entre duas datas
     * @param dt1
     * @param dt2
     * @return
     */
    public static int getDiffInMonths(java.sql.Date dt1, java.sql.Date dt2)
    {

        try
        {

            Calendar oCal1 = Calendar.getInstance();
            Calendar oCal2 = Calendar.getInstance();

            //*************************************************************************
            //** Inicio da rotina que acha dias corridos
            //*************************************************************************
            oCal1.setTime(dt1);
            oCal2.setTime(dt2);
            int totMonthsDif = 0;

            // se for o mesmo ano pega os dias restantes do ano
            if (oCal1.get(Calendar.YEAR) == oCal2.get(Calendar.YEAR))
            {
                totMonthsDif = oCal2.get(Calendar.MONTH) - oCal1.get(Calendar.MONTH);
            }

            // se forem anos diferentes
            else
            {
                Calendar oCal3 = Calendar.getInstance();
                int dif = oCal2.get(Calendar.YEAR) - oCal1.get(Calendar.YEAR);

                for (int i = 0; i <= dif; i++)
                {
                    oCal3.set(Calendar.YEAR, oCal1.get(Calendar.YEAR) + i);
                    // se for o primeiro ano pega os meses restantes do ano
                    if (i == 0)
                    {
                        totMonthsDif = 12 - oCal1.get(Calendar.MONTH);
                        //SgInfra.out("totDaysDif " + totDaysDif );
                    }
                    // se for o ultimo ano pega os meses iniciais do no
                    else if (oCal2.get(Calendar.YEAR) == oCal3.get(Calendar.YEAR))
                    {
                        totMonthsDif = totMonthsDif + oCal2.get(Calendar.MONTH);
                        //SgInfra.out("totDaysDif " + totDaysDif );
                    }
                    // se for um ano intermediario pega o numero de meses do ano
                    else
                    {
                        totMonthsDif = totMonthsDif + 12 ;
                        //SgInfra.out("totDaysDif " + totDaysDif );
                    }
                }
                //          SgInfra.out("Anos diferentes "  );
            }
            // se nao fez aniversario ainda
            if (oCal2.get(Calendar.DAY_OF_MONTH) < oCal1.get(Calendar.DAY_OF_MONTH) )
            {
                totMonthsDif = totMonthsDif - 1;
            }

            //        SgInfra.out("Resultado " + totDaysDif );
            return totMonthsDif;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Calcula a diferenca em anos entre duas datas
     * @param dt1
     * @param dt2
     * @return
     */
    public static int getDiffInYears(java.sql.Date dt1, java.sql.Date dt2)
    {

        try
        {

            Calendar oCal1 = Calendar.getInstance();
            Calendar oCal2 = Calendar.getInstance();

            //*************************************************************************
            //** Inicio da rotina que acha dias corridos
            //*************************************************************************
            oCal1.setTime(dt1);
            oCal2.setTime(dt2);
            int totYearsDif = 0;

            // se for o mesmo ano a diferenca eh 0
            if (oCal1.get(Calendar.YEAR) == oCal2.get(Calendar.YEAR))
            {
                totYearsDif = 0 ;
            }

            // se forem anos diferentes
            else
            {
                Calendar oCal3 = Calendar.getInstance();
                int dif = oCal2.get(Calendar.YEAR) - oCal1.get(Calendar.YEAR);
                totYearsDif= dif;
                // se nao fez aniversario ainda
                if (oCal2.get(Calendar.DAY_OF_YEAR) < oCal1.get(Calendar.DAY_OF_YEAR) )
                {
                    totYearsDif = totYearsDif - 1;
                }
            }

            return totYearsDif;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Troca em "text" todos os strings "source" por "replace".
     * @param text
     * @param source
     * @param replace
     * @return
     */
    public static String replace(String text, String source, String replace)
    {
		StringBuffer sbTemp = new StringBuffer(text);
		String sTemp = new String(text);
		while (sTemp.indexOf(source) >= 0)
		{
			int nInit = sTemp.indexOf(source);
			int nEnd = nInit + source.length();
			sbTemp.replace(nInit, nEnd, replace);
			sTemp = sbTemp.toString();
		}
		return sbTemp.toString();
    }

}
