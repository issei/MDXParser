package br.com.issei.mdx.plugin;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;

import br.com.issei.mdx.entity.PluginParameters;
import br.com.issei.mdx.entity.PluginRecord;
import br.com.issei.mdx.exception.PluginException;
import br.com.issei.mdx.metadata.ColumnMetaData;
import br.com.issei.mdx.metadata.ErrorMetaData;
import br.com.issei.mdx.metadata.ParameterMetaData;
import br.com.issei.mdx.util.FormatUtil;
import br.com.issei.mdx.util.InfraUtil;


/**
 * Responsible to analise e respond to dashboard(multimentional) queries 
 *
 */
public class DashboardPlugin implements PluginInterface
{

	private SimpleDateFormat     formatDate   = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat     formatTime   = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat     formatTimestamp  = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	
    private transient Connection dbConn       = null;
    private Properties           properties = null;
    private String               expression = null; 
    private List<List<String>> aExpressions = null;  
    private String              firstStatement = null;
    
    private PluginParameters    aParameters = null;
    
    private static Map dsPool = null;
    private static String sqlCommand = null;
    
    int[] coltype = null;
    int   colcount= 0;
    int   parmcount= 0;
    
    

    public List<PluginRecord> execute() throws PluginException
    {
    	List<PluginRecord> result = new ArrayList<PluginRecord>();
        aExpressions = expandExpressions(expression); // Qdo eh a execucao real eh a expressao normal

        dbConn = getConnection();
        TreeMap<String, DashboardStageRecord> sortMap = new TreeMap<String, DashboardStageRecord>();
        try
		{
			List<List<String>> axes = aExpressions;
			//List<ColumnMetaData> tmpMD = new ArrayList();
			Stack<DashboardStageRecord> stk = new Stack<DashboardStageRecord>();
			for (int i = 0; i < axes.size(); i++)
			{                		
				List<String> sels = axes.get(i);
				///Novo jeito de fazer
				int level = 0;

				DashboardStageRecord dr = new DashboardStageRecord();
				dr.occ = level;
				//dr.key = dr.key.substring(0, dr.occ * 2) + "." + 0 + SgFormat.toStringRepeat(".0", sels.size() - dr.occ); ; 
				dr.key = "A" + i + FormatUtil.toStringRepeat(".00000", sels.size()); 
				dr.record= null;
				dr.parmMetaData = null;
				dr.colMetaData = null;
				dr.parameters = aParameters;
				dr.children = null;
				stk.push(dr);
				
				
				while (true)
				{
					if (stk.size() <=0)
					{
						break;
					}
					dr = stk.pop();
					//InfraUtil.out( SgFormat.toStringRepeat("   ", dr.occ) + dr.occ + " dr="+dr);
					sortMap.put(dr.key, dr);
					if (dr.occ < sels.size())
					{
						String sel = sels.get(dr.occ);
	    				String alias = "";
	    				String value = "";
	    				String valueAlias = "";
	    				if (sel.startsWith("(ALIAS="))
	    				{
	    					int commaPos = sel.indexOf(",");
	    					int equalPos = sel.indexOf("=",commaPos +1);
	    					alias = sel.substring("(ALIAS=".length(),commaPos);
	    					alias = FormatUtil.replace(alias, "'", "");
	    					value = sel.substring(commaPos +1, equalPos);
	    					valueAlias = sel.substring(equalPos +1, sel.indexOf(")"));
	    					valueAlias = FormatUtil.replace(valueAlias, "'", "");
	    					sel = sel.substring(sel.indexOf(")") + 1);
	    				}	
	    				sqlCommand = sel;
						PreparedStatement stmt = dbConn.prepareStatement(sel);
						dr.parmMetaData = processParametersMetaData(dbConn , stmt );
						//InfraUtil.out( "executando ===========> " + sel);
						processBind(stmt,dr.parmMetaData, dr.parameters);
						boolean rc = stmt.execute();            
						if (rc)
						{
							ResultSet rs = stmt.getResultSet();
							ResultSetMetaData rsm = rs.getMetaData();			                
							dr.colMetaData = processColumnsMetaData(rsm);
							if (!alias.equals(""))
	    					{
								dr.colMetaData[0].setAlias(alias);
	    					}
							else
							{
								alias = dr.colMetaData[0].getCode();
							}
							if (!valueAlias.equals(""))
	    					{
								dr.colMetaData[1].setAlias(valueAlias);
	    					}
							else
							{
								valueAlias = dr.colMetaData[1].getCode();
							}
							List  resp  = processValuesFromResultSet(rs, dr.colMetaData); 
							for (int w = 0; w < resp.size(); w++)
							{
								PluginRecord record = (PluginRecord)resp.get(w);
								//InfraUtil.out(w + "                             record="+record);
								DashboardStageRecord drNew = new DashboardStageRecord();
								drNew.occ = dr.occ + 1;
								drNew.key = dr.key.substring(0, 2 + (dr.occ*6) ) + "." + FormatUtil.toString(w + 1,5) + FormatUtil.toStringRepeat(".00000", sels.size() - drNew.occ); 
								drNew.record= record;
								drNew.record.setDimensionName(alias);

								drNew.record.setDimension(convertToString(record.get(0)));	
								drNew.record.setOcc(drNew.occ);
								if (drNew.record.size()> 2)
					        	{
									drNew.record.set(0, drNew.record.get(2));
					        	}
								drNew.parmMetaData = dr.parmMetaData;
								drNew.colMetaData = dr.colMetaData;
								drNew.parameters = dr.parameters.copy();
								//InfraUtil.out(" Antes dr.parameters="+dr.parameters  + " record(0)=" + record.get(0));
								drNew.parameters.set(record.get(0)); //Adiciona o novo parametro
								//InfraUtil.out(" Depois dr.parameters="+dr.parameters);
								drNew.children = null;
								//salva qual foi a query usada.
								drNew.record.setQuery(sel);
								stk.push(drNew);
								if (w>=99999)
								{
									break;
								}
							}
						}	
			            stmt.close();				        
						
					}
				}
			}
		} catch (SQLException e)
		{
            dbConn = removeConnection();
            InfraUtil.out(sqlCommand);
            if ((e.getSQLState() != null) && (e.getSQLState().trim().equals("42704")))
            {
                throw new PluginException("TABLE_NOT_FOUND"," ");                
            }
            throw new PluginException(e);
		}
        dbConn = removeConnection(); 

        Stack<PluginRecord> out = new Stack<PluginRecord>();
        
        PluginRecord father = new PluginRecord();
        
        for (Iterator iterator = sortMap.keySet().iterator(); iterator.hasNext();)
		{
			String key = (String) iterator.next();
			DashboardStageRecord dr = (DashboardStageRecord)sortMap.get(key);
			//InfraUtil.out("                                         Leu=" + dr.occ + " dr="+dr);
			
			//Move o texto formatado da dimensao para a primeira posição
			if (dr!=null && dr.record!=null && dr.record.size()> 2)
        	{
				dr.record.set(0, dr.record.get(2));
        	}
			
			if (dr.occ == 0)
			{
		        father = new PluginRecord();
		        father.setChildren(new ArrayList<PluginRecord>());
			}
			else if (dr.occ == father.occ + 1)
			{			
				PluginRecord el = dr.record ; //Nao precisa ser copy
				father.getChildren().add(el);
				//InfraUtil.out( SgFormat.toStringRepeat("   ", el.occ) + el.dimension +" Adicionando. " + father + " children=" + father.getChildren());
			}
			else if (dr.occ > father.occ)
			{
					//InfraUtil.out( SgFormat.toStringRepeat("   ", dr.occ) +" Subindo. salvando " + father + " children=" + father.getChildren());
					//father.printAll();
					out.push(father);
					int pos = father.getChildren().size();
					father = father.getChildren().get(pos -1);
			        father.setChildren(new ArrayList<PluginRecord>());
					father.getChildren().add(dr.record); //Nao precisa ser copy
					//InfraUtil.out( SgFormat.toStringRepeat("   ", dr.occ) +" Subindo. novo father " + father + " children=" + father.getChildren());
			}
			else if (dr.occ <= father.occ)
			{
				while (father.occ >= dr.occ)
				{
					father = out.pop();
				}
				father.getChildren().add(dr.record); //Nao precisa ser copy
//				if (out.size() <= 0)
//				{
//					InfraUtil.out( SgFormat.toStringRepeat("   ", dr.occ) +"Novo father " + father + " children=" + father.getChildren());
//				}
				
				out.push(father);
				int pos = father.getChildren().size();
				father = father.getChildren().get(pos -1);
				father.setChildren(new ArrayList<PluginRecord>());
			}
		}
		while (out.size() > 0)
		{
			father = out.pop();
		}
		for (int w=0;w<father.getChildren().size();w++)
        {
        	PluginRecord rec = (PluginRecord)father.getChildren().get(w);
        	result.add(rec);
        }
        
        return result;
    }

    public ColumnMetaData[] getColumnsMetaData() throws PluginException
    {
    	ColumnMetaData[] aColumnsMetaData = null;
    	//Tem que fazer isto pois precisa fazer o "PREPARE" na tela de manutencao
    	//procExpression = processChangeExpr(expression); // Qdo eh a so MetaData a expressao tem (1 = 0)
    	aExpressions = expandExpressions(expression); 
    	//InfraUtil.out(aExpressions);
    	dbConn = getConnection();
    	try
    	{
    		List<List<String>> axes = aExpressions;
    		List<ColumnMetaData> tmpMD = new ArrayList();
    		for (int i = 0; i < axes.size(); i++)
    		{                		
    			List<String> sels = axes.get(i);
    			for (int j = 0; j < sels.size(); j++)
    			{
    				String sel = sels.get(j);
    				String alias = "";
    				String value = "";
    				String valueAlias = "";
    				if (sel.startsWith("(ALIAS="))
    				{
    					int commaPos = sel.indexOf(",");
    					int equalPos = sel.indexOf("=",commaPos +1);
    					alias = sel.substring("(ALIAS=".length(),commaPos);
    					alias = FormatUtil.replace(alias, "'", "");
    					value = sel.substring(commaPos +1, equalPos);
    					valueAlias = sel.substring(equalPos +1, sel.indexOf(")"));
    					valueAlias = FormatUtil.replace(valueAlias, "'", "");
    					sel = sel.substring(sel.indexOf(")") + 1);
    				}		
    				//InfraUtil.out(" ================================================== Vai processar comando ");
    				//System.out.println("Alias=" + alias + ";");
    				//System.out.println("Sel=" + sel + ";");
    				String saveStatement = sel;
    				sqlCommand = sel;
    				PreparedStatement stmt = dbConn.prepareStatement(saveStatement);
    				ParameterMetaData[] aParametersMetaData = processParametersMetaData(dbConn , stmt );
    				//InfraUtil.out(" aParametersMetaData=" + aParametersMetaData.length);
    				processBindMetadata(stmt,aParametersMetaData);
    				//Vector result = processExecute(stmt); //Result tem que ser sempre 0  

    				boolean rc = stmt.execute();            
    				if (rc)
    				{
    					ResultSet rs = stmt.getResultSet();
    					ResultSetMetaData rsm = rs.getMetaData();			                
    					ColumnMetaData[] tmpColumnsMetaData = processColumnsMetaData(rsm);     
    					if (!alias.equals(""))
    					{
    						tmpColumnsMetaData[0].setDescription(alias);
    						tmpColumnsMetaData[0].setAlias(alias);
    						if (tmpColumnsMetaData[0].getCode().length() <=2) // se era funcao coloca o alia TB
    						{
        						tmpColumnsMetaData[0].setCode(alias.toUpperCase());
        						tmpColumnsMetaData[0].setDescription(alias.toUpperCase());
    						}
    					}
    					tmpMD.add(tmpColumnsMetaData[0]);
    					if (j == sels.size() -1)
    					{    					
    						if (!valueAlias.equals(""))
    						{
    							tmpColumnsMetaData[1].setCode(value);
    							tmpColumnsMetaData[1].setDescription(valueAlias);
    							tmpColumnsMetaData[1].setAlias(valueAlias);
    						}
    						tmpMD.add(tmpColumnsMetaData[1]);
    					}
    				}	
    				stmt.close();				        
    			}
    		}
    		aColumnsMetaData = new ColumnMetaData[tmpMD.size()];
    		int i=0;
    		for (Iterator iterator = tmpMD.iterator(); iterator.hasNext();)
    		{
    			ColumnMetaData columnMetaData = (ColumnMetaData) iterator.next();
    			//InfraUtil.out("colMetaData="+columnMetaData);
    			aColumnsMetaData[i] = columnMetaData;
    			i++;					
    		}	
    	} catch (SQLException e)
    	{
    		dbConn = removeConnection();
    		InfraUtil.out(sqlCommand);
    		e.printStackTrace();
    		if ((e.getSQLState() != null) && (e.getSQLState().trim().equals("42704")))
    		{
    			throw new PluginException("TABLE_NOT_FOUND"," ");                
    		}
    		throw new PluginException(e);
    	}

    	dbConn = removeConnection();
        return aColumnsMetaData;
    }
    public ParameterMetaData[] getParametersMetaData() throws PluginException
    {
    	ParameterMetaData[] aParametersMetaData = null;
    	//Tem que fazer isto pois precisa fazero "PREPARE" na tela de manutencao
    	//procExpression = processChangeExpr(expression); // Qdo eh a so MetaData a expressao tem (1 = 0)
    	aExpressions = expandExpressions(expression); 
    	//InfraUtil.out(procExpression);
    	dbConn = getConnection();

    	String saveStatement = firstStatement;
    	sqlCommand = saveStatement;
    	try
    	{  
    		//Executa a expressao
    		PreparedStatement stmt = dbConn.prepareStatement(saveStatement);
    		aParametersMetaData = processParametersMetaData(dbConn , stmt );
    		stmt.close();
    	}
    	catch (SQLException e)
    	{
    		dbConn = removeConnection();
    		InfraUtil.out(sqlCommand);
    		if ((e.getSQLState() != null) && (e.getSQLState().trim().equals("42704")))
    		{
    			throw new PluginException("TABLE_NOT_FOUND"," ");                
    		}
    		throw new PluginException(e);
    	}  
    	catch (PluginException e)
    	{
    		dbConn = removeConnection();    
    		throw e;
    	} 

    	dbConn = removeConnection(); 
        return aParametersMetaData;
    }    

    public ErrorMetaData[] getErrorsMetaData() throws PluginException
    {
        
        ErrorMetaData[] resp = new ErrorMetaData[4];
        {
            ErrorMetaData err = new ErrorMetaData("GENERIC");
            resp[0] = err;
        }
        {
            ErrorMetaData err = new ErrorMetaData("TABLE_NOT_FOUND");
            resp[1] = err;
        }
        {
            ErrorMetaData err = new ErrorMetaData("CONNECTION");
            resp[2] = err;
        }
        {
            ErrorMetaData err = new ErrorMetaData("CONFIGURATION");
            resp[3] = err;
        }
        return resp;
    }

    public void setExpression(String p) throws PluginException
    {
        this.expression = p;
    }

    public void setParameters(PluginParameters p) throws PluginException
    {
        this.aParameters = p;
    }

    public void setProperties(Properties p) throws PluginException
    {
        properties = p;
    }
    public String getExpression() throws PluginException
    {
         return expression;
    }

    public PluginParameters getParameters() throws PluginException
    {
        return aParameters;
    }

    public Properties getProperties() throws PluginException
    {
        return properties ;
    }    
    //*********************************************************************************
    //*********************************************************************************
    //*********************************************************************************
    private Connection getConnection() throws PluginException
    {

        Connection con = null;
        //DatabaseMetaData dbmd;
        if (properties == null)
        {
            throw new PluginException("CONFIGURATION","Properties must be defined to execute this source!");
        }
        if (aExpressions == null)
        {
            throw new PluginException("CONFIGURATION","Expression must be defined to execute this source!");
        }     
        String dx = properties.getProperty("driver");
        if (dx == null)
        {
            throw new PluginException("CONFIGURATION", "Property 'driver' must be defined");
        }
        String ux = properties.getProperty("URL");
        if (ux == null)
        {
            throw new PluginException("CONFIGURATION", "Property 'URL' must be defined");
        }
        String usx = properties.getProperty("user");
//        if (usx == null)
//        {
//            throw new PluginException(9005, "Property 'user' must be defined");
//        }
        String psx = properties.getProperty("password");
//        if (psx == null)
//        {
//            throw new PluginException(9006, "Property 'password' must be defined");
//        }
        String dbschx = System.getProperty("schema");
        
        if (dsPool == null)
        {
            dsPool = new HashMap();
        }
        int sConnectionType = 0;
        
        if (sConnectionType == 0)
        {
            try
            {
                Class.forName(dx);
            }
            catch (Exception e)
            {
                throw new PluginException(9010,"Driver '" + dx + "' not found.");
            }

            try
            {
                if (usx == null || usx.equals(""))
                {
                    con = DriverManager.getConnection(ux);
                }
                else
                {
                    con = DriverManager.getConnection(ux, usx, psx);
                }

                //dbmd = con.getMetaData();

                if ((dbschx != null) && (!dbschx.trim().equals("")))
                {
                    boolean rc = false;
                    Statement stmt = con.createStatement();
                    stmt.execute("set schema " + dbschx);
                    stmt.close();
                }

            }
            catch (SQLException e)
            {
                //e.printStackTrace();
                try
                {
                    if ((con != null) && (!con.isClosed()))
                    {
                        con.rollback();
                        con.close();
                    }
                } catch (SQLException e1)
                {
                }            
                throw new PluginException("CONNECTION", e.getMessage());
            }   
        }
//        else if (sConnectionType == 2)
//        {
//            String key = ux + ";" + usx + ";" + psx ;
//            try
//            {
//                DataSource ds = (DataSource)dsPool.get(key);
//                if (ds == null)
//                {
//                    ds = createLocalConnectionPool(ux , usx , psx );
//                    //InfraUtil.out("CRIADO!!!!" + ds + " hash=" + ds.hashCode());
//                }
//                if (ds != null)
//                {
//                    dsPool.put(key, ds);
//                    //InfraUtil.out(ds + " hash=" + ds.hashCode());
//                    con = ds.getConnection();
//                    //InfraUtil.out("con hash=" + con.hashCode());
//                }
//                if ((dbschx != null) && (!dbschx.trim().equals("")))
//                {
//                    boolean rc = false;
//                    Statement stmt = con.createStatement();
//                    stmt.execute("set schema " + dbschx);
//                    stmt.close();
//                }                
//            } catch (SQLException e)
//            {
//                try
//                {
//                    if ((con != null) && (!con.isClosed()))
//                    {
//                        con.rollback();
//                        con.close();
//                    }
//                } catch (SQLException e1)
//                {
//                }                 
//                throw new PluginException("CONNECTION", e.getMessage());
//            }
//        }
        return con;
    }
    /**
     * 
     * @return
     * @throws PluginException
     */    
    private Connection removeConnection() throws PluginException
    {

    	try
    	{
    		if ((dbConn != null) && (!dbConn.isClosed()))
    		{
    			dbConn.rollback();
    			dbConn.close();
    		}
    	} catch (SQLException e1)
    	{
    	}  
    	return null;
    }
    
    private ColumnMetaData[] processColumnsMetaData(ResultSetMetaData rsm ) throws SQLException , PluginException
    {
        colcount = rsm.getColumnCount();
        ColumnMetaData[] resp = new ColumnMetaData[colcount];
        coltype = new int[colcount + 1]; 
        for (int i = 1; i < colcount + 1; i++)
        {   
            ColumnMetaData cmd = new ColumnMetaData();
            cmd.setCode(rsm.getColumnName(i));
            cmd.setDescription(rsm.getColumnName(i));
            cmd.setAlias(rsm.getColumnName(i));
            coltype[i] = rsm.getColumnType(i);
            switch (rsm.getColumnType(i))
            {
                case Types.TINYINT :
                case Types.SMALLINT :
                case Types.INTEGER :
                    cmd.setType("java.lang.Integer");
                    break;
                case Types.BIGINT :
                case Types.FLOAT :
                case Types.REAL :
                case Types.DOUBLE :
                case Types.NUMERIC :
                case Types.DECIMAL :
                    cmd.setType("java.math.BigDecimal");
                    break;
                case Types.CHAR :
                case Types.VARCHAR :
                case Types.LONGVARCHAR :
                    cmd.setType("java.lang.String");
                    break; 
                case Types.DATE :
                    cmd.setType("java.sql.Date");
                    break;

                case Types.TIME :
                    cmd.setType("java.sql.Time");
                    break;

                case Types.TIMESTAMP :
                    cmd.setType("java.sql.Timestamp");
                    break;
                case Types.BIT :
                    cmd.setType("java.lang.Boolean");
                    break;

                case Types.BINARY :
                case Types.VARBINARY :
                case Types.LONGVARBINARY :
                    break;

                case Types.NULL :
                    break;

                case Types.OTHER :
                	cmd.setType("java.lang.String");
                    break;

                default :
            } 
            cmd.setLength(rsm.getPrecision(i));
            cmd.setDecimal(rsm.getScale(i));

            //InfraUtil.out("CMD=" + cmd);
            resp[i - 1]  = cmd;
        } 
        return resp;
    }
    
    private ParameterMetaData[] processParametersMetaData(Connection con , PreparedStatement stmt ) throws SQLException , PluginException
    {
    	ParameterMetaData[] resp = null;
    	
        //InfraUtil.out("con =" + con.getMetaData().getDatabaseProductName());
        String prodName = con.getMetaData().getDatabaseProductName().toUpperCase();  
   
        {        
	        java.sql.ParameterMetaData parmsx = stmt.getParameterMetaData();
	    	parmcount = parmsx.getParameterCount();
	    	resp = new ParameterMetaData[parmcount];
	    	coltype = new int[parmcount + 1]; 
	    	for (int i = 1; i < parmcount + 1; i++)
	    	{   
	    		ParameterMetaData parm = new ParameterMetaData();
	
	    		parm.setClassName("java.lang.String");
	    		parm.setDescription("PARAMETER " + FormatUtil.toString(i,3));
	    		parm.setAlias("PARM_" + FormatUtil.toString(i,3));
	    		
	    		try{
	    			//No Oracle ocorre java.sql.SQLException: Recurso não suportado \n at oracle.jdbc.driver.OracleParameterMetaData.getParameterType(OracleParameterMetaData.java:166)
	    			switch (parmsx.getParameterType(i))
	        		{
	        			case Types.TINYINT :
	        			case Types.SMALLINT :
	        			case Types.INTEGER :
	        				parm.setType("java.lang.Integer");
	        				break;
	        			case Types.BIGINT :
	        			case Types.FLOAT :
	        			case Types.REAL :
	        			case Types.DOUBLE :
	        			case Types.NUMERIC :
	        			case Types.DECIMAL :
	        				parm.setType("java.math.BigDecimal");
	        				break;
	        			case Types.CHAR :
	        			case Types.VARCHAR :
	        			case Types.LONGVARCHAR :
	        				parm.setType("java.lang.String");
	        				break; 
	        			case Types.DATE :
	        				parm.setType("java.sql.Date");
	        				break;
	
	        			case Types.TIME :
	        				parm.setType("java.sql.Time");
	        				break;
	
	        			case Types.TIMESTAMP :
	        				parm.setType("java.sql.Timestamp");
	        				break;
	
	        			case Types.BIT :
	        				parm.setType("java.lang.Boolean");
	        				break;
	        			case Types.BINARY :
	        			case Types.VARBINARY :
	        			case Types.LONGVARBINARY :
	        				break;
	
	        			case Types.NULL :
	        				break;
	
	        			case Types.OTHER :
	        				break;
	
	        			default :
	        		} 
	    		}catch(SQLException e)
            	{
//	    			e.printStackTrace();
	    			parm.setType("java.lang.String");
            	}
	    		parm.setLength(1);
	    		parm.setDecimal(1);
	
	    		resp[i - 1]  = parm;                     		
	    		
	    	}
        }
        return resp;
    }
    private String convertToString(Object obj)  throws SQLException , PluginException
    {
    	String resp = null;
    	if (obj instanceof String)
    	{
    		 resp = (String)obj;
    	}
    	else if (obj instanceof Integer)
    	{
    		Integer val  = (Integer)obj;
    		resp = val.toString();									
    	}
    	else if (obj instanceof BigDecimal)
    	{
    		BigDecimal val  = (BigDecimal)obj;
    		resp = val.toString();											
    	}
    	else if (obj instanceof Date)
    	{
    		Date val  = (Date)obj;
    		resp = formatDate.format(val);								
    	}
    	else if (obj instanceof Time)
    	{
    		Time val  = (Time)obj;
    		resp = formatTime.format(val);								
    	}
    	else if (obj instanceof Timestamp)
    	{
    		Timestamp val  = (Timestamp)obj;
    		resp = formatTimestamp.format(val);							
    	}
    	return resp;
    }	
    private void processBind(PreparedStatement stmt, ParameterMetaData[] pParametersMetaData, PluginParameters pParameters)  throws SQLException , PluginException
    {
    	if (pParametersMetaData != null)
    	{
    		for (int w = 0; w < pParametersMetaData.length; w++)
    		{   
    			PluginParameters parm = pParameters;
    			//------------------------- tratamento parametro null
    			if(parm.get(w) == null || (parm.get(w) instanceof String && ("".equals((String)parm.get(w)) || "null".equalsIgnoreCase((String)parm.get(w)))))
            	{
            		//No Oracle ocorre java.sql.SQLException: Recurso não suportado \n at oracle.jdbc.driver.OracleParameterMetaData.getParameterType(OracleParameterMetaData.java:166)
            		try{
                		java.sql.ParameterMetaData parmsx = stmt.getParameterMetaData();
                		stmt.setNull(w + 1, parmsx.getParameterType(w )); 
            		}catch(SQLException e)
                	{
            			//e.printStackTrace();
            			 stmt.setNull(w + 1, Types.VARCHAR );  
                	}
            	}else
            		//------------------------------    			
    			if (parm.get(w) instanceof Integer)
    			{
    				Integer tmp = (Integer)parm.get(w);
    				stmt.setInt(w + 1, tmp.intValue() );                    
    			}
    			else if (parm.get(w) instanceof BigDecimal)
    			{              
    				stmt.setBigDecimal(w + 1, (BigDecimal)parm.get(w) );                    
    			} 
    			else if (parm.get(w) instanceof String)
    			{                  
    				stmt.setString(w + 1, (String)parm.get(w) );                    
    			} 
    			else if (parm.get(w) instanceof Date)
    			{
    				stmt.setDate(w + 1, (Date)parm.get(w) );                    
    			} 
    			else if (parm.get(w) instanceof Time)
    			{
    				stmt.setTime(w + 1, (Time)parm.get(w) );                    
    			}  
    			else if (parm.get(w) instanceof Timestamp)
    			{
    				stmt.setTimestamp(w + 1, (Timestamp)parm.get(w) );                    
    			}   
    		}
    	}
    }
    private void processBindMetadata(PreparedStatement stmt, ParameterMetaData[] pParametersMetaData)  throws SQLException , PluginException
    {
        if (pParametersMetaData != null)
        {
            for (int i = 0; i < pParametersMetaData.length; i++)
            {   
            	PluginParameters parm = aParameters;
            	
            	if (pParametersMetaData[i].getType().equals("java.lang.Integer"))
            	{
            		stmt.setNull(i + 1, Types.INTEGER );                    
            	}
            	else if (pParametersMetaData[i].getType().equals("java.math.BigDecimal"))
            	{
            		stmt.setNull(i + 1, Types.DECIMAL );                    
            	} 
            	else if (pParametersMetaData[i].getType().equals("java.lang.String"))
            	{
            		stmt.setNull(i + 1, Types.VARCHAR );                    
            	} 
            	else if (pParametersMetaData[i].getType().equals("java.sql.Date"))
            	{
            		stmt.setNull(i + 1, Types.DATE );                  
            	} 
            	else if (pParametersMetaData[i].getType().equals("java.sql.Time"))
            	{
            		stmt.setNull(i + 1, Types.TIME );                
            	}  
            	else if (pParametersMetaData[i].getType().equals("java.sql.Timestamp"))
            	{
            		stmt.setNull(i + 1, Types.TIMESTAMP );                
            	}
            	else if (pParametersMetaData[i].getType().equals("Dimension"))
            	{
            		stmt.setNull(i + 1, Types.VARCHAR );                    
            	} 
            }
       }
    }
    
    
    private List processValuesFromResultSet(ResultSet rs, ColumnMetaData[] pColumnsMetaData)  throws SQLException , PluginException
    {
        List resp = new LinkedList();
        while (rs.next())
        {
            PluginRecord record = new PluginRecord();
            for (int j = 1; j < pColumnsMetaData.length + 1; j++)
            {
//                String colName = aColumnsMetaData[j - 1].getAlias();
//                if (colName == null)
//                {
//                    colName = aColumnsMetaData[j - 1].getCode();
//                }
                switch (coltype[j])
                {
                    case Types.TINYINT :
                        int n0 = rs.getShort(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(new Integer(n0));
                        break;

                    case Types.SMALLINT :
                        int n00 = rs.getShort(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(new Integer(n00));
                        break;

                    case Types.INTEGER :
                        int n1 = rs.getInt(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(new Integer(n1));
                        break;

                    case Types.BIGINT :
                        long n2 = rs.getLong(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(new BigDecimal(n2));
                        break;

                    case Types.FLOAT :
                        float n3 = rs.getFloat(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(new BigDecimal(n3));
                        break;

                    case Types.REAL :
                        double n4 = rs.getDouble(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(new BigDecimal(n4));
                        break;

                    case Types.DOUBLE :
                        //sval.append(rs.getDouble(j));
                        double n5 = rs.getDouble(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(new BigDecimal(n5));
                        break;

                    case Types.NUMERIC :
                        BigDecimal n6 = rs.getBigDecimal(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(n6);
                        break;

                    case Types.DECIMAL :
                        BigDecimal n7 = rs.getBigDecimal(j);
                        if (rs.wasNull())
                            record.set(null);
                        else
                            record.set(n7);
                        break;
                    case Types.CHAR :
                    case Types.VARCHAR :
                    case Types.LONGVARCHAR :
                        String str = rs.getString(j);
                        if (str != null)
                        {
                            record.set(str);                                                                   
                        }
                        else
                            record.set(null);
                        break;

                    case Types.DATE :
                        //TO_DATE('12/10/1998','mm/dd/YYYY')
                        //SimpleDateFormat formatDate  = new SimpleDateFormat ("yyyy-MM-dd");
                        //SimpleDateFormat formatTime  = new SimpleDateFormat ("HH:mm:ss");
                        //SimpleDateFormat formatTimestamp  = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

                        java.sql.Date td = rs.getDate(j);
                        if (td != null)
                        {
                            record.set(td);                                                                   
                        }
                        else
                            record.set(null);
                        break;

                    case Types.TIME :
                        java.sql.Time th = rs.getTime(j);
                        if (th != null)
                        {
                            record.set(th);                                                                   
                        }
                        else
                            record.set(null);
                        break;

                    case Types.TIMESTAMP :
                        java.sql.Timestamp tt = rs.getTimestamp(j);
                        if (tt != null)
                        {
                            record.set(tt);                                                                   
                        }
                        else
                            record.set(null);
                        break;
                    case Types.BIT :
                    	boolean bol = rs.getBoolean(j);
                    	record.set(new Boolean(bol));
                    	break;
                    case Types.BINARY :
                    case Types.VARBINARY :
                    case Types.LONGVARBINARY :
                        byte b[] = rs.getBytes(j);
                        //TODO Fazer??????
                        record.set(null);
                        break;

                    case Types.NULL :
                        record.set(null);
                        break;

                    case Types.OTHER :
                        record.set(rs.getObject(j));
                        break;

                    default :
                        record.set(null);
                }
            } // for (int j = 1; j < colcount + 1; j++)
            resp.add(record);
        } // while (rs.next())
        return resp;
    }
    
    
    /**
     * Retorna o DataSource local para o conection pool criado com o DBCP
     * @return String
     */
//    public static DataSource createLocalConnectionPool(String url,String user,String password) 
//    {
//        GenericObjectPool connectionPool = new GenericObjectPool(null);
//        connectionPool.setMaxActive(100);
//        connectionPool.setMaxIdle(10);;
//        connectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
//        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url,user,password);
//        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
//                  connectionFactory,
//                  connectionPool,
//                  null,
//                  null,
//                  false,
//                  false);
//        PoolingDataSource dataSource = new PoolingDataSource(connectionPool);  
//        return dataSource;
//    }    

    
    public List expandExpressions(String p) throws PluginException
    {
    	String table = null;
    	String where = null;
    	
    	List axes = new ArrayList();
        if (p == null)
        {
            throw new PluginException("GENERIC","Expression must be informed!");
        }
    	p = p.trim();
        if (!p.toLowerCase().startsWith("select "))
        {
            throw new PluginException("GENERIC","Expression must start with 'select ', please verify your statement(note the spaces).");
        }
        if (p.toLowerCase().indexOf(" from ") <= 0)
        {
            throw new PluginException("GENERIC","Expression must have a ' from ' element, please verify your statement(note the spaces).");
        }        

    	//InfraUtil.out("p=" + p.trim());
        int posSel = "select ".length();
        int posFrom = p.toLowerCase().indexOf(" from ");
        int posWhere = p.toLowerCase().indexOf(" where ");
        String specsTmp = p.substring(posSel, posFrom);
        if (specsTmp.trim().equals(""))
        {
            throw new PluginException("GENERIC","Dimension axis not specified. Use 'select { <dim1> } ON 0 , { <dim2> } ON 1 ,...' ");        	
        }
        if (posWhere <= 0)
        	posWhere = p.length();
        table = p.substring(posFrom + " from ".length(),posWhere);
        if (table.trim().equals(""))
        {
            throw new PluginException("GENERIC","Table not specified. Use ' from <table> [where ...]' ");        	
        }
        if (posWhere < p.length())
        {
        	where = p.substring(posWhere + " where ".length(),p.length());
        }
    	//InfraUtil.out("     " + "table=" + table);
        
        //Processa cada um dos eixos
    	String[] ps =  specsTmp.split(",");
    	int maxSpec = -1;
        int posSpec = 0;
    	List aSpecs = new ArrayList();
    	for (int i = 0; i < ps.length; i++)
		{
			String spc = ps[i].trim();
			//InfraUtil.out("     " + i + " spec=" + spc);
			if (spc.equals(""))
			{
	            throw new PluginException("GENERIC","Empty Axis specification, please verify please verify if you have an extra ','.");				
			}
	        if (spc.toLowerCase().indexOf(" on ") <= 0)
	        {
	            throw new PluginException("GENERIC","Axis specification must have a ' on ' element, please verify your statement(note the spaces). Err=" + spc);
	        }  

	        String p1 = spc.substring(0, spc.toLowerCase().indexOf(" on "));
	        String p2 = spc.substring(spc.toLowerCase().indexOf(" on ") + " on ".length());
	        p1 = p1.trim();
	        p2 = p2.trim();
	        try
			{
				posSpec = Integer.parseInt(p2);
			} catch (NumberFormatException e)
			{
				throw new PluginException("GENERIC","Axis specification clause ' on ' must be followed by an Integer. Err=" + spc);
			}
	        if (!p1.toLowerCase().startsWith("{"))
	        {
	            throw new PluginException("GENERIC","Dimension specification  must start with '{'. Err=" + spc);
	        }
	        if (!p1.toLowerCase().endsWith("}"))
	        {
	            throw new PluginException("GENERIC","Dimension specification  must end with '}'. Err=" + spc);
	        }
			//Analisa as dimensoes possiveis em cada eixo
	        p1 = p1.replace("{", "");
	        p1 = p1.replace("}", "");
	        p1 = p1.trim();
			String[] dims = p1.split("\\.");
			
			String value = dims[dims.length - 1];	
			String valueAlias = value;
			String valueLabel = null;
			if (value.toLowerCase().indexOf(" as ") >= 0)
			{
				valueAlias = value.substring(value.toLowerCase().indexOf(" as ") + " as ".length());
				
				value = value.substring(0,value.toLowerCase().indexOf(" as "));
				
				if(valueAlias.indexOf("[")>=0 && valueAlias.indexOf("]")>=0 )
				{
					String subValueAlias = valueAlias.substring(valueAlias.indexOf("[") + 1,valueAlias.indexOf("]"));
					//tirar do valueAlias o resto;
					valueAlias = valueAlias.substring(0, valueAlias.indexOf("["));
					String columnAlias = null;
					if (subValueAlias  != null)
					{
						
						if (subValueAlias.indexOf("/") > 0)
						{
							String parms = "";
							String[] pcs = subValueAlias.split("/");
							subValueAlias = pcs[0];
							if(subValueAlias.indexOf("(")>=0 && subValueAlias.indexOf(")")>=0)
							{
								columnAlias = subValueAlias.substring(subValueAlias.indexOf("(") + 1,subValueAlias.indexOf(")"));
								subValueAlias = subValueAlias.substring(0,subValueAlias.indexOf("("));
							}
							for (int r=1;r<pcs.length;r++)
							{
								parms = parms + "," + pcs[r];
							}
							valueLabel = " , " + subValueAlias + "( (SUM("+ ((columnAlias!=null)?columnAlias:value)+") )" + parms + ")";						
						}
						else
						{
							valueLabel = " , " +subValueAlias + "( (SUM("+ ((columnAlias!=null)?columnAlias:value)+") )" + ")";
						}
					}
				}
			}

			String locwhere = null;
			if (where != null)
				locwhere = " where " + where ;
			
			List<String> aSelects = new ArrayList();
			
			for (int j = 0; j < dims.length - 1; j++)
			{
				String alias = "";
				String subDim = null;
				String dimSave = dims[j].trim();
				String dim = dims[j].trim();
				if (dim.startsWith("["))
				{
					dim = dim.substring(1);
				}
				if (dim.endsWith("]"))
				{
					dim = dim.substring(0,dim.length() -1);
				}
				else
				{
		            throw new PluginException("GENERIC","Expression must have a closing ']' at end of '" + dimSave + "' .");
				}
				if (dim.indexOf("[") > 0)
				{

					if (dim.indexOf("]") < 0)
					{
			            throw new PluginException("GENERIC","Expression missing closing ']' in dimension '" + dim + "' .");
					}
					subDim = dim.substring(dim.indexOf("[") + 1,dim.indexOf("]"));
					dim = dim.substring(0,dim.indexOf("[")) + dim.substring(dim.indexOf("]") +1 );
					
				}
				dim = dim.replace("[", "");
				dim = dim.replace("]", "");	
				if (dim.toLowerCase().indexOf(" as ") >= 0)
				{
					alias = dim.substring(dim.toLowerCase().indexOf(" as ") + " as ".length());
					dim = dim.substring(0,dim.toLowerCase().indexOf(" as "));
				}
				String additionaltDim = null;
				if (subDim  != null)
				{
					if (subDim.indexOf("/") > 0)
					{
						String parms = "";
						String[] pcs = subDim.split("/");
						subDim = pcs[0];
						for (int r=1;r<pcs.length;r++)
						{
							parms = parms + "," + pcs[r];
						}
						additionaltDim = subDim + "(" + dim + parms + ")";						
					}
					else
					{
						dim = subDim + "(" + dim + ")";
					}
				}
				
				StringBuffer sb = new StringBuffer();
				sb.append("select " + dim + ", sum(" + value + ") " + ((additionaltDim!=null)?" , "+additionaltDim:","+dim+" as label") + ((valueLabel!=null)?valueLabel:",sum(" + value + ") as labelvalue") +
				          ((additionaltDim!=null)?" , '"+additionaltDim+"' as dim ": ", '"+dim+"' as dim ") + ", count(" + value + ") " +
						      " \n from " + table);
				if (locwhere != null)
				{
					sb.append(" \n" + locwhere);
				}
				sb.append(" \n group by " + dim);
				sb.append(" \n order by 1,2 ");
				//InfraUtil.out(sb);
				aSelects.add("(ALIAS=" + alias + "," + value + "=" + valueAlias +")" + sb.toString());				
				if ((i==0) && (j==0))
				{
					firstStatement = sb.toString();
				}
				
				//Concatena para a proxima dimensao
				//if (j > 0)
				{
					if (locwhere == null)
						locwhere = " where ";
					else
						locwhere = locwhere + " and ";
					locwhere = locwhere + " " + dim + " = ? "; 
				}
			}
			axes.add(aSelects);				
		}    	
    	return axes;
    }
    

}
