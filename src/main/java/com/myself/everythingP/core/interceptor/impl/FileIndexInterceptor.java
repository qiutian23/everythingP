package com.myself.everythingP.core.interceptor.impl;

import com.myself.everythingP.core.common.FileConvertThing;
import com.myself.everythingP.core.dao.FileIndexDao;
import com.myself.everythingP.core.interceptor.FileInterceptor;
import com.myself.everythingP.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {
    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
//        System.out.println("thing ==> " + thing);
        this.fileIndexDao.insert(thing);
    }
}
