package java8;

@FunctionalInterface
public interface ISink<T> {
    default void begin(long size) { }
    default void end(){ };
    default boolean cancellation() { return false; };

    void accept(T t);

    abstract static class ChainedSink<T, R> implements ISink<T> {
        private final ISink<R> downstream;

        public ChainedSink(ISink<R> downstream){
            this.downstream = downstream;
        }

        @Override
        public void begin(long size) {
            downstream.begin(size);
        }

        @Override
        public void end() {
            downstream.end();
        }
    }

}