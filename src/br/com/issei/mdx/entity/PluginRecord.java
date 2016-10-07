package br.com.issei.mdx.entity;


import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import br.com.issei.mdx.exception.PluginException;
import br.com.issei.mdx.util.FormatUtil;



/**
 * Classe that holds the DataSource columns values 
 * @author Sysgen
 *
 */
public class PluginRecord implements java.io.Serializable,java.lang.Cloneable
{
	private List<Object> values = null ;
	private List<String> content = null ;
	private int pos = 0;
	private String dimension = null;
	private String dimensionName = null;
	private List<PluginRecord> children = null ;
    public int occ = 0;
    private int chartType;
    private String query;
    
    public PluginRecord()
    {
        values = new ArrayList<Object>();
    }
    
    public PluginRecord copy() throws PluginException
    {
    	PluginRecord resp = new PluginRecord();
    	{
    		for (int z=0;z<values.size();z++)
    		{
    			Object val = values.get(z);
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
    	}
        resp.setDimensionName(this.dimensionName);
        resp.setDimension(this.dimension);
        resp.setOcc(this.occ);
        if (children != null)
        {
        	List<PluginRecord> copyChildren = new ArrayList<PluginRecord>();
    		for (int z=0;z<children.size();z++)
    		{
    			PluginRecord child = (PluginRecord)children.get(z);
    			copyChildren.add(child.copy());   			
    		}   
    		resp.setChildren(copyChildren);
        }
        
    	return resp;
    }

    public Object get(int col)  throws PluginException
    {
        if (values == null)
        {
            throw new PluginException(8121,"No value assigned to this record yet... ");            
        }
        if (col > values.size())
        {
            throw new PluginException(8122,"Requested column greater than the record's number of columns(" + values.size() + ")...");            
        }
        return values.get(col);
    }
    
    public final void set(LinkedList<Object> val) throws PluginException
    {
    	if(val!=null)
    	for (Object obj : val) {
    		this.set(obj);
		}
    }

    public final void set(Object val) throws PluginException
    {
        set(pos,val);
        pos++;
    }
    public final int size() throws PluginException
    {
        if (values == null)
        {
            return 0; 
        }
        return values.size();
    }
    public final void set(int index , Object val) throws PluginException
    {
        if ( (val == null) ||
           (val instanceof String) ||
           (val instanceof Integer) ||
           (val instanceof BigDecimal) ||
           (val instanceof Boolean) ||
           (val instanceof Date) ||
           (val instanceof Time) ||
           (val instanceof Timestamp)
           )           
        {
            if (index == values.size())
            {
               this.values.add(val);
            }
            else if (index > values.size())
            {
               for (int z=0;z<index - 1;z++)
               {
                   this.values.add(null);
               }
               this.values.add(val);
            }
            else if (index < values.size())
            {
               this.values.set(index,val);
            }
        }
        else
        {
            throw new PluginException(8120,"Assigned value must have one of the valid types, please verify index=" + index + " . Type=" + val.getClass().getName());
        }
    }
    public final int getCols()
    {
        if (values == null)
        {
            return 0;            
        }
        return values.size();
    }
    public String toString()
    {

        DecimalFormatSymbols decSymbols   = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat        formatDec    = new DecimalFormat("#,###,###,###,##0.00",decSymbols);
        DecimalFormat        formatInt    = new DecimalFormat("#,###,###,###,##0",decSymbols);
        SimpleDateFormat     formatDate   = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat     formatTime   = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatTimestamp  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        
        StringBuffer sb = new StringBuffer();
        if (values == null)
        {
            sb.append("{empty}");
            return sb.toString();            
        }
        sb.append("{");
        for (int z=0;z<values.size();z++)
        {
            if (z > 0)
            {
                sb.append(";");
            }
            Object val = values.get(z);
            String str= "null";
            if (val != null)
            {
	            str= values.get(z).toString();
	            //SgInfra.out(" val=" + val + " " + val.getClass().getName());
	            if (val instanceof BigDecimal)
	            {
	                BigDecimal val2 = (BigDecimal)val;
	                str = formatDec.format(val2.doubleValue());
	                //SgInfra.out(" str=" + str);
	            }
	            else if (val instanceof Integer)
	            {
	                Integer val2 = (Integer)val;
	                str = formatInt.format(val2.doubleValue());
	            }
	            else if (val instanceof Date)
	            {
	                Date val2 = (Date)val;
	                str = formatDate.format(val2);                
	            }
	            else if (val instanceof Time)
	            {
	                Time val2 = (Time)val;
	                str = formatTime.format(val2);                   
	            }
	            else if (val instanceof Timestamp)
	            {
	                Timestamp val2 = (Timestamp)val;
	                str = formatTimestamp.format(val2);                   
	            }
	            else
	            {
	                str= values.get(z).toString();
	            }
            }
            sb.append(z+ "=" +str);
        }
        sb.append("}");
        return sb.toString();
    }

    public String toString(String formatMsg)
    {

        DecimalFormatSymbols decSymbols   = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat        formatDec    = new DecimalFormat("#,###,###,###,##0.00",decSymbols);
        DecimalFormat        formatInt    = new DecimalFormat("#,###,###,###,##0",decSymbols);
        SimpleDateFormat     formatDate   = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat     formatTime   = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatTimestamp  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        
        String resp = new String(formatMsg);
        if (values == null)
        {
            return resp;            
        }
        for (int z=0;z<values.size();z++)
        {
            Object val = values.get(z);
            String str= "null";
            if (val != null)
            {
	            str= values.get(z).toString();
	            //SgInfra.out(" val=" + val + " " + val.getClass().getName());
	            if (val instanceof BigDecimal)
	            {
	                BigDecimal val2 = (BigDecimal)val;
	                str = formatDec.format(val2.doubleValue());
	                //SgInfra.out(" str=" + str);
	            }
	            else if (val instanceof Integer)
	            {
	                Integer val2 = (Integer)val;
	                str = formatInt.format(val2.doubleValue());
	            }
	            else if (val instanceof Date)
	            {
	                Date val2 = (Date)val;
	                str = formatDate.format(val2);                
	            }
	            else if (val instanceof Time)
	            {
	                Time val2 = (Time)val;
	                str = formatTime.format(val2);                   
	            }
	            else if (val instanceof Timestamp)
	            {
	                Timestamp val2 = (Timestamp)val;
	                str = formatTimestamp.format(val2);                   
	            }
	            else
	            {
	                str= values.get(z).toString();
	            }
            }
            resp = FormatUtil.replace(resp, "{" + (z + 1)+ "}", str );
        }
        return resp;
    }    
    public final void parse(String p) throws Exception
    {        
        if ((p == null) || p.equals("{empty}"))
        {
            values = null;            
        }
        String p1 = p;
        if (p1.startsWith("{"))
        {
            p1 = p1.substring(1, p1.length());
        }
        if (p1.endsWith("}"))
        {
            p1 = p1.substring(0, p1.length() -1);
        }
        StringTokenizer st = new StringTokenizer(p1,";");
        while (st.hasMoreTokens())
        {
            String piece =  st.nextToken();
            StringTokenizer st2 = new StringTokenizer(piece,"=");
            if (st2.countTokens() != 2)
            {
                throw new Exception("Erro parsing PluginRecord. value=" + p);
            }
            String sIndex = st2.nextToken();
            String val    = st2.nextToken();
            int index = Integer.parseInt(sIndex);
            if (index == values.size())
            {
               this.values.add(val);
            }
            else if (index > values.size())
            {
               for (int z=0;z<index - 1;z++)
               {
                   this.values.add(null);
               }
               this.values.add(val);
            }
            else if (index < values.size())
            {
               this.values.set(index,val);
            }            
        }
    }
    

    public final void printAll() 
    {
    	System.out.println(FormatUtil.toStringRepeat("   ", this.occ) + 
    			  " Occ="+ this.occ + 
    			  " Val="+ FormatUtil.toString(this.toString(),80) + 
    			  " DimensionName=" + FormatUtil.toString(this.dimensionName,15) + 
    			  " Dimension=" + FormatUtil.toString(this.dimension,15));
		if (children != null) {
			List<PluginRecord> copyChildren = new ArrayList<PluginRecord>();
			for (int z = 0; z < children.size(); z++) {
				PluginRecord child = (PluginRecord) children.get(z);
				child.printAll();
			}
		}
    }
    
    public List<EntidadeValor> getAll(EntidadeValor pai) 
    {
    	List<EntidadeValor> lista = new LinkedList<EntidadeValor>();
    	EntidadeValor entity = new EntidadeValor((String)values.get(0),values.get(1),pai);
    	lista.add(entity);
		if (children != null) {
			List<PluginRecord> copyChildren = new ArrayList<PluginRecord>();
			for (int z = 0; z < children.size(); z++) {
				PluginRecord child = (PluginRecord) children.get(z);
				lista.addAll(child.getAll(entity));
			}
		}
		return lista;
    }

	public final String getDimension()
	{
		return dimension;
	}

	public final void setDimension(String p)
	{
		this.dimension = p;
	}
	public final String getDimensionName()
	{
		return dimensionName;
	}

	public final void setDimensionName(String p)
	{
		this.dimensionName = p;
	}

	public final List<PluginRecord> getChildren()
	{
		return children;
	}

	public final void setChildren(List<PluginRecord> children)
	{
		this.children = children;
	}

	public final int getOcc()
	{
		return occ;
	}

	public final void setOcc(int occ)
	{
		this.occ = occ;
	}

	public final List<String> getContent()
	{

		List<String> resp = new ArrayList<String>();
        DecimalFormatSymbols decSymbols   = new DecimalFormatSymbols(new Locale("pt", "BR"));
        DecimalFormat        formatDec    = new DecimalFormat("############0.00",decSymbols);
        DecimalFormat        formatInt    = new DecimalFormat("############0",decSymbols);
        SimpleDateFormat     formatDate   = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat     formatTime   = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatTimestamp  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        
        if (values == null)
        {
            return resp;            
        }
        for (int z=0;z<values.size();z++)
        {
            Object val = values.get(z);
            String str= "null";
            if (val != null)
            {
	            str= values.get(z).toString();
	            //SgInfra.out(" val=" + val + " " + val.getClass().getName());
	            if (val instanceof BigDecimal)
	            {
	                BigDecimal val2 = (BigDecimal)val;
	                if (val2.scale() <= 0)
	                {
	                	str = formatInt.format(val2.doubleValue());	                	
	                }
	                else
	                {
	                	str = formatDec.format(val2.doubleValue());
	                }
	                //SgInfra.out(" str=" + str);
	            }
	            else if (val instanceof Integer)
	            {
	                Integer val2 = (Integer)val;
	                str = formatInt.format(val2.doubleValue());
	            }
	            else if (val instanceof Date)
	            {
	                Date val2 = (Date)val;
	                str = formatDate.format(val2);                
	            }
	            else if (val instanceof Time)
	            {
	                Time val2 = (Time)val;
	                str = formatTime.format(val2);                   
	            }
	            else if (val instanceof Timestamp)
	            {
	                Timestamp val2 = (Timestamp)val;
	                str = formatTimestamp.format(val2);                   
	            }
	            else
	            {
	                str= values.get(z).toString();
	            }
            }
            resp.add(str);
        }
        return resp;
	}

	public final void setContent(List<String> content)
	{
		this.content = content;
	}

	public final int getChartType()
	{
		return chartType;
	}

	public final void setChartType(int chartType)
	{
		this.chartType = chartType;
	}

	public final String getQuery()
	{
		return query;
	}

	public final void setQuery(String query)
	{
		this.query = query;
	}

	/**
	 * @return the values
	 */
	public final List<Object> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public final void setValues(List<Object> values) {
		this.values = values;
	}

	/**
	 * @return the pos
	 */
	public  final int getPos() {
		return pos;
	}

	/**
	 * @param pos the pos to set
	 */
	public final void setPos(int pos) {
		this.pos = pos;
	}
}
