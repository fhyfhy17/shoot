package com.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> getFiles(String filePath, String filter) {
        File root = new File(filePath);
        File[] files = root.listFiles(pathname -> {
            String name = pathname.getName();
            return name.endsWith(filter);
        });
        List<String> filelist = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                getFiles(file.getAbsolutePath(), filter);
                System.out.println("显示" + filePath + "下所有子目录及其文件" + file.getAbsolutePath());
            } else {
                filelist.add(file.getAbsolutePath());
                System.out.println("显示" + filePath + "下所有子目录" + file.getAbsolutePath());
            }
        }
        return filelist;
    }

    public static String getShootMainPath() {
        String binPath = getBinPath();
        String main = binPath.substring(0, binPath.lastIndexOf(File.separator));
        return main;
    }

    public static String getJavaTemplatesPath() {
        return getShootMainPath()
                + File.separator
                + "common"
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "java"
                + File.separator
                + "com"
                + File.separator
                + "template"
                + File.separator
                + "templates";

    }

    public static String getBinPath() {
        return System.getProperty("user.dir");
    }

    public static String getTemplatesPah() {
        return getBinPath()
                + File.separator
                + "templates";
    }

    public static String getTemplatesTypePah() {
        return getTemplatesPah()
                + File.separator
                + "type";
    }

    public static void writeStringToFile(String path, String content, Charset charset) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        } else {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
        }

        try (
                Writer writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(file), charset))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
