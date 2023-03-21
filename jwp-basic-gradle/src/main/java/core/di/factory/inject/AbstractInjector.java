package core.di.factory.inject;

import com.google.common.collect.Lists;
import core.annotation.Inject;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

public abstract class AbstractInjector implements Injector {
    private BeanFactory beanFactory;

    public AbstractInjector(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public void inject(Class<?> clazz) {
        instantiateClass(clazz);
        Set<?> injectedBeans = getInjectedBeans(clazz);
        for (Object injectedBean : injectedBeans) {
            Class<?> injectedBeanClass = getBeanClass(injectedBean);
            inject(injectedBean, instantiateClass(injectedBeanClass), beanFactory);
        }
    }

    abstract void inject(Object injectedBean, Object instantiateClass, BeanFactory beanFactory);

    abstract Class<?> getBeanClass(Object injectedBean);

    abstract Set<?> getInjectedBeans(Class<?> clazz);

    private Object instantiateClass(Class<?> clazz) {
        Class<?> concreateClazz = BeanFactoryUtils.findConcreteClass(clazz, beanFactory.getBeanClasses());
        Object bean = beanFactory.getBean(concreateClazz);
        if (bean != null) {
            return bean;
        }
        Constructor<?> injectConstructor = getConstructorsContainingInjects(concreateClazz);
        if (injectConstructor == null) {
            bean = instantiate(concreateClazz);
            beanFactory.registerBean(concreateClazz, bean);
            return bean;
        }
        bean = instantiateConstructor(injectConstructor);
        beanFactory.registerBean(clazz, bean);
        return bean;
    }


    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] pTypes = constructor.getParameterTypes();
        Set<Class<?>> preInstanticateBeans = beanFactory.getBeanClasses();
        List<Object> args = Lists.newArrayList();
        for (Class<?> clazz : pTypes) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
            if (!preInstanticateBeans.contains(concreteClass)) {
                throw new IllegalStateException(clazz + "는 빈이 아니다.");
            }

            Object bean = beanFactory.getBean(concreteClass);
            if (bean == null) {
                bean = instantiateClass(concreteClass);
            }
            args.add(bean);
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    private Object instantiate(Class<?> clazz) {
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "인터페이스는 안돼요 안됑.");
        }
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Constructor<?> getConstructorsContainingInjects(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            boolean isContainInject = Arrays.stream(constructor.getAnnotations())
                    .anyMatch(annotation -> annotation.annotationType().equals(Inject.class));
            if (isContainInject) {
                return constructor;
            }
        }
        return null;
    }

}
