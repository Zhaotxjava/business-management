package com.hfi.insurance.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @ClassName: EnumHelper.java
 * @Description: 枚举
 *
 */
@Slf4j
@SuppressWarnings("all")
public final class EnumHelper implements Serializable {

    private static final long serialVersionUID = 5L;
    private static final String GET_CODE_METHOD = "getCode";
    private static final String GET_LABEL_METHOD = "getText";
    private static final String VALUEOF_METHOD = "valueOf";

    private EnumHelper() {

    }

    /**
     * Gets an mutable list of enumerations defined in the given Enum.
     * 
     * @param <T>
     *            subclass of Enum
     * @param clazz
     *            the class instance
     * @return a list of objects of type T
     */
    public static <T extends Enum<T>> List<T> inspectConstants(final Class<T> clazz) {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
    }

    /**
     * Gets an mutable list of enumerations defined in the given Enum.
     * 
     * @param <T>
     *            subclass of Enum
     * @param clazz
     *            the class instance
     * @return a list of objects of type T
     */
    public static <T extends Enum<T>> List<T> inspectConstants(final Class<T> clazz, boolean containsNull) {
        List<T> list = new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
        if (!containsNull) {
            list.remove(0);
        }
        return list;
    }

    /**
     * Translates code into its corresponding enum instance.
     * <p>
     * NOTE: To make this function work, please DO implement the following method in your Enum class:
     * 
     * <pre>
     * public String getCode() {
     *     // return the unique code
     *     return &quot;CODE&quot;;
     * }
     * </pre>
     * 
     * </p>
     * 
     * @param <T>
     *            type of enum
     * @param clazz
     *            the type of enum instance expected
     * @param code
     *            the unique customized code for the enum instance. Usually, the code is the used in underlying database
     *            table.
     * @return an instance of type T, or null if the code is not defined
     */
    public static <T extends Enum<T>> T translate(final Class<T> clazz, final Integer code) {
        if (code == null) {
            return null;
        }
        try {
            final Method m = clazz.getDeclaredMethod(GET_CODE_METHOD);
            for (T t : inspectConstants(clazz)) {
                if (code.equals(m.invoke(t))) {
                    return t;
                }
            }
        } catch (Exception e) { // NOSONAR
            log.warn("failed to translate code {} into object of type {}", code, clazz);
        }

        return null;
    }
    
	/**
	* 
	* @Title  translateByName  
	* @Description
	* @param  clazz Class<T>
	* @param  name String
	* @return <T> 数组
	* @throws
	*/
	public static <T extends Enum<T>> T translateByName(final Class<T> clazz, final String name) {
	    if (name == null) {
	        return null;
	    }
		try {
			Object value = clazz.getMethod(VALUEOF_METHOD, String.class).invoke(null, new Object[]{name});
			if (value == null) {
				return null;
			}
			return (T) value;
		} catch (Exception e) {
		    log.warn("failed to translateByName name {} into object of type {}", name, clazz);
		}
		return null;
	}

    /**
     * Retrieve code value of certain Enum instance, this method is null-safe. i.e. returns null if input instance is
     * null.
     * 
     * @param <T>
     * @param clazz
     * @param obj
     * @return code
     */
    public static <T extends Enum<T>> String getCode(final T obj) {
        if (obj == null) {
            return null;
        }
        try {
            Class<?> clazz = obj.getClass();
            final Method m = clazz.getDeclaredMethod(GET_CODE_METHOD);
            return m.invoke(obj).toString();
        } catch (Exception e) { // NOSONAR
            // ignore
        }
        return null;
    }

    /**
     * Translates label into its corresponding enum instance.
     * 
     * @param <T>
     * @param clazz
     * @param label
     * @return
     */
    public static <T extends Enum<T>> T translateByLabel(final Class<T> clazz, final String label) {
        if (label == null) {
            return null;
        }
        try {
            final Method m = clazz.getDeclaredMethod(GET_LABEL_METHOD);
            for (T t : inspectConstants(clazz)) {
                if (label.equals(m.invoke(t))) {
                    return t;
                }
            }
        } catch (Exception e) {
            // ignore
            log.warn("failed to translate label {} into object of type {}", label, clazz);
        }
        return null;
    }
    
//
//    public static List<EnumOption> getStaticOptions(Class<? extends Enum> enumClass, boolean containBlankOption) {
//		List<Enum> enumList = (List<Enum>) EnumHelper.inspectConstants(enumClass, containBlankOption);
//        List<EnumOption> options = new ArrayList<EnumOption>();
//        for (Enum e : enumList) {
//            options.add(new EnumOption(((PageEnum) e).getCode(), ((PageEnum) e).getText(), e.name()));
//        }
//        return options;
//    }
    
    


}
