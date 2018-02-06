package com.company.product.project.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luojiahuaguet@163.com on 2017/12/19.
 */
public class ModelUtils{
    public static long[] text2IDArray(Map<String, Long> dict, String text){
        if(text == null || dict == null) {
            return null;
        }
        long[] target = new long[text.length()];
        for(int i=0; i<text.length(); i++){
            String element=Character.toString(text.charAt(i));
            if(dict.containsKey(element)){
                target[i] = Long.valueOf(dict.get(element).toString());
            }else{
                System.out.println(text + ", character not in word dict: "+element);
                return null;
            }
        }

        return target;
    }

    /**
     *
     * @param srcStr
     * @param sep: segmentation pattern. e.g.\t,,
     * @param requredLen: to align length
     * @param defaultStr
     * @return
     * To align strings，when the length of srcStr is greater than requireLen，truncate to requireLen，else conjoins @defaultStr
     */
    public static String alignString(String srcStr, String sep, int requredLen, String defaultStr){
        String newStr = "";
        if(srcStr != null && !srcStr.equalsIgnoreCase("null") && !srcStr.equals("")){
            String[] wordArr = srcStr.split(sep);
            int wordAppLen = wordArr.length;
            if(wordAppLen == requredLen){
                return srcStr;
            }
            else if(wordAppLen < requredLen){
                //concate defaultStr
                newStr = srcStr;
                for( int i = wordAppLen; i<requredLen; i++){
                    newStr += sep + defaultStr;
                }
            }else{
                //truncate: refer to: StringUtils.join(Arrays.copyOfRange())
                newStr = wordArr[0];
                for( int i = 1; i<requredLen; i++){
                    newStr += sep + wordArr[i];
                }
            }
        }else{
            newStr = defaultStr;
            for(int i = 1; i < requredLen; i ++){
                newStr += sep + defaultStr;
            }
        }
        return newStr;
    }


}



