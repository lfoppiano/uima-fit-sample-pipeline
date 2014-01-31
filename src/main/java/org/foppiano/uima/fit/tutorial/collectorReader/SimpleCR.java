package org.foppiano.uima.fit.tutorial.collectorReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.fit.component.initialize.ConfigurationParameterInitializer;
import org.apache.uima.fit.component.initialize.ExternalResourceInitializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lf84914
 * Date: 1/16/14
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCR extends CollectionReader_ImplBase {

    public final static String PARAM_SOURCE_FILE = "sourceFile";
    List<String> inputUrls = null;
    @ConfigurationParameter(name = PARAM_SOURCE_FILE)

    private String sourceFile;
    private Integer index = 0;

    public void initialize() throws ResourceInitializationException {
        ConfigurationParameterInitializer.initialize(this, getUimaContext());
        ExternalResourceInitializer.initialize(this, getUimaContext());

        File file = new File(sourceFile);

        inputUrls = new ArrayList<String>();

        String kimePrefix = "http://kimedb.internal.epo.org/tei/families/__familyId__/publications/__publicationId__/metadata";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine(); //Headers
            line = br.readLine();
            while (line != null) {
                String[] tokens = line.split(",");

                String family = tokens[0].replace('"', ' ').trim();
                String pubId = tokens[1].replace('"', ' ').trim();

                inputUrls.add(kimePrefix.replace("__familyId__", family).replace("__publicationId__", pubId).trim());

                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            // joke
        } catch (IOException e) {
            // joke 2
        }
    }

    @Override
    public void getNext(CAS cas) throws IOException, CollectionException {

        String url = inputUrls.get(index++);
        HttpGet get = new HttpGet(url);

        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(get);

        System.out.println("Querying " + url);

        HttpEntity entity = response.getEntity();

        String responseText = new String(IOUtils.toByteArray(entity.getContent()));

        try {
            JCas jcas = cas.getJCas();
            jcas.setDocumentText(responseText);
        } catch (CASException e) {
            // joking
        }finally {
            response.close();
        }
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return index < inputUrls.size();
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[]{
                new ProgressImpl(index, inputUrls.size(), Progress.ENTITIES)
        };
    }

    @Override
    public void close() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
