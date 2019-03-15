package hck.services;

import com.google.common.collect.Lists;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import hck.interfaces.HckReflectUtils;
import hck.utils.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

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

public class DocxService {

    Logger logger = LogManager.getLogger(DocxService.class);

    private final static String FORMAT = ".docx";
    private final static String MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

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

    public File generateDocument(File template, File out, List<? extends HckReflectUtils> objs, List<List<? extends HckReflectUtils>> lists, HashMap<String, String> fixedMappings) {
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


                        HashMap<String, String> replace = getMappings(placeHolders, objs, lists, fixedMappings);
                        replace.entrySet().stream().map(x -> x.getKey() + " > " + x.getValue()).forEach(logger::info);

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

    public HashMap<String, String> getMappings(List<String> placeHolders, List<? extends HckReflectUtils> objs, List<List<? extends HckReflectUtils>> lists, HashMap<String, String> fixedMappings) {
        HashMap<String, String> map = new HashMap<>();
        for (String p : placeHolders) {
            try {
                List<String> split = Arrays.asList(StringUtils.split(p, "."));
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

                List<String> extraFields = Arrays.asList(StringUtils.split(field, "#"));


                if (StringUtils.startsWith(head, "list_")) {
                    List<String> list_headObj = Arrays.asList(StringUtils.split(head, "_"));
                    if (list_headObj.size() == 2) {
                        List<String> headObj_separator = Arrays.asList(StringUtils.split(list_headObj.get(1), "@"));
                        String separator = "\r\n\f";
                        if (headObj_separator.size() == 2) {
                            separator = headObj_separator.get(1);

                            separator=StringUtils.replaceChars(separator,"\\n","\n");
                            separator=StringUtils.replaceChars(separator,"\\r","\r");
                            separator=StringUtils.replaceChars(separator,"\\f","\f");
                        }
                        head=headObj_separator.get(0);

                        for (List<? extends HckReflectUtils> rList : lists) {
                            if (rList != null && !rList.isEmpty() && StringUtils.equalsIgnoreCase(rList.get(0).getClass().getSimpleName(), head)) {
                                String out = "";
                                for (HckReflectUtils r : rList) {
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
                    for (HckReflectUtils r : objs) {
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
        map.put("today", Utility.dateToString(new Date(), "dd/MM/yyyy"));
        if (fixedMappings != null) {
            map.putAll(fixedMappings);
        }


        return map;
    }

    private String getValueByObj(HckReflectUtils r, List<String> extraFields) {
        String out = "";
        for (String extraF : extraFields) {
            List<String> extr = Arrays.asList(StringUtils.split(extraF, "@"));
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

    private static String checkNewLines(Object r) {
        if (r instanceof String) {
            return checkNewLines(r);
        } else {
            return "";
        }
    }

    private static String checkNewLines(String r) {
//        https://github.com/plutext/docx4j/blob/master/src/samples/docx4j/org/docx4j/samples/VariableReplace.java
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