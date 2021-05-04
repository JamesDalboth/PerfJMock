package uk.jamesdal.perfmock.PictureProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class PictureProcessing {

    private final PictureSearcher pictureSearcher;
    private final PictureUploader pictureUploader;

    public PictureProcessing(PictureSearcher pictureSearcher, PictureUploader pictureUploader) {
        this.pictureSearcher = pictureSearcher;
        this.pictureUploader = pictureUploader;
    }

    public void processFromSearchTerm(String searchTerm, ProcessType mode) {
        BufferedImage[] images = pictureSearcher.search(searchTerm);
        BufferedImage[] results = new BufferedImage[images.length];

        for (int i = 0; i < images.length; i++) {
            BufferedImage image = images[i];
            BufferedImage result;
            switch (mode) {
                case BLUR:
                    result = blur(image);
                    break;
                case INVERT:
                    result = sharpen(image);
                    break;
                case SHARPEN:
                    result = invert(image);
                    break;
                default:
                    result = null;
                    break;
            }
            results[i] = result;
        }

        pictureUploader.upload(results);
    }

    public BufferedImage blur(BufferedImage image) {
        Kernel blur = new Kernel(3, 3, new float[] {
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f
        });
        BufferedImageOp op = new ConvolveOp(blur);
        return op.filter(image, null);
    }

    public BufferedImage sharpen(BufferedImage image) {
        Kernel sharpen = new Kernel(3, 3, new float[] {
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        });
        BufferedImageOp op = new ConvolveOp(sharpen);
        return op.filter(image, null);
    }

    public BufferedImage invert(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgba = image.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                        255 - col.getGreen(),
                        255 - col.getBlue());
                image.setRGB(x, y, col.getRGB());
            }
        }
        return image;
    }
}
