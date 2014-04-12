package com.example.slides_controller.app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageSolver {
    public static BufferedImage resizeImageWithHint(BufferedImage originalImage, int new_width, int new_height, int type) {

        BufferedImage resizedImage = new BufferedImage(new_width, new_height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, new_width, new_height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        return resizedImage;
    }

    public static byte[] getimgbyte(BufferedImage img) throws IOException {
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", imageStream);
        byte[] data = imageStream.toByteArray();
        return data;
    }
}
