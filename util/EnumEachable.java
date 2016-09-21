package economy.util;

import java.util.EnumSet;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import economy.util.ObjIntToIntFunction;

public interface EnumEachable<T extends Enum<T>> {
	/**
	 * 型を明示的に指定することで、eachInt()の結果に使う型を選択した形で同様のことができます
	 */
	default <R> R each(Class<T> clazz, BiFunction<T,R,R> func) {
		R ret = null;
		for (T elem : clazz.getEnumConstants()) {
			ret = func.apply(elem, ret);
		}
		return ret;
	}
	/**
	 * すべてのEnum要素に対し、その要素とそれまでの結果を引数とした関数適用を繰り返します。また、その戻り値が新たな結果となります
	 * インターフェース実装時の型パラメータに縛られない
	 * @param clazz イテレートする列挙型のクラスインスタンス
	 * @param func 実行する処理を定義したラムダ型あるいはメソッド参照。第一引数はEnum要素、第二引数はそれまでの結果を入れたint変数
	 * @return すべての要素に関数を適用し終えた時点での結果
	 */
	static <E extends Enum<E>> int eachInt(Class<E> clazz, ObjIntToIntFunction<E> func) {
		int ret = 0;
		for (E elem : clazz.getEnumConstants()) {
			ret = func.apply(elem, ret);
		}
		return ret;
	}

	/**
	 * clazzで表されるEnumの要素のうち、funcでtrueと判定された要素の集合を返します
	 * @param clazz イテレートする列挙型のクラスインスタンス
	 * @param func 各要素を判定するラムダ式、あるいはメソッド参照。その引数に各要素が渡されるので、戻値をbool値にする
	 * @return true判定された要素の集合
	 */
	static <E extends Enum<E>> EnumSet<E> selectSet(Class<E> clazz, Predicate<E> func) {
		EnumSet<E> ret = EnumSet.noneOf(clazz);
		for (E elem : clazz.getEnumConstants()) {
			if (func.test(elem)) ret.add(elem);
		}
		return ret;
	}
}
