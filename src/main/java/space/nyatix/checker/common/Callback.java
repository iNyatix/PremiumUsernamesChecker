package space.nyatix.checker.common;

/**
 * @author Nyatix
 * @since 26.08.2022 - 12:55
 **/
public interface Callback<T> {

    void accept(T t);

    default void onFailure(Throwable cause) {
        cause.printStackTrace();
    }

}
