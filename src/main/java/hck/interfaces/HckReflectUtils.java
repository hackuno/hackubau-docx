package hck.interfaces;

import com.google.common.collect.Lists;
import hck.annotations.HckReflect;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public abstract class HckReflectUtils {

    private static Logger logger = LoggerFactory.getLogger(HckReflectUtils.class);
    private static final String concat = ".";


    @HckReflect(reflect = false)
    public Object getNested(String field) {
        String[] splitted = StringUtils.split(field, concat);
        LinkedList<String> nestedFields = Lists.newLinkedList();

        for (String spl : splitted) {
            nestedFields.add(spl);
        }

        try {
            if (nestedFields.size() > 1) {
                String meth = nestedFields.pollFirst();
                for (Method m : this.getClass().getMethods()) {
                    if (StringUtils.equalsIgnoreCase(m.getName(), "get" + meth)) {
                        HckReflectUtils o = (HckReflectUtils) m.invoke(this);
                        return o.getNested(nestedFields.stream().reduce("", (x, y) -> StringUtils.isBlank(x) ? y : x + concat + y));
                    }
                }
            } else {
                return this.get(field);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return null;
    }


    @HckReflect(reflect = false)
    public Object get(String field) {
        Object rez = null;
        try {
            for (Method m : this.getClass().getMethods()) {
                if (StringUtils.equalsIgnoreCase(m.getName(), "get" + field)) {
                    return m.invoke(this);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rez;
    }

    @HckReflect(reflect = false)
    public List<String> getAvaiableFields() {
        List<String> campi = Lists.newArrayList();
        try {
            for (Field f : this.getClass().getFields()) {
                campi.add(f.getName());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return campi;
    }

    @HckReflect(reflect = false)
    public static LinkedList<String> getAvaiableFieldsNested(Class c, String prefix) {


        HckReflect refl = (HckReflect) c.getAnnotation(HckReflect.class);

        LinkedList<String> lista = Lists.newLinkedList();
        try {
            List<Method> metodi = Arrays.asList(c.getMethods());
            for (Method m : metodi) {
                String name = m.getName();
                HckReflect r = m.getAnnotation(HckReflect.class);
                if (r == null || r.reflect()) {
                    if (name.startsWith("get") && m.getParameterCount() == 0 && m.getReturnType() != void.class
                            && !m.getReturnType().isArray()) {
                        lista.add(prefix + name.substring(3));
                        Class subC = m.getReturnType();
                        if (refl != null) {
                            for (String s : refl.toReflect()) {
                                if (StringUtils.equals(s, name)) {
                                    if (HckReflectUtils.class.isAssignableFrom(subC)) {
                                        lista.addAll(HckReflectUtils.getAvaiableFieldsNested(subC, name.substring(3) + concat));
                                    }
                                }
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return lista;
    }


}
