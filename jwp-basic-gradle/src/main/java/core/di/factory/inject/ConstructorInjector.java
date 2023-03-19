package core.di.factory.inject;

import com.google.common.collect.Sets;
import core.di.factory.BeanFactory;
import java.util.Set;

public class ConstructorInjector extends AbstractInjector {


    public ConstructorInjector(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    void inject(Object injectedBean, Object instantiateClass, BeanFactory beanFactory) {
    }

    @Override
    Class<?> getBeanClass(Object injectedBean) {
        return null;
    }

    @Override
    Set<?> getInjectedBeans(Class<?> clazz) {
        return Sets.newHashSet();
    }


}
