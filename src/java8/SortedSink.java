package java8;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class SortedSink<T> implements ISink {
    private ArrayList<T> list;
    private Comparator<? super T> comparator;
    private ISink<T> downstream;

    @Override
    public void begin(long size) {
        if (size >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        } else {
            this.list = size >= 0L ? new ArrayList((int)size) : new ArrayList();
        }
    }

    @Override
    public void end() {
        this.list.sort(this.comparator);
        this.downstream.begin((long)this.list.size());
        Iterator var1 = this.list.iterator();

        while(var1.hasNext()) {
            T t = (T) var1.next();
            this.downstream.accept(t);
        }
        this.downstream.end();
        this.list = null;
    }

    @Override
    public void accept(Object o) {
        this.list.add((T)o);
    }

}
