package java8;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DistinctSink<T> implements ISink {
    private LinkedHashMap<T, Integer> map;
    private Comparator<? super T> comparator;
    private ISink<T> downstream;

    @Override
    public void begin(long size) {
        ConcurrentHashMap<T, Integer> map = new ConcurrentHashMap<>();
    }

    @Override
    public void end() {
        this.downstream.begin((long)this.map.size());
        Iterator var1 = this.map.entrySet().iterator();

        while(var1.hasNext()) {
            T t = (T) var1.next();
            this.downstream.accept(t);
        }
        this.downstream.end();
        this.map = null;
    }

    @Override
    public void accept(Object o) {
        if (!this.map.containsKey((T)o))
            this.map.put((T)o,map.get((T)o) + 1);
    }

}
