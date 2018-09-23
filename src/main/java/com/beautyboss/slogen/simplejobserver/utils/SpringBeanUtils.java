package com.beautyboss.slogen.simplejobserver.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
@Component
public class SpringBeanUtils implements BeanFactoryAware {

    private static BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringBeanUtils.beanFactory = beanFactory;

    }

    public static Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    public static Object getBean(String beanName, Class aclass) {
        return beanFactory.getBean(beanName, aclass);
    }
}
