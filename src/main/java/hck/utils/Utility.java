package hck.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utility {

    private static Logger logger = LoggerFactory.getLogger(Utility.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static String dateToString(Date obj, String formato) {
        if (obj != null) {
            try {
                SimpleDateFormat sdfCustom = new SimpleDateFormat(formato);
                return sdfCustom.format(obj);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return "";
    }


    public static String cleanStr(Object obj) {
        String rez = "";
        if (obj != null) {
            rez = obj.toString();
            rez = rez.replaceAll("\\&", " e ");

        }
        return rez;
    }
}
