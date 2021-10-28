package java8;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractPipeline<IN, OUT> implements IStream<OUT> {

    private Supplier<Iterator> sourceIterator;
    private AbstractPipeline upstream;
    private int level;

    public AbstractPipeline(Collection<OUT> source) {
        this.sourceIterator = () -> source.iterator();
    }

    public AbstractPipeline(AbstractPipeline<?, IN> upstream) {
        this.upstream = upstream;
        this.sourceIterator = upstream.sourceIterator;
        this.level = upstream.level + 1;
    }

    @Override
    public abstract <R> IStream<R> map(Function<OUT, R> mapper);

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, OUT> accumulator) {
        R collectionResult = supplier.get();
        ISink head = wrapSink(val -> accumulator.accept(collectionResult, val));
        Iterator<OUT> itr = this.sourceIterator.get();
        head.begin(-1);
        while(itr.hasNext()) {
            OUT val = itr.next();
            System.out.println(val);
            head.accept(val);
        }
        head.end();
        return collectionResult;
    }

//    public <R> R sorted(Supplier<R> supplier, BiConsumer<R, OUT> accumulator, Comparator c) {
//        R sortingData = supplier.get();
//        ISink head = wrapSink(val -> accumulator.accept(sortingData, val));
//        Iterator<OUT> itr = this.sourceIterator.get();
//        ArrayList<R> list = new ArrayList<>();
//        while(itr.hasNext()) {
//            OUT val = itr.next();
//            list.add((R) val);
//        }
//        list.sort(c);
//        for (R r: list){
//            head.accept(r);
//        }
//        head.end();
//        return (R) list;
//    }

//    @Override
//    public <R> R distinct(Supplier<R> supplier, BiConsumer<R, OUT> accumulator) {
//        R distinctResult = supplier.get();
//        ISink head = wrapSink(val -> accumulator.accept(distinctResult, val));
//        Iterator<OUT> itr = this.sourceIterator.get();
//        ConcurrentHashMap<R,Integer> distinctMap = new ConcurrentHashMap<>();
//        while(itr.hasNext()) {
//            OUT val = itr.next();
//            if (distinctMap.containsKey(val)) continue;
//            else {
//                distinctMap.put((R) val,1);
//                head.accept(val);
//            }
//        }
//        head.end();
//        return (R) distinctMap.keys();
//    }

    private ISink<?> wrapSink(ISink<OUT> sink) {
        for(AbstractPipeline p = this; p.level > 0; p = p.upstream) {
            //sink3 = op3.onWrapSink(sink4)
            sink = p.onWrapSink(sink);
        }
        return sink;
    }

    abstract ISink<IN> onWrapSink(ISink<OUT> sink);
}

/**
 *  IStream.of(col) => op1 = new Operation()
 *          .map(x -> x * 2) => op1.map(x -> x * 2) => op2 = new Operation()
 *          .map(x -> new String(x)) => op2.map(..) => op3 = new Operation()
 *          .collect() => this = op3
 *
 *   op1(level = 0) <=> op2(level = 1) <=> op3(level = 2)     -> collect()
 *                                           this = op3
 *      sink1      ->     sink2(mapper) ->    sink3(mapper)  ->   sink4(collect)
 *
 *      downstream = sink4
 *      return new sink = sink3 {sink4.accept(val)}
 *      ISink<OUT> onWrapSink(ISink<R> downstreamSink) {
 *                 return new ISink<OUT>() {
 *                     @Override
 *                     public void accept(OUT val) {
 *                         R r = mapper.apply(val);
 *                         downstreamSink.accept(r);
 *                     }
 *                 };
 *             }
 *      1. stream -> op1 -> op2 -> op3
 *      2. terminal op3 sink3 -> op2 sink2 -> op1 sink1
 *      3. iterator -> sink1 -> sink2 -> sink3
 */