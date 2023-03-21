package core.di.factory.inject;

import core.annotation.Controller;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanDefinitionScanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApplicationContext {
    private BeanFactory beanFactory;

    public ApplicationContext(Object... basePackages) {
        beanFactory = new BeanFactory();
        ClasspathBeanDefinitionScanner scanner = new ClasspathBeanDefinitionScanner(beanFactory);
        scanner.doScan(basePackages);
        beanFactory.initialize();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<?> clazz) {
        return (T) beanFactory.getBean(clazz);
    }

    public Set<Class<?>> getBeanClasses() {
        return beanFactory.getBeanClasses();
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = new HashMap<>();
        for (Class<?> clazz : getBeanClasses()) {
            Controller annotation = clazz.getAnnotation(Controller.class);
            if (annotation != null) {
                controllers.put(clazz, getBean(clazz));
            }
        }
        return controllers;
    }
}
