package com.hfi.insurance.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 集合流操作工具类，使用并行流，需要保证函数式是无状态、线程安全的
 *
 * @author SC
 * @since 1.0.0
 */
public final class StreamUtils {

    private StreamUtils() {

    }

    /**
     * 将集合转换为并行流
     *
     * @param source 原集合
     * @return 并行流
     */
    private static <T> Stream<T> toStream(Collection<T> source) {
        return source.stream().parallel();
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return true表示集合为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 将一个List拆分成多个子list，保留输入集合的null对象
     *
     * @param <T>     对象类型
     * @param source  对象列表
     * @param maxSize 子列表大小
     * @return 分组后的子列表
     */
    public static <T> List<List<T>> split(final List<T> source,
                                          final int maxSize) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive"); // NOSONAR
        }

        final int size = source.size();
        if (size <= maxSize) {
            return Collections.singletonList(source); // NOSONAR
        }

        final int step = (size + maxSize - 1) / maxSize;

        return IntStream.range(0, step).parallel()
                .mapToObj(i -> {
                    int fromIdx = i * maxSize;
                    int toIdx = fromIdx + maxSize;
                    if (toIdx > size) {
                        toIdx = size;
                    }
                    return source.subList(fromIdx, toIdx);
                }).collect(Collectors.toList());
    }

    /**
     * 将一个集合拆分成多个子list，保留输入集合的null对象
     *
     * @param <T>     对象类型
     * @param source  对象集合
     * @param maxSize 子列表大小
     * @return 分组后的子列表
     */
    public static <T> List<List<T>> split(final Collection<T> source,
                                          final int maxSize) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }

        final int size = source.size();
        if (size <= maxSize) {
            if (!(source instanceof List)) {
                return Collections.singletonList(new ArrayList<>(source));
            }
            return Collections.singletonList((List<T>) source);
        }

        final int step = (size + maxSize - 1) / maxSize;

        return IntStream
                .range(0, step).parallel().mapToObj(i -> source.stream()
                        .skip((long) i * maxSize).limit(maxSize).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * 将一个集合拆分成多个子Set，保留输入集合的null对象
     *
     * @param <T>     对象类型
     * @param source  对象集合
     * @param maxSize 子列表大小
     * @return 分组后的子列表
     */
    public static <T> List<Set<T>> splitToSet(final Collection<T> source,
                                              final int maxSize) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }

        final int size = source.size();
        if (size <= maxSize) {
            if (!(source instanceof Set)) {
                return Collections.singletonList(new HashSet<>(source));
            }
            return Collections.singletonList((Set<T>) source);
        }

        final int step = (size + maxSize - 1) / maxSize;

        return IntStream
                .range(0, step).parallel().mapToObj(i -> source.stream()
                        .skip((long) i * maxSize).limit(maxSize).collect(Collectors.toSet()))
                .collect(Collectors.toList());
    }

    /**
     * 将集合按特定的键转换成map，满足一对一关系，若key重复则后者覆盖前者
     *
     * @param <K>       Map的key类型
     * @param <V>       Map的value类型
     * @param source    对象集合
     * @param keyMapper 值->key映射
     * @return map
     */
    public static <K, V> Map<K, V> toMap(Collection<V> source,
                                         Function<V, K> keyMapper) {
        if (isEmpty(source)) {
            return new HashMap<>(0);
        }
        return toStream(source).collect(Collectors.toMap(keyMapper, Function.identity(), (key1, key2) -> key2));
    }

    /**
     * 将集合按特定的键转换成map，满足一对一关系，若key重复则后者覆盖前者
     *
     * @param <E>       输入元素类型
     * @param <K>       Map的key类型
     * @param <V>       Map的value类型
     * @param source    对象集合
     * @param keyMapper E->key映射
     * @param valMapper E->value映射
     * @return map
     */
    public static <E, K, V> Map<K, V> toMap(Collection<E> source,
                                            Function<E, K> keyMapper, Function<E, V> valMapper) {
        if (isEmpty(source)) {
            return new HashMap<>(0);
        }
        return toStream(source).collect(Collectors.toMap(keyMapper, valMapper, (key1, key2) -> key2));
    }

    /**
     * 将集合筛选并按特定的键转换成map，满足一对一关系，若key重复则后者覆盖前者
     *
     * @param <K>       Map的key类型
     * @param <V>       Map的value类型
     * @param source    对象集合
     * @param predicate 筛选条件
     * @param keyMapper 值->key映射
     * @return map
     */
    public static <K, V> Map<K, V> filterToMap(Collection<V> source,
                                               Predicate<V> predicate, Function<V, K> keyMapper) {
        if (isEmpty(source)) {
            return new HashMap<>(0);
        }
        return toStream(source).filter(predicate)
                .collect(Collectors.toMap(keyMapper, Function.identity(),
                        (key1, key2) -> key2));
    }

    /**
     * 将集合筛选并按特定的键转换成map，满足一对一关系，若key重复则后者覆盖前者
     *
     * @param <K>       Map的key类型
     * @param <V>       Map的value类型
     * @param source    对象集合
     * @param predicate 筛选条件
     * @param keyMapper 值->key映射
     * @return map
     */
    public static <E, K, V> Map<K, V> filterToMap(Collection<E> source,
                                                  Predicate<E> predicate,
                                                  Function<E, K> keyMapper,
                                                  Function<E, V> valMapper) {
        if (isEmpty(source)) {
            return new HashMap<>(0);
        }
        return toStream(source).filter(predicate)
                .collect(Collectors.toMap(keyMapper, valMapper, (key1, key2) -> key2));
    }

    /**
     * 将集合转换成相同对象类型的列表，保留输入集合的null对象
     *
     * @param <T>    对象类型
     * @param source 对象集合
     * @return list
     */
    public static <T> List<T> toList(Collection<T> source) {
        if (source instanceof List) {
            return (List<T>) source;
        }
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return new ArrayList<>(source);
    }

    /**
     * 将集合转换成特定对象类型的列表
     *
     * @param <T>    转换的目标类型
     * @param <R>    原始类型
     * @param source 对象集合
     * @param mapper 类型转换映射
     * @return list
     */
    public static <T, R> List<T> toList(Collection<R> source,
                                        Function<R, T> mapper) {
        return convert(source, mapper);
    }

    /**
     * 将集合转换成特定对象类型的列表(去重)
     *
     * @param <T>    转换的目标类型
     * @param <R>    原始类型
     * @param source 对象集合
     * @param mapper 类型转换映射
     * @param cmp    比较器
     * @return list
     */
    public static <T, R> List<T> toDistinctList(Collection<R> source,
                                        Function<R, T> mapper,
                                        Comparator<T> cmp) {
        return distinct(convert(source, mapper), cmp);
    }

    /**
     * 将集合转换成相同对象类型的集合，保留输入集合的null对象
     *
     * @param <T>    对象类型
     * @param source 对象集合
     * @return set
     */
    public static <T> Set<T> toSet(Collection<T> source) {
        if (source instanceof Set) {
            return (Set<T>) source;
        }
        if (isEmpty(source)) {
            return new HashSet<>(0);
        }
        return new HashSet<>(source);
    }

    /**
     * 将集合转换成特定对象类型的集合
     *
     * @param <T>    转换的目标类型
     * @param <R>    原始类型
     * @param source 对象集合
     * @param mapper 类型转换映射
     * @return set
     */
    public static <T, R> Set<T> toSet(Collection<R> source,
                                      Function<R, T> mapper) {
        if (isEmpty(source)) {
            return new HashSet<>(0);
        }
        return toStream(source).map(mapper)
                .collect(Collectors.toSet());
    }

    /**
     * 去重，过滤集合中的重复对象
     *
     * @param <T>    对象类型
     * @param source 对象集合
     * @param cmp    比较器
     * @return list
     */
    public static <T> List<T> distinct(Collection<T> source,
                                       Comparator<T> cmp) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(cmp)),
                        ArrayList::new));
    }

    /**
     * 将集合按特定的键分组, 满足一对多关系
     *
     * @param <K>       Map的key类型
     * @param <V>       Map的value列表的元素类型
     * @param source    对象集合
     * @param keyMapper 值->key映射
     * @return map
     */
    public static <K, V> Map<K, List<V>> group(Collection<V> source,
                                               Function<V, K> keyMapper) {
        if (isEmpty(source)) {
            return new HashMap<>(0);
        }
        return toStream(source)
                .collect(Collectors.groupingBy(keyMapper));
    }

    /**
     * 将集合过滤并按特定的键分组, 满足一对多关系
     *
     * @param <K>       Map的key类型
     * @param <V>       Map的value列表的元素类型
     * @param source    对象集合
     * @param predicate 筛选条件
     * @param keyMapper 值->key映射
     * @return map
     */
    public static <K, V> Map<K, List<V>> filterGroup(Collection<V> source,
                                                     Predicate<V> predicate, Function<V, K> keyMapper) {
        if (isEmpty(source)) {
            return new HashMap<>(0);
        }
        return toStream(source).filter(predicate)
                .collect(Collectors.groupingBy(keyMapper));
    }

    /**
     * 将集合排序后按特定的键分组, 满足一对多关系
     *
     * @param <K>        Map的key类型
     * @param <V>        Map的value列表的元素类型
     * @param source     对象集合
     * @param comparator 比较器
     * @param keyMapper  值->key映射
     * @return map
     */
    public static <K, V> Map<K, List<V>> sortGroup(Collection<V> source,
                                                   Comparator<V> comparator, Function<V, K> keyMapper) {
        if (isEmpty(source)) {
            return new HashMap<>(0);
        }
        return toStream(source).sorted(comparator)
                .collect(Collectors.groupingBy(keyMapper));
    }

    /**
     * 按特定规则筛选集合元素，返回List
     *
     * @param <T>       对象类型
     * @param source    对象集合
     * @param predicate 筛选条件
     * @return list
     */
    public static <T> List<T> filter(Collection<T> source,
                                     Predicate<T> predicate) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 按特定规则筛选集合元素，返回List
     *
     * @param <T>       对象类型
     * @param source    对象集合
     * @param predicate 筛选条件
     * @return list
     */
    public static <T, R> List<R> filterToList(Collection<T> source,
                                     Predicate<T> predicate,
                                     Function<T, R> mapper) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        List<T> collect = toStream(source).filter(predicate)
                .collect(Collectors.toList());
        return convert(collect, mapper);
    }

    /**
     * 获取第一个对象
     *
     * @param <T>    对象类型
     * @param source 对象集合
     * @return 第一个对象
     */
    public static <T> T getFirst(Collection<T> source) {
        if (isEmpty(source)) {
            return null;
        }
        return toList(source).get(0);
    }

    /**
     * 获取最后一个对象
     *
     * @param <T>    对象类型
     * @param source 对象集合
     * @return 最后一个对象
     */
    public static <T> T getLast(Collection<T> source) {
        if (isEmpty(source)) {
            return null;
        }
        return toList(source).get(source.size() - 1);
    }

    /**
     * 按匹配条件返回第一个对象
     *
     * @param <T>       对象类型
     * @param source    对象集合
     * @param predicate 匹配条件
     * @return 符合条件的第一个对象
     */
    public static <T> T findFirst(Collection<T> source,
                                  Predicate<T> predicate) {
        if (isEmpty(source)) {
            return null;
        }
        return toStream(source).filter(predicate)
                .findFirst().orElse(null);
    }

    /**
     * 将集合按匹配条件筛选并排序返回第一个对象
     *
     * @param <T>        对象类型
     * @param source     对象集合
     * @param predicate  匹配条件
     * @param comparator 比较器
     * @return 符合条件的第一个对象
     */
    public static <T> T sortFindFirst(Collection<T> source,
                                      Predicate<T> predicate, Comparator<T> comparator) {
        if (isEmpty(source)) {
            return null;
        }
        return toStream(source).filter(predicate).min(comparator).orElse(null);
    }

    /**
     * 按匹配条件返回某一个对象
     *
     * @param <T>       对象类型
     * @param source    对象集合
     * @param predicate 匹配条件
     * @return 符合条件的某个对象
     */
    public static <T> T findAny(Collection<T> source, Predicate<T> predicate) {
        if (isEmpty(source)) {
            return null;
        }
        return toStream(source).filter(predicate)
                .findAny().orElse(null);
    }

    /**
     * 按特定规则移除集合元素
     *
     * @param <T>       对象类型
     * @param source    对象集合
     * @param predicate 移除条件
     */
    public static <T> void remove(Collection<T> source,
                                  Predicate<T> predicate) {
        if (isEmpty(source)) {
            return;
        }
        source.removeIf(predicate);
    }

    /**
     * 将集合转换成特定对象列表
     *
     * @param <T>    转换的目标类型
     * @param <R>    原始类型
     * @param source 对象集合
     * @param mapper 类型转换映射
     * @return list
     */
    public static <T, R> List<T> convert(Collection<R> source,
                                         Function<R, T> mapper) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).map(mapper).collect(Collectors.toList());
    }

    /**
     * 将集合转换成特定对象列表并排序
     *
     * @param <T>        转换的目标类型
     * @param <R>        原始类型
     * @param source     对象集合
     * @param comparator 比较器
     * @param mapper     类型转换映射
     * @return list
     */
    public static <T, R> List<T> sortConvert(Collection<R> source,
                                             Comparator<R> comparator, Function<R, T> mapper) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).sorted(comparator)
                .map(mapper).collect(Collectors.toList());
    }

    /**
     * 将集合转换成特定对象列表并排序
     *
     * @param <T>        转换的目标类型
     * @param <R>        原始类型
     * @param source     对象集合
     * @param mapper     类型转换映射
     * @param comparator 比较器
     * @return 排序后的集合
     */
    public static <T, R> List<T> convertSort(Collection<R> source,
                                             Function<R, T> mapper, Comparator<T> comparator) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).map(mapper)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * 将集合每个元素分别转换成特定对象列表，然后合并成一个列表
     *
     * @param <T>    转换的目标类型
     * @param <R>    原始类型
     * @param source 对象集合
     * @param mapper 类型转换映射
     * @return list
     */
    public static <T, R> List<T> convertMerge(Collection<R> source,
                                              Function<R, ? extends Collection<T>> mapper) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).map(mapper)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换成特定对象列表，过滤重复对象
     *
     * @param <T>       转换的目标类型
     * @param <R>       原始类型
     * @param source 对象集合
     * @param mapper 类型转换映射
     * @param cmp    Comparator
     * @return list
     */
    public static <T, R> List<T> convertDistinct(Collection<R> source,
                                                 Function<R, T> mapper, Comparator<T> cmp) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).map(mapper)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(cmp)),
                        ArrayList::new));
    }

    /**
     * 对集合筛选并转换成特定对象列表
     *
     * @param <T>       转换的目标类型
     * @param <R>       原始类型
     * @param source    对象集合
     * @param predicate 筛选条件
     * @param mapper    类型转换映射
     * @return list
     */
    public static <T, R> List<T> filterConvert(Collection<R> source,
                                               Predicate<R> predicate, Function<R, T> mapper) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).filter(predicate)
                .map(mapper).collect(Collectors.toList());
    }

    /**
     * 对集合转换成特定对象列表并筛选
     *
     * @param <T>       转换的目标类型
     * @param <R>       原始类型
     * @param source    对象集合
     * @param mapper    类型转换映射
     * @param predicate 筛选条件
     * @return list
     */
    public static <T, R> List<T> convertFilter(Collection<R> source,
                                               Function<R, T> mapper, Predicate<T> predicate) {
        if (isEmpty(source)) {
            return new ArrayList<>(0);
        }
        return toStream(source).map(mapper)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 将集合的元素归并
     *
     * @param <T>         对象类型
     * @param source      对象集合
     * @param accumulator 归并操作
     * @return 归并后的对象
     */
    public static <T> T reduce(Collection<T> source,
                               BinaryOperator<T> accumulator) {
        if (isEmpty(source)) {
            return null;
        }
        return toStream(source).reduce(accumulator)
                .orElse(null);
    }

    /**
     * 将集合的元素转换后归并
     *
     * @param <T>         归并的目标类型
     * @param <R>         原始类型
     * @param source      对象集合
     * @param mapper      类型转换映射
     * @param accumulator 归并操作
     * @return 转换后的归并对象
     */
    public static <T, R> T convertReduce(Collection<R> source,
                                         Function<R, T> mapper, BinaryOperator<T> accumulator) {
        if (isEmpty(source)) {
            return null;
        }
        return toStream(source).map(mapper)
                .reduce(accumulator).orElse(null);
    }

    /**
     * 将集合的元素以特定分隔符拼接成字符串
     *
     * @param <R>       原始类型
     * @param source    对象集合
     * @param separator 分隔符
     * @param mapper    类型转换映射
     * @return 拼接后的集合
     */
    public static <R> String joinString(Collection<R> source, String separator,
                                        Function<R, String> mapper) {
        if (isEmpty(source)) {
            return StringUtils.EMPTY;
        }
        return toStream(source).map(mapper)
                .reduce((a, b) -> String.format("%s%s%s", a, separator, b))
                .orElse(StringUtils.EMPTY);
    }

    /**
     * 将集合元素以特定分隔符拼接成的字符串分裂还原
     *
     * @param <T>       对象类型
     * @param source    拼接字符串
     * @param separator 分隔符
     * @param mapper    类型转换映射
     * @return List string分割后产生的list
     */
    public static <T> List<T> splitString(String source, String separator,
                                          Function<String, T> mapper) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>(0);
        }
        return Stream.of(source.split(separator)).parallel().map(mapper)
                .collect(Collectors.toList());
    }

}
