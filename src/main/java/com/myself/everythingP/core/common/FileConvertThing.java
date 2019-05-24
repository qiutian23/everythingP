package com.myself.everythingP.core.common;

import com.myself.everythingP.core.model.FileType;
import com.myself.everythingP.core.model.Thing;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileConvertThing {
    private FileConvertThing() {

    }

    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(fileDepth(file));
        thing.setFileType(fileType(file));
        thing.setLength(fileLength(file));
        thing.setLastModifyTime(fileTime(file));
        return thing;
    }

    private static long fileLength(File file) {
        //file.length()获取文件大小单位为字节，/1024可得KB数，由于整数运算省略小数部分，故加1
        return file.length() / 1024 + 1;
    }

    private static String fileTime(File file) {
        Date date = new Date(file.lastModified());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dateFormat.format(date);
    }

    private static FileType fileType(File file) {
        String name = file.getName();
        int index = name.indexOf(".");
        if (index != -1 && name.length() > index + 1) {
            String fileType = name.substring(index + 1);
            return FileType.lookup(fileType);
        }
        return FileType.OTHER;
    }

    private static int fileDepth(File file) {
        String path = file.getAbsolutePath();
        String[] segments = path.split("\\\\");
        return segments.length - 1;
    }
}
