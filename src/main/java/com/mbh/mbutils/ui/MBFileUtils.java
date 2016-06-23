package com.mbh.mbutils.ui;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created By MBH on 2016-06-21.
 */
public class MBFileUtils {

    public static boolean FileExists(String path) {
        return new File(path).exists();
    }

    public static void CreateFile(String path, String content) {
        File file = CreateFile(path);
        if (file != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(content.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public static File CreateFile(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                String parentFolderPath = path.substring(0, path.lastIndexOf("/"));
                File parentFolder = new File(parentFolderPath);
                if (!parentFolder.exists()) {
                    parentFolder.mkdir();
                }
            }
            boolean isCreated = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String ReadFile(String path) throws IOException {
        return Files.toString(new File(path), Charsets.UTF_8);
    }

    public static void WriteToFile(String content, String path) throws IOException {
        Files.write(content, new File(path), Charsets.UTF_8);
    }

    public static boolean CreateFolderIfNotExists(String path) {
        File file = new File(path);
        return file.exists() || file.mkdir();
    }
}
