package org.foppiano.uima.fit.tutorial.casConsumer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.annotator.WhitespaceTokenizer;
import org.apache.uima.cas.*;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.type.Sentence;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.foppiano.uima.fit.tutorial.annotator.SimpleParserAE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import static org.apache.uima.fit.util.JCasUtil.select;

/**
 * Created with IntelliJ IDEA.
 * User: lf84914
 * Date: 1/22/14
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCC extends JCasAnnotator_ImplBase {

    public final static String OUTPUT_DIR_PARAM = "outputDir";
    List<String> inputUrls = null;
    @ConfigurationParameter(name = OUTPUT_DIR_PARAM)
    private String outputDirectory;

    private Logger logger;

    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {

        String original = jcas.getDocumentText();
        String onlyText = "";
        try {
            onlyText = jcas.getView(SimpleParserAE.SOFA_NAME_TEXT_ONLY).getDocumentText();
        } catch (CASException e) {

        }

        String name = UUID.randomUUID().toString();

        Type tokens = JCasUtil.getAnnotationType(jcas, "org.apache.uima.TokenAnnotation");
        Type sentences = JCasUtil.getAnnotationType(jcas, "org.apache.uima.SentenceAnnotation");



        try {
            File outputDir = new File(outputDirectory+"/"+name);
            FileOutputStream fos = new FileOutputStream(outputDir);
            PrintWriter pw = new PrintWriter(fos);

            pw.println("XML");
            pw.println(original);
            pw.println("");
            pw.println("");
            pw.println("TEXT");
            pw.println(onlyText);
            pw.println("");
            pw.println("");
            pw.println("ANNOTATIONS");


            pw.close();
        } catch (FileNotFoundException e) {
            //
        }
    }

    public void initialize(final UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        logger = context.getLogger();
    }
}
