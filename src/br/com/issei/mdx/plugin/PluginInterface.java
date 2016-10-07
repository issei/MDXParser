package br.com.issei.mdx.plugin;


import java.util.List;
import java.util.Properties;
import java.util.Vector;

import br.com.issei.mdx.entity.PluginParameters;
import br.com.issei.mdx.entity.PluginRecord;
import br.com.issei.mdx.exception.PluginException;
import br.com.issei.mdx.metadata.ColumnMetaData;
import br.com.issei.mdx.metadata.ErrorMetaData;
import br.com.issei.mdx.metadata.ParameterMetaData;



/**
 * Interface tha must be implemented by all classes to be used as Vigilo´s DataSource
 * @author Sysgen
 *
 */
public interface PluginInterface
{
    /**
     * Assigns the properties to be used in SgInfra.javathe Source
     * @param prop
     * @throws PluginException
     */
    public void setProperties(Properties prop) throws PluginException;

    /**
     * Assigns the Expression to be executed
     * @param expression
     * @throws PluginException
     */
    public void setExpression(String expression) throws PluginException;    
    

    /**
     * Assigns the Parameters to be used during execution
     * @param expression
     * @throws PluginException
     */
    public void setParameters(PluginParameters parameters) throws PluginException;   
    
    /**
     * Gets the properties used in the Source
     * @param prop
     * @throws PluginException
     */
    public Properties getProperties() throws PluginException;

    /**
     * Gets the Expression to be executed
     * @param expression
     * @throws PluginException
     */
    public String getExpression() throws PluginException;    
    

    /**
     * Gets the Parameters to be used during execution
     * @param expression
     * @throws PluginException
     */
    public PluginParameters getParameters() throws PluginException;          
    
    /**
     * Returns a Vector with the column definition - ColumnMetaData
     * @return
     * @throws PluginException
     */
    public ColumnMetaData[] getColumnsMetaData() throws PluginException;
    
    /**
     * Returns a Vector with the possible errors in the source - ErrorMetaData
     * @return
     * @throws PluginException
     */    
    public ErrorMetaData[] getErrorsMetaData() throws PluginException;
  
    
    /**
     * Returns a Vector with the Parameters in the source - ParameterMetaData
     * @return
     * @throws PluginException
     */    
    public ParameterMetaData[] getParametersMetaData() throws PluginException;

    public List<PluginRecord> execute() throws PluginException;
    

}
