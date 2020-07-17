package com.matbia.misc;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void extractYouTubeId() {
        Map<String, String> testUrls = new HashMap<>();
        testUrls.put("CbUjuwhQPKs", "https://www.youtube.com/watch?v=CbUjuwhQPKs");
        testUrls.put("sa-TUpSx1JA", "https://youtu.be/sa-TUpSx1JA");
        testUrls.put("9sJUDx7iEJw", "https://www.youtube.com/embed/9sJUDx7iEJw");
        testUrls.put("1UzTf0Qo37A", "http://gdata.youtube.com/feeds/api/videos/1UzTf0Qo37A&whatever");

        testUrls.forEach((k, v) -> assertEquals(k, Utils.extractYouTubeId(v)));
    }
}