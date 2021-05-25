package uk.jamesdal.perfmock.PictureProcessor.impl;

import uk.jamesdal.perfmock.PictureProcessor.PictureUploader;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.awt.image.BufferedImage;

public class UploaderImpl implements PictureUploader {
    @Override
    public void upload(BufferedImage[] images) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
    }
}
