package com.myself.everythingP.core.model;

import lombok.Data;

@Data
public class Thing {
    private String name;
    private String path;
    private int depth;
    private FileType fileType;
    private long length;
    private String lastModifyTime;
}