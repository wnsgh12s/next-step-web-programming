package core.di.factory;

import core.annotation.Controller;
import core.annotation.Repository;
import core.annotation.Service;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;

public class ClasspathBeanDefinitionScanner {
    private final BeanDefinitionRegistry beanDefinitionRegister;

    public ClasspathBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegister) {
        this.beanDefinitionRegister = beanDefinitionRegister;
    }

    public Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotationClass) {
        Set<Class<?>> beans = new HashSet<>();
        for (Class<? extends Annotation> annotation : annotationClass) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    public void doScan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> beanClasses = getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);
        for (Class<?> clazz : beanClasses) {
            beanDefinitionRegister.registerBeanDefinition(clazz, new BeanDefinition(clazz));
        }
    }
}
