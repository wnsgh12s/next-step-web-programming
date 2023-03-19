package core.di.factory.inject;

import core.annotation.Inject;
import core.di.factory.BeanFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class SetterInjector extends AbstractInjector {

    public SetterInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    void inject(Object injectedBean, Object bean, BeanFactory beanFactory) {
        Method method = (Method) injectedBean;
        try {
            method.invoke(beanFactory.getBean(method.getDeclaringClass()), bean);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    Class<?> getBeanClass(Object injectedBean) {
        Method method = (Method) injectedBean;
        Class<?>[] parameters = method.getParameterTypes();
        if(parameters.length > 1){
            throw new IllegalArgumentException("의존성 주입을 하는 매서드의 길이는 1을 초과할 수 없습니다.");
        }
        return parameters[0];
    }

    @Override
    Set<?> getInjectedBeans(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(Inject.class) != null) {
                methods.add(method);
            }
        }
        return methods;
    }
}
