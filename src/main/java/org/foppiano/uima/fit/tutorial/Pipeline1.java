package org.foppiano.uima.fit.tutorial;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.annotator.WhitespaceTokenizer;
import org.apache.uima.annotator.dict_annot.impl.DictionaryAnnotator;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.foppiano.uima.fit.tutorial.annotator.SimpleParserAE;
import org.foppiano.uima.fit.tutorial.casConsumer.SimpleCC;
import org.foppiano.uima.fit.tutorial.collectorReader.LocalFileCR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pipeline1: TEI reader-Whitespace-Dictionary-Writer to file
 */
public class Pipeline1 {
    public static void main(String[] args) throws UIMAException, IOException {
        CollectionReaderDescription localReader = CollectionReaderFactory.createReaderDescription(
                LocalFileCR.class,
                LocalFileCR.PARAM_SOURCE_DIR, LocalFileCR.class.getResource("/input").getPath()
        );

        AnalysisEngineDescription preparationEngine = AnalysisEngineFactory.createEngineDescription(
                SimpleParserAE.class
        );

        AnalysisEngineDescription dictionaryEngine = AnalysisEngineFactory.createEngineDescription(
                DictionaryAnnotator.class,
                "DictionaryFiles", new String[]{"dictionary.xml"},
                "InputMatchType", "org.apache.uima.TokenAnnotation"
        );

        AnalysisEngineDescription whitespaceEngine = AnalysisEngineFactory.createEngineDescription(
                WhitespaceTokenizer.class
        );

        List<String> types = new ArrayList<String>();
        types.add("org.apache.uima.SentenceAnnotation");
        types.add("org.apache.uima.TokenAnnotation");
        types.add("org.apache.uima.DictionaryEntry");


        AnalysisEngineDescription casConsumer = AnalysisEngineFactory.createEngineDescription(
                SimpleCC.class,
                SimpleCC.PARAM_OUTPUT_DIR, "out",
                SimpleCC.PARAM_ANNOTATION_TYPES, types
        );

        AggregateBuilder builder = new AggregateBuilder();
        builder.add(preparationEngine);
        builder.add(whitespaceEngine, CAS.NAME_DEFAULT_SOFA, SimpleParserAE.SOFA_NAME_TEXT_ONLY);
        builder.add(dictionaryEngine, CAS.NAME_DEFAULT_SOFA, SimpleParserAE.SOFA_NAME_TEXT_ONLY);

        SimplePipeline.runPipeline(localReader, builder.createAggregateDescription(), casConsumer);
    }
}
