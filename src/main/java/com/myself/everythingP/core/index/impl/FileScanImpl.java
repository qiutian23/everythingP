package com.myself.everythingP.core.index.impl;

import com.myself.everythingP.config.EverythingPConfig;
import com.myself.everythingP.core.dao.FileIndexDao;
import com.myself.everythingP.core.index.FileScan;
import com.myself.everythingP.core.interceptor.FileInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanImpl implements FileScan {
    private final FileIndexDao fileIndexDao;
    private final EverythingPConfig config = EverythingPConfig.getInstance();
    private List<FileInterceptor> interceptors=new ArrayList<>();

    public FileScanImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void index(String path) {
        File file = new File(path);
        if (file.isFile()) {
            if (config.getExcludePath().contains(file.getParent())) {
                return;
            }
        } else {
            if (config.getExcludePath().contains(path)) {
                return;
            }
            File[] files = file.listFiles();
            if(files!=null){
                for(File f:files){
                    index(f.getAbsolutePath());
                }
            }
        }
        for(FileInterceptor interceptor:interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptors(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }


}
