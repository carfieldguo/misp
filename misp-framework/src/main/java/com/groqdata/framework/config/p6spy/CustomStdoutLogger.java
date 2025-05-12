package com.groqdata.framework.config.p6spy;

import com.p6spy.engine.spy.appender.StdoutLogger;

public class CustomStdoutLogger extends StdoutLogger{

    @Override
    public void logText(String text) {
        System.out.println("sql:" + text);
    }
}
