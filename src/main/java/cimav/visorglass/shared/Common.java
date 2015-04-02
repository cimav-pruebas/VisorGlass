/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cimav.visorglass.shared;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author calderon
 */
public class Common {
    
    public static String DNS_SERVER; // = "icrosby.cimav.edu.mx"; 
    public static String IP_SERVER; // = "201.174.72.42"; 
    public static String URL_SERVIDOR_ARCHIVOS_VIGENTES; // = "http://" + DNS_SERVER + ":8181/CONTROL_DE_DOCUMENTOS/VIGENTES/";
    
    public static String IpExterno;
    public static String PuertoExterno;
    
    public static String DropBoxPath;
    public static String DropBoxVigentesPath;
    public static String DropBoxAdministradoresPath;
    public static String DropBoxDeptosPath;
    public static String DropBoxTiposPath;
    public static String LuceneIndexPath;
    public static File FileToLuceneIndexDirectory;
    
    public static String NO_PATH = "NoPath";

    private static Properties prop;
    
    public static boolean loadSandBox(String realPath) {
        
        if (prop != null) {
            return true;
        }
        
        prop = new Properties();
        
    	try {
               
                if (!realPath.endsWith("/")) {
                    //Mac termina en / pero Linux no
                    realPath = realPath + "/";
                }
                
                String sandBoxPath = realPath + "sandbox.properties";
                
                String vigentesPath = "";
                
                File fSandBox = new File(sandBoxPath);
                if(fSandBox.exists()) { 
                    
                    prop.load(new FileInputStream(fSandBox));
                 
//    public static final String DNS_SERVER = "icrosby.cimav.edu.mx"; 
//    public static final String IP_SERVER = "201.174.72.42"; 
//    public static String URL_SERVIDOR_ARCHIVOS_VIGENTES = "http://" + DNS_SERVER + ":8181/CONTROL_DE_DOCUMENTOS/VIGENTES/";
                               
                            
                    Common.DropBoxPath = prop.getProperty("dropBoxPath", Common.NO_PATH);
                    vigentesPath = prop.getProperty("dropBoxVigentesPath",Common.NO_PATH);
                    Common.DropBoxVigentesPath = Common.DropBoxPath + vigentesPath;
                    Common.DropBoxAdministradoresPath = Common.DropBoxPath + prop.getProperty("dropBoxAdministradoresPath",Common.NO_PATH);
                    Common.DropBoxDeptosPath = Common.DropBoxPath + prop.getProperty("dropBoxDeptosPath",Common.NO_PATH);
                    Common.DropBoxTiposPath = Common.DropBoxPath + prop.getProperty("dropBoxTiposPath",Common.NO_PATH);

                    Common.DNS_SERVER = prop.getProperty("dnsServer", "crosby.cimav.edu.mx");
                    Common.IpExterno = prop.getProperty("ipExterno", "201.174.72.42");
                    Common.PuertoExterno = prop.getProperty("puertoExterno", Common.NO_PATH);
                    
                    Common.LuceneIndexPath = prop.getProperty("lucenIndexPath",Common.NO_PATH);
                } else {

                    Common.DNS_SERVER = "localhost";
                    Common.IpExterno = "10.0.0.1";
                    Common.PuertoExterno = "8383";
                    vigentesPath = "/CONTROL_DE_DOCUMENTOS/VIGENTES";

                    Common.DropBoxPath = Common.NO_PATH;
                    Common.DropBoxVigentesPath = Common.NO_PATH;
                    Common.DropBoxAdministradoresPath = Common.NO_PATH;
                    Common.DropBoxDeptosPath = Common.NO_PATH;
                    Common.DropBoxTiposPath = Common.NO_PATH;
                    Common.LuceneIndexPath = Common.NO_PATH;
                }

                Common.URL_SERVIDOR_ARCHIVOS_VIGENTES = "http://" + DNS_SERVER + ":"+PuertoExterno + vigentesPath;
                
//                Common.DropBoxVigentesPath = Common.DropBoxVigentesPath.replace("vigentes", "test");
                
                if (!Common.LuceneIndexPath.equals(Common.NO_PATH)) {
                    Common.FileToLuceneIndexDirectory = new File(Common.LuceneIndexPath);            
                }
                
 
    	} catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;
    }
    
}
