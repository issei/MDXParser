import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import br.com.issei.mdx.QueryMDX;
import br.com.issei.mdx.entity.EntidadeValor;
import br.com.issei.mdx.entity.PluginParameters;
import br.com.issei.mdx.entity.PluginRecord;
import br.com.issei.mdx.exception.PluginException;
import br.com.issei.mdx.plugin.DashboardPlugin;
import br.com.issei.mdx.plugin.PluginInterface;



public class TestaPlugin
{
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		
		 String DRIVER = "oracle.jdbc.driver.OracleDriver";
		 String URL = "jdbc:oracle:thin:@localhost:1521:XE";
		 String USER = "dashboard";
		 String PASSWORD = "dashboard";
		 String SCHEMA = "dashboard";
		
		 String expression =  " SELECT { [assigned_Group as 'GRUPO'].[operational_Tier_1].[assignee].1 as 'QTDE' } ON 0  " +
	 			  " FROM INCIDENTE " +
	               " WHERE SUBMIT_DATE >= ? and SUBMIT_DATE <= ? and assigned_Group is not null and assignee is not null " +
		                  " ";
		 // Plugin Dashboar
		 PluginInterface plugin = new DashboardPlugin();
		 LinkedList<Object> parametersValues = new LinkedList<>();
		 
        try
        {
        	parametersValues.add(new Date(format.parse("2013/01/01").getTime()));
        	parametersValues.add(new Date(format.parse("2013/12/31").getTime()));
          	
        	    // 
        	    //         	    
            	
            	
            	List<PluginRecord>  resp = null;
            	QueryMDX util = new QueryMDX(DRIVER, URL, USER,PASSWORD, SCHEMA, plugin,expression,parametersValues);
                try
				{
                	resp = util.execute();
					
				} catch (PluginException e)
				{
					e.printStackTrace();
				}           	

                
                List<EntidadeValor> lista = new LinkedList<EntidadeValor>();
        		for (int i = 0; i < resp.size(); i++)
           		{
        			PluginRecord rec = (PluginRecord)resp.get(i);
        			rec.printAll();
        			lista.addAll(rec.getAll(null));
           		}
//        		System.out.println("lista->"+lista);
        		
        		System.out.println("getColumnMetaData->"+util.getColumnMetaData());
        		System.out.println("getErrorMetaData->"+util.getErrorMetaData());
        		System.out.println("getParameterMetaData->"+util.getParameterMetaData());
                
        } catch (Exception e)
        {
            e.printStackTrace();
        }

	}


}
