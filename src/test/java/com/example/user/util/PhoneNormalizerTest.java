package com.example.user.util;

import com.example.github.action.demo.api.PhoneNormalizer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PhoneNormalizerTest {

    @Test
    public void testNormalizeKeepsDigitsAndPlus() {
        String in = "+1 (234) 567-8901";
        String out = PhoneNormalizer.normalize(in);
        assertEquals("+12345678901", out);
    }

    @Test
    public void testNormalizeNullReturnsNull() {
        assertNull(PhoneNormalizer.normalize(null));
    }

    @Test
    public void testIsValidPhoneFormat() {
        assertTrue(PhoneNormalizer.isValidPhoneFormat("+12345678901"));
        assertTrue(PhoneNormalizer.isValidPhoneFormat("234-567-8901"));
        assertFalse(PhoneNormalizer.isValidPhoneFormat("abc"));
        assertFalse(PhoneNormalizer.isValidPhoneFormat(""));
    }
}