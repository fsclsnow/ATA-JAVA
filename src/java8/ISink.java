package java8;

@FunctionalInterface
public interface ISink<T> {
    default void begin(long size) { }
    default void end(){ };
    default boolean cancellation() { return false; };

    void accept(T t);
}