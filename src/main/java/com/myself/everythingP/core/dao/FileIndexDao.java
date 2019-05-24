package com.myself.everythingP.core.dao;

import com.myself.everythingP.core.model.Condition;
import com.myself.everythingP.core.model.Thing;

import java.util.List;

public interface FileIndexDao {
    void insert(Thing thing);

    List<Thing> search(Condition condition);

    void delete(Thing thing);
}
