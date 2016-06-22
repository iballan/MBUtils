package com.mbh.mbutils.ui;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * Created By MBH on 2016-06-21.
 */
public class MBFileUtils {
    public static String ReadFile(String path) throws IOException {
        return Files.toString(new File(path), Charsets.UTF_8);
    }

    public static void WriteToFile(String content, String path) throws IOException {
        Files.write(content, new File(path), Charsets.UTF_8);
    }
}
