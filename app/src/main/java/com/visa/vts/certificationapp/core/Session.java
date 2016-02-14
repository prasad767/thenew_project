package com.visa.vts.certificationapp.core;

import com.visa.vts.certificationapp.model.LogObject;
import com.visa.vts.certificationapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prrathin on 11/18/15.
 */
public class Session {

    private static Session session;
    private List<LogObject> logObjects;
    public int appFlow = Constants.FLOW_NONE;

    private Session(){

    }

    public  static Session getInstance(){
        if(session == null){
            session = new Session();
        }
        return session;
    }

    public void cleanup(){
        session = new Session();
    }


    public void addLog(LogObject logObject){
        if(logObjects == null){
            logObjects = new ArrayList<LogObject>();
        }
        logObjects.add(logObject);
    }

    public List<LogObject> getLogObjects(){
        return logObjects;
    }

    public void cleanLog(){
        logObjects = null;
    }



}
