package it.hackubau.services;

import com.google.common.collect.Lists;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import it.hackubau.interfaces.HckReflect;
import it.hackubau.utils.Utility;
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
 * @author
 * 15/03/2019 - Marco Guassone <hck@hackubau.it> but with Credits to:
 * ErisoHV > https://github.com/ErisoHV/docx4jExample.git  (for the core of docx manipulation)
 * plutext > https://github.com/plutext/docx4j/blob/master/src/samples/docx4j/org/docx4j/samples/VariableReplace.java (for the right replace of \r\n characters in docx)
 * @implNote
 * just call the generateDocument(template, output, List(obj) -optional, List(List of obj) -optional , HashMap -optional) passing all the objects, List of Objects or HashMap that you think you will have to map in the corrispondenting template.
 */

@Service
public class DocxService {

    Logger logger = LogManager.getLogger(DocxService.class);

    /**
     * the right template format
     */
    private final static String FORMAT = ".docx";

    /**
     * the mime type of Microsoft Word .docx files
     */
    private final static String MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";


    /**
     * OpenOffice .docx format mime type
     */
    private static final String MIME_TYPE_OPEN_OFFICE = "application/zip";


    /**
     * char used in .docx template before the personalized separator chars to identify them
     * - default @
     */
    private String separatorIdentifier;

    /**
     * char used in .docx template to separate multiple entries of the same object to identify them
     * - default #
     */
    private String mutlifieldsIdentifier;

    private String dateFormat;

    public DocxService() {
        this.setSeparatorIdentifier("@");
        this.setMutlifieldsIdentifier("#");
        this.setDateFormat("dd/MM/yyyy");
    }

    /**
     * The basic and simpliest method(Credits to https://github.com/ErisoHV/docx4jExample.git that inspired me for the core)
     * @param template       - the .docx template
     * @param replace        - the hash map of key-value to replace
     * @param outputDocument - the output target file
     * @return - the out file
     */
    public File generateDocument(File template, HashMap<String, String> replace, String outputDocument) {

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
     * The magic is here(this let you replace placeholders with right value just typing in .docx)
     * @param template      - the .docx file (Save by openoffice or microsoft word)
     * @param out           - the output file target
     * @param objs          - the non-mandatory objects that you will try to map from your .docx template
     *                      (es. a file .docx containing "${anagrafics.nome}" will require an obj=(HckReflect)anagrafics or (HckReflect)obj.identifier="anagrafics")
     * @param listsObj      - the non-mandatory List of objects that you will try to map from your .docx template
     *                      (es. aa file .docx containing "${list_anagrafics.name}" will require a listsObj containing a List of (HckReflect)anagrafics or List of (HckReflect)obj.identifier="anagrafics")
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

    /**
     * @param placeHolders  - the list of placeholders found in the .docx files
     * @param objs          - the optional objects that will be automatically mapped to the right placeholders checking the corrispondence between the first slice of the placeholder and the return of object.getRightIdentifier() method.
     *                      for example Address.java will find a match with "address.fieldThatYouWant"
     * @param lists         - the same of objs but this time it is a List of your object to be mapped with list_placeholder that you need to recursively replace on the .docx.
     * @param fixedMappings - the simple key-value mappings matchings the placeholders.
     * @return the hashMap of key-value that will be used to replace the placeholders.
     */
    private HashMap<String, String> getMappings(List<String> placeHolders, List<? extends HckReflect> objs, List<List<? extends HckReflect>> lists, HashMap<String, String> fixedMappings) {
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
     * @param r         (HckReflectUtis)obj used to retrieve the getField
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
     *
     * @param file the file wich you want to get the mymeType
     * @return the String representing the mimeType
     */
    private static String getMimeType(File file) {
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


}