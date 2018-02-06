package com.company.product.project.utils;

import java.io.*;
import java.nio.charset.Charset;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by luojiahuaguet@163.com on 2017/12/18.
 */
public class IOUtils {

    /**
     *
     * @return 读取classpath文件字符串
     */
    public static String readTextFromClassPath(String path) {
        if(path == null) return null;
        try {
            InputStream inputStream =
                    IOUtils.class.getClassLoader().getResourceAsStream(path);
            String str = org.apache.commons.io.IOUtils.toString(inputStream);
            return str;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return  null;
    }

     /**
      * @param
      * @return  按字节读取classpath路径文件
      * convert stream to bytes: refer to http://www.baeldung.com/convert-input-stream-to-array-of-bytes
      */
    public static byte[] readBytesFromClassPath(String path) {
        if(path == null) return null;
        try {
            InputStream inputStream =
                    IOUtils.class.getClassLoader().getResourceAsStream(path);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] byteArray = buffer.toByteArray();
            return byteArray;
        }catch(Exception e ){
            System.out.println(e.getMessage());
        }
        return  null;
    }

    /**
     * @param path 本地路径
     * @return 读取本地文件字符串
     */
    public static String readTextFromLocalFile(String path) {
        if(path == null) return null;
        try{
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            String str = new String(bytes, Charset.forName("UTF-8"));
            return str;
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: "
                + e.getMessage());
        }
        return null;
    }

    /**
     * @param path 本地路径
     * @return 读取本地文件字节
     */
    public static byte[] readBytesFromLocalFile(String path) {
        if(path == null) return null;
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
                System.err.println("Failed to read [" + path + "]: "
                        + e.getMessage());
        }
        return null;
    }

    public static List<String> readAllLinesFromLocalFile(String path) {
        if(path == null) return null;
        try {
            return Files.readAllLines(Paths.get(path),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Failed to read [" + path + "]: "
                    + e.getMessage());
        }
        return null;
    }

    public static String classPath2AbsolutePath(String path){
        try{
            return Paths.get(IOUtils.class.getClassLoader().getResource(path).toURI()).toAbsolutePath().toString();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }

}