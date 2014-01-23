package org.foppiano.uima.fit.tutorial.annotator;


import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.foppiano.uima.fit.tutorial.parser.TeiSimpleParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lf84914
 * Date: 1/16/14
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleParserAE extends JCasAnnotator_ImplBase {

    public static String SOFA_NAME_TEXT_ONLY = "textOnly";

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {

        JCas newCas = null;
        try {
            jCas.createView(SOFA_NAME_TEXT_ONLY);
           newCas = jCas.getView(SOFA_NAME_TEXT_ONLY);
        } catch (CASException e) {
        }

        newCas.setDocumentText(new TeiSimpleParser().parse(jCas.getDocumentText()));



    }
}
