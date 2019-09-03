
package com.airtel.helper.logging;

import java.io.*;
import java.net.URL;

import com.airtel.Utils.PropertyParser;
import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.io.Resources;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;

import com.airtel.helper.constants.Constants;
import com.airtel.helper.exception.resourceloader.ConfigServiceException;

public class AirtelLoggingMgr
{

    private static Logger logger = LogManager.getLogger(AirtelLoggingMgr.class);

    public static File logXMLConfigFile;

    public static String templateFile;

    public static String templateDir;

    private static LoggerContext loggerContext;

    static
    {
        initializeLogger();
    }

    /**
     *
     */
    private static void initializeLogger()
    {
        try
        {
            configureAndWatch();
        }
        catch (Exception e)
        {
            logger.error(e.getStackTrace());
        }
    }

    /**
     * @throws IOException
     * @throws ConfigServiceException
     */
    private static synchronized void configureAndWatch() throws IOException, ConfigServiceException
    {
        PropertyParser propertyParser = PropertyParser.load(Constants.AIRTEL_COMMON_CONFIG);

        String logFileName = propertyParser.getProperty(Constants.LOG_NAME_KEY);
        if (logFileName != null)
        {

            ClassLoader loader = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(),
                    PropertyParser.class.getClassLoader()
            );

            Optional<URL> logFilepath;
            logFilepath = Optional.fromNullable(loader.getResource(logFileName));

            File f = new File( logFilepath.get().getFile());
            setLoggerContext(f);
        }
    }



    /**
     * @param <T>
     * @param name
     * @return
     */
    public static <T> Logger getLogger(Class<T> name)
    {

        Logger logger = null;

        try
        {

            return LogManager.getLogger(name);

        }
        catch (LogConfigurationException ex)
        {
            ex.printStackTrace();
        }
        return logger;

    }

    public static void setLoggerContext(File configFile) throws IOException
    {
        loggerContext = (LoggerContext) LogManager.getContext(false);
        InputStream in = new FileInputStream(configFile);
        ConfigurationSource source = new ConfigurationSource(in,configFile);
        Configuration updated = ConfigurationFactory.getInstance().getConfiguration(loggerContext, source);
        loggerContext.start(updated);
    }


}
