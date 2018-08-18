package net.sokontokoro_factory.lovelive;

import java.io.*;
import java.lang.reflect.Field;

public class PrivateField {
  public static void set(Object targetObject, String fieldName, Object value) throws Exception {
    Class clazz = targetObject.getClass();
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(targetObject, value);
  }

  public static void set(Class targetClass, String fieldName, Object value) throws Exception {
    Field field = targetClass.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(targetClass, value);
  }

  public static Object get(Object targetObject, String fieldName) throws Exception {
    Class clazz = targetObject.getClass();
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(targetObject);
  }

  public static Object get(Class targetClass, String fieldName) throws Exception {
    Field field = targetClass.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(targetClass);
  }
}
