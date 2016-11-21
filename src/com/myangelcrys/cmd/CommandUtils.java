package com.myangelcrys.cmd;

import org.apache.commons.cli.*;

/**
 * Created by cs on 16-10-13.
 */
public class CommandUtils {
    static CommandLine commandLine=null;
    public static CommandLine getInstance(String[] args){
        if (commandLine!=null)return commandLine;
        CommandLineParser parser=new DefaultParser();
        try {
            commandLine=parser.parse(getOptions(),args);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return commandLine;

    }
    private static Options getOptions(){
        Options options=new Options();
        options.addOption("u","url",true,"the url to download");
        options.addOption("f","fn",true,"the file name to save");
        options.addOption("c","ck",true,"cookie to add to header");
        options.addOption("t","ts",true,"thread size to download,max value is 5");
        options.addOption("p","proxy",true,"the proxy url");
        options.addOption(null,"ua",true,"user-agent");
        options.addOption(null,"max-speed",true,"max speed");
        options.addOption(null,"retry-times",true,"retry-times");
        options.addOption(null,"retry-delay",true,"retry-delay");
        return options;
    }
}
