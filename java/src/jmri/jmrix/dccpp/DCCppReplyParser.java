/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmri.jmrix.dccpp;

import jmri.jmrix.dccpp.DCCppReply;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author munderwd
 */
public class DCCppReplyParser {
    
    public static void parseReply(DCCppReply r, String s) {
        switch (s.charAt(0)) {
            case DCCppConstants.VERSION_REPLY:
                
        }
    }
    
    public static DCCppReply parseReply(String s) {
        Matcher m;
        log.debug("Parse charAt(0): {} ({})", s.charAt(0), Character.toString(s.charAt(0)));
        DCCppReply r = new DCCppReply(s.charAt(0), null);
        switch(s.charAt(0)) {
            case DCCppConstants.VERSION_REPLY:
                return(DCCppStatusReply.parseDCCppReply(s));
            case DCCppConstants.COMM_TYPE_REPLY:
                return(DCCppCommTypeReply.parseDCCppReply(s));
            case DCCppConstants.THROTTLE_REPLY:
            case DCCppConstants.TURNOUT_REPLY:
            case DCCppConstants.OUTPUT_REPLY:
            case DCCppConstants.PROGRAM_REPLY:
            case DCCppConstants.CURRENT_REPLY:
            case DCCppConstants.WRITE_EEPROM_REPLY:
            case DCCppConstants.MEMORY_REPLY:
            case DCCppConstants.SENSOR_REPLY_H:
            case DCCppConstants.SENSOR_REPLY_L:
            case DCCppConstants.MADC_FAIL_REPLY:
            case DCCppConstants.MADC_SUCCESS_REPLY:
            default:
                return(DCCppReply.parseDCCppReply(s));
        }
    }
    // initialize logging
    static Logger log = LoggerFactory.getLogger(DCCppReplyParser.class.getName());
    
}
