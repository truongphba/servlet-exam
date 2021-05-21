package helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX= Pattern.compile("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$");

    public static boolean validate(String emailStr) {
        Matcher mathcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);

        return mathcher.find();
    }
}
