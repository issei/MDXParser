package br.com.issei.mdx.metadata;
 

 
 
 
/**
 * Definition of all possible errors returned in a DataSource execution
 * @author Sysgen
 *
 */

public class ErrorMetaData
       implements java.io.Serializable , 
                  java.lang.Cloneable    
{ 
   
   
   /* instance variables */
    
   private String sCode;
    
   private String sDescription;
    
   private String sAlias;
    
    
   /** Construtor Basico 
   */
   public ErrorMetaData () 
   { 
      
   } // public SourceField
   /** Construtor Basico 
    */
    public ErrorMetaData (String code) 
    { 
       sCode = code; 
    } // public SourceField
    public ErrorMetaData (String code, String description) 
    { 
       sCode = code; 
       sDescription = description;
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
    
    
   /* setters */
    
   /** Executa o assinalamento do atributo <b>Code</b>
   */
   public void setCode(String p )  
   {
       sCode = p ;
   }
    
   /** Executa o assinalamento do atributo <b>Description</b>
   */
   public void setDescription(String p )  
   {
       sDescription = p ;
   }
    
   /** Executa o assinalamento do atributo <b>Alias</b>
   */
   public void setAlias(String p )  
   {
       sAlias = p ;
   }
    
   /**  Cria uma copia do objeto retornando este novo objeto.<br> */
   public ErrorMetaData copy ()   
   {
      
     ErrorMetaData r = new ErrorMetaData();
      
     r.copyFrom(this);
      
     return r;
      
   } // public SgEntity copy () 
    
    
   /**  Cria uma copia do objeto retornando este novo objeto.<br> */
   public void copyFrom (ErrorMetaData r)  
   {    
      
     this.setCode                               (r.sCode ); 
     this.setDescription                        (r.sDescription ); 
     this.setAlias                              (r.sAlias ); 
           
   } // public void copyFrom (SgEntity pEntity)
    
   /**  Retorna representacao do Objeto em String.<br> */
   public String toString () 
   {
      
     try {
       return 
            " Code: "                           + getCode() + 
            " Description: "                    + getDescription() + 
            " Alias: "                          + getAlias()  ; 
     } catch (Exception e)
     {
         e.printStackTrace();
     }
      
     return "";
      
   } // String toString ()
    
    

    
   /** 
   * Metodo que verifica a igualdade entre objetos desta classe.
   */
   public boolean equals (ErrorMetaData p) 
   {
      
     if (this.getCode().equals(p.getCode()))
     {
       return true ;
     }
      
     return false;
      
   } // public void equals(SourceFieldp)
   
    
 } // public class SourceField
