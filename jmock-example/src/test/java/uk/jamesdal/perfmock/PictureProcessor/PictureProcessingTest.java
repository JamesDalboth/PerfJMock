package uk.jamesdal.perfmock.PictureProcessor;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.generators.ArrayGenerator;
import uk.jamesdal.perfmock.perf.generators.ConstGenerator;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

import java.awt.image.BufferedImage;

public class PictureProcessingTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule);

    private final PictureSearcher pictureSearcher = ctx.mock(PictureSearcher.class);
    private final PictureUploader pictureUploader = ctx.mock(PictureUploader.class);

    private final String SEARCH_TERM = "search term";
    private final int IMAGE_SIZE = 100;
    private final BufferedImage IMAGE =
            new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, 1);
    private final ArrayGenerator arrayGenerator = new ArrayGenerator(
            BufferedImage.class,
            new ConstGenerator<>(IMAGE),
            new NormalDistribution(10, 2)
    );

    private BufferedImage[] IMAGES;

    @Test
    @PerfTest(iterations = 2000, warmups = 100)
    public void blurTest() {
        PictureProcessing processor = new PictureProcessing(pictureSearcher, pictureUploader);

        IMAGES = (BufferedImage[]) arrayGenerator.generate();

        ctx.checking(new Expectations() {{
            allowing(pictureSearcher).search(SEARCH_TERM); will(returnValue(IMAGES)); taking(seconds(1));
            allowing(pictureUploader).upload(with(any(BufferedImage[].class))); taking(seconds(0.5));
        }});

        processor.processFromSearchTerm(SEARCH_TERM, ProcessType.BLUR);
    }

    @Test
    @PerfTest(iterations = 100, warmups = 0)
    public void sharpenTest() {
        PictureProcessing processor = new PictureProcessing(pictureSearcher, pictureUploader);

        ctx.checking(new Expectations() {{
            allowing(pictureSearcher).search(SEARCH_TERM); will(returnValue(IMAGES)); taking(seconds(1));
            allowing(pictureUploader).upload(with(any(BufferedImage[].class))); taking(seconds(0.5));
        }});

        processor.processFromSearchTerm(SEARCH_TERM, ProcessType.SHARPEN);
    }

    @Test
    @PerfTest(iterations = 100, warmups = 0)
    public void invertTest() {
        PictureProcessing processor = new PictureProcessing(pictureSearcher, pictureUploader);

        ctx.checking(new Expectations() {{
            allowing(pictureSearcher).search(SEARCH_TERM); will(returnValue(IMAGES)); taking(seconds(1));
            allowing(pictureUploader).upload(with(any(BufferedImage[].class))); taking(seconds(0.5));
        }});

        processor.processFromSearchTerm(SEARCH_TERM, ProcessType.INVERT);
    }
}