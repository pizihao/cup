package reflection.property;

import java.lang.reflect.Field;

/**
 * 属性复制器
 */
public final class PropertyCopier {

    private PropertyCopier() {
    }


    //复制属性,类似功能的还有别的类，
    public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean) {
        Class<?> parent = type;
        while (parent != null) {
            //循环将父类的属性都要复制过来
            final Field[] fields = parent.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(destinationBean, field.get(sourceBean));
                } catch (Exception e) {
                }
            }
            parent = parent.getSuperclass();
        }
    }

}
