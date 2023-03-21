package core.di.factory;

import static org.reflections.ReflectionUtils.getAllConstructors;
import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import com.google.common.collect.Sets;
import core.annotation.Inject;
import core.annotation.Order;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class BeanFactoryUtils {
    /**
     * 인자로 전달하는 클래스의 생성자 중 @Inject 애노테이션이 설정되어 있는 생성자를 반환
     *
     * @param clazz
     * @return
     * @Inject 애노테이션이 설정되어 있는 생성자는 클래스당 하나로 가정한다.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Constructor<?> getInjectedConstructor(Class<?> clazz) {
        //클래스의 @Inject 클래스를 찾아서 반환 생성자를 반환
        Set<Constructor> injectedConstructors = getAllConstructors(clazz, withAnnotation(Inject.class));
        if (injectedConstructors.isEmpty()) {
            return null;
        }
        return injectedConstructors.iterator().next();
    }

    /**
     * 인자로 전달되는 클래스의 구현 클래스. 만약 인자로 전달되는 Class가 인터페이스가 아니면 전달되는 인자가 구현 클래스, 인터페이스인 경우 BeanFactory가 관리하는 모든 클래스 중에 인터페이스를
     * 구현하는 클래스를 찾아 반환
     *
     * @param injectedClazz
     * @param preInstanticateBeans
     * @return
     */
    public static Class<?> findConcreteClass(Class<?> injectedClazz, Set<Class<?>> preInstanticateBeans) {
        if (!injectedClazz.isInterface()) {
            return injectedClazz;
        }

        Set<Class<?>> classes = findConcreteClassesByInterface(injectedClazz, preInstanticateBeans);

        if (classes.isEmpty()) {
            throw new IllegalStateException(injectedClazz + "인터페이스를 구현하는 Bean이 존재하지 않는다.");
        }

        return getConcreateclass(classes);
    }

    private static Class<?> getConcreateclass(Set<Class<?>> classes) {
        int count = 0;
        Class<?> concreateclass = classes.iterator().next();
        for (Class<?> haveInterfaceClass : classes) {
            Order order = haveInterfaceClass.getAnnotation(Order.class);
            if (order != null) {
                if (order.value() > count) {
                    count = order.value();
                    concreateclass = haveInterfaceClass;
                }
            }
        }
        return concreateclass;
    }

    private static Set<Class<?>> findConcreteClassesByInterface(Class<?> injectedClazz,
                                                                Set<Class<?>> preInstanticateBeans) {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> clazz : preInstanticateBeans) {
            Set<Class<?>> interfaces = Sets.newHashSet(clazz.getInterfaces());
            if (interfaces.contains(injectedClazz)) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    public static Set<Field> getInjectedFields(Class<?> clazz) {
        Set<Field> injectedFields = getAllFields(clazz, withAnnotation(Inject.class));
        //        while (injectedFields.iterator().hasNext()) {
//            fields.add(injectedFields.iterator().next());
//        }
        return new HashSet<>(injectedFields);
    }

    static Set<Method> getInjectedMethod(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(Inject.class) != null) {
                methods.add(method);
            }
        }
        return methods;
    }
}
