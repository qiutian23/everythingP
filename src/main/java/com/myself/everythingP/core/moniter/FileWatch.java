package com.myself.everythingP.core.moniter;

import com.myself.everythingP.core.common.HandlePath;

public interface FileWatch {
    void start();
    void stop();
    void monitor(HandlePath handlePath);
}
