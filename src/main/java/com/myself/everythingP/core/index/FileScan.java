package com.myself.everythingP.core.index;

import com.myself.everythingP.core.interceptor.FileInterceptor;

public interface FileScan {
    void index(String path);
    void interceptors(FileInterceptor interceptor);
}
