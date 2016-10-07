package br.com.issei.mdx.exception;



public class CoreException extends Exception
{
    private String code;
    private String cause;
    
    public CoreException(String code, String msg)
    {
        super(msg);
        printStackTrace();
        setCode(code);
    }
    
    public CoreException(int seq, String msg)
    {
        super(msg);
        printStackTrace();
        setCode("GENERIC");
    }   

    public CoreException(Throwable e)
    {
        super(e);
        printStackTrace();
        setCode("GENERIC");
    }    

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

}
