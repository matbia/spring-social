package com.matbia.misc;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static byte[] scaleImage(byte[] imgByteArr, String fileExtension, int targetSize) throws IOException {
        BufferedImage scaledImg = Scalr.resize(ImageIO.read(new ByteArrayInputStream(imgByteArr)), targetSize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( scaledImg, fileExtension.toLowerCase(), baos );
        baos.flush();
        byte[] scaledImageByteArr = baos.toByteArray();
        baos.close();
        return scaledImageByteArr;
    }

    public static String extractYouTubeId(String ytUrl) {
        String regex = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Matcher matcher = Pattern.compile(regex).matcher(ytUrl);
        return matcher.find() ? matcher.group() : "";
    }

    public static String getExternalIP() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
