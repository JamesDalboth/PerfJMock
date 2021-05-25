package uk.jamesdal.perfmock.PictureProcessor;

import uk.jamesdal.perfmock.PictureProcessor.impl.SearcherImpl;
import uk.jamesdal.perfmock.PictureProcessor.impl.UploaderImpl;
import uk.jamesdal.perfmock.production.ProductionTest;

public class PPProduction implements ProductionTest.Runner {
    @Override
    public void run() {
        PictureProcessing processing = new PictureProcessing(new SearcherImpl(), new UploaderImpl());
        processing.processFromSearchTerm("SEARCH", ProcessType.BLUR);
    }
}
