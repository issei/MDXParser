package br.com.issei.mdx.plugin;


import java.util.TreeMap;

import br.com.issei.mdx.entity.PluginParameters;
import br.com.issei.mdx.entity.PluginRecord;
import br.com.issei.mdx.metadata.ColumnMetaData;
import br.com.issei.mdx.metadata.ParameterMetaData;




/**
 * Temporario structure to hold cube tuples during assembly
 * @author KleperRamos
 *
 */
public class DashboardStageRecord
{

	public String key; 
	public PluginRecord record ;
	public PluginParameters parameters ;
	public ParameterMetaData[] parmMetaData;
	public ColumnMetaData[] colMetaData;
	public int occ;
	public TreeMap<String,PluginRecord> children;
	
	public String toString()
	{
		if (record == null)
		{
			return key + " " + " " + " " + record;			
		}
		return key + " " + record.getDimensionName() + " " + record;
	}
	
}
