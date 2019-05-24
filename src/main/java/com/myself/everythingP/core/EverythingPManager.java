package com.myself.everythingP.core;

import com.myself.everythingP.config.EverythingPConfig;
import com.myself.everythingP.core.common.HandlePath;
import com.myself.everythingP.core.dao.DataSourceFactory;
import com.myself.everythingP.core.dao.FileIndexDao;
import com.myself.everythingP.core.dao.impl.FileIndexDaoImpl;
import com.myself.everythingP.core.index.FileScan;
import com.myself.everythingP.core.index.impl.FileScanImpl;
import com.myself.everythingP.core.interceptor.impl.FileIndexInterceptor;
import com.myself.everythingP.core.model.Condition;
import com.myself.everythingP.core.model.Thing;
import com.myself.everythingP.core.moniter.FileWatch;
import com.myself.everythingP.core.moniter.impl.FileWatchImpl;
import com.myself.everythingP.core.search.FileSearch;
import com.myself.everythingP.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class EverythingPManager {
    private static volatile EverythingPManager manager;
    private FileSearch fileSearch;
    private FileScan fileScan;
    private ExecutorService executorService;

//    private ThingDeleteInterceptor thingInterceptor;
//    private Thread thingClearThread;
//    private AtomicBoolean backgroundThreadStatus = new AtomicBoolean(false);

    private FileWatch fileWatch;

    private EverythingPManager() {
        this.initComponent();
    }

    public static EverythingPManager getInstance() {
        if (manager == null) {
            synchronized (EverythingPManager.class) {
                if (manager == null) {
                    manager = new EverythingPManager();
                }
            }
        }
        return manager;
    }

    private void initComponent() {
        DataSource dataSource = DataSourceFactory.getInstance();
        initOrResetDatabase();
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl(fileIndexDao);
        this.fileScan.interceptors(new FileIndexInterceptor(fileIndexDao));

//        this.thingInterceptor = new ThingDeleteInterceptor(fileIndexDao);
//        this.thingClearThread = new Thread(this.thingInterceptor);
//        this.thingClearThread.setDaemon(true);
//        this.thingClearThread.setName("Thing-Clear-Thread");

        this.fileWatch = new FileWatchImpl(fileIndexDao);

        new Thread(new Runnable() {
            @Override
            public void run() {
                buildIndex();
            }
        }).start();
    }

    public List<Thing> search(Condition condition) {
        return this.fileSearch.search(condition);
//                .stream().filter(new Predicate<Thing>() {
//            @Override
//            public boolean test(Thing thing) {
//                String path = thing.getPath();
//                File file = new File(path);
//                boolean flag = file.exists();
//                if (!flag) {
//                    thingInterceptor.apply(thing);
//                }
//                return flag;
//            }
//        }).collect(Collectors.toList());
    }

    public void buildIndex() {
        initOrResetDatabase();
        Set<String> directories = EverythingPConfig.getInstance().getIncludePath();
        if (this.executorService == null) {
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadID = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Clear-" + threadID.getAndIncrement());
                    return thread;
                }
            });
        }
        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());
        System.out.println("Start scanning ...");
        long start = System.currentTimeMillis();
        for (String path : directories) {
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    EverythingPManager.this.fileScan.index(path);
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Finish scanning ...");
        System.out.println("Cost time of scanning is " + (end - start)/Math.pow(10,3) + " seconds");
    }

    private void initOrResetDatabase() {
        DataSourceFactory.initDatabase();
    }


//    public void startThingClearThread() {
//        if (this.backgroundThreadStatus.compareAndSet(false, true)) {
//            this.thingClearThread.start();
//        } else {
//            System.out.println("can not start Thread repeatedly");
//        }
//    }

    public void startFileSystemMonitor() {
        EverythingPConfig config = EverythingPConfig.getInstance();
        HandlePath handlePath = new HandlePath();
        handlePath.setIncludePath(config.getIncludePath());
        handlePath.setExcludePath(config.getExcludePath());
        this.fileWatch.monitor(handlePath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("文件系统监控启动");
                fileWatch.start();
            }
        }).start();
    }
}
