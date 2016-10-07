package br.com.issei.mdx.metadata;
import br.com.issei.mdx.exception.PluginException;

 

 
 
 
/**
 * Definition of all columns returned in a DataSource execution
 * @author Sysgen
 *
 */
 
 
public class ColumnMetaData
       implements java.io.Serializable , 
                  java.lang.Cloneable    
{ 
   
   
   /* instance variables */
    
   private String sCode;
    
   private String sDescription;
    
   private String sAlias;
    
   private String sType;
    
   private int nLength;
    
   private int nDecimal;
   
   private int nPosition;
  
    
   /** Construtor Basico 
   */
   public ColumnMetaData () 
   { 
      
   } // public SourceField
    
    
   /* getters */
    
   /** Retorna o conteudo do atributo <b>Code</b>
   */
   public String getCode() 
   {
      return sCode;
   }
    
   /** Retorna o conteudo do atributo <b>Description</b>
   */
   public String getDescription() 
   {
      return sDescription;
   }
    
   /** Retorna o conteudo do atributo <b>Alias</b>
   */
   public String getAlias() 
   {
      return sAlias;
   }
    
   /** Retorna o conteudo do atributo <b>Type</b>
   */
   public String getType()
   {
      return sType;
   }
    
   /** Retorna o conteudo do atributo <b>Length</b>
   */
   public int getLength() 
   {
      return nLength;
   }
    
   /** Retorna o conteudo do atributo <b>Decimal</b>
   */
   public int getDecimal() 
   {
      return nDecimal;
   }

   public int getPosition()
   {
	   return nPosition;
   }

    
   /* setters */
    
   /** Executa o assinalamento do atributo <b>Code</b>
   */
   public void setCode(String p )   throws PluginException
   {
       if (p == null)
       {
           throw new PluginException(8101,"Code cannot be null..");
       }
       sCode = p ;
   }
    
   /** Executa o assinalamento do atributo <b>Description</b>
   */
   public void setDescription(String p )   throws PluginException
   {
       if (p == null)
       {
           throw new PluginException(8102,"Description cannot be null..");
       }
       sDescription = p ;
   }
    
   /** Executa o assinalamento do atributo <b>Alias</b>
   */
   public void setAlias(String p )   throws PluginException
   {
       if (p == null)
       {
           throw new PluginException(8103,"Alias cannot be null..");
       }
       sAlias = p ;
   }
    
   /** Executa o assinalamento do atributo <b>Type</b>
   */
   public void setType(String p ) throws PluginException
   {
       if (p == null)
       {
           throw new PluginException(8104,"Type cannot be null..");
       }  
       //Boolean x = new Boolean(true);
       if (p.equals("java.lang.String") ||
           p.equals("java.lang.Integer") ||  
           p.equals("java.lang.Boolean") ||  
           p.equals("java.math.BigDecimal") ||   
           p.equals("java.lang.Long") ||  
           p.equals("java.sql.Date") ||    
           p.equals("java.sql.Time") ||    
           p.equals("java.sql.Timestamp")
          )
       {
           sType = p ;
       }
       else
       {
           throw new PluginException(8105,"Type must be one of 'String','Integer','BigDecimal','Date','Time' or 'Timestamp' ...");           
       }
   }
    
   /** Executa o assinalamento do atributo <b>Length</b>
   */
   public void setLength(int p )  
   {
       nLength = p ;
   }
    
   /** Executa o assinalamento do atributo <b>Decimal</b>
   */
   public void setDecimal(int p)  
   {
       nDecimal = p ;
   }

   public void setPosition(int nPosition)
   {
	   this.nPosition = nPosition;
   }
    
   /**  Cria uma copia do objeto retornando este novo objeto.<br> */
   public ColumnMetaData copy ()  throws PluginException 
   {
      
     ColumnMetaData r = new ColumnMetaData();
      
     r.copyFrom(this);
      
     return r;
      
   } // public SgEntity copy () 
    
    
   /**  Cria uma copia do objeto retornando este novo objeto.<br> */
   public void copyFrom (ColumnMetaData r)  throws PluginException
   {    
      
     this.setCode                               (r.sCode ); 
     this.setDescription                        (r.sDescription ); 
     this.setAlias                              (r.sAlias ); 
     this.setType                               (r.sType ); 
     this.setLength                             (r.nLength ); 
     this.setDecimal                            (r.nDecimal ); 
     this.setPosition                           (r.nPosition ); 
            
   } // public void copyFrom (SgEntity pEntity)
    
   /**  Retorna representacao do Objeto em String.<br> */
   public String toString () 
   {
      
     try {
       return 
            " Code: "                           + getCode() + 
            " Description: "                    + getDescription() + 
            " Alias: "                          + getAlias() + 
            " Type: "                           + getType() + 
            " Length: "                         + getLength() + 
            " Decimal: "                        + getDecimal() + 
            " Position: "                        + getPosition(); 
     } catch (Exception e)
     {
         e.printStackTrace();
     }
      
     return "";
      
   } // String toString ()
    
    

    
   /** 
   * Metodo que verifica a igualdade entre objetos desta classe.
   */
   public boolean equals (ColumnMetaData p) 
   {
      
     if (this.getCode().equals(p.getCode()))
     {
       return true ;
     }
      
     return false;
      
   } // public void equals(SourceFieldp)

  
    
 } // public class SourceField
