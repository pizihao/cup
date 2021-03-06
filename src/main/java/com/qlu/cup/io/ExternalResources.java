package com.qlu.cup.io;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Properties;

/**
 * @author liuwenhao
 */
public class ExternalResources {

    private ExternalResources() {
    }

    //复制文件
    public static void copyExternalResource(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            closeQuietly(source);
            closeQuietly(destination);
        }

    }

    //安静地关闭
    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    //读取property
    public static String getConfiguredTemplate(String templatePath, String templateProperty) throws FileNotFoundException {
        String templateName = "";
        Properties migrationProperties = new Properties();
        try {
            migrationProperties.load(new FileInputStream(templatePath));
            templateName = migrationProperties.getProperty(templateProperty);
        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return templateName;
    }

}
