package br.com.issei.mdx;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import br.com.issei.mdx.entity.PluginParameters;
import br.com.issei.mdx.entity.PluginRecord;
import br.com.issei.mdx.exception.PluginException;
import br.com.issei.mdx.metadata.ColumnMetaData;
import br.com.issei.mdx.metadata.ErrorMetaData;
import br.com.issei.mdx.metadata.ParameterMetaData;
import br.com.issei.mdx.plugin.PluginInterface;


public class QueryMDX {
	
	private String driver; 
	private String url;
	private String user; 
	private String password; 
	private String schema; 
	private PluginInterface plugin; 
	private String expression;
	private LinkedList<Object> parametersValues;
	private final Properties dataBaseAccessProperty = new Properties();
	
	
	/**
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 * @param schema
	 * @param plugin
	 * @param expression
	 * @param parametersValues
	 * @throws PluginException 
	 */
	public QueryMDX(String driver, String url, String user,String password,String schema,
			PluginInterface plugin,String expression,LinkedList<Object> parametersValues) throws PluginException {
		super();
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.schema = schema;
		this.plugin = plugin;
		this.expression = expression;
		this.parametersValues = parametersValues;
		dataBaseAccessProperty.setProperty("driver", driver);
		dataBaseAccessProperty.setProperty("URL", url);
		dataBaseAccessProperty.setProperty("user", user);
		dataBaseAccessProperty.setProperty("password", password);
		dataBaseAccessProperty.setProperty("schema", schema);
		this.plugin.setProperties(dataBaseAccessProperty);
		PluginParameters parameters = new PluginParameters();
		parameters.set(parametersValues);
		this.plugin.setParameters(parameters);
		this.plugin.setExpression(expression);
	}
	
	
	public List<PluginRecord> execute()
			throws PluginException {
		List<PluginRecord> resp;
		// List<List<String>> axes =
		// plugin.expandExpressions(plugin.getExpression());
		// for (int i = 0; i < axes.size(); i++)
		// {
		// System.out.println("==================================================== Axe="+
		// i);
		// Vector<String> sels = axes.get(i);
		// for (int j = 0; j < sels.size(); j++)
		// {
		// String sel = sels.get(j);
		// System.out.println(sel + ";");
		// }
		// }
		// {
		// System.out.println("================================== ErrorMetaData");
		// ErrorMetaData[] err = plugin.getErrorsMetaData();
		// for (int i = 0; i < err.length; i++)
		// {
		// ErrorMetaData errorMetaData = err[i];
		// SgInfra.out("errorMetaData="+errorMetaData);
		// }
		// }
		// {
		// System.out.println("================================== ParameterMetaData");
		// ParameterMetaData[] parm = plugin.getParametersMetaData();
		// for (int i = 0; i < parm.length; i++)
		// {
		// ParameterMetaData parmMetaData = parm[i];
		// SgInfra.out("parmMetaData="+parmMetaData);
		// }
		// }
		// {
		// System.out.println("================================== ColumnMetaData");
		// ColumnMetaData[] col = plugin.getColumnsMetaData();
		// for (int i = 0; i < col.length; i++)
		// {
		// ColumnMetaData colMetaData = col[i];
		// SgInfra.out("colMetaData="+colMetaData);
		// }
		// }
		// System.out.println("  ");
		// System.out.println("  ");
		// System.out.println("  ");
		// System.out.println("  ");
		// System.out.println("  ");
		resp = plugin.execute();
		return resp;
	}
	
	public ParameterMetaData[] getParameterMetaData() throws PluginException
	{
		return plugin.getParametersMetaData();
	}
	
	public ErrorMetaData[] getErrorMetaData() throws PluginException
	{
		return plugin.getErrorsMetaData();
	}
	
	public ColumnMetaData[] getColumnMetaData() throws PluginException
	{
		return plugin.getColumnsMetaData();
	}


	

}
