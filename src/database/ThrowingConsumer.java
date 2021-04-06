package database;

/**
 * Based on `java.util.function.Consumer`, but allows functions to throw.
 *
 * Generic exceptions implemented from: https://stackoverflow.com/a/9779224/11354266
 *
 * @param <T> the type of the input to the operation
 * @param <ExceptionType> type of exception that can be thrown
 */
@FunctionalInterface
public interface ThrowingConsumer<T, ExceptionType extends Throwable> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws ExceptionType;
}
