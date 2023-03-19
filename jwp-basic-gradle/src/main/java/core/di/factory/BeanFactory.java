package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Controller;
import core.di.factory.inject.ConstructorInjector;
import core.di.factory.inject.FieldInjector;
import core.di.factory.inject.Injector;
import core.di.factory.inject.SetterInjector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory implements BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

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
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> clazz : preInstanticateBeans) {
            if (beans.get(clazz) == null) {
                inject(clazz);
            }
        }
    }

    public Set<Class<?>> getPreInstanticateBeans() {
        return preInstanticateBeans;
    }

    private void inject(Class<?> clazz) {
        for (Injector injector : injectors) {
            injector.inject(clazz);
        }
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = new HashMap<>();
        for (Class<?> clazz : preInstanticateBeans) {
            Controller annotation = clazz.getAnnotation(Controller.class);
            if (annotation != null) {
                controllers.put(clazz, getBean(clazz));
            }
        }
        return controllers;
    }

    public void registerBean(Class<?> clazz, Object instance) {
        beans.put(clazz, instance);
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        beanDefinitions.put(clazz, beanDefinition);
    }
}
