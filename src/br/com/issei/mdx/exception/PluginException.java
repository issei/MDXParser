package br.com.issei.mdx.exception;




public class PluginException extends CoreException
{
    
    public PluginException(String code, String msg)
    {
        super(code, msg);
    }
    
    public PluginException(int seq, String msg)
    {
        super(seq , msg);
    }   

    public PluginException(Throwable e)
    {
        super(e);
    }    
}
