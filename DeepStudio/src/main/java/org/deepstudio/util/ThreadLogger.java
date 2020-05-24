package org.deepstudio.util;

import org.apache.log4j.*;

import java.io.File;
import java.io.IOException;

/**
 * 线程日志记录，每个线程会记录一个日志文件
 */
public class ThreadLogger {

    public static Logger getLogger(String logName, String logPath) {

        Logger logger = Logger.getLogger(logName);

        Layout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %m%n");

        try {
            ThreadLogger.ThreadFileAppender fileAppender = new ThreadFileAppender(layout, logPath, logName, "yyyy-MM-dd");
            //不追加
            fileAppender.setAppend(false);
            fileAppender.setImmediateFlush(true);
            fileAppender.setThreshold(Level.INFO);
            fileAppender.activateOptions();

            // 绑定到logger
            logger.setLevel(Level.INFO);
            logger.addAppender(fileAppender);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logger;
    }

    /*
     * 继承了log4j类的内部类
     */
    public static class ThreadFileAppender extends DailyRollingFileAppender {
        public ThreadFileAppender(Layout layout, String filePath, String fileName, String datePattern)
                throws IOException {
            super(layout, filePath+ File.separator + fileName + ".log", datePattern);
        }
    }
}
