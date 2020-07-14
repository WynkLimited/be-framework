package com.report;

import com.airtel.Models.LogItemReq;
import com.airtel.helper.logging.AirtelLoggingMgr;
import org.apache.logging.log4j.Logger;

public class SentinelLogger
{

    private static Logger logger = AirtelLoggingMgr.getLogger(SentinelLogger.class);


    public static void info(LogItemReq message)
    {
        logger.info(message);
    }

    public static void info(Object message)
    {
        logger.info(message);
    }


    public static void debug(Object message)
    {
        logger.debug(message);
    }


    public static void error(Object message)
    {
        logger.error(message);
    }

    public static void trace(Object message)
    {
        logger.trace(message);
    }
}
