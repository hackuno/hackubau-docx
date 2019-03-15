import com.google.common.collect.Lists;
import hck.interfaces.HckReflectUtils;
import hck.services.DocxService;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class playTest {

    @Test
    public void howToUse() {
        System.out.println("Estendi le classi che vuoi rendere disponibili al template con HckReflectUtils e passale al mio service.");
        System.out.println("Se devi stampare delle liste di oggetti fai la stessa cosa ma passali come Lista di Lista di oggetti che estendono HckReflectUtils");
        System.out.println("Attenzione, la mia procedura richiama qualunque metodo che inizi con get, quindi puoi farti anche restituire quel che vuoi (es fai un get di una lista che funzioni tipo toString della stessa oppure unisci piu campi , formatta le date, etc etc):");
        System.out.println("Sul word scrivi:");
        System.out.println("${OGGETTO.campoDiCuiInvocareLaGet.eventualeCampoACascata}");
        System.out.println("${list__OGGETTO@separatore.campoGetDaInvocare#campo2DaInvocare@separatoredacampo1#campo3DaInvocare@separatoredacampo2");
    }

    @Test
    public void test() {
        Anagrafica a = new Anagrafica();
        a.setNome("gigi");
        a.setCognome("alla cremeria");
        Indirizzo i = new Indirizzo();
        i.setCompleto("alla pizzeria perchè sbaglia sempre");
        a.setIndirizzo(i);


        Anagrafica b = new Anagrafica();
        b.setNome("nando");
        b.setCognome("bartolazzi");
        Indirizzo i2 = new Indirizzo();
        i2.setCompleto("emengusco sulladda");
        b.setIndirizzo(i2);


        ClassLoader classLoader = getClass().getClassLoader();

        File f = new File("src/test/resources/template.docx");
        File control = new File("src/test/resources/expectedResult.docx");
        File fout = new File("src/test/resources/out.docx");

        List<Anagrafica> lemieAnagrafiche = Lists.newArrayList(a, b);

        List<List<? extends HckReflectUtils>> listaDiListeDiOggetti = new ArrayList<>();
        listaDiListeDiOggetti.add(lemieAnagrafiche);

        DocxService serv = new DocxService();
        fout = serv.generateDocument(f, fout, Lists.newArrayList(a), listaDiListeDiOggetti, null);
        boolean result = false;

        WordprocessingMLPackage wordMLPackage1;
        WordprocessingMLPackage wordMLPackage2;
        try {

            wordMLPackage1 = WordprocessingMLPackage.load(fout);
            wordMLPackage2 = WordprocessingMLPackage.load(fout);

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
            result = equals;

        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(result);

    }

    public class Indirizzo extends HckReflectUtils {
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

    public class Anagrafica extends HckReflectUtils {
        String nome;
        String cognome;
        playTest.Indirizzo indirizzo;

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

        public playTest.Indirizzo getIndirizzo() {
            return this.indirizzo;
        }

        public void setIndirizzo(final playTest.Indirizzo indirizzo) {
            this.indirizzo = indirizzo;
        }
    }
}