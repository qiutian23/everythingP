package com.myself.everythingP.core.moniter.impl;

import com.myself.everythingP.core.common.FileConvertThing;
import com.myself.everythingP.core.common.HandlePath;
import com.myself.everythingP.core.common.Message;
import com.myself.everythingP.core.dao.FileIndexDao;
import com.myself.everythingP.core.moniter.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;

public class FileWatchImpl implements FileWatch,FileAlterationListener {
    private final FileIndexDao fileIndexDao;
    private final FileAlterationMonitor monitor;

    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(10);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandlePath handlePath) {
        for (String path : handlePath.getIncludePath()) {
            FileAlterationObserver observer = new FileAlterationObserver(path, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String path = pathname.getAbsolutePath();
                    for (String p : handlePath.getExcludePath()) {
                        if (p.startsWith(path)) {
                            return false;
                        }
                    }
                    return true;
                }
            });
            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {
        this.fileIndexDao.insert(FileConvertThing.convert(directory));
        System.out.println("onDirectoryCreate " + Message.print(FileConvertThing.convert(directory)));
    }

    @Override
    public void onDirectoryChange(File directory) {

    }

    @Override
    public void onDirectoryDelete(File directory) {
        this.fileIndexDao.delete(FileConvertThing.convert(directory));
        System.out.println("onDirectoryDelete " + Message.print(FileConvertThing.convert(directory)));
    }

    @Override
    public void onFileCreate(File file) {
        this.fileIndexDao.insert(FileConvertThing.convert(file));
        System.out.println("onFileCreate " + Message.print(FileConvertThing.convert(file)));
    }

    @Override
    public void onFileChange(File file) {

    }

    @Override
    public void onFileDelete(File file) {
        this.fileIndexDao.delete(FileConvertThing.convert(file));
        System.out.println("onFileDelete " + Message.print(FileConvertThing.convert(file)));
    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }
}
