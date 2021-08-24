package com.network.moeidbannerlibrary.banner;

public class Utils {

    //public static final String ip = "http://thestockbazaar.com/admin/e-commerce/unniss";
    public static final String ip = "http://192.168.1.100:8113/prisma/unniss";
    public static final String IMAGE_URL = ip + "/images/";

    public static String getResizedImage(String path, boolean isResized) {
        if (isResized) {
            return IMAGE_URL+"small/"+path.substring(path.lastIndexOf("/")+1);
        }
        return path;
    }
}
