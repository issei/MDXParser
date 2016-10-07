import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import br.com.issei.mdx.entity.PluginParameters;
import br.com.issei.mdx.entity.SourceError;
import br.com.issei.mdx.entity.SourceField;
import br.com.issei.mdx.entity.SourceParameter;
import br.com.issei.mdx.exception.PluginException;
import br.com.issei.mdx.metadata.ColumnMetaData;
import br.com.issei.mdx.metadata.ErrorMetaData;
import br.com.issei.mdx.metadata.ParameterMetaData;
import br.com.issei.mdx.plugin.DashboardPlugin;
import br.com.issei.mdx.util.InfraUtil;




public class TestaSource
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		// Plugin Dashboard
		Properties prop = new Properties();
		prop.setProperty("driver", "com.mysql.jdbc.Driver");
		prop.setProperty("URL", "jdbc:mysql://localhost:3307/dashboard");
		prop.setProperty("user", "root");
		prop.setProperty("password", "indra");
		prop.setProperty("schema", "dashboard");

		DashboardPlugin plugin = new DashboardPlugin();
		try
		{
			plugin.setProperties(prop);

			PluginParameters parameters = new PluginParameters();
			parameters.set(new Date(format.parse("2013/03/01").getTime()));
			parameters.set(new Date(format.parse("2013/04/31").getTime()));
			plugin.setParameters(parameters);

			plugin.setExpression(" SELECT { [assigned_Group as 'GRUPO'].[assignee].1 as 'TEMPO' } ON 0  " + " FROM INCIDENTE " + " WHERE 1=0 and SUBMIT_DATE >= ? and SUBMIT_DATE <= ? and assigned_Group is not null and assignee is not null " + " ");
			
			{
	            LinkedList<SourceField> sourceFields = new LinkedList<SourceField>();
	            ColumnMetaData[] cols = plugin.getColumnsMetaData();

	            for (int z=0;z<cols.length;z++)
	            {
	            	//SgInfra.out(" z=" + z + " code=" + cols[z].getCode() + " type=" + cols[z].getType());
	                SourceField field = new SourceField();
	                //field.setSeqSource(src.getSequence());
	                //field.setSubsequence(new Integer(z));
	                field.setCode(cols[z].getCode());
	                field.setAlias(cols[z].getAlias());
	                field.setDescription(cols[z].getDescription());
	                field.setType(cols[z].getType());
	                field.setFieldLen(new Integer(cols[z].getLength()));
	                field.setFieldDec(new Integer(cols[z].getDecimal()));
	                sourceFields.add(field);

	            }
	            //src.setSubField(temp);
	            InfraUtil.out(sourceFields);
	        }
	        {
	        	LinkedList<SourceParameter> sourceParameters = new LinkedList<SourceParameter>();
	            ParameterMetaData[] parms = plugin.getParametersMetaData();

	            for (int z=0;z<parms.length;z++)
	            {
	                SourceParameter parm = new SourceParameter();
	                //parm.setSeqSource(src.getSequence());
	                //parm.setSubsequence(new Integer(z));
	                parm.setCode(parms[z].getAlias());
	                parm.setAlias(parms[z].getAlias());
	                parm.setDescription(parms[z].getDescription());
	                parm.setType(parms[z].getType());
	                parm.setFieldLen(new Integer(parms[z].getLength()));
	                parm.setFieldDec(new Integer(parms[z].getDecimal()));
	                sourceParameters.add(parm);
	            }
	            //src.setSubParameter(temp);
	            InfraUtil.out(sourceParameters);
	        }        
	        {
	            Vector sourceErrors = new Vector();
	            ErrorMetaData[] errs = plugin.getErrorsMetaData();
	            for (int z=0;z<errs.length;z++)
	            {
	                SourceError error = new SourceError();
//	                error.setSeqSource(src.getSequence());
//	                error.setSubsequence(new Integer(z));
	                error.setCode(errs[z].getCode());
	                error.setDescription(errs[z].getCode());
	                sourceErrors.add(error);
	            }
	            InfraUtil.out(sourceErrors);
	        }
		} catch (PluginException e)
		{
			e.printStackTrace();
		} catch (ParseException e)
		{
			e.printStackTrace();
		}

	}

}
