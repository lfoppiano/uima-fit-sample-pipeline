package org.foppiano.uima.fit.tutorial;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.annotator.WhitespaceTokenizer;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.conceptMapper.ConceptMapper;
import org.apache.uima.conceptMapper.support.tokenizer.OffsetTokenizer;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.foppiano.uima.fit.tutorial.annotator.SimpleParserAE;
import org.foppiano.uima.fit.tutorial.casConsumer.SimpleCC;
import org.foppiano.uima.fit.tutorial.collectorReader.SimpleCR;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pipeline2 {
    public static void main(String[] args) throws UIMAException, IOException {

        /** Base components **/
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                SimpleCR.class,
                SimpleCR.PARAM_SOURCE_FILE,
                SimpleCR.class.getResource("/sampleInput.csv").getPath()
        );

        AnalysisEngineDescription preparationEngine = AnalysisEngineFactory.createEngineDescription(
                SimpleParserAE.class
        );

        List<String> types = new ArrayList<String>();
        types.add("uima.tt.TokenAnnotation");
        types.add("org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation");
        types.add("uima.tcas.DocumentAnnotation");

        AnalysisEngineDescription casConsumer = AnalysisEngineFactory.createEngineDescription(
                SimpleCC.class,
                SimpleCC.PARAM_OUTPUT_DIR, "out",
                SimpleCC.PARAM_ANNOTATION_TYPES, types
        );

        AnalysisEngineDescription offsetTokenizer = AnalysisEngineFactory.createEngineDescription(
                OffsetTokenizer.class,
                OffsetTokenizer.PARAM_CASE_MATCH, "false"/*,
                OffsetTokenizer.PARAM_TOKEN_DELIM, "/-*&amp;@(){}|[]&gt;&lt;\\'`\":;,$%+.?!"*/
        );

                                     /*
        AnalysisEngineDescription conceptMapper = AnalysisEngineFactory.createEngineDescription(
                ConceptMapper.class,
                "TokenizerDescriptorPath", .getAbsolutePath(),
                "LanguageID", "en",
                ConceptMapper.PARAM_TOKENANNOTATION, "org.apache.uima.TokenAnnotation",
                "SpanFeatureStructure", "org.apache.uima.SentenceAnnotation",
                ConceptMapper.PARAM_FEATURE_LIST, new String[]{""},
                ConceptMapper.PARAM_ATTRIBUTE_LIST, new String[]{""}
        );                         */

        AggregateBuilder builder = new AggregateBuilder();
        builder.add(preparationEngine, SimpleParserAE.SOFA_NAME_TEXT_ONLY, "newSofa");
        builder.add(offsetTokenizer, CAS.NAME_DEFAULT_SOFA, "newSofa");
        builder.add(casConsumer, SimpleParserAE.SOFA_NAME_TEXT_ONLY, "newSofa");

        SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
    }
}
