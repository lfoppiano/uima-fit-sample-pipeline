package org.foppiano.uima.fit.tutorial.collectorReader;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lf84914
 * Date: 07/02/14
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
public class LocalFileCR extends JCasCollectionReader_ImplBase {

    public final static String PARAM_SOURCE_DIR = "sourceDirectory";
    public final static String PARAM_IS_RECURSIVE = "isRecursive";
    List<File> fileList = new ArrayList<File>();
    int progressIndex = 0;
    @ConfigurationParameter(name = PARAM_SOURCE_DIR, defaultValue = "file:input")
    private String sourceDirectory;
    @ConfigurationParameter(name = PARAM_IS_RECURSIVE, mandatory = false, defaultValue = "false")
    private boolean isRecursive;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        File sourceDirFile = new File(sourceDirectory);

        if (!sourceDirFile.isDirectory() || !sourceDirFile.exists()) {
            throw new ResourceInitializationException();
        }

        addFilesFromSource(sourceDirFile);
        progressIndex = 0;
    }

    private void addFilesFromSource(File sourceDir) {
        for (File file : sourceDir.listFiles()) {
            if (!file.isDirectory()) {
                fileList.add(file);
            } else if (isRecursive == true) {
                addFilesFromSource(file);
            }
        }
    }

    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException {
        File file = fileList.get(progressIndex++);
        String text = FileUtils.file2String(file);

        jCas.setDocumentText(text);
    }

    @Override
    public void close() throws IOException {
        //nothing to do bro.
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[]{
                new ProgressImpl(progressIndex, fileList.size(), Progress.ENTITIES)
        };
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return progressIndex < fileList.size();
    }
}
