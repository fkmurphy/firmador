package org.openjfx.Main.file.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PathHelper {
    public static String generateDestionationPath(String srcPath)
    {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        int indexDot = srcPath.lastIndexOf(".");
        String datetime, extension, dstStr;

        datetime = formatter.format(date);
        dstStr = "";
        if(indexDot >= 0) {
            extension = srcPath.substring(indexDot, srcPath.length());
            if (extension.compareTo(".pdf") == 0) {
                dstStr = srcPath.substring(0, srcPath.lastIndexOf(".")) + "firmado_"+ datetime + ".pdf";
            }
        }
        return dstStr;
    }
}
