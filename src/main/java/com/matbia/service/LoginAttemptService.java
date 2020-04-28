package com.matbia.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service class responsible for preventing brute-force attacks on user accounts.
 * Does so by caching IP addresses of clients who provided incorrect login credentials.
 */
@Service
public class LoginAttemptService {
    /**
     * Amount of allowed failed login attempts before client's IP address gets blocked
     */
    private  final int MAX_ATTEMPT = 5;
    private LoadingCache<String, Integer> attemptsCache;

    /**
     * Constructor configures and builds the login attempts cache.
     */
    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(3, TimeUnit.HOURS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    /**
     * Resets login attempts counter of given IP by removing it from cache.
     * @param key client's IP address
     */
    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    /**
     * Caches the IP, if it isn't already cached, and increments the attempts counter
     * @param key client's IP address
     */
    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    /**
     * Checks if given IP address is blocked
     * @param key client's IP address
     * @return true if login attempts exceed predetermined limit, false otherwise
     */
    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
