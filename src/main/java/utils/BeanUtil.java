package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author bug1024
 * @date 2017-04-03
 */
public class BeanUtil {

    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static void transMap2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    String type = property.getPropertyType().getName();
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, getValue(type, value));
                }
            }
        } catch (Exception e) {
            logger.warn("transMap2Bean Error" + e.getMessage());
        }
    }

    public static Object getValue(String type, Object obj) {
        if ("java.lang.String".equals(type)) {
            obj = String.valueOf("" + obj);
        } else if ("java.lang.Integer".equals(type)) {
            obj = Integer.valueOf("" + obj);
        } else if ("java.lang.Long".equals(type)) {
            obj = Long.valueOf("" + obj);
        } else if ("java.lang.Float".equals(type)) {
            obj = Float.valueOf("" + obj);
        } else if ("java.sql.Timestamp".equals(type)) {
            obj = Timestamp.valueOf("" + obj);
        } else {
            obj = obj.toString();
        }

        return obj;
    }


}
