/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.resolution;

import static org.jboss.weld.logging.messages.ResolutionMessage.CANNOT_EXTRACT_RAW_TYPE;
import static org.jboss.weld.logging.messages.ResolutionMessage.CANNOT_EXTRACT_TYPE_INFORMATION;
import static org.jboss.weld.logging.messages.ResolutionMessage.INVALID_MEMBER_TYPE;
import static org.jboss.weld.util.reflection.Reflections.EMPTY_ANNOTATIONS;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.bean.AbstractClassBean;
import org.jboss.weld.exceptions.ForbiddenArgumentException;
import org.jboss.weld.exceptions.InvalidOperationException;
import org.jboss.weld.injection.WeldInjectionPoint;
import org.jboss.weld.introspector.WeldAnnotated;
import org.jboss.weld.introspector.jlr.AbstractWeldAnnotated;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.metadata.TypeStore;
import org.jboss.weld.util.Names;
import org.jboss.weld.util.reflection.Reflections;

public class ResolvableWeldClass<T> extends AbstractWeldAnnotated<T, Class<T>> implements Resolvable
{
   private final Class<T> rawType;
   private final Set<Type> typeClosure;
   private final Type[] actualTypeArguments;

   public static <T> WeldAnnotated<T, Class<T>> of(Type type, Annotation[] annotations, BeanManagerImpl manager)
   {
      return new ResolvableWeldClass<T>(type, annotations, manager);
   }

   public static <T> WeldAnnotated<T, Class<T>> of(InjectionPoint injectionPoint, BeanManagerImpl manager)
   {
      if (injectionPoint instanceof WeldInjectionPoint)
      {
         @SuppressWarnings("unchecked")
         WeldAnnotated<T, Class<T>> ip = (WeldAnnotated<T, Class<T>>) injectionPoint;
         return ip;
      }
      else
      {
         return new ResolvableWeldClass<T>(injectionPoint.getType(), injectionPoint.getAnnotated().getAnnotations(), manager);
      }
   }

   public static <T> WeldAnnotated<T, Class<T>> of(Member member, Annotation[] annotations, BeanManagerImpl manager)
   {
      if (member instanceof Field)
      {
         return new ResolvableWeldClass<T>(((Field) member).getGenericType(), annotations, manager);
      }
      else if (member instanceof Method)
      {
         return new ResolvableWeldClass<T>(((Method) member).getGenericReturnType(), annotations, manager);
      }
      else
      {
         throw new ForbiddenArgumentException(INVALID_MEMBER_TYPE, member);
      }
   }

   private ResolvableWeldClass(Type type, Map<Class<? extends Annotation>, Annotation> annotationMap, Map<Class<? extends Annotation>, Annotation> declaredAnnotationMap, BeanManagerImpl manager)
   {
      super(annotationMap, declaredAnnotationMap, manager.getServices().get(TypeStore.class));

      if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;
         if (parameterizedType.getRawType() instanceof Class<?>)
         {
            this.rawType = (Class<T>) parameterizedType.getRawType();
         }
         else
         {
            throw new ForbiddenArgumentException(CANNOT_EXTRACT_RAW_TYPE, type);
         }
         this.actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
      }
      else if (type instanceof Class<?>)
      {
         this.rawType = (Class<T>) type;
         this.actualTypeArguments = new Type[0];
      }
      else
      {
         throw new ForbiddenArgumentException(CANNOT_EXTRACT_TYPE_INFORMATION, type);
      }
      this.typeClosure = new HashSet<Type>();
      typeClosure.add(type);
   }

   private ResolvableWeldClass(Type type, Annotation[] annotations, BeanManagerImpl manager)
   {
      this(type, buildAnnotationMap(annotations), buildAnnotationMap(EMPTY_ANNOTATIONS), manager);
   }

   private ResolvableWeldClass(Type type, Set<Annotation>annotations, BeanManagerImpl manager)
   {
      this(type, buildAnnotationMap(annotations), buildAnnotationMap(EMPTY_ANNOTATIONS), manager);
   }

   @Override
   public String toString()
   {
      return Names.toString(getJavaClass(), getAnnotations(), getActualTypeArguments());
   }

   @Override
   public Class<T> getDelegate()
   {
      return rawType;
   }

   @Override
   public Type[] getActualTypeArguments()
   {
      return actualTypeArguments;
   }

   public String getName()
   {
      throw new InvalidOperationException();
   }

   @Override
   public Class<T> getJavaClass()
   {
      return rawType;
   }

   public boolean isFinal()
   {
      throw new InvalidOperationException();
   }

   public boolean isPublic()
   {
      throw new InvalidOperationException();
   }
   
   public boolean isPrivate()
   {
      throw new InvalidOperationException();
   }
   
   public boolean isPackagePrivate()
   {
      throw new InvalidOperationException();
   }
   
   public Package getPackage()
   {
      throw new InvalidOperationException();
   }

   public boolean isStatic()
   {
      throw new InvalidOperationException();
   }

   @Override
   public boolean isProxyable()
   {
      throw new InvalidOperationException();
   }

   @Override
   public Set<Type> getTypeClosure()
   {
      return typeClosure;
   }
   
   public AbstractClassBean<?> getDeclaringBean()
   {
      return null;
   }

   @Override
   public Type getBaseType()
   {
      return getJavaClass();
   }

   public boolean isAssignableTo(Class<?> clazz)
   {
      return Reflections.isAssignableFrom(clazz, getBaseType());
   }

}