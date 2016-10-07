package br.com.issei.mdx.util;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import br.com.issei.mdx.exception.CoreException;


/**
 * Contem funcoes utilizadas por varios pacotes do Asper
 * @author Sysgen
 */
public class InfraUtil
{

    /**
     * Indica se as impressoes realizadas por SgInfra.out e SgInfra.err sao normais ou reduzidas(sem o pacote)
     */
    private static boolean printReduced = false;
    /**
     * Indica se as impressoes realizadas por SgInfra.out e SgInfra.err devem vir acompanhadas de todo STACK
     */
    private static boolean fullStack = false;    
    /**
     * Lista de caracteres, ou conjunto de caracteres, que podem ser utilizados como indicadores de geracao
     * de sequencia nas telas ou paginas. 
     */
	private static Vector sequenceChars ;
	

	private static SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	
	/**
	 * Verifica se o Valor passado em psValue eh compativel com o Tipo de dados(Java) psType para o 
	 * Atributo indentificado por psAttribute, retornando um String com o valor em formato aceito pelo Java. 
	 * 
	 * O Indicador bNotStrict=true identifica se sao aceitos caracteres especiais como
	 *    "*" | "-" | "<G>" | "G" para indicadores de geracao automatica
	 *  para campos numericos, o que implica em retorno "0"	 *
	 */
	public static String transformValue(String psValue, String psType, String psAttribute, boolean bNotStrict) throws CoreException
	{
		FormatUtil.initDateFormats();
		FormatUtil.initNumberFormats();
		
		//Carrega a tabela de caracteres aceitos para geracao automatica
        loadSequenceChars();
		
		String tmpValue = psValue;
		if (psValue == null)
		{
			return psValue;
		}

		psValue = psValue.trim();
		if ((psValue != null) && (!psValue.equals("")))
		{
			if ((psType.equals("java.lang.Integer")) ||
				(psType.equals("java.math.BigInteger"))
				|| (psType.equals("short"))
				|| (psType.equals("long"))
				|| (psType.equals("double")))
			{
				if ((bNotStrict) && (inSequenceChars(psValue)))
				{
					psValue = "0";
				}
                try
                {
                    if (!RegExpUtil.match(psValue, "^(\\+|\\-)?[0-9]{1,}$"))
                    {
                    	throw new CoreException(
                    		0002,
                    		"Valor '" + tmpValue + "' para o atributo '" + psAttribute + "' não é um Inteiro válido. verifique.");
                    }
                }
                catch (CoreException e1)
                {
                    throw new CoreException(3454,"ASPER: Erro Interno. Formato da mascara de validacao de Inteiro incorreta e SgInfra.");
                }
				return psValue;
			}
			else if ((psType.equals("java.lang.Float")) || (psType.equals("java.math.BigDecimal")) || (psType.equals("float")))
			{
                try
                {
                    // Original if (!SgRegExp.match(psValue, "^(((\\+|\\-)?[0-9]+([,][0-9]*)?)|(((\\+|\\-)?[0-9]*[,])?[0-9]+))$"))
                    if (!RegExpUtil.match(psValue, "^[+-]?((\\d+|\\d{1,3}(\\.\\d{3})+)?(\\,\\d*)?|\\,\\d+)$"))    
                    {
                    	throw new CoreException(
                    		0002,
                    		"Valor '" + tmpValue + "' para o atributo '" + psAttribute + "' não é um número Decimal válido. verifique.");
                    }
                }
                catch (CoreException e1)
                {
					throw new CoreException(3455,"ASPER: Erro Interno. Formato da mascara de validacao de Float incorreta e SgInfra.");
                }
                String tmp = FormatUtil.replace(psValue,".", "");
                tmp = FormatUtil.replace(tmp,",", ".");
				return tmp;
			}
			else if (psType.equals("java.sql.Date"))
			{
				try
				{
					java.util.Date x = FormatUtil.getFormatFieldDate().parse(psValue);
					return psValue;
				}
				catch (java.text.ParseException e)
				{
					throw new CoreException(
						0002,
						"Valor '" + tmpValue + "' para o atributo '" + psAttribute + "' não é uma data válida. verifique.");
				}
			}
			else if (psType.equals("java.sql.Time"))
			{
				try
				{
					java.util.Date x = FormatUtil.getFormatFieldTime().parse(psValue);
					return psValue;
				}
				catch (java.text.ParseException e)
				{
					throw new CoreException(
						0002,
						"Valor '" + tmpValue + "' para o atributo '" + psAttribute + "' não é uma hora válida. verifique.");
				}
			}
			else if (psType.equals("java.sql.Timestamp"))
			{
				try
				{
					java.util.Date x = FormatUtil.getFormatFieldTimestamp().parse(psValue);
					return psValue;
				}
				catch (java.text.ParseException e)
				{
					throw new CoreException(
						0002,
						"Valor '" + tmpValue + "' para o atributo '" + psAttribute + "' não é um Timestamp válido. verifique.");
				}

			}
			else if (psType.equals("java.lang.String"))
			{
			}
            else if (psType.equals("java.lang.Boolean"))
            {
                if ((psValue == null) || (psValue.trim().equals("")))
                {
                   return "false"; 
                }
                else
                {
                    if (psValue.trim().toUpperCase().equals("FALSE") || psValue.trim().equals("0") || psValue.trim().toUpperCase().startsWith("N"))
                    {
                       return "false"; 
                    }
                    else if (psValue.trim().toUpperCase().equals("TRUE") || psValue.trim().equals("1") || psValue.trim().toUpperCase().startsWith("S") || psValue.trim().toUpperCase().startsWith("Y"))
                    {
                       return "true"; 
                    }
                    else
                    {
                        throw new CoreException(
                                        0002,
                                        "Valor '" + psValue + "' para o atributo '" + psAttribute + "' não é um Boolean válido. verifique.");
                        
                    }
                }
            }
            else if (psType.equals("br.com.sysgen.framework.model.db.SgBlob"))
            {
            }
			else
			{
				out("Tipo de atributo nao reconhecido " + psType + " em classe form.");
				throw new CoreException(
					9999,
					"Atributo '" + psAttribute + "' possui tipo '" + psType + "' não reconhecido, verifique.");
			}
		} // if ((psValue != null) && (!psValue.equals("")))

		return psValue;
	} // public static String tranformValue
	

	/**
	 * Transforma o Valor passado no String psValue no Tipo de dado(Java) especificado em psType para o 
	 * Atributo indentificado por psAttribute, retornando um String com o valor em formato aceito pelo Java. 
	 * 
	 * Chama o metodo <b>tranformValue</b> para transformar o String
	 */
	public static Object transformObject(String psValue, String psType, String psAttribute, boolean bNotStrict) throws CoreException
	{
		String sFType  = psType.trim();
		String sFValue = transformValue(psValue, sFType, psAttribute, bNotStrict);
        

		if ((sFValue != null) && (!sFValue.equals("")))
		{
			if (sFType.equals("java.lang.String"))
			{
				return sFValue;

			}
			else if (sFType.equals("java.lang.Integer"))
			{
				try
				{
					return new Integer(Integer.parseInt(sFValue));
				}
				catch (NumberFormatException e)
				{
					throw new CoreException(3456,"ASPER: '" + sFValue + "' não é um Inteiro válido.");
				}
			}
            else if (sFType.equals("java.lang.Boolean"))
            {
                return new Boolean(sFValue);
            }
			else if (sFType.equals("java.math.BigInteger"))
			{
				try
				{
					return new java.math.BigInteger(sFValue);
				}
				catch (NumberFormatException e)
				{
					throw new CoreException(3456,"ASPER: '" + sFValue + "' não é um BigInteger válido.");
				}				

			}
			else if (sFType.equals("short"))
			{
				try
				{
					return new Short(Short.parseShort(sFValue));
				}
				catch (NumberFormatException e)
				{
					throw new CoreException(3456,"ASPER: '" + sFValue + "' não é um short válido.");
				}

			}
			else if (sFType.equals("long"))
			{
				try
				{
					return new Long(Long.parseLong(sFValue));
				}
				catch (NumberFormatException e)
				{
					throw new CoreException(3456,"ASPER: '" + sFValue + "' não é um long válido.");
				}

			}
			else if (sFType.equals("java.lang.Float"))
			{
				try
				{
					return new Float(sFValue);
				}
				catch (NumberFormatException e)
				{
					throw new CoreException(3456,"ASPER: '" + sFValue + "' não é um Float válido.");
				}

			}
			else if (sFType.equals("java.math.BigDecimal"))
			{
				try
				{
                    if (sFValue.indexOf(".") < 0)
                    {
                        java.math.BigDecimal resp =  new java.math.BigDecimal(sFValue);
                        resp.setScale(2);
                        return resp;
                    }
					return new java.math.BigDecimal(sFValue);
				}
				catch (NumberFormatException e)
				{
					throw new CoreException(3456,"ASPER: '" + sFValue + "' não é um BigDecimal válido.");
				}            	

			}
			else if (sFType.equals("double"))
			{
				try
				{
					return new Double(Double.parseDouble(sFValue));
				}
				catch (NumberFormatException e)
				{
					throw new CoreException(3456,"ASPER: '" + sFValue + "' não é um double válido.");
				}

			}
			else if (sFType.equals("java.sql.Date"))
			{
				try
				{
					java.sql.Date x = new java.sql.Date(FormatUtil.getFormatFieldDate().parse(sFValue).getTime());
					return x;
				}
				catch (java.text.ParseException e)
				{
					return null;
				}
			}
			else if (sFType.equals("java.sql.Time"))
			{
				try
				{
					java.sql.Time x = new java.sql.Time(FormatUtil.getFormatFieldTime().parse(sFValue).getTime());
					return x;
				}
				catch (java.text.ParseException e)
				{
					return null;
				}
			}
			else if (sFType.equals("java.sql.Timestamp"))
			{
				try
				{
					java.sql.Timestamp x = new java.sql.Timestamp(FormatUtil.getFormatFieldTimestamp().parse(sFValue).getTime());
					return x;
				}
				catch (java.text.ParseException e)
				{
					return null;
				}
			}
            //else if (sFType.equals("br.com.sysgen.framework.model.db.SgBlob"))
            //{
              //  SgBlob x = new SgBlob(sFValue.getBytes());
                //return x;
            //}
			else
			{
				throw new CoreException(3456,"ASPER: Tipo de atributo nao reconhecido '" + psType + "' em classe form.");
			}

		}
		return null;

	} //public static Object transformObject
	
    public static Object setFieldValue( Object oActualObj, String pAttribute, String pValue) throws CoreException
    {
        Object result = null;
        /************************************************************************
         * Pega o metodo get para este atributo para pegar o tipo de dado
         *************************************************************************/
        String sAttribute = pAttribute;
        String sValue     = pValue;
        try
        {                          

            Class[] oClasses = null;
            Object[] oParams = null;

            //Trata de aninhamento de objetos
            String sMethodName = "set" + sAttribute;
            String txtMessage = "get" + sAttribute + "()";
            Object locObj = oActualObj;
            java.lang.reflect.Method getAttribute = null;
            StringBuffer sb = new StringBuffer();
            StringTokenizer st = new StringTokenizer(sAttribute,".");
            while (st.hasMoreTokens())
            {
                String sToken = st.nextToken();
                if (sb.length() > 0)
                {
                    sb.append(".");
                }
                sb.append("get" + sToken + "()");
                sMethodName = "set" + sToken;
                getAttribute = locObj.getClass().getMethod("get" + sToken, oClasses);
                if (getAttribute == null)
                {
                    throw new CoreException(3457, "Metodo " + txtMessage + "  nao  encontrado em " + oActualObj.getClass().getName());
                }     
                if (!st.hasMoreTokens())
                {
                    break; 
                }
                locObj = getAttribute.invoke(locObj, oParams);  
                if (locObj == null)
                {
                    break;
                }                                          
            }                               

//          java.lang.reflect.Method getAttribute = oActualObj.getClass().getMethod("get" + sAttribute, oClasses);
//          if (getAttribute == null)
//          {

//          throw new CoreException(9999, "Metodo get" + sAttribute + "()  nao  encontrado em " + oActualObj.getClass().getName());
//          }
            oClasses = new Class[1];
            oClasses[0] = getAttribute.getReturnType();

            java.lang.reflect.Method setAttribute = locObj.getClass().getMethod(sMethodName, oClasses);
            if (setAttribute == null)
            {
                throw new CoreException(3457, "Metodo set" + sAttribute + "(" + getAttribute.getReturnType().getName() + ")  nao  encontrado em " + oActualObj.getClass().getName());
            }

            oParams = new Object[1];

            // se for vetor converte direto
            // Sempre usar Vector para retornar um HTML_SELECT_MULTIPLE
            //Converte o valor de String para o tipo correto
            if ((sValue == null) || (sValue.trim().equals("")))
            {
                oParams[0] = null;
            }
            else
            {
                oParams[0] = InfraUtil.transformObject(sValue, getAttribute.getReturnType().getName(), sAttribute, true);
                result = oParams[0] ;
            }
            Object oParamReturn = setAttribute.invoke(locObj, oParams);
            //SgInfra.out(" Assinalou= " + sHtmlField + " valor=" + oParams[0]);


        }
        catch (NoSuchMethodException e)
        {
            throw new CoreException(3457, "Nao foram encontrados GET e/ou SET para '" + pAttribute + "'. Verifique.(NoSuchMethodException)");
        }                               
        catch (IllegalAccessException e)
        {            
            throw new CoreException(3457, "Usuario nao possui permissao para executar GET e/ou SET para '" + pAttribute + "'. Verifique.(IllegalAccessException)");
        }
        catch (java.lang.reflect.InvocationTargetException e)
        {
            Throwable ex = e.getTargetException();
            throw new CoreException(3457, "Erro '" + ex.getMessage() + "' dentro da execucao de GET e/ou SET para '" + pAttribute + "'. Verifique.(InvocationTargetException)");
        }                       
        catch (CoreException e)
        {
            throw e;
        }
        catch (Exception e)
        {            
            throw new CoreException(3457, "Exception generica '" + e.getMessage() + "' ocorreu na execucao de GET e/ou SET para '" + pAttribute + "'. Verifique.(Exception)");
        }
        return result;
    } // private static Object setFieldValue( Object oActualObj, String pAttribute, String pValue) ... 	
	
	/**
	 * Define os caracteres(ou cadeia de caracteres) especiais que serao indicadores de geracao automatica
	 * para campos numericos
	 *
	 */
	public static Vector loadSequenceChars() throws CoreException
	{
		if (sequenceChars == null)
		{
			sequenceChars = new Vector();
			sequenceChars.add("*");
			sequenceChars.add("-");
			sequenceChars.add("<G>");
			sequenceChars.add("G");
			sequenceChars.add("<g>");
			sequenceChars.add("g");
		}
		
		return sequenceChars;
		
	}
	/**
	 * Verifica se o Valor passado em psValue estah no vetor de sequenceCHars
	 *
	 */
	private static boolean inSequenceChars(String p) throws CoreException
	{
        for (Iterator iter = sequenceChars.iterator(); iter.hasNext();)
        {
            String element = (String) iter.next();
            if (element.equals(p))
            {
            	return true;
            }
        
        }		
		return false;
		
	}	
	
	/**
	 * Retorna um OutputStream a partir do StackTrace de uma Exception mas sem as classes de
	 * infra-estrutura do servidor de aplicacao<br>
	 * Rejeita as classe dos pacotes a seguir:<br>
	 * org.apache.catalina<br>
	 * org.jboss.web<br>
	 * org.apache.coyote<br>
	 * org.mortbay<br>
	 * com.ibm.servlet<br>
	 * com.ibm.ws<br>
	 * 
	 */
	public static ByteArrayOutputStream getCleanStack(Throwable e)
	{
		ByteArrayOutputStream baosTmp = new ByteArrayOutputStream();
		PrintWriter pwTmp = new PrintWriter(baosTmp);
		e.printStackTrace(pwTmp);
		pwTmp.flush();
		pwTmp.close();
		ByteArrayInputStream baisSaveStack = new ByteArrayInputStream(baosTmp.toByteArray());

		ByteArrayOutputStream baosSaveStack = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baosSaveStack);		   
		 try
		 {
				InputStreamReader file = new InputStreamReader(baisSaveStack);	            
				BufferedReader bf = new BufferedReader(file);
				while (true)
				{
					String sLine = bf.readLine();			
					if (sLine == null)
						break;			
					if ((sLine.indexOf("org.jboss") >= 0) ||
					    (sLine.indexOf("org.apache.catalina") >= 0) ||					    
				   	    (sLine.indexOf("org.apache.coyote") >= 0) ||
					    (sLine.indexOf("org.apache.tomcat") >= 0) ||
					    (sLine.indexOf("sun.reflect") >= 0) ||
					    (sLine.indexOf("java.lang.reflect") >= 0) ||
					    (sLine.indexOf("javax.servlet") >= 0) ||
					    (sLine.indexOf("org.mortbay") >= 0) ||
					    (sLine.indexOf("com.ibm.servlet") >= 0) ||
					    (sLine.indexOf("com.ibm.ws") >= 0) ||
                        (sLine.indexOf("javax.swing") >= 0) ||
                        (sLine.indexOf("java.awt") >= 0) )
					{	
					}
					else
					{
					   pw.println(sLine);	
					}
	            	               
				}
		 }
		 catch (java.io.IOException e1)
		 {  
		 }	
		 pw.flush();
		 pw.close();
		 return baosSaveStack;
	}
    /** 
     * Verifica se a impressao de OUT esta bloqueada
     */
    public static boolean outAllowed()
    {
        String allow = System.getProperty("sysgen.out.allowed");
        if (allow != null)
        {
           if ((allow.equalsIgnoreCase("N"))  || 
              (allow.equalsIgnoreCase("No"))  ||
              (allow.equalsIgnoreCase("Nao")) ||
              (allow.equalsIgnoreCase("Não")) )
           {
               return false;
           }
        }
        return true; 
    }    
    /**
     * Executa a impressao de uma informacao em System.out precedido da classe/linha em que ocorreu a chamada.      * 
     */
    public static void out(Object text)
    {
        if (outAllowed())
        {
            String sErrorLine = getErrorLine();
            if (text == null)
            {
                System.out.println("[" + now()  +"] " + "ASPER.OUT at "  + sErrorLine + " : " + "<Objeto nulo>");
            }
            else
            {            
                System.out.println("[" + now()  +"] " + "ASPER.OUT at "  + sErrorLine + " : " + text.toString());
            }
        }
    }    
    /**
     * Executa a impressao de uma informacao em System.out precedido da classe/linha em que ocorreu a chamada.      * 
     */
    public static void out(String text)
    {
        if (outAllowed())
        {
            String sErrorLine = getErrorLine();
            if (text == null)
            {
                System.out.println("[" + now()  +"] " + "ASPER.OUT at "  + sErrorLine + " : " + "<Objeto nulo>");
            }
            else
            {            
                System.out.println("[" + now()  +"] " + "ASPER.OUT at "  + sErrorLine + " : " + text);
            }
        }
    }
    /**
     * Executa a impressao de uma informacao em System.out precedido da classe/linha em que ocorreu a chamada.      * 
     */
    public static void out(StringBuffer text)
    {
        if (outAllowed())
        {
            String sErrorLine = getErrorLine();

            if (text == null)
            {
                System.out.println("[" + now()  +"] " + "ASPER.OUT at "  + sErrorLine + " : " + "<Objeto nulo>");
            }
            else
            {                        
                System.out.println("[" + now()  +"] " + "ASPER.OUT at "  + sErrorLine + " : " + text.toString());
            }
        }
    }	
    /**
     * Executa a impressao de uma informacao em System.err precedido da classe/linha em que ocorreu a chamada.      * 
     */
    public static void err(String text)
    {
        String sErrorLine = getErrorLine();
        System.err.println("[" + now()  +"] " + "ASPER.ERR at "  + sErrorLine + " : " + text);
    }    
    /**
     * Executa a impressao de uma informacao em System.err precedido da classe/linha em que ocorreu a chamada.      * 
     */
    public static void err(StringBuffer text)
    {
        String sErrorLine = getErrorLine();
        if (text == null)
        {
            System.err.println("[" + now()  +"] " + "ASPER.ERR at "  + sErrorLine + " : " + "<Objeto nulo>");
        }
        else
        {
            System.err.println("[" + now()  +"] " + "ASPER.ERR at "  + sErrorLine + " : " + text.toString());
        }
    }  
    /**
     * Executa a impressao de uma informacao em System.err precedido da classe/linha em que ocorreu a chamada.      * 
     */
    public static void err(Object text)
    {
        String sErrorLine = getErrorLine();
        if (text == null)
        {
            System.err.println("[" + now()  +"] " + "ASPER.ERR at "  + sErrorLine + " : " + "<Objeto nulo>");
        }
        else
        {
            System.err.println("[" + now()  +"] " + "ASPER.ERR at "  + sErrorLine + " : " + text.toString());
        }
    }     
    /**
     * Pega a linha em que ocorreu a chamda do pout ou perr
     */
    private static String getErrorLine()
    {
        Throwable e = null;
        try
        {
            throw new Exception("Sysgen - fake exception!!!!");
        }
        catch (Exception e2)
        {
            e = e2;
        }
        ByteArrayOutputStream baosTmp = new ByteArrayOutputStream();
        PrintWriter pwTmp = new PrintWriter(baosTmp);
        e.printStackTrace(pwTmp);
        pwTmp.flush();
        pwTmp.close();
        ByteArrayInputStream baisSaveStack = new ByteArrayInputStream(baosTmp.toByteArray());
        int countLine = 0;
        StringBuffer sErrorLine = new StringBuffer();
        try
         {
                InputStreamReader file = new InputStreamReader(baisSaveStack);              
                BufferedReader bf = new BufferedReader(file);
                while (true)
                {
                    String sLine = bf.readLine();       
                    countLine++;      
                    if (sLine == null)
                        break;  
                    if (countLine == 4)
                    {
                       if (sLine.indexOf("at ") > 0)
                       {
                           sLine = sLine.substring(sLine.indexOf("at ") + 3,sLine.length());
                       }
                       if (isPrintReduced())
                       {
                           if (sLine.indexOf("(") > 0)
                           {
                               sLine = sLine.substring(sLine.indexOf("("),sLine.length());
                           }                           
                       }
                       sErrorLine.append(sLine);   
                       // se nao for full stack soh mostra a linha da impressao
                       if (!isFullStack())
                       {
                           break;
                           
                       }
                    }
                    else if ((countLine > 4) && (countLine <= 999))
                    {
                        sErrorLine.append("\n" + sLine ); 
                    }
                                   
                }
         }
         catch (java.io.IOException e1)
         {  
         }
         if (isFullStack())
         {
             sErrorLine.append("\n");             
         }
         return sErrorLine.toString();
    }

    /**
     * Verifica o indicador de impressao reduzida em SgInfra.out e SgInfra.err 
     * @return boolean
     */
    public static boolean isPrintReduced()
    {
        return printReduced;
    }
    /**
     * Assinala o indicador de impressao reduzida utilizado em SgInfra.out e SgInfra.err 
     */
    public static void setPrintReduced(boolean printReduced)
    {
        InfraUtil.printReduced = printReduced;
    }


    /**
     * Retorna o indicador FullStack
     * @return boolean
     */
    public static boolean isFullStack()
    {
        return fullStack;
    }


    /**
     * Assinala o Indicador FullStack, indicando se imprime somente a linha de impressao(false) ou
     * todo o stack de chamada(true)
     */
    public static void setFullStack(boolean fullStack)
    {
        InfraUtil.fullStack = fullStack;
    }
    /**
    * Retorna a Data do sistema ignorando a hora minuto segundo.
    */
    private static String now()
    {
        java.util.Date dt = new Date();
        String ds = fmt.format(new Date());
        return ds;

    }    
}

