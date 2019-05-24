package com.myself.everythingP.core.search;

import com.myself.everythingP.core.model.Condition;
import com.myself.everythingP.core.model.Thing;

import java.util.List;

public interface FileSearch {
    List<Thing> search(Condition condition);
}
