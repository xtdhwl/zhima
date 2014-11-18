/* 
* @Title: JsonHelper.java
* Created by liubingsr on 2012-5-17 下午2:02:37 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.text.TextUtils;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: JsonHelper
 * @Description: Json解析、序列化类
 * @author liubingsr
 * @date 2012-5-17 下午2:02:37
 *
 */
public class JsonHelper {
	private final static String TAG = "JsonHelper";
	private final static String GETTER_METHOD = "get";
	private final static String SETTER_METHOD = "set";
	//时间格式
	private final static SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ", Locale.CHINA);
	/**
	* @Title: toJson
	* @Description: 将对象序列化成json数据包
	* @param obj
	* @return String
	*/
	public static String toJson(Object obj) {
		JSONStringer js = new JSONStringer();
		serialize(js, obj);
		Logger.getInstance(TAG).debug("JsonHelper toJSON :" + js.toString());
		return js.toString();
	}
	
	/**
	* @Title: parseObject
	* @Description: 将json字符串反序列化成一个对象
	* @param jsonString
	* @param clazz
	* @return T
	*/
	public static <T> T parseObject(String jsonString, Class<T> clazz) {
		if (clazz == null || jsonString == null || jsonString.length() == 0) {
			return null;
		}

		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonString);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug("parseObject :" + e.getMessage());
		}

		if (isNull(jo)) {
			return null;
		}

		return parseObject(jo, clazz);
	}
	
	/**
	* @Title: parseObject
	* @Description: 将JSONObject对象反序列化成一个对象
	* @param jo
	* @param clazz
	* @return T
	*/
	public static <T> T parseObject(JSONObject jo, Class<T> clazz) {
		if (clazz == null || isNull(jo)) {
			return null;
		}

		T obj = newInstance(clazz);
		if (obj == null) {
			return null;
		}
		if (isMap(clazz)) {
			setField(obj, jo);
		} else {
			// 获取对象的所有方法
			Method[] methods = clazz.getDeclaredMethods();
			// 获取所有属性
			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields) {
				String setMetodName = makeMethodName(f.getName(), SETTER_METHOD);
				if (!hasMethod(methods, setMetodName)) {
					continue;
				}
				try {
					Method fieldMethod = clazz.getMethod(setMetodName, f.getType());
					setField(obj, fieldMethod, f, jo);
				} catch (Exception e) {
					Logger.getInstance(TAG).debug("parseObject :" + e.getMessage());
				}
			}
		}
		return obj;
	}
	
	/**
	* @Title: serialize
	* @Description: 对象序列化成json
	* @param js
	* @param obj
	* @return void
	*/
	private static void serialize(JSONStringer js, Object obj) {
		if (isNull(obj)) {
			try {
				js.value(null);
			} catch (JSONException e) {
				Logger.getInstance(TAG).debug("serialize :" + e.getMessage());
			}
			return;
		}

		Class<?> clazz = obj.getClass();
		if (isObject(clazz)) { // 对象
			serializeObject(js, obj);
		} else if (isArray(clazz)) { // 数组
			serializeArray(js, obj);
		} else if (isCollection(clazz)) { // 集合
			Collection<?> collection = (Collection<?>) obj;
			serializeCollect(js, collection);
		} else { // 单值
			try {
				js.value(obj);
			} catch (JSONException e) {
				Logger.getInstance(TAG).debug("serialize :" + e.getMessage());
			}
		}		
	}

	/**
	* @Title: serializeCollect
	* @Description: 序列化集合对象
	* @param js
	* @param collection
	* @return void
	*/
	private static void serializeCollect(JSONStringer js, Collection<?> collection) {
		try {
			js.array();
			for (Object o : collection) {
				serialize(js, o);
			}
			js.endArray();
		} catch (Exception e) {
			Logger.getInstance(TAG).debug("serializeCollect :" + e.getMessage());
		}		
	}	

	/**
	* @Title: serializeArray
	* @Description: 序列化数组
	* @param js
	* @param obj
	* @return void
	*/
	private static void serializeArray(JSONStringer js, Object array) {
		try {
			js.array();
			for (int i = 0; i < Array.getLength(array); ++i) {
				Object obj = Array.get(array, i);
				serialize(js, obj);
			}
			js.endArray();
		} catch (Exception e) {
			Logger.getInstance(TAG).debug("serializeArray :" + e.getMessage());
		}		
	}	

	/**
	* @Title: serializeObject
	* @Description: 序列化普通对象(除集合、数组、list外的对象)
	* @param js
	* @param obj
	* @return void
	*/
	private static void serializeObject(JSONStringer js, Object obj) {
		try {
			js.object();
			Class<? extends Object> objClazz = obj.getClass();
			Method[] methods = objClazz.getDeclaredMethods();
			Field[] fields = objClazz.getDeclaredFields();
			for (Field field : fields) {
				try {
					String fieldType = field.getType().getSimpleName();
					String fieldGetName = makeMethodName(field.getName(), GETTER_METHOD);
					if (!hasMethod(methods, fieldGetName)) {
						continue;
					}
					Method fieldGetMet = objClazz.getMethod(fieldGetName, new Class[] {});
					Object fieldVal = fieldGetMet.invoke(obj, new Object[] {});
					js.key(field.getName());
					Class<?> clazz = fieldVal.getClass();
					if (isObject(clazz)) { // 对象
						serializeObject(js, fieldVal);
					} else if (isArray(clazz)) { // 数组
						serializeArray(js, fieldVal);
					} else if (isCollection(clazz)) { // 集合
						Collection<?> collection = (Collection<?>) fieldVal;
						serializeCollect(js, collection);
					} else {
						String result = null;
						if ("Date".equals(fieldType)) {							
							result = DATETIME_FORMAT.format((Date) fieldVal);

						} else {
							if (null != fieldVal) {
								result = String.valueOf(fieldVal);
							}
						}
						serialize(js, result);
					}
				} catch (Exception e) {
					Logger.getInstance(TAG).debug("serializeObject :" + e.getMessage());
					continue;
				}
			}
			js.endObject();
		} catch (Exception e) {
			Logger.getInstance(TAG).debug("serializeObject :" + e.getMessage());
		}		
	}

	/**
	* @Title: hasMethod
	* @Description: 是否存在某属性的 get方法
	* @param methods
	* @param fieldMethod
	* @return boolean
	*/
	private static boolean hasMethod(Method[] methods, String fieldMethod) {
		for (Method met : methods) {
			if (fieldMethod.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	* @Title: makeMethodName
	* @Description: 拼接属性的 getter、setter方法名
	* 注意：对于属性名称及其对应的getter、setter方法名称作如下约定：
	* getter、setter方法名称为 ：get + 属性名(将属性名的第一个字母变成大写)、set + 属性名(将属性名的第一个字母变成大写)，例如某个类有如下属性：
	* String name，则对应的getter、setter方法名称必须是：getName、setName
	* @param fieldName
	* @param methodType
	* @return String
	*/
	private static String makeMethodName(String fieldName, String methodType) {
		if (TextUtils.isEmpty(fieldName)) {
			return null;
		}
		return methodType + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	/**
	* @Title: isObject
	* @Description: 是否是一个对象
	* @param clazz
	* @return boolean
	*/
	private static boolean isObject(Class<?> clazz) {
		return clazz != null && !isSingle(clazz) && !isArray(clazz)
				&& !isCollection(clazz);
	}

	/**
	* @Title: isSingle
	* @Description: 是否是值类型
	* @param clazz
	* @return boolean
	*/
	private static boolean isSingle(Class<?> clazz) {
		return isBoolean(clazz) || isNumber(clazz) || isString(clazz);
	}

	/**
	* @Title: isString
	* @Description: 是否为字符串
	* @param clazz
	* @return boolean
	*/
	private static boolean isString(Class<?> clazz) {
		return (clazz != null)
				&& ((String.class.isAssignableFrom(clazz))
						|| (Character.TYPE.isAssignableFrom(clazz)) 
						|| (Character.class.isAssignableFrom(clazz)));
	}

	/**
	* @Title: isNumber
	* @Description: 数值类型
	* @param clazz
	* @return boolean
	*/
	private static boolean isNumber(Class<?> clazz) {
		return (clazz != null)
				&& ((Byte.TYPE.isAssignableFrom(clazz))
						|| (Short.TYPE.isAssignableFrom(clazz))
						|| (Integer.TYPE.isAssignableFrom(clazz))
						|| (Long.TYPE.isAssignableFrom(clazz))
						|| (Float.TYPE.isAssignableFrom(clazz))
						|| (Double.TYPE.isAssignableFrom(clazz)) 
						|| (Number.class.isAssignableFrom(clazz)));
	}

	/**
	* @Title: isBoolean
	* @Description: 布尔值类型值
	* @param clazz
	* @return boolean
	*/
	private static boolean isBoolean(Class<?> clazz) {
		return (clazz != null)
				&& ((Boolean.TYPE.isAssignableFrom(clazz)) 
						|| (Boolean.class.isAssignableFrom(clazz)));
	}
	
	/**
	* @Title: isCollection
	* @Description: 对象是否是集合
	* @param clazz
	* @return boolean
	*/
	private static boolean isCollection(Class<?> clazz) {
		return clazz != null && Collection.class.isAssignableFrom(clazz);
	}
	
	/**
	* @Title: isArray
	* @Description: 对象是否是数组
	* @param clazz
	* @return boolean
	*/
	private static boolean isArray(Class<?> clazz) {
		return clazz != null && clazz.isArray();
	}
	
	/**
	* @Title: isNull
	* @Description: 是否为空值
	* @param obj
	* @return boolean
	*/
	private static boolean isNull(Object obj) {
		if (obj instanceof JSONObject) {
			return JSONObject.NULL.equals(obj);
		}
		return obj == null;
	}
	
	/**
	* @Title: isMap
	* @Description: 是否是Map对象
	* @param clazz
	* @return boolean
	*/
	private static boolean isMap(Class<?> clazz) {
		return clazz != null && Map.class.isAssignableFrom(clazz);
	}
	
	/**
	* @Title: isList
	* @Description: 是否是列表
	* @param clazz
	* @return boolean
	*/
	private static boolean isList(Class<?> clazz) {
		return clazz != null && List.class.isAssignableFrom(clazz);
	}
	
	/**
	* @Title: newInstance
	* @Description: 创建类的新实例
	* @param clazz
	* @return T
	*/
	private static <T> T newInstance(Class<T> clazz) {
		if (clazz == null) {
			return null;
		}
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			Logger.getInstance(TAG).debug("newInstance :" + e.getMessage());
		}
		return obj;
	}
	
	/**
	* @Title: setField
	* @Description: 设置属性
	* @param obj
	* @param jo
	* @return void
	*/
	private static void setField(Object obj, JSONObject jo) {
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jo.keys();
			String key;
			Object value;
			@SuppressWarnings("unchecked")
			Map<String, Object> valueMap = (Map<String, Object>) obj;
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jo.get(key);
				valueMap.put(key, value);

			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug("setField2 :" + e.getMessage());
		}
	}
	
	/**
	* @Title: setField
	* @Description: 设置属性
	* @param obj
	* @param setter
	* @param field
	* @param jo
	* @return void
	*/
	private static void setField(Object obj, Method setter, Field field, JSONObject jo) {
		String name = field.getName();
		Class<?> clazz = field.getType();
		
		try {
			if (isArray(clazz)) { // 数组
				Class<?> c = clazz.getComponentType();
				JSONArray ja = jo.optJSONArray(name);
				if (!isNull(ja)) {
					Object array = parseArray(ja, c);
					setFiedlValue(obj, setter, clazz.getSimpleName(), array);
				}
			} else if (isCollection(clazz)) { // 泛型集合
				// 获取定义的泛型类型
				Class<?> c = null;
				Type gType = field.getGenericType();
				if (gType instanceof ParameterizedType) {
					ParameterizedType ptype = (ParameterizedType) gType;
					Type[] targs = ptype.getActualTypeArguments();
					if (targs != null && targs.length > 0) {
						Type t = targs[0];
						c = (Class<?>) t;
					}
				}

				JSONArray ja = jo.optJSONArray(name);
				if (!isNull(ja)) {
					Object o = parseCollection(ja, clazz, c);
					setFiedlValue(obj, setter, clazz.getSimpleName(), o);
				}
			} else if (isSingle(clazz)) { // 值类型
				Object o = jo.opt(name);
				if (o != null) {
					setFiedlValue(obj, setter, clazz.getSimpleName(), o);
				}
			} else if (isList(clazz)) { // List列表
				Class<?> c = null;
				Type gType = field.getGenericType();
				if (gType instanceof ParameterizedType) {
					ParameterizedType ptype = (ParameterizedType) gType;
					Type[] targs = ptype.getActualTypeArguments();
					if (targs != null && targs.length > 0) {
						Type t = targs[0];
						c = (Class<?>) t;
					}
				}
				JSONArray ja = jo.optJSONArray(name);
				if (!isNull(ja)) {
					Object o = parseList(ja, clazz, c);
					setFiedlValue(obj, setter, clazz.getSimpleName(), o);
				}
			} else if (isObject(clazz)) { // 对象
				JSONObject j = jo.optJSONObject(name);
				if (!isNull(j)) {
					Object o = parseObject(j, clazz);
					setFiedlValue(obj, setter, clazz.getSimpleName(), o);
				}
			} else {
				throw new Exception("unknow type!");
			}
		} catch (Exception e) {
			Logger.getInstance(TAG).debug("setField4 :" + e.getMessage());
		}
	}
	
	/**
	* @Title: getFieldValueMap
	* @Description: 对象转Map
	* @param obj
	* @return
	* Map<String,String>
	*/
	public static Map<String, String> getFieldValueMap(Object obj) {
		Class<?> cls = obj.getClass();
		Map<String, String> valueMap = new HashMap<String, String>();
		Method[] methods = cls.getDeclaredMethods();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			try {
				String fieldType = field.getType().getSimpleName();
				String fieldGetName = makeMethodName(field.getName(), GETTER_METHOD);
				if (!hasMethod(methods, fieldGetName)) {
					continue;
				}
				Method fieldGetMet = cls.getMethod(fieldGetName, new Class[] {});
				Object fieldVal = fieldGetMet.invoke(obj, new Object[] {});
				String result = null;
				if ("Date".equals(fieldType)) {					
					result = DATETIME_FORMAT.format((Date) fieldVal);

				} else {
					if (null != fieldVal) {
						result = String.valueOf(fieldVal);
					}
				}
				valueMap.put(field.getName(), result);
			} catch (Exception e) {
				Logger.getInstance(TAG).debug("getFieldValueMap :" + e.getMessage());
				continue;
			}
		}
		return valueMap;
	}
	
	/**
	* @Title: setFieldValue
	* @Description: 设置属性值
	* @param obj
	* @param valMap
	* void
	*/
	public static void setFieldValue(Object obj, Map<String, String> valMap) {
		Class<?> cls = obj.getClass();
		//
		Method[] methods = cls.getDeclaredMethods();
		Field[] fields = cls.getDeclaredFields();

		for (Field field : fields) {
			try {
				String setMetodName = makeMethodName(field.getName(), SETTER_METHOD);
				if (!hasMethod(methods, setMetodName)) {
					continue;
				}
				Method fieldMethod = cls.getMethod(setMetodName, field.getType());
				String value = valMap.get(field.getName());
				if (null != value && !"".equals(value)) {
					String fieldType = field.getType().getSimpleName();
					if ("String".equals(fieldType)) {
						fieldMethod.invoke(obj, value);
					} else if ("Date".equals(fieldType)) {						
						Date tempValue = DATETIME_FORMAT.parse(value);
						fieldMethod.invoke(obj, tempValue);
					} else if ("Integer".equals(fieldType)
							|| "int".equals(fieldType)) {
						Integer intval = Integer.parseInt(value);
						fieldMethod.invoke(obj, intval);
					} else if ("Long".equalsIgnoreCase(fieldType)) {
						Long tempValue = Long.parseLong(value);
						fieldMethod.invoke(obj, tempValue);
					} else if ("Double".equalsIgnoreCase(fieldType)) {
						Double tempValue = Double.parseDouble(value);
						fieldMethod.invoke(obj, tempValue);
					} else if ("Boolean".equalsIgnoreCase(fieldType)) {
						Boolean tempValue = Boolean.parseBoolean(value);
						fieldMethod.invoke(obj, tempValue);
					} else {
						Logger.getInstance(TAG).debug("setFiedlValue not supper type:" + fieldType);
					}
				}
			} catch (Exception e) {
				Logger.getInstance(TAG).debug("setFiedlValue2 :" + e.getMessage());
				continue;
			}
		}

	}
	
	/**
	* @Title: setFiedlValue
	* @Description: 设置属性的值
	* @param obj
	* @param setter
	* @param fieldType
	* @param value
	* @return void
	*/
	private static void setFiedlValue(Object obj, Method setter,
			String fieldType, Object value) {

		try {
			if (null != value && !"".equals(value)) {
				if ("String".equals(fieldType)) {
					setter.invoke(obj, value.toString());
				} else if ("Date".equals(fieldType)) {					
					Date tempValue = DATETIME_FORMAT.parse(value.toString());
					setter.invoke(obj, tempValue);
				} else if ("Integer".equals(fieldType)
						|| "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value.toString());
					setter.invoke(obj, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long tempValue = Long.parseLong(value.toString());
					setter.invoke(obj, tempValue);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double tempValue = Double.parseDouble(value.toString());
					setter.invoke(obj, tempValue);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean tempValue = Boolean.parseBoolean(value.toString());
					setter.invoke(obj, tempValue);
				} else {
					setter.invoke(obj, value);
					Logger.getInstance(TAG).debug("setFiedlValue not supper type:" + fieldType);
				}
			}

		} catch (Exception e) {
			Logger.getInstance(TAG).debug("setFiedlValue :" + e.getMessage());
		}
	}
	
	/**
	* @Title: setFiedlValue
	* @Description: 设置属性值
	* @param obj
	* @param fieldSetMethod
	* @param fieldType
	* @param value
	* @param clazz
	* @param jo
	* @return void
	*/
	public static void setFiedlValue(Object obj, Method fieldSetMethod,
			String fieldType, Object value, Class<?> clazz, JSONObject jo) {

		try {
			if (null != value && !"".equals(value)) {
				if ("String".equals(fieldType)) {
					fieldSetMethod.invoke(obj, value.toString());
				} else if ("Date".equals(fieldType)) {					
					Date tempValue = DATETIME_FORMAT.parse(value.toString());
					fieldSetMethod.invoke(obj, tempValue);
				} else if ("Integer".equals(fieldType)
						|| "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value.toString());
					fieldSetMethod.invoke(obj, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long tempValue = Long.parseLong(value.toString());
					fieldSetMethod.invoke(obj, tempValue);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double tempValue = Double.parseDouble(value.toString());
					fieldSetMethod.invoke(obj, tempValue);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean tempValue = Boolean.parseBoolean(value.toString());
					fieldSetMethod.invoke(obj, tempValue);
				} else {
					JSONObject j = jo.optJSONObject(fieldType);
					if (!isNull(j)) {
						Object o = parseObject(j, clazz);
						setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o, clazz, j);
					} else {
						fieldSetMethod.invoke(obj, value);
					}			
					Logger.getInstance(TAG).debug("setFiedlValue6 not supper type:" + fieldType);
				}
			}

		} catch (Exception e) {
			Logger.getInstance(TAG).debug("setFiedlValue6 :" + e.getMessage());
		}
	}	

	/**
	* @Title: parseArray
	* @Description:  反序列化数组对象
	* @param ja
	* @param clazz
	* @return T[]
	*/
	public static <T> T[] parseArray(JSONArray ja, Class<T> clazz) {
		if (clazz == null || isNull(ja)) {
			return null;
		}

		int len = ja.length();

		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(clazz, len);

		for (int i = 0; i < len; ++i) {
			try {
				JSONObject jo = ja.getJSONObject(i);
				T o = parseObject(jo, clazz);
				array[i] = o;
			} catch (JSONException e) {
				Logger.getInstance(TAG).debug("parseArray :" + e.getMessage());
			}
		}

		return array;
	}

	/**
	* @Title: parseArray
	* @Description: 反序列化数组对象
	* @param jsonString
	* @param clazz
	* @return T[]
	*/
	public static <T> T[] parseArray(String jsonString, Class<T> clazz) {
		if (clazz == null || jsonString == null || jsonString.length() == 0) {
			return null;
		}
		JSONArray jo = null;
		try {
			jo = new JSONArray(jsonString);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug("parseArray :" + e.getMessage());
		}

		if (isNull(jo)) {
			return null;
		}

		return parseArray(jo, clazz);
	}
	
	/**
	* @Title: parseCollection
	* @Description: 反序列化泛型集合
	* @param ja
	* @param collectionClazz
	* @param genericType
	* @return Collection<T>
	*/
	@SuppressWarnings("unchecked")
	private static <T> Collection<T> parseCollection(JSONArray ja,
			Class<?> collectionClazz, Class<T> genericType) {

		if (collectionClazz == null || genericType == null || isNull(ja)) {
			return null;
		}

		Collection<T> collection = (Collection<T>) newInstance(collectionClazz);

		for (int i = 0; i < ja.length(); ++i) {
			try {
				JSONObject jo = ja.getJSONObject(i);
				T o = parseObject(jo, genericType);
				collection.add(o);
			} catch (JSONException e) {
				Logger.getInstance(TAG).debug("parseCollection :" + e.getMessage());
			}
		}

		return collection;
	}
	
	/**
	* @Title: parseCollection
	* @Description: 反序列化泛型集合
	* @param jsonString
	* @param collectionClazz
	* @param genericType
	* @return Collection<T>
	*/
	public static <T> Collection<T> parseCollection(String jsonString,
			Class<?> collectionClazz, Class<T> genericType) {
		if (collectionClazz == null || genericType == null
				|| jsonString == null || jsonString.length() == 0) {
			return null;
		}
		JSONArray jo = null;
		try {
			jo = new JSONArray(jsonString);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug("parseCollection :" + e.getMessage());
		}

		if (isNull(jo)) {
			return null;
		}

		return parseCollection(jo, collectionClazz, genericType);
	}
	
	/**
	* @Title: parseList
	* @Description: 反序列化List
	* @param ja
	* @param listClazz
	* @param genericType
	* @return Object
	*/
	@SuppressWarnings("unchecked")
	private static <T> List<T> parseList(JSONArray ja,
			Class<?> listClazz, Class<T> genericType) {

		if (listClazz == null || genericType == null || isNull(ja)) {
			return null;
		}

		List<T> list = (List<T>) newInstance(listClazz);
		for (int i = 0; i < ja.length(); ++i) {
			try {
				JSONObject jo = ja.getJSONObject(i);
				T o = parseObject(jo, genericType);
				list.add(o);
			} catch (JSONException e) {
				Logger.getInstance(TAG).debug("parseList :" + e.getMessage());
			}
		}
		return list;
	}
}
