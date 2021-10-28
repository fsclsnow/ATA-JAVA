package java8;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DistinctSink<T> implements ISink<T> {
    private LinkedHashMap<T, Integer> map;
    private final ISink<T> downstream;

    public <R, OUT> DistinctSink(ISink<T> downstream, Supplier<R> supplier, BiConsumer<R, OUT> accumulator) {
        this.downstream = downstream;
    }

    public DistinctSink(ISink<T> downstreamSink, ISink<T> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void begin(long size) {
        map = new LinkedHashMap<T, Integer>();
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
            this.map.put((T)o, 1);
    }

}
