package hck.services;

import com.google.common.collect.Lists;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import hck.interfaces.HckReflect;
import hck.utils.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 15/03/2019 - ATTENTION! The steps in this page marked with comments that start with 1. or 2. or 3. or 4. are copied by the following project:
 * https://github.com/ErisoHV/docx4jExample.git
 */

@Service
public class DocxService {

    Logger logger = LogManager.getLogger(DocxService.class);

    private final static String FORMAT = ".docx";
    private final static String MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    //char used in .docx template before the personalized separator chars - default @
    private String separatorIdentifier;

    //char used in .docx template to separate multiple entries of the same object - default #
    private String mutlifieldsIdentifier;

    private String dateFormat;

    public DocxService() {
        this.setSeparatorIdentifier("@");
        this.setMutlifieldsIdentifier("#");
        this.setDateFormat("dd/MM/yyyy");
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getSeparatorIdentifier() {
        return this.separatorIdentifier;
    }

    public void setSeparatorIdentifier(final String separatorIdentifier) {
        this.separatorIdentifier = separatorIdentifier;
    }

    public String getMutlifieldsIdentifier() {
        return this.mutlifieldsIdentifier;
    }

    public void setMutlifieldsIdentifier(final String mutlifieldsIdentifier) {
        this.mutlifieldsIdentifier = mutlifieldsIdentifier;
    }

    //aggiunto compatibilita' docx salvati con open office
    private static final String MIME_TYPE_OPEN_OFFICE = "application/zip";

    public static String getMimeType(File file) {
        ContentInfo info;
        try {
            info = (new ContentInfoUtil()).findMatch(file);
            if (info != null)
                return info.getMimeType();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * This method was copied by https://github.com/ErisoHV/docx4jExample.git on 15/03/2019 and i used it as base for my project
     * @param template - the .docx template
     * @param replace - the hash map of key-value to replace
     * @param outputDocument - the output target file
     * @return - the out file
     */
    public File generateDocument(File template, HashMap<String, String> replace,
                                 String outputDocument) {

        File rez = null;
        if (template != null && replace != null && replace.size() > 0
                && outputDocument != null && !outputDocument.isEmpty()) {
            if (template.exists()) {
                if (!outputDocument.endsWith(FORMAT)) {
                    logger.warn("The output document must be .docx");
                    return rez;
                }
                //1. Check file extension with simplemagic
                String mimeType = getMimeType(template);
                if (mimeType != null && (mimeType.equals(MIME_TYPE) || mimeType.equals(MIME_TYPE_OPEN_OFFICE))) {
                    WordprocessingMLPackage wordMLPackage;
                    try {
                        //2. Load template
                        wordMLPackage = WordprocessingMLPackage.load(template);
                        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

                        //3. Replace placeholders
                        VariablePrepare.prepare(wordMLPackage);
                        documentPart.variableReplace(replace);

                        //4. output docx
                        OutputStream output = new FileOutputStream(outputDocument);
                        Save saver = new Save(wordMLPackage);
                        if (saver.save(output)) {
                            logger.info("Document " + outputDocument + " ok");
                            return new File(outputDocument);
                        }
                    } catch (Docx4JException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    logger.error("Invalid document mime type");
                }
            }
        }
        return rez;
    }

    /**
     *
     * @param template - the .docx file (Save by openoffice or microsoft word)
     * @param out - the output file target
     * @param objs - the non-mandatory objects that you will try to call by your .docx template
     *             (es. a file .docx containing "${Anagrafica.nome}" will require an obj=(HckReflect)Anagrafica or (HckReflect)obj.identifier="Anagrafica")
     * @param listsObj - the non-mandatory List of objects that you will try to call by your .docx template
     *              (es. aa file .docx containing "${list_anagrafica.nome}" will require a listsObj containing a List of (HckReflect)Anagrafica or List of (HckReflect)obj.identifier="Anagrafica")
     * @param fixedMappings - the non-mandatory fixed String params that you will try to call by your .docx template
     *                      (es. a file with ${cioppy} wille require an HashMap<"cioppy","bau"> - n.b. the special key today is used for today date.
     * @return - the out file
     */
    public File generateDocument(File template, File out, List<? extends HckReflect> objs, List<List<? extends HckReflect>> listsObj, HashMap<String, String> fixedMappings) {
        try {

            String outputDocument = out.getPath();


            if (template.exists()) {
                if (!outputDocument.endsWith(FORMAT)) {
                    logger.warn("The output document must be .docx");
                    return out;
                }
                //1. Check file extension with simplemagic
                String mimeType = getMimeType(template);
                if (mimeType != null && (mimeType.equals(MIME_TYPE) || mimeType.equals(MIME_TYPE_OPEN_OFFICE))) {
                    WordprocessingMLPackage wordMLPackage;
                    try {
                        //2. Load template
                        wordMLPackage = WordprocessingMLPackage.load(template);
                        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

                        //check for placeHolders
                        List<String> placeHolders = Lists.newArrayList();
                        for (Object o : documentPart.getContent()) {
                            Pattern pattern = Pattern.compile("(\\$\\{[^\\$^\\{^\\}]*\\})");
                            Matcher m = pattern.matcher("" + o);
                            while (m.find()) {
                                placeHolders.add(StringUtils.remove(StringUtils.remove(StringUtils.remove(m.group(), "$"), "}"), "{"));
                            }
                        }


                        HashMap<String, String> replace = getMappings(placeHolders, objs, listsObj, fixedMappings);
                        replace.entrySet().stream().map(x -> x.getKey() + " > " + x.getValue()).forEach(logger::debug);

                        //3. Replace placeholders
                        VariablePrepare.prepare(wordMLPackage);
                        documentPart.variableReplace(replace);

                        //4. output docx
                        OutputStream output = new FileOutputStream(outputDocument);
                        Save saver = new Save(wordMLPackage);
                        if (saver.save(output)) {
                            logger.info("Document " + outputDocument + " ok");
                            out = new File(outputDocument);
                        }
                    } catch (Docx4JException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    logger.error("Invalid document mime type");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return out;
    }

    public HashMap<String, String> getMappings(List<String> placeHolders, List<? extends HckReflect> objs, List<List<? extends HckReflect>> lists, HashMap<String, String> fixedMappings) {
        HashMap<String, String> map = new HashMap<>();

        for (String p : placeHolders) {
            try {
                List<String> split = Arrays.asList(StringUtils.split(p, HckReflect.concat));
                String head = "";
                String field = "";
                for (int i = 0; i < split.size(); i++) {
                    if (i == 0) {
                        head = split.get(i);
                    } else {
                        if (StringUtils.isNotBlank(field)) {
                            field += ".";
                        }
                        field += split.get(i);
                    }
                }

                List<String> extraFields = Arrays.asList(StringUtils.split(field, mutlifieldsIdentifier));


                if (StringUtils.startsWith(head, "list_")) {
                    List<String> list_headObj = Arrays.asList(StringUtils.split(head, "_"));
                    if (list_headObj.size() == 2) {
                        List<String> headObj_separator = Arrays.asList(StringUtils.split(list_headObj.get(1), separatorIdentifier));
                        String separator = "\r\n\f";
                        if (headObj_separator.size() == 2) {
                            separator = headObj_separator.get(1);

                            separator = StringUtils.replaceChars(separator, "\\n", "\n");
                            separator = StringUtils.replaceChars(separator, "\\r", "\r");
                            separator = StringUtils.replaceChars(separator, "\\f", "\f");
                        }
                        head = headObj_separator.get(0);

                        for (List<? extends HckReflect> rList : lists) {
                            if (rList != null && !rList.isEmpty() && StringUtils.equalsIgnoreCase(rList.get(0).getRightIdentifier(), head)) {
                                String out = "";
                                for (HckReflect r : rList) {
                                    if (StringUtils.isNotBlank(out)) {
                                        out += separator;
                                    }
                                    out += getValueByObj(r, extraFields);
                                }
                                map.put(p, Utility.cleanStr(checkNewLines(out)));

                            }
                            break;
                        }
                    }
                } else {
                    for (HckReflect r : objs) {
                        if (StringUtils.equalsIgnoreCase(r.getClass().getSimpleName(), head)) {
                            map.put(p, getValueByObj(r, extraFields));
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        map.put("today", Utility.dateToString(new Date(), dateFormat));
        if (fixedMappings != null) {
            map.putAll(fixedMappings);
        }


        return map;
    }


    /**
     *
     * @param r (HckReflectUtis)obj used to retrieve the getField
     * @param fieldList all the fields you want to retrieve
     *                  (with optional separator char, es.: field@\r\n will use \r\n between him and the next field)
     * @return the fieldList concatenated with separators
     */
    private String getValueByObj(HckReflect r, List<String> fieldList) {
        String out = "";
        for (String extraF : fieldList) {
            List<String> extr = Arrays.asList(StringUtils.split(extraF, getSeparatorIdentifier()));
            String separator = ", ";
            String ff = extraF;
            if (extr.size() == 2) {
                ff = extr.get(0);
                separator = extr.get(1);
            }
            if (StringUtils.isNotBlank(out)) {
                out += separator;
            }
            out += r.getNested(ff);

        }
        return out;

    }




    /**
     * @param r - the entire String containing chars as \r or \n oe \f
     * @return - the string with the correct conversion of \r \n \f for .docx files
     */
    private static String checkNewLines(Object r) {
        if (r instanceof String) {
            return checkNewLines(r);
        } else {
            return "";
        }
    }


    /**
     * 16/03/2019 - Attention: i used this project as a source https://github.com/plutext/docx4j/blob/master/src/samples/docx4j/org/docx4j/samples/VariableReplace.java
     * @param r - the entire String containing chars as \r or \n oe \f
     * @return - the string with the correct conversion of \r \n \f for .docx files
     */
    private static String checkNewLines(String r) {
        StringTokenizer st = new StringTokenizer(r, "\n\r\f"); // tokenize on the newline character, the carriage-return character, and the form-feed character
        StringBuilder sb = new StringBuilder();

        boolean firsttoken = true;
        while (st.hasMoreTokens()) {
            String line = (String) st.nextToken();
            if (firsttoken) {
                firsttoken = false;
            } else {
                sb.append("</w:t><w:br/><w:t>");
            }
            sb.append(line);
        }
        return sb.toString();
    }

}