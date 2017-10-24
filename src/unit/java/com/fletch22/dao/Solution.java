package com.fletch22.dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Solution {
    
    interface LogEntry {
        String getDate();
        String getCustomerId();
    }
 
    /*
     * Complete the function.
     */
    static int countRepeatVisitors(List<LogEntry> logEntries) {

        HashMap<String, LogEntry> map = new HashMap<String, LogEntry>();
        for (LogEntry logEntry : logEntries) {
           map.put(logEntry.getDate() + logEntry.getCustomerId(), logEntry);
        }
        
        Set<String> set = new HashSet<String>();
        for (Map.Entry<String, LogEntry> entry : map.entrySet()) {
            LogEntry logEntry = (LogEntry) entry.getValue();
            set.add(logEntry.getCustomerId());
        }
        
        return set.size();
    }
    
    public static void main(String[] args) throws IOException{
        Scanner in = new Scanner(System.in);
        final String fileName = System.getenv("OUTPUT_PATH");
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        int res;
        
        int _logEntries_size = 0;
        _logEntries_size = Integer.parseInt(in.nextLine());
        final List<LogEntry> _logEntries = new ArrayList<>(_logEntries_size);
        String _logEntries_item;
        for(int _logEntries_i = 0; _logEntries_i < _logEntries_size; _logEntries_i++) {
            try {
                _logEntries_item = in.nextLine();
            } catch (Exception e) {
                _logEntries_item = null;
                continue;
            }
            LogEntry newLogEntry = createLogEntry(_logEntries_item);
            if (newLogEntry != null) {
                _logEntries.add(newLogEntry);
            }
        }
        
        res = countRepeatVisitors(_logEntries);
        bw.write(String.valueOf(res));
        bw.newLine();
        
        bw.close();
        in.close();
    }

    private static LogEntry createLogEntry(String line) {
        final String[] split = line.split(" ");
        if (split.length != 2) {
            return null;
        }
        return new LogEntry() {
            @Override
            public String getDate() {
                return split[0];
            }
            
            @Override
            public String getCustomerId() {
                return split[1];
            }
        };
    }
}