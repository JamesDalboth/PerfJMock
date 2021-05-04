package uk.jamesdal.perfmock.PictureProcessor;

import java.awt.image.BufferedImage;

public interface PictureSearcher {
    BufferedImage[] search(String searchTerm);
}
