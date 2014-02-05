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
import org.foppiano.uima.fit.tutorial.collectorReader.SimpleCR;

import java.io.IOException;

/**
 * Hello world!
 */
public class Pipeline1 {
    public static void main(String[] args) throws UIMAException, IOException {
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                SimpleCR.class,
                SimpleCR.PARAM_SOURCE_FILE,
                SimpleCR.class.getResource("/sampleInput.csv").getPath()
        );

        AnalysisEngineDescription preparationEngine = AnalysisEngineFactory.createEngineDescription(
                SimpleParserAE.class
        );

        AnalysisEngineDescription dictionaryEngine = AnalysisEngineFactory.createEngineDescription(
                DictionaryAnnotator.class,
                "DictionaryFiles",
                new String[]{
                        "dictionary.xml"
                },
                "InputMatchType",
                "org.apache.uima.TokenAnnotation"
        );

        AnalysisEngineDescription whitespaceEngine = AnalysisEngineFactory.createEngineDescription(
                WhitespaceTokenizer.class//,
                //"SofaNames",
                //new String[]{SimpleParserAE.SOFA_NAME_TEXT_ONLY}
        );

        AnalysisEngineDescription casConsumer = AnalysisEngineFactory.createEngineDescription(
                SimpleCC.class,
                SimpleCC.PARAM_OUTPUT_DIR,
                "/Users/lf84914/development/apl/data/out"
        );

        AggregateBuilder builder = new AggregateBuilder();
        builder.add(preparationEngine);
        builder.add(whitespaceEngine, CAS.NAME_DEFAULT_SOFA, SimpleParserAE.SOFA_NAME_TEXT_ONLY);
        builder.add(dictionaryEngine, CAS.NAME_DEFAULT_SOFA, SimpleParserAE.SOFA_NAME_TEXT_ONLY);

        SimplePipeline.runPipeline(reader, builder.createAggregateDescription(), casConsumer);
    }
}
