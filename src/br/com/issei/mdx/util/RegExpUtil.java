package br.com.issei.mdx.util;

import java.util.regex.Pattern;
import br.com.issei.mdx.exception.CoreException;


public class RegExpUtil
{
    /** 
    * Verifica se um string esta compativel com a regular expression fornecida
    * @param text Parametro com ...
    */
    public static boolean match(String text, String re) throws CoreException
    {
        if (text == null)
        {
            return false;
        }
            if (!Pattern.matches(re,text))
            {
                return false;
            }

        return true;

    } 

} 
