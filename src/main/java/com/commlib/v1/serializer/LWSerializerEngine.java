package com.commlib.v1.serializer;

import com.commlib.v1.network.utils.Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;

public class LWSerializerEngine {

    private static final Map<String, Field[]> blueprint = new HashMap<>();

    private Field[] analyzeClass(Class<? extends LWSerializable> object) {
        TreeMap<String, Field> map = new TreeMap<>();
        Class<?> clazz = object;
        while (!Object.class.equals(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String nameOfField = field.getName();
                map.put(nameOfField, field);
            }
            clazz = clazz.getSuperclass();
        }

        return map.values().toArray(new Field[map.size()]);
//        return map.entrySet().toArray(new Map.Entry[map.size()]);
    }

    private void analyze(Class<? extends LWSerializable> object) {
        blueprint.put(object.getCanonicalName(), analyzeClass(object));
    }

    public <T extends LWSerializable> byte[] serialize(T input) {
        String nameOfClass = input.getClass().getCanonicalName();
        Field[] fields = blueprint.get(nameOfClass);
        if (fields == null) {
            fields = analyzeClass(input.getClass());
            blueprint.put(nameOfClass, fields);
        }

        ByteBuffer buf = ByteBuffer.wrap(new byte[1024 * 1024]);
        List<byte[]> listOfBytes = new LinkedList<>();
        for (Field field : fields) {

            if (Integer.class.equals(field.getType()) || int.class.equals(field.getType())) {
                try {
                    int value = (int) field.get(input);
                    listOfBytes.add(Util.intToByteArray(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        buf.position(4);
        buf.putInt(listOfBytes.size());
        int mark = 8;
        for (byte[] bytes : listOfBytes) {
            buf.position(mark);
            buf.putInt(bytes.length);
            buf.position(mark + 4);
            buf.put(bytes);
            mark += 4 + bytes.length;
        }

        return buf.array();
    }

    public <T extends LWSerializable> T deSerialize(byte[] array, Class<T> clazz) {
        return this.deSerialize(array, clazz, 4);
    }

    private <T extends LWSerializable> T deSerialize(byte[] array, Class<T> clazz, int offset) {
        Field[] fields = blueprint.get(clazz.getCanonicalName());

        int countOfObjects = Util.byteArrayToInt(array, offset);

        if (countOfObjects != fields.length) {
            throw new RuntimeException("Size don't match...");
        }

        ByteBuffer src = ByteBuffer.wrap(array, 4, array.length - 4);

        LWSerializable instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < countOfObjects; i++) {
            Field field = fields[i];
            Class<?> type = field.getType();


            Object value = null;
            if (String.class.equals(type)) {
                int size = src.getInt();
                int mark = src.position();
                value = new String(array, mark, size);
            } else if (Integer.class.equals(type) || int.class.equals(type)) {
                value = src.getInt();
            } else if (Float.class.equals(type) || float.class.equals(type)) {
                value = src.getFloat();
            }

            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }
        return (T) instance;
    }

    public void out() {


    }


}
