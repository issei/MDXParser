package br.com.issei.mdx.util;



import java.security.AccessController;
import java.security.PrivilegedAction;

import br.com.issei.mdx.exception.CoreException;

/**
* Classe com metodos que facilitam o uso de classloaders.<br>
* @author SYSGEN
*/

public class ClassLoaderUtil
{
    /**
    * Carrega uma classe utilizandos os diversos classloaders disponiveis
    */
    public static Class getClass(String _className, Object _obj) throws CoreException
    {
        // Create final vars for doPrivileged block
        final String className = _className;
        final Object obj       = _obj;

        // Get the class within a doPrivleged block
        Object ret = AccessController.doPrivileged(new PrivilegedAction()
        {
            public Object run()
            {
                try
                {
                    // Check if the class is a registered class then
                    // use the classloader for that class.
                    ClassLoader classLoader = getClassLoader(className);
                    //Infra.out(classLoader.toString());
                    return Class.forName(className, true, classLoader);
                }
                //catch (ClassNotFoundException cnfe)
                catch (Exception cnfe)
                {
                }

                try
                {
                    // Try the context class loader
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    //Infra.out(classLoader.toString());
                    return Class.forName(className, true, classLoader);
                }
                catch (ClassNotFoundException cnfe2)
                {
                    try
                    {
                        // Try the classloader that loaded this class.
                        ClassLoader classLoader = obj.getClass().getClassLoader();
                        //Infra.out(classLoader.toString());
                        return Class.forName(className, true, classLoader);
                    }
                    catch (ClassNotFoundException cnfe3)
                    {
                        // Try the default class loader.
                        try
                        {

                            //Infra.out(obj.getClass().getClassLoader().toString());
                            return obj.getClass().getClassLoader().loadClass(className);
                        }
                        catch (Throwable e)
                        {
                            // Still not found, return exception
                            return e;
                        }
                    }
                }
            }
        });

        // If the class was located, return it.  Otherwise throw exception
        if (ret instanceof Class)
        {
            return (Class) ret;
        }
        else if (ret instanceof ClassNotFoundException)
        {
            //throw (ClassNotFoundException) ret;
            throw new CoreException(null, "Classe '" + _className + "' nao encontrada em nenhum classloader");
        }
        else
        {
            //throw new ClassNotFoundException(_className);
            throw new CoreException(null, "Classe '" + _className + "' nao encontrada em nenhum classloader");
        }
    }
    /**
    * Carrega uma classe utilizandos os diversos classloaders disponiveis
    */
    public static Class getClassOld(String sClassName, Object obj) throws CoreException
    {
        Class cls = null;
        if ((Thread.currentThread() != null) && (Thread.currentThread().getContextClassLoader() != null))
        {
            //Infra.out(" Thread.currentThread().getContextClassLoader() ==========> " + Thread.currentThread().getContextClassLoader().toString() );
            try
            {
                cls = Class.forName(sClassName, true, Thread.currentThread().getContextClassLoader());
            }
            catch (ClassNotFoundException e)
            {
                cls = null;
            }
        }
        if ((cls == null) && (obj.getClass() != null) && (obj.getClass().getClassLoader() != null))
        {
            //Infra.out("  obj.getClass().getClassLoader() ==========> " +  obj.getClass().getClassLoader().toString() );

            try
            {
                cls = Class.forName(sClassName, true, obj.getClass().getClassLoader());
            }
            catch (ClassNotFoundException e)
            {
                cls = null;
            }
        }
        if ((cls == null) && (ClassLoader.getSystemClassLoader() != null))
        {
            //Infra.out("  ClassLoader.getSystemClassLoader() ==========> " +  ClassLoader.getSystemClassLoader().toString() );
            try
            {
                cls = Class.forName(sClassName, true, ClassLoader.getSystemClassLoader());
            }
            catch (ClassNotFoundException e)
            {
                cls = null;
            }
        }
        if (cls == null)
        {
            throw new CoreException(null, "Classe '" + sClassName + "' nao encontrada em nenhum classloader");
        }
        return cls;
    } //  public static Class getClass(String sClassName, Object obj) ..
    /**
    * Carrega uma classe utilizandos os diversos classloaders disponiveis
    */
    public static ClassLoader getClassLoader(Object obj) throws CoreException
    {
        if ((Thread.currentThread() != null) && (Thread.currentThread().getContextClassLoader() != null))
        {
            return Thread.currentThread().getContextClassLoader();
        }
        if ((obj.getClass() != null) && (obj.getClass().getClassLoader() != null))
        {
            return obj.getClass().getClassLoader();
        }
        if (ClassLoader.getSystemClassLoader() != null)
        {
            return ClassLoader.getSystemClassLoader();
        }
        throw new CoreException(null, "Nenhum classloader encontrado para " + obj.getClass().getName());
    } //  public static Class getClass(String sFileName) throws CoreException

} 
