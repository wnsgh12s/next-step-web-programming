package core.di.factory;

import core.di.factory.enums.InjectType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanDefinition {
    private static final Logger log = LoggerFactory.getLogger(BeanDefinition.class);

    private final Class<?> beanClazz;
    private final Constructor<?> injectConstructor;
    private final Set<Field> injectFields;

    public BeanDefinition(Class<?> clazz) {
        beanClazz = clazz;
        this.injectConstructor = getInjectConstructor(clazz);
        this.injectFields = getInjectFields(injectConstructor, beanClazz);
    }

    private Set<Field> getInjectFields(Constructor<?> injectConstructor, Class<?> beanClazz) {
        if (injectConstructor != null) {
            return new HashSet<>();
        }

        Set<Field> injectFields = new HashSet<>();
        Set<Class<?>> injectProperties = getinjectProperTiesType(beanClazz);
        Field[] fields = beanClazz.getDeclaredFields();

        for (Field field : fields) {
            if (injectProperties.contains(field.getType())) {
                injectFields.add(field);
            }
        }

        return injectFields;
    }

    private Set<Class<?>> getinjectProperTiesType(Class<?> clazz) {
        Set<Class<?>> injectProperties = new HashSet<>();
        Set<Method> injectedMethod = BeanFactoryUtils.getInjectedMethod(clazz);
        for (Method method : injectedMethod) {
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length > 1) {
                throw new IllegalStateException("DI 메소드의 인자는 하나여야 합니다");
            }
            injectProperties.add(parameters[0]);
        }

        Set<Field> injectField = BeanFactoryUtils.getInjectedFields(clazz);
        for (Field field : injectField) {
            injectProperties.add(field.getType());
        }
        return injectProperties;
    }

    private Constructor<?> getInjectConstructor(Class<?> clazz) {
        return BeanFactoryUtils.getInjectedConstructor(clazz);
    }

    public Constructor<?> getInjectConstructor() {
        return this.injectConstructor;
    }

    public InjectType getResolvedInjectMode() {
        if (injectConstructor != null) {
            return InjectType.INJECT_CONSTRUCTOR;
        }
        if (!injectFields.isEmpty()) {
            return InjectType.INJECT_FIELD;
        }
        return InjectType.INJECT_NO;
    }

    public Class<?> getBeanClass() {
        return this.beanClazz;
    }

    public Set<Field> getInjectFields() {
        return this.injectFields;
    }
}
