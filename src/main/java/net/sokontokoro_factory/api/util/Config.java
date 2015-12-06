package net.sokontokoro_factory.api.util;

import java.util.ResourceBundle;

public class Config {
    static final String propConfFileName = "config";

    public static ResourceBundle getResouce(){
    	ResourceBundle resouce = ResourceBundle.getBundle(propConfFileName);
    	return resouce;
    }
    /**
     * 引数をkeyとして、propConfigFuleName + .properties内のvalue(String)を返す
     * @param key
     * @return
     */
    public static String getString(String key){
    	return getResouce().getString(key);
    }
    
    /**
     * 引数をkeyとして、propConfigFuleName + .properties内のvalue(int)を返す
     * @param key
     * @return
     */
    public static int getInt(String key){
    	return Integer.parseInt(getResouce().getString(key));
    }
}