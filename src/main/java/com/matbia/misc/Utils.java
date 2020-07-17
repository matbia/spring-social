package com.matbia.misc;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Miscellaneous static utility methods
 */
public abstract class Utils {
    /**
     * Scales image dimensions
     * @param imgByteArr image file as a byte array
     * @param fileExtension image file extension
     * @param targetSize desired image width
     * @return Scaled image
     */
    public static byte[] scaleImage(byte[] imgByteArr, String fileExtension, int targetSize) throws IOException {
        BufferedImage scaledImg = Scalr.resize(ImageIO.read(new ByteArrayInputStream(imgByteArr)), targetSize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( scaledImg, fileExtension.toLowerCase(), baos );
        baos.flush();
        byte[] scaledImageByteArr = baos.toByteArray();
        baos.close();
        return scaledImageByteArr;
    }

    /**
     * Extracts ID from YouTube video URL
     * @param ytUrl URL adress of a YouTube video
     * @return YouTube video ID or empty string if URL is invalid
     */
    public static String extractYouTubeId(String ytUrl) {
        String regex = "(?<=watch\?v=|/videos/|embed\/|youtu.be\/|\/v\/|watch\?v%3D|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#\&\?\n]*";
        Matcher matcher = Pattern.compile(regex).matcher(ytUrl);
        return matcher.find() ? matcher.group() : "";
    }

    /**
     * Fetches the external IP address
     * @return public IP address or 127.0.0.1 if any exception is thrown 
     */
    public static String getExternalIP() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
