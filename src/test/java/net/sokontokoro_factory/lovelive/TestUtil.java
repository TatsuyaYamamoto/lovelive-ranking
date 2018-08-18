package net.sokontokoro_factory.lovelive;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

  /**
   * クラス内に定義されたインスタンスのフィールド変数に方に合わせて一時的な値を代入する。 setter, getterカバレッジ用
   *
   * @param targetObject
   * @return
   * @throws IllegalAccessException
   */
  public static List<Method> getAllGetter(Object targetObject)
      throws IllegalAccessException, IntrospectionException {
    List<Method> getterMethods = new ArrayList();

    Field[] allFields = targetObject.getClass().getDeclaredFields();
    for (Field field : allFields) {

      // jacocoによるカバレッジリポート用のfieldが挿入されているので、無視する
      if (!field.getName().equals("$jacocoData")) {
        // getterを取得
        PropertyDescriptor pd = new PropertyDescriptor(field.getName(), targetObject.getClass());
        getterMethods.add(pd.getReadMethod());
      }
    }
    return getterMethods;
  }

  public static List<Field> getAllField(Object targetObject)
      throws IllegalAccessException, IntrospectionException {
    List<Field> fields = new ArrayList();

    Field[] allFields = targetObject.getClass().getDeclaredFields();
    for (Field field : allFields) {

      // jacocoによるカバレッジリポート用のfieldが挿入されているので、無視する
      if (!field.getName().equals("$jacocoData") && !field.getName().equals("serialVersionUID")) {
        fields.add(field);
      }
    }
    return fields;
  }
}
