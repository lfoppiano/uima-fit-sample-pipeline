package org.foppiano.uima.fit.tutorial.casConsumer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Logger;
import org.foppiano.uima.fit.tutorial.annotator.SimpleParserAE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: lf84914
 * Date: 1/22/14
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCC extends CasAnnotator_ImplBase {

    public final static String PARAM_OUTPUT_DIR = "outputDir";
    @ConfigurationParameter(name = PARAM_OUTPUT_DIR)
    private String outputDirectory;

    public final static String PARAM_ANNOTATION_TYPES = "annotationTypes";
    @ConfigurationParameter(name = PARAM_ANNOTATION_TYPES)
    private List<String> annotationTypes;

    private Logger logger;

    @Override
    public void process(CAS cas) throws AnalysisEngineProcessException {

        JCas jcas = null;
        try {
            jcas = cas.getJCas();
        } catch (CASException e) {

        }

        String original = jcas.getDocumentText();
        String onlyText = "";


        CAS sofaText = cas.getView(SimpleParserAE.SOFA_NAME_TEXT_ONLY);
        onlyText = sofaText.getDocumentText();

        String name = UUID.randomUUID().toString();

        Iterator<String> annotationTypesIterator = annotationTypes.iterator();

        try {
            File outputDir = new File(this.outputDirectory + "/" + name);
            System.out.println("Saving file to " + outputDir.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(outputDir);
            PrintWriter pw = new PrintWriter(fos);

            pw.println("XML");
            pw.println(original);
            pw.println("");
            pw.println("");

            while(annotationTypesIterator.hasNext()) {
                Type annotationType = CasUtil.getType(sofaText, annotationTypesIterator.next());
                Collection<AnnotationFS> annotations = CasUtil.select(sofaText, annotationType);

                String textWithSentenceAnnotation = mergeTextAndAnnotation(onlyText, annotations);

                pw.println("TEXT + " + annotationType.getName());
                pw.println("Annotations: " +annotations.size());
                pw.println(textWithSentenceAnnotation);
                pw.println("");
                pw.println("");
            }

            pw.close();
        } catch (FileNotFoundException e) {
            //
        }
    }

    public void initialize(final UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        logger = context.getLogger();

    }

    private String mergeTextAndAnnotation(String input, Collection<AnnotationFS> annotations) {

        StringBuilder sb = new StringBuilder();

        Iterator<AnnotationFS> it = annotations.iterator();

        int totalBegin = 0;

        while(it.hasNext()) {
            AnnotationFS annotation = it.next();

            int begin = annotation.getBegin();
            int end = annotation.getEnd();

            if (totalBegin < begin ) {
                sb.append(input.substring(totalBegin, begin));
            }

            sb.append("<annotation>" + input.substring(begin, end) + "</annotation>");

            totalBegin = end;


        }

        sb.append(input.substring(totalBegin));

        return sb.toString();
    }
}
