package edu.msu.mi.loom.process;

import edu.msu.mi.loom.process.IRandomStringGenerator;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Created by Emil Matevosyan
 * Date: 10/7/15.
 */

@Component("randomStringGenerator")
public class RandomStringGenerator implements IRandomStringGenerator {
    private static final char[] UPPERCASE_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890".toCharArray();
    private static final char[] LOWERCASE_CHARS = "qwertyuiopasdfghjklzxcvbnm1234567890".toCharArray();
    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateUppercase(int length) {
        return generate(length, UPPERCASE_CHARS);
    }

    @Override
    public String generateLowercase(int length) {
        return generate(length, LOWERCASE_CHARS);
    }

    private String generate(int length, char[] chars) {
        return RandomStringUtils.random(length, 0, chars.length, false, false, chars, secureRandom);
    }
}
