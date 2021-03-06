/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.cuba.core.sys;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextAwareProcessor;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.servlet.ServletContext;

public class CubaClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {
    ServletContext servletContext;

    public CubaClassPathXmlApplicationContext(String[] locations) {
        super(locations);
    }

    public CubaClassPathXmlApplicationContext(String[] locations, ServletContext sc) {
        super();
        servletContext = sc;
        setConfigLocations(locations);
        afterPropertiesSet();
    }

    @Override
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
        setValidating(false);
        super.initBeanDefinitionReader(reader);
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        return new CubaDefaultListableBeanFactory(getInternalParentBeanFactory());
    }

    @Override
    protected ConfigurableEnvironment createEnvironment() {
        StandardServletEnvironment standardServletEnvironment = new StandardServletEnvironment();
        standardServletEnvironment.initPropertySources(servletContext, null);
        return standardServletEnvironment;
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        if (this.servletContext != null) {
            beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext));
            beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        }
    }
}