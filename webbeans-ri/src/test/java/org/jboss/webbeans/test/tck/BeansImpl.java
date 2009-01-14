package org.jboss.webbeans.test.tck;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.webbeans.manager.Bean;

import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.bean.AbstractClassBean;
import org.jboss.webbeans.bean.EnterpriseBean;
import org.jboss.webbeans.bean.ProducerMethodBean;
import org.jboss.webbeans.bean.SimpleBean;
import org.jboss.webbeans.tck.api.Beans;

public class BeansImpl implements Beans
{

   public <T> Bean<T> createSimpleBean(Class<T> clazz)
   {
      return SimpleBean.of(clazz, CurrentManager.rootManager());
   }

   public <T> Bean<T> createProducerMethodBean(Method method, Bean<?> declaringBean)
   {
      if (declaringBean instanceof AbstractClassBean)
      {
         return ProducerMethodBean.of(method, (AbstractClassBean<?>) declaringBean, CurrentManager.rootManager());
      }
      else
      {
         throw new IllegalStateException("Cannot create a producer method from a bean that wasn't created by the RI " + declaringBean);
      }
   }

   public <T> Bean<T> createEnterpriseBean(Class<T> clazz)
   {
      return EnterpriseBean.of(clazz, CurrentManager.rootManager());
   }

   public Set<Annotation> getBindings(Bean<?> bean)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Set<Class<?>> getRequiredTypes(Bean<?> bean)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Set<Class<? extends Annotation>> getSupportedScopes(Bean<?> bean)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isBeanNameDefaulted(Bean<?> bean)
   {
      // TODO Auto-generated method stub
      return false;
   }

}