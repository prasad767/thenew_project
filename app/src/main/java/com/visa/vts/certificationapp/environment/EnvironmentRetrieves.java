package com.visa.vts.certificationapp.environment;

import java.util.HashMap;


public class EnvironmentRetrieves {

    private HashMap<Environment,String> endpointMap = new HashMap<Environment, String>();
    protected static volatile EnvironmentRetrieves instance;


    public Environment getEnvironmentFromString(String environmentString){

        Environment environment = null;

        for (Environment environTmp : Environment.values())
        {
            if(environTmp.toString().equalsIgnoreCase(environmentString)){
                environment = environTmp;
                break;
            }
        }

        return environment;

    }

//    public static EnvironmentRetrieves getInstance() {
//
//        if (instance == null) {
//            synchronized (EnvironmentRetrieves.class) {
//                if (instance == null) {
//
//                    // It is best here to keep init outside the constructor,
//                    // preventing the "this" object from escaping before initialization is complete.
//                    EnvironmentRetrieves privateInstance = new EnvironmentRetrieves();
//                    privateInstance.init();
//                    instance = privateInstance;
//                }
//            }
//        }
//        return instance;
//    }
//
//    private void init() {
//
//        endpointMap.put(Environment.SANDBOX, "https://sandbox.digital.visa.com");
//        endpointMap.put(Environment.OCC_SANDBOX, "https://cert1.secure.checkout.visa.com");
//    }




//    public String getStringFromEnvironment(Environment environment){
//        String s_environment;
//
////        s
//
//
//
//        return s_environment;
//    }


}
