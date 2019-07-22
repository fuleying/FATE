package com.webank.ai.fate.serving.adapter.processing;

import java.util.HashMap;
import java.util.Map;

public class ReturnResult {

    private int retcode;
    private String retmsg = "";
    private String caseid = "";
    private Map<String, Object> data = new HashMap();
    private Map<String, Object> log = new HashMap();
    private Map<String, Object> warn = new HashMap();

    public ReturnResult() {
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public int getRetcode() {
        return this.retcode;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public String getRetmsg() {
        return this.retmsg;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public void setLog(Map<String, Object> log) {
        this.log = log;
    }

    public Map<String, Object> getLog() {
        return this.log;
    }

    public void setWarn(Map<String, Object> warn) {
        this.warn = warn;
    }

    public Map<String, Object> getWarn() {
        return this.warn;
    }

    public void setCaseid(String caseid) {
        this.caseid = caseid;
    }

    public String getCaseid() {
        return this.caseid;
    }
}