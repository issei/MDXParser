package br.com.issei.mdx.entity;


import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import br.com.issei.mdx.exception.PluginException;



/**
 * Classe that holds the DataSource Parameters values 
 * @author Sysgen
 *
 */
public class PluginParameters extends PluginRecord
{
    public PluginParameters()
    {
        super();
    }
    public PluginParameters copy() throws PluginException
    {
    	PluginParameters resp = new PluginParameters();
        for (int z=0;z<getValues().size();z++)
        {
            Object val = getValues().get(z);
            String str= "null";
            if (val == null)
            {
            	resp.set(null);
            }
            else
            {
	            if (val instanceof BigDecimal)
	            {
	            	BigDecimal val2 = (BigDecimal)val;
	                resp.set(val2.multiply(new BigDecimal(1)));
	            }
	            else if (val instanceof Integer)
	            {
	                Integer val2 = (Integer)val;
	                resp.set(new Integer(val2.intValue()));
	            }
	            else if (val instanceof Date)
	            {
	                Date val2 = (Date)val;
	                resp.set(new Date(val2.getTime()));               
	            }
	            else if (val instanceof Time)
	            {
	                Time val2 = (Time)val;  
	                resp.set(new Time(val2.getTime()));                
	            }
	            else if (val instanceof Timestamp)
	            {
	                Timestamp val2 = (Timestamp)val;
	                resp.set(new Timestamp(val2.getTime()));                   
	            }
	            else if (val instanceof String)
	            {
	            	String val2 = (String)val;
	                resp.set(new String(val2));   
	            }
            }
        }
    	return resp;
    }
    
    
    
    public Object get(int col)  throws PluginException
    {
        if (getValues() == null)
        {
            throw new PluginException(8121,"No value assigned to this parameter yet... ");            
        }
        if (col > getValues().size())
        {
            throw new PluginException(8122,"Requested column greater than the parameter's number of columns(" + getValues().size() + ")...");            
        }
        return getValues().get(col);
    }
}
