package net.sokontokoro_factory.lovelive;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrivateField {
    public static void set(Object targetObject, String fieldName, Object value) throws Exception{
        Class clazz = targetObject.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);

    }
    public static void set(Class targetClass, String fieldName, Object value) throws Exception{
        Field field = targetClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetClass, value);

    }

    public static Object get(Object targetObject, String fieldName) throws Exception{
        Class clazz = targetObject.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(targetObject);
    }
    public static Object get(Class targetClass, String fieldName) throws Exception{
        Field field = targetClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(targetClass);
    }
}