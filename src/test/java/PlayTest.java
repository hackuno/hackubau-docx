import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hck.interfaces.HckReflect;
import hck.services.DocxService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayTest {

    Logger logger = LogManager.getLogger(PlayTest.class);


    @Test
    public void howToUse() {

        System.out.println("Estendi le classi che vuoi rendere disponibili al template con HckReflect e passale al mio service.");
        System.out.println("Se devi stampare delle liste di oggetti fai la stessa cosa ma passali come Lista di Lista di oggetti che estendono HckReflect");
        System.out.println("Attenzione, la mia procedura richiama qualunque metodo che inizi con get, quindi puoi farti anche restituire quel che vuoi (es fai un get di una lista che funzioni tipo toString della stessa oppure unisci piu campi , formatta le date, etc etc):");
        System.out.println("Sul word scrivi:");
        System.out.println("${OGGETTO.campoDiCuiInvocareLaGet.eventualeCampoACascata}");
        System.out.println("${list__OGGETTO@separatore.campoGetDaInvocare#campo2DaInvocare@separatoredacampo1#campo3DaInvocare@separatoredacampo2");
    }

    @Test
    public void test() {

        //just instantiate a random (HckReflect)  object
        Anagrafica a = new Anagrafica();
        a.setNome("gigi");
        a.setCognome("alla cremeria");
        Indirizzo i = new Indirizzo();
        i.setCompleto("alla pizzeria perchè sbaglia sempre");
        a.setIndirizzo(i);

        //just instantiate a random (HckReflect) object
        Anagrafica b = new Anagrafica();
        b.setNome("nando");
        b.setCognome("bartolazzi");
        Indirizzo i2 = new Indirizzo();
        i2.setCompleto("emengusco sulladda");
        b.setIndirizzo(i2);


        //just loading input template (.docx) and output file, plus the control file for assertions
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File("src/test/resources/template.docx");
        File control = new File("src/test/resources/expectedResult.docx");
        File failingControl = new File("src/test/resources/differentResult.docx");
        File fout = new File("src/test/resources/out.docx");

        //just setting a random list of object (extending HckRelfect) that i will call back from the word
        List<Anagrafica> myListOfObj1 = Lists.newArrayList(a, b);

        //adding the list of objects to the list
        List<List<? extends HckReflect>> listaDiListeDiOggetti = new ArrayList<>();
        listaDiListeDiOggetti.add(myListOfObj1);

        //preparing extra fixed mappings
        HashMap<String,String> myFixedValues = Maps.newHashMap();
        myFixedValues.put("dogName","bau");

        //instantiate the service
        DocxService serv = new DocxService();

        /*calling the service. i am passing:
        template .docx
        output destination
        List of the (HckReflect)objects that i will call back from the .docx template (optional)
        List of the List<(HckReflect)objects> that i will call back from the .docx template (optional)
        Eventually extra fixed mappings. (optional)
        */

        fout = serv.generateDocument(f, fout, Lists.newArrayList(a), listaDiListeDiOggetti, myFixedValues);

        logger.info("OUTPUT PATH: "+fout.getAbsolutePath());

        Assertions.assertTrue(checkEqualWords(fout,control));
        Assertions.assertFalse(checkEqualWords(fout,failingControl));

    }


    /**
     *
     * @param fout - first file .docx
     * @param fctrl - control file .docx
     * @return true if equals
     */
    public boolean checkEqualWords(File fout, File fctrl)
    {
        try {

            WordprocessingMLPackage wordMLPackage1;
            WordprocessingMLPackage wordMLPackage2;

            wordMLPackage1 = WordprocessingMLPackage.load(fout);
            wordMLPackage2 = WordprocessingMLPackage.load(fctrl);

            MainDocumentPart documentPart1 = wordMLPackage1.getMainDocumentPart();
            MainDocumentPart documentPart2 = wordMLPackage2.getMainDocumentPart();

            boolean equals = true;
            for (int ii = 0; ii < documentPart1.getContent().size(); ii++) {
                Object ob1 = documentPart1.getContent().get(ii);
                Object ob2 = documentPart2.getContent().get(ii);
                equals = equals & StringUtils.equals(ob1.toString(), ob2.toString());
                if (!equals) {
                    break;
                }
            }
            return equals;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    //HERE I AM CREATING SOME SIMPLE EXAMPLE CLASS THAT EXTENDS HckReflect JUST TO USE THEM IN THE PLAY TEST//

    public class Indirizzo extends HckReflect {
        String completo;

        public Indirizzo() {

        }

        public String getCompleto() {
            return this.completo;
        }

        public void setCompleto(final String completo) {
            this.completo = completo;
        }
    }

    public class Anagrafica extends HckReflect {
        String nome;
        String cognome;
        PlayTest.Indirizzo indirizzo;

        public Anagrafica() {

        }

        public String getCampoSpeciale() {
            return nome + " - " + cognome;
        }

        public String getNome() {
            return this.nome;
        }

        public void setNome(final String nome) {
            this.nome = nome;
        }

        public String getCognome() {
            return this.cognome;
        }

        public void setCognome(final String cognome) {
            this.cognome = cognome;
        }

        public PlayTest.Indirizzo getIndirizzo() {
            return this.indirizzo;
        }

        public void setIndirizzo(final PlayTest.Indirizzo indirizzo) {
            this.indirizzo = indirizzo;
        }
    }
}