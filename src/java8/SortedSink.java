package java8;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SortedSink<T> implements ISink<T> {
    private List<T> list;
    private Comparator<? super T> comparator;
    private final ISink<T> downstream;

    public SortedSink(ISink<T> downstream, Comparator<T> comparator) {
        this.downstream = downstream;
        this.comparator = comparator;
    }

    @Override
    public void begin(long size) {
//        if (size >= 2147483639L) {
//            throw new IllegalArgumentException("Stream size exceeds max array size");
//        } else {
//            this.list = size >= 0L ? new ArrayList((int)size) : new ArrayList();
//        }
        list = new ArrayList<>();
    }

    @Override
    public void end() {
        this.list.sort(this.comparator);
        downstream.begin(-1);
        list.forEach(t -> downstream.accept(t));
//        this.downstream.begin((long)this.list.size());
//        Iterator var1 = this.list.iterator();
//
//        while(var1.hasNext()) {
//            T t = (T) var1.next();
//            this.downstream.accept(t);
//        }
        this.downstream.end();
        //this.list = null;
    }

    @Override
    public void accept(T t) {
        list.add(t);
    }
}
