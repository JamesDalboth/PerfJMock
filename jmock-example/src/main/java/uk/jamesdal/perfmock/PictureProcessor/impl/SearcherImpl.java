package uk.jamesdal.perfmock.PictureProcessor.impl;

import org.apache.commons.math3.distribution.NormalDistribution;
import uk.jamesdal.perfmock.PictureProcessor.PictureSearcher;
import uk.jamesdal.perfmock.perf.generators.ArrayGenerator;
import uk.jamesdal.perfmock.perf.generators.ConstGenerator;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.awt.image.BufferedImage;

public class SearcherImpl implements PictureSearcher {

    private final int IMAGE_SIZE = 100;
    private final BufferedImage IMAGE =
            new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, 1);
    private final ArrayGenerator arrayGenerator = new ArrayGenerator(
            BufferedImage.class,
            new ConstGenerator<>(IMAGE),
            new NormalDistribution(10, 2)
    );

    @Override
    public BufferedImage[] search(String searchTerm) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return (BufferedImage[]) arrayGenerator.generate();
    }
}
