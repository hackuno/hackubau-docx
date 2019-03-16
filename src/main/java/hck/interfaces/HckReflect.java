package hck.interfaces;

import com.google.common.collect.Lists;
import hck.annotations.HckReflecting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class HckReflect {

    private static Logger logger = LoggerFactory.getLogger(HckReflect.class);
    public static String concat = ".";
    private String identifier = null;

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getRightIdentifier() {
        return StringUtils.isNotBlank(identifier) ? identifier : this.getClass().getSimpleName();
    }

    @HckReflecting(reflect = false)
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
                        HckReflect o = (HckReflect) m.invoke(this);
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


    @HckReflecting(reflect = false)
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

    @HckReflecting(reflect = false)
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

    @HckReflecting(reflect = false)
    public static LinkedList<String> getAvaiableFieldsNested(Class c, String prefix) {


        HckReflecting refl = (HckReflecting) c.getAnnotation(HckReflecting.class);

        LinkedList<String> lista = Lists.newLinkedList();
        try {
            List<Method> metodi = Arrays.asList(c.getMethods());
            for (Method m : metodi) {
                String name = m.getName();
                HckReflecting r = m.getAnnotation(HckReflecting.class);
                if (r == null || r.reflect()) {
                    if (name.startsWith("get") && m.getParameterCount() == 0 && m.getReturnType() != void.class
                            && !m.getReturnType().isArray()) {
                        lista.add(prefix + name.substring(3));
                        Class subC = m.getReturnType();
                        if (refl != null) {
                            for (String s : refl.toReflect()) {
                                if (StringUtils.equals(s, name)) {
                                    if (HckReflect.class.isAssignableFrom(subC)) {
                                        lista.addAll(HckReflect.getAvaiableFieldsNested(subC, name.substring(3) + concat));
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
