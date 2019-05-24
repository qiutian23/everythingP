package com.myself.everythingP.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum  FileType {
    IMG("png", "jpg", "gif", "jpeg"),
    DOC("doc", "docx", "pdf", "xls", "xlsx", "ppt", "pptx", "txt"),
    BIN("exe", "sh", "jar", "msi"),
    ARCHIVE("zip", "rar"),
    OTHER("*");

    private Set<String> extend = new HashSet<>();

    FileType(String... extend) {
        this.extend.addAll(Arrays.asList(extend));
    }

    public static FileType lookup(String extendName) {
        for (FileType fileType : FileType.values()) {
            if (fileType.extend.contains(extendName)) {
                return fileType;
            }
        }
        return FileType.OTHER;
    }

    public static FileType lookupByName(String name){
        for(FileType fileType:FileType.values()){
            if(fileType.name().equals(name)){
                return fileType;
            }
        }
        return FileType.OTHER;
    }
}
