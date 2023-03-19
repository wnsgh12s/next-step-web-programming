package core.di.factory.inject;

import core.annotation.Inject;
import core.di.factory.BeanFactory;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldInjector extends AbstractInjector {
    private static final Logger logger = LoggerFactory.getLogger(FieldInjector.class);

    public FieldInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    void inject(Object injectedBean, Object bean, BeanFactory beanFactory) {
        Field field = (Field) injectedBean;
        try {
            field.setAccessible(true);
            field.set(beanFactory.getBean(field.getDeclaringClass()), bean);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    Class<?> getBeanClass(Object injectedBean) {
        return ((Field) injectedBean).getType();
    }

    @Override
    Set<?> getInjectedBeans(Class<?> clazz) {
        Set<Field> injectedBeans = new HashSet<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Inject.class) != null) {
                injectedBeans.add(field);
            }
        }
        return injectedBeans;
    }
}
