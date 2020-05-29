package com.github.jeuxjeux20.loupsgarous.config.serialization;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SimpleYamlMapper {
    private static final Map<Class<?>, Map<String, Field>> mappingCache = new HashMap<>();

    public static Map<String, Object> serialize(Object object) {
        if (object == null) return Collections.emptyMap();

        Map<String, Object> data = new HashMap<>();
        Map<String, Field> propertyNameToField =
                mappingCache.computeIfAbsent(object.getClass(), SimpleYamlMapper::createSerializedFieldsMap);

        propertyNameToField.forEach((name, field) -> {
            field.setAccessible(true);

            try {
                Object value = field.get(object);
                if (value != null)
                    data.put(name, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return data;
    }

    public static void deserializeFields(Map<String, Object> data, Object target) {
        Map<String, Field> propertyNameToField =
                mappingCache.computeIfAbsent(target.getClass(), SimpleYamlMapper::createSerializedFieldsMap);

        data.forEach((name, value) -> {
            Field field = propertyNameToField.get(name);
            if (field == null || !field.getType().isInstance(value)) return;
            field.setAccessible(true);

            try {
                field.set(target, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private static Map<String, Field> createSerializedFieldsMap(Class<?> clazz) {
        Map<String, Field> propertyNameToField = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            YamlProperty annotation = field.getAnnotation(YamlProperty.class);
            if (annotation != null) {
                String value = annotation.value();
                if (propertyNameToField.containsKey(value)) {
                    throw new IllegalArgumentException("The YamlProperty of name " + value + " is present twice.");
                }

                propertyNameToField.put(value, field);
            }
        }
        return propertyNameToField;
    }
}
