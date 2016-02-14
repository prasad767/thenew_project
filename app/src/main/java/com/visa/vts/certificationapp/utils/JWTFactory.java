package com.visa.vts.certificationapp.utils;


import android.content.Context;

import com.visa.dmpd.token.JWTUtility;
import com.visa.vts.certificationapp.VTSCertApp;
import com.visa.vts.certificationapp.environment.Environment;

/**
 * Created by chandrakanta_sahoo on 12/11/2015.
 */
public class JWTFactory {
    public static String createJwtFromEnvironment(Context context,String paymentInstrument) {

        String key = null;
        String sharedSecret = null;

        VTSCertApp mainApp = (VTSCertApp) context;
        Environment environment = Environment.valueOf(mainApp.getsEnvironment());


        switch (environment) {

//            case DEV1: {
//                key ="CBGS63TYK3SUS40QVUV711Z0gxLt8dns4VBjLcGLJjE6X_yBk";
//                sharedSecret ="y/pg71xdo#KLrWjLIlp{U8#xV/0adZq@jXKxvGTT";
//            }
//            break;
//
//            case CERT1: {
//                key ="IYG876NGREPPESJDOGXK11SbOryoUpIeJ82lkeHK_2x7zwC3U";
//                sharedSecret ="hjIRkp$8U}JSvqKzLX95eLXx/4TGh}b+-G}a/2rp";
//            }
//            break;
//
//            case DEV2: {
//                key ="M3BD03UTH5LCWFX6D2SG11H-peFP6ERtzfOI7377WuqL4WHjI";
//                sharedSecret ="Ff$dgUxd{vYR3@Qr@{6ZoB4I0b+lwCiCu5bBI5bD";
//            }
//            break;
//
//            case QA1: {
//                key = "VPELP1S5QLB52N3ZZ2J011u6X6T6_1kFsrfIlbHMgZYKVV-2Q";
//                sharedSecret = "7ScJL+Zs3R3zc{OTH27UO29nwhoL7ycpDvsiA9U@";
//            }
//            break;
//
//            case QA2: {
//                key = "7T09K4JX4LHI4IT6JRD511ztpCx7EZYbrx__9sfV9VQFy1Vos";
//                sharedSecret = "78O#NCq5opvysOKOG+P}3D}oL0tZFFprDk-SliG1";
//            }
//            break;
//
//            case QAINT: {
//                key = "FTPI6A1HFNZVULJWZ3NM11Y8RdOKzOft4QNDS-7QUCZP3JH8s";
//                sharedSecret = "Sjadl0Tj8M}{DIiK$Wf3yIfubn0CSNTxPl7BWPIB";
//            }
//            break;
//
//            case QAINT2: {
//                key = "79ZM46AZGBOTXQBTD9EP11sN9uFEFzEDNisuPDLX-zZQxeOOI";
//                sharedSecret = "CROh9zwd$MiuUmCtIZJF8TgtWaA-5X/Ju-JX8jCM";
//            }
//            break;
//
//            case DEVINT: {
//                key = "9AJJX6JZRFNCI3I5G4F0113kbSr4N0s2IiD7bPME7XvelM-Cc";
//                sharedSecret = "9wy{N2dQWmvoHGL-Gv@1fmWvWmWgZL5vCmlsIFOn";
//            }
//            break;

            case SANDBOX: {
                key = Constants.SANDBOX_API_KEY;
                sharedSecret = Constants.SANDBOX_SHARED_SECRET;
            }
            break;

        }
        return JWTUtility.createSharedSecretJwe(paymentInstrument, key, sharedSecret);
    }
}
