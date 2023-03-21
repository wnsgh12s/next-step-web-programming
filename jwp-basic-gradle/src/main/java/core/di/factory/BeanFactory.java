package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.di.factory.enums.InjectType;
import core.di.factory.inject.ConstructorInjector;
import core.di.factory.inject.FieldInjector;
import core.di.factory.inject.Injector;
import core.di.factory.inject.SetterInjector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class BeanFactory implements BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    private List<Injector> injectors;

    public BeanFactory() {
        this.injectors = List.of(
                new ConstructorInjector(this),
                new FieldInjector(this),
                new SetterInjector(this)
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        Object bean = beans.get(clazz);
        if (bean != null) {
            return (T) bean;
        }
        Class<?> concreateClass = findConcreateClass(clazz);
        BeanDefinition beanDefinition = beanDefinitions.get(concreateClass);
        bean = inject(beanDefinition);
        beans.put(concreateClass, bean);
        return (T) bean;
    }

    private <T> Class<?> findConcreateClass(Class<T> clazz) {
        Set<Class<?>> beanClasses = getBeanClasses();
        Class<?> concreateClass = BeanFactoryUtils.findConcreteClass(clazz, beanClasses);
        if (!beanClasses.contains(concreateClass)) {
            throw new IllegalStateException(clazz + "는 빈이 아닙니다");
        }
        return concreateClass;
    }

    public void initialize() {
        for (Class<?> clazz : getBeanClasses()) {
            getBean(clazz);
        }
    }


    private Object inject(BeanDefinition beanDefinition) {
        if (beanDefinition.getResolvedInjectMode() == InjectType.INJECT_NO) {
            return BeanUtils.instantiate(beanDefinition.getBeanClass());
        }
        if (beanDefinition.getResolvedInjectMode() == InjectType.INJECT_FIELD) {
            return injectFields(beanDefinition);
        }
        return injectConstructor(beanDefinition);
    }

    private Object injectConstructor(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getInjectConstructor();
        List<Object> args = Lists.newArrayList();
        for (Class<?> clazz : constructor.getParameterTypes()) {
            args.add(getBean(clazz));
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    private Object injectFields(BeanDefinition beanDefinition) {
        Object bean = BeanUtils.instantiate(beanDefinition.getBeanClass());
        Set<Field> injectFields = beanDefinition.getInjectFields();
        for (Field field : injectFields) {
            injectField(bean, field);
        }
        return bean;
    }

    private void injectField(Object bean, Field field) {
        try {
            field.setAccessible(true);
            field.set(bean, getBean(field.getType()));
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    public void registerBean(Class<?> clazz, Object instance) {
        beans.put(clazz, instance);
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        beanDefinitions.put(clazz, beanDefinition);
    }

    public Set<Class<?>> getBeanClasses() {
        return beanDefinitions.keySet();
    }
}
