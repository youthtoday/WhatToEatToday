package com.whattoeattoday.recommendationservice.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lijie Huang lh3158@columbia.edu
 * @date 10/13/23
 */
public class ParamUtil {
    static final Pattern numericPattern = Pattern.compile("[0-9]*");
    public static boolean isBlank(String param) {
        return param == null || param.isEmpty();
    }

    public static boolean isAllNotBlank(String[] params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        Matcher isNum = numericPattern.matcher(str);
        return isNum.matches();
    }

    // TODO
    public static boolean isFiledNames(List<String> filedNames) {
        return true;
    }
}