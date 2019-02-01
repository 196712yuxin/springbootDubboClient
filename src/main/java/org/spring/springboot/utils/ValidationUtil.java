package org.spring.springboot.utils;



import org.spring.springboot.base.base.BaseRequest;
import org.spring.springboot.base.base.BaseResponse;
import org.spring.springboot.base.base.ErrorType;
import org.spring.springboot.base.base.Passport;


import javax.validation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidationUtil {
    public ValidationUtil() {
    }

    public static Boolean checkId(Long id) {
        return null != id && id >= 1L;
    }

    public static Boolean checkDate(String value) {
        try {
            Double.valueOf(value);
        } catch (Exception var5) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                sdf.parse(value);
                return true;
            } catch (ParseException var4) {
                var4.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public static Boolean checkDatetime(String value) {
        try {
            Double.valueOf(value);
        } catch (Exception var8) {
            List<String> sdfList = new ArrayList();
            sdfList.add("yyyy-MM-dd HH:mm:ss");
            sdfList.add("yyyy-MM-dd hh:mm:ss");
            sdfList.add("yyyy/MM/dd HH:mm:ss");
            sdfList.add("yyyy/MM/dd hh:mm:ss");
            int i = 0;

            while(i < sdfList.size()) {
                String sdfValue = (String)sdfList.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat(sdfValue);

                try {
                    sdf.parse(value);
                    return true;
                } catch (ParseException var7) {
                    ++i;
                }
            }

            return false;
        }

        return true;
    }

    public static Boolean checkDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (Exception var2) {
            return false;
        }

        return true;
    }

    public static Boolean checkInteger(String value) {
        try {
            Double d = Double.parseDouble(value);
            int i = d.intValue();
            return d == (double)i;
        } catch (Exception var3) {
            return false;
        }
    }

    public static boolean checkLong(String value) {
        try {
            Double d = Double.parseDouble(value);
            long l = d.longValue();
            return d == (double)l;
        } catch (Exception var4) {
            return false;
        }
    }

    public static Boolean checkIdList(List<Long> ids) {
        return null != ids && ids.size() != 0;
    }

    public static Boolean checkString(String str) {
        return null != str && 0 != str.trim().length();
    }

    public static Boolean checkStringLength(String str, int minLength, int maxLength) {
        if (null == str) {
            return false;
        } else {
            return str.length() >= minLength && str.length() <= maxLength ? true : false;
        }
    }

    public static Boolean checkEmail(String email) {
        Pattern p = Pattern.compile("^([a-zA-Z0-9]+[_|_|-|-|.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|_|-|-|.]?)*[a-zA-Z0-9]+.[a-zA-Z]{2,3}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static Boolean checkFilePath(String filePath) {
        Pattern p = Pattern.compile("^(?<path>(?:[a-zA-Z]:)?\\\\(?:[^\\\\\\?\\/\\*\\|<>:\"]+\\\\)+)(?<filename>(?<name>[^\\\\\\?\\/\\*\\|<>:\"]+?)\\.(?<ext>[^.\\\\\\?\\/\\*\\|<>:\"]+))$");
        Matcher m = p.matcher(filePath);
        return m.matches();
    }

    public static Boolean checkMobile(String mobile) {
        Pattern pattern = Pattern.compile("^[1][34578][0-9]{9}$");
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }

    public static Boolean checkPhone(String phone) {
        List<Pattern> list = new LinkedList();
        list.add(Pattern.compile("^[1][34578][0-9]{9}$"));
        list.add(Pattern.compile("^[0][1-9]{1,2}[0-9]-[2-9][0-9]{6,7}$"));
        list.add(Pattern.compile("^[2-9][0-9]{6,7}$"));
        list.add(Pattern.compile("([0][1-9]{1,2}[0-9])[2-9][0-9]{6,7}$"));
        Boolean result = false;

        Matcher matcher;
        for(Iterator var4 = list.iterator(); var4.hasNext(); result = result | matcher.matches()) {
            Pattern pattern = (Pattern)var4.next();
            matcher = pattern.matcher(phone);
        }

        return result;
    }

    public static Boolean checkUrl(String url) {
        List<Pattern> list = new LinkedList();
        list.add(Pattern.compile("^(http|ftp|https)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$"));
        Boolean result = false;

        Matcher matcher;
        for(Iterator var4 = list.iterator(); var4.hasNext(); result = result | matcher.matches()) {
            Pattern pattern = (Pattern)var4.next();
            matcher = pattern.matcher(url);
        }

        return result;
    }

    public static Boolean checkUrlStrict(String url) {
        List<Pattern> list = new LinkedList();
        list.add(Pattern.compile("^(http|ftp|https)(://)(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$"));
        Boolean result = false;

        Matcher matcher;
        for(Iterator var4 = list.iterator(); var4.hasNext(); result = result | matcher.matches()) {
            Pattern pattern = (Pattern)var4.next();
            matcher = pattern.matcher(url);
        }

        return result;
    }

    public static <T extends BaseResponse> T validate(BaseRequest request, T response) {
        return validate(request, response, (Passport)null);
    }

    public static <T extends BaseResponse> T validate(BaseRequest request, T response, Passport passport) {
        if (request == null) {
            response.addError(ErrorType.EXPECTATION_NULL, "请求对象不能为空");
            return response;
        } else {
            Locale locale = Locale.getDefault();
            if (passport != null && passport.getLanguage() != null) {
                locale = Locale.forLanguageTag(passport.getLanguage().toString().toLowerCase().replace("_", "-"));
            }

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            MessageInterpolator interpolator = new ValidationUtil.LocalizedMessageInterpolator(factory.getMessageInterpolator(), locale);
            Validator validator = factory.usingContext().messageInterpolator(interpolator).getValidator();
            Set<ConstraintViolation<BaseRequest>> constraintViolations = validator.validate(request, new Class[0]);
            if (constraintViolations.size() > 0) {
                Iterator var8 = constraintViolations.iterator();

                while(var8.hasNext()) {
                    ConstraintViolation<BaseRequest> violation = (ConstraintViolation)var8.next();
                    response.addError(ErrorType.INVALID_PARAMETER, violation.getMessage());
                }
            }

            return response;
        }
    }

    public static class LocalizedMessageInterpolator implements MessageInterpolator {
        private MessageInterpolator defaultInterpolator;
        private Locale defaultLocale;

        public LocalizedMessageInterpolator(MessageInterpolator interpolator, Locale locale) {
            this.defaultLocale = locale;
            this.defaultInterpolator = interpolator;
        }

        public String interpolate(String message, Context context) {
            return this.defaultInterpolator.interpolate(message, context, this.defaultLocale);
        }

        public String interpolate(String message, Context context, Locale locale) {
            return this.defaultInterpolator.interpolate(message, context, locale);
        }
    }
}
