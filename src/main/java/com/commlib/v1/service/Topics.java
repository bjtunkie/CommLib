package com.commlib.v1.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Topics {

    private static final Map<Integer, String> mapIntWithTopic = new HashMap<>();
    private static final Map<String, Integer> mapTopicWithInt = new HashMap<>();

    static {
        validate(AdminTopics.class);
        validate(ChatTopics.class);

        BiConsumer<Object, Object> log = (key, value) -> {
            if (!(key instanceof Number)) {
                Object temp = key;
                key = value;
                value = temp;
            }
            System.out.println(String.format("Mapped key: %s with topic: %s", key, value));
        };
        mapTopicWithInt.forEach(log);
        mapIntWithTopic.forEach(log);
    }

    private Topics() {
    }

    public static String getTopicFromIntValue(int value) {
        return mapIntWithTopic.get(value);
    }

    public static int getIntValueFromTopic(String topic) {
        return mapTopicWithInt.get(topic);
    }

    private static int validate(Class<? extends Topic> klass) {

        final String prefix = klass.getAnnotation(WithPrefix.class).value();
        final Field[] fields = klass.getDeclaredFields();
        final Function<Field, Integer> extractInt = (field -> {
            try {
                return field.getInt(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.exit(1);
                return 0;
            }
        });
        for (Field field : fields) {
            String value = "";
            if (field.isAnnotationPresent(WithTopic.class) && (value = field.getAnnotation(WithTopic.class).value()).length() > 0) {
                int modifier = field.getModifiers();
                if (Modifier.isPublic(modifier) && Modifier.isStatic(modifier) && field.getType().equals(Integer.TYPE)) {
                    String error = null;
                    final Integer uniqueKey = extractInt.apply(field);
                    final String uniqueTopic = prefix + "/" + value;

                    if (mapIntWithTopic.containsKey(uniqueKey)) {
                        String existingTopic = mapIntWithTopic.get(uniqueKey);
                        error = String.format("Key %d for the topic: %s is already assigned for the topic: %s", uniqueKey, uniqueTopic, existingTopic);
                    } else if (mapTopicWithInt.containsKey(uniqueTopic)) {
                        Integer existingKey = mapTopicWithInt.get(uniqueTopic);
                        error = String.format("Topic %s for the Key: %d is already assigned for the Key: %d", uniqueTopic, uniqueKey, existingKey);
                    }
                    if (error != null) {
                        throw new RuntimeException(error);
                    }

                    mapIntWithTopic.put(uniqueKey, uniqueTopic);
                    mapTopicWithInt.put(uniqueTopic, uniqueKey);
                }
            } else {
                String error = String.format("Variable %s is not assigned the Topic/Annotation %s", field.getName(), Topic.class.getName());
                throw new RuntimeException(error);
            }
        }

        return 0;
    }


    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface WithTopic {
        String value();
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface WithPrefix {
        String value();
    }

    interface Topic {
    }
}
