package java8;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DistinctSink<T> implements ISink<T> {
    private Map<T, Integer> map;
    private final ISink<T> downstream;

    public DistinctSink(ISink<T> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void begin(long size) {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public void end() {
        this.downstream.begin((long)this.map.size());
        Iterator var1 = this.map.keySet().iterator();

        while(var1.hasNext()) {
            T t = (T) var1.next();
            this.downstream.accept(t);
        }
        this.downstream.end();
        this.map = null;
    }

    @Override
    public void accept(T t) {
        map.putIfAbsent(t, 0);
    }
}
