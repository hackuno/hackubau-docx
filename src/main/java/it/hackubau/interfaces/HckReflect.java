package it.hackubau.interfaces;

import com.google.common.collect.Lists;
import it.hackubau.annotations.HckReflecting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Marco Guassone <hck@hackubau.it>
 * @apiNote
 * a) This class allow you to call "getMethods()" just typing get("methods")
 * b) If you have nested HckReflect classes you can also inside the nested childs: getNested("method.child1Method.child2Method");
 * c) You can use the identifier property to specify the string that you will use as object placeholder in the .docx template.
 * @implNote
 * just extend your class with this.
 * if you need you are free to call the setIdentifier("newPlaceholder") so in the .docx you will write: ${placeholder.methodToinvoke[...]}
 */
public abstract class HckReflect {

    private static Logger logger = LoggerFactory.getLogger(HckReflect.class);

    /**
     * the concatenator choosen in the invocation of the methods.
     * by default it is the dot. For example "field1.nestedField.nestedNestedField"
     */
    public static String concat = ".";

    /**
     * Used in DocxService.
     *
     * @param identifier - the identifier used in .docx files to find the right object. If null or blank it will be the class-name.
     */
    private String identifier = null;


    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }


    /**
     * @return the identifier to be used in .docx files to retrieve the right object. (identifier if present, class-name if identifier is null)
     */
    public String getRightIdentifier() {
        return StringUtils.isNotBlank(identifier) ? identifier : this.getClass().getSimpleName();
    }

    /**
     * @param field the name of the GET method you want to invoke >>> without the GET word!
     *              (example: to invoke this.getCioppy() you will have to call -> this.get("cioppy");
     * @return the return of the invoked method.
     */
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


    /**
     *
     * @param field the name of the GET-Method you want to invoke. Here you can specify nested fields.
     *              > for example: if your object represent a human and you want to invoke "human.getSurname()"
     *              you'll have to call -> human.getNested("surname");
     *              if you want to invoke: human.getAddress().getCity() you have to call -> human.getNested("address.city");
     * @return the return of the (eventually nesteds) GET-methods founded in the corrispondenting classes that match the given String.
     */
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


    /**
     * Just a utility to print all the avaiable methods of a class
     * @return the avaiable methods of the class.
     */
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

    /**
     * Just a utility to print all the avaiable methods of a class
     * @param c - the starting class
     * @param prefix - a prefix used as method-filter (such as "get" word)
     * @return all the methods found in the class that start with given prefix and are allowed by the HckReflecting Annotation (by default is yes). If one of them return another HckReflect class it will do the same for this class.
     */
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
                    if (name.startsWith(prefix) && m.getParameterCount() == 0 && m.getReturnType() != void.class
                            && !m.getReturnType().isArray()) {
                        lista.add(name);
                        Class subC = m.getReturnType();
                        if (refl != null) {
                            for (String s : refl.toReflect()) {
                                if (StringUtils.equals(s, name)) {
                                    if (HckReflect.class.isAssignableFrom(subC)) {
                                        lista.addAll(HckReflect.getAvaiableFieldsNested(subC, prefix));
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
