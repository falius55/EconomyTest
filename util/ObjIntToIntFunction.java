package economy.util;

@FunctionalInterface
public interface ObjIntToIntFunction<T> {
	int apply(T obj, int ret);
}
