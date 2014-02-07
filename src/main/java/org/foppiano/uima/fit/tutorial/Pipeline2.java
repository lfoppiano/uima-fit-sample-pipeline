package org.foppiano.uima.fit.tutorial;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.conceptMapper.ConceptMapper;
import org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl;
import org.apache.uima.conceptMapper.support.tokenizer.OffsetTokenizer;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.foppiano.uima.fit.tutorial.annotator.SimpleParserAE;
import org.foppiano.uima.fit.tutorial.casConsumer.SimpleCC;
import org.foppiano.uima.fit.tutorial.collectorReader.LocalFileCR;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.uima.fit.factory.ExternalResourceFactory.createDependencyAndBind;

public class Pipeline2 {
    public static void main(String[] args) throws UIMAException, IOException {

        /** Base components **/
        CollectionReaderDescription localReader = CollectionReaderFactory.createReaderDescription(
                LocalFileCR.class,
                LocalFileCR.PARAM_SOURCE_DIR, LocalFileCR.class.getResource("/input").getPath()
        );

        AnalysisEngineDescription preparationEngine = AnalysisEngineFactory.createEngineDescription(
                SimpleParserAE.class
        );

        List<String> types = new ArrayList<String>();
        types.add("uima.tt.TokenAnnotation");
        types.add("org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation");
        types.add("uima.tcas.DocumentAnnotation");
        types.add("org.apache.uima.conceptMapper.DictTerm");

        AnalysisEngineDescription casConsumer = AnalysisEngineFactory.createEngineDescription(
                SimpleCC.class,
                SimpleCC.PARAM_OUTPUT_DIR, "out",
                SimpleCC.PARAM_ANNOTATION_TYPES, types
        );

        AnalysisEngineDescription offsetTokenizer = AnalysisEngineFactory.createEngineDescription(
                OffsetTokenizer.class,
                OffsetTokenizer.PARAM_CASE_MATCH, "ignoreall"/*,
                OffsetTokenizer.PARAM_TOKEN_DELIM, "/-*&amp;@(){}|[]&gt;&lt;\\'`\":;,$%+.?!"*/
        );

        File tmpTokenizerDescription = File.createTempFile("prefffix_", "_suffix");
        tmpTokenizerDescription.deleteOnExit();

        try {
            offsetTokenizer.toXML(new FileWriter(tmpTokenizerDescription));
        } catch (SAXException e) {

        }

        AnalysisEngineDescription conceptMapper = AnalysisEngineFactory.createEngineDescription(
                ConceptMapper.class,
                "TokenizerDescriptorPath", tmpTokenizerDescription.getAbsolutePath(),
                "LanguageID", "en",
                ConceptMapper.PARAM_TOKENANNOTATION, "uima.tt.TokenAnnotation",
                ConceptMapper.PARAM_ANNOTATION_NAME, "org.apache.uima.conceptMapper.DictTerm",
                "SpanFeatureStructure", "uima.tcas.DocumentAnnotation",
                ConceptMapper.PARAM_FEATURE_LIST, new String[]{"DictCanon"},
                ConceptMapper.PARAM_ATTRIBUTE_LIST, new String[]{"canonical"}
        );

        createDependencyAndBind(conceptMapper, "DictionaryFile", DictionaryResource_impl.class, "file:testDict2.xml");

        AggregateBuilder builder = new AggregateBuilder();
        builder.add(preparationEngine, SimpleParserAE.SOFA_NAME_TEXT_ONLY, "newSofa");
        builder.add(offsetTokenizer, CAS.NAME_DEFAULT_SOFA, "newSofa");
        builder.add(conceptMapper, CAS.NAME_DEFAULT_SOFA, "newSofa");
        builder.add(casConsumer, SimpleParserAE.SOFA_NAME_TEXT_ONLY, "newSofa");

        SimplePipeline.runPipeline(localReader, builder.createAggregateDescription());
    }
}
