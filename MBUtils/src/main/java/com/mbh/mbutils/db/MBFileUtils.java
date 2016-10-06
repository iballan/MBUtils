package com.mbh.mbutils.db;

import android.graphics.Bitmap;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.ByteArrayOutputStream;
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
                int indexOfLasSlash = path.lastIndexOf("/");
                if(indexOfLasSlash==-1) return null;
                String parentFolderPath = path.substring(0, indexOfLasSlash);
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

    public static File CreateFileIfNotExists(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                String parentFolderPath = path.substring(0, path.lastIndexOf("/"));
                File parentFolder = new File(parentFolderPath);
                if (!parentFolder.exists()) {
                    parentFolder.mkdir();
                }
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String ReadFile(String path) throws IOException {
        try{
            return Files.toString(new File(path), Charsets.UTF_8);
        }catch (Exception e){
            return "";
        }
    }

    public static byte[] ReadFileBytes(String path) throws IOException{
        try {
            return Files.toByteArray(new File(path));
        }catch (Exception e){
            return null;
        }
    }

    public static void WriteToFile( String path, String content) throws IOException {
        Files.write(content, new File(path), Charsets.UTF_8);
    }

    public static void ForceWriteToFile(String path, String content){
        try{
            Files.touch(new File(path));
            WriteToFile(path, content);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean ForceWriteBytesToFile(String path, byte[] content) {
        try{
            File file = new File(path);
            Files.touch(file);
            Files.write(content, file);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean CreateFolderIfNotExists(String path) {
        File file = new File(path);
        if(!file.exists()){
            return file.mkdirs();
        }else {
            return true;
        }
//        return file.exists() || file.mkdir();
    }

    public static File SaveBitmapToFile(Bitmap bm, String dir, String name) {
        File file = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        try {
            file = new File(dir, name + ".jpg");
            file.createNewFile();
            Files.write(bytes.toByteArray(), file);
//            OutputStream outputStream = new FileOutputStream(file).write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static boolean DeleteFile(String fullFilePath){
        if(fullFilePath == null || fullFilePath.isEmpty())
            return false;
        try {
            File file = new File(fullFilePath);

            if(file.isFile() && file.delete())
                return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean DeleteFolder(String folderFullPath){
        if(folderFullPath == null || folderFullPath.isEmpty())
            return false;
        try {
            File file = new File(folderFullPath);
            if(file.isDirectory() && file.delete())
                return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void ForceDeleteFolder(String path) {
        try {
            File file = new File(path);

            if (file.exists()) {
                String deleteCmd = "rm -r " + path;
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(deleteCmd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void ForceDeleteFile(String path) {
        try {
            File file = new File(path);

            if (file.exists()) {
                String deleteCmd = "rm " + path;
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(deleteCmd);
            }
        }catch (Exception exc){
            exc.printStackTrace();
        }
    }
}
