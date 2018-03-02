package com.zcj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexUtils {

    private static final Pattern MOBILE = Pattern.compile("^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$");
    private static final Pattern NUM = Pattern.compile("^[0-9]+$");
    private static final Pattern DATE = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");

    /** 地区编码 */
    private static final Map<String, String> AREA_CODE = new HashMap<>();

    static {
        AREA_CODE.put("11", "北京");
        AREA_CODE.put("12", "天津");
        AREA_CODE.put("13", "河北");
        AREA_CODE.put("14", "山西");
        AREA_CODE.put("15", "内蒙古");
        AREA_CODE.put("21", "辽宁");
        AREA_CODE.put("22", "吉林");
        AREA_CODE.put("23", "黑龙江");
        AREA_CODE.put("31", "上海");
        AREA_CODE.put("32", "江苏");
        AREA_CODE.put("33", "浙江");
        AREA_CODE.put("34", "安徽");
        AREA_CODE.put("35", "福建");
        AREA_CODE.put("36", "江西");
        AREA_CODE.put("37", "山东");
        AREA_CODE.put("41", "河南");
        AREA_CODE.put("42", "湖北");
        AREA_CODE.put("43", "湖南");
        AREA_CODE.put("44", "广东");
        AREA_CODE.put("45", "广西");
        AREA_CODE.put("46", "海南");
        AREA_CODE.put("50", "重庆");
        AREA_CODE.put("51", "四川");
        AREA_CODE.put("52", "贵州");
        AREA_CODE.put("53", "云南");
        AREA_CODE.put("54", "西藏");
        AREA_CODE.put("61", "陕西");
        AREA_CODE.put("62", "甘肃");
        AREA_CODE.put("63", "青海");
        AREA_CODE.put("64", "宁夏");
        AREA_CODE.put("65", "新疆");
        AREA_CODE.put("71", "台湾");
        AREA_CODE.put("81", "香港");
        AREA_CODE.put("82", "澳门");
        AREA_CODE.put("91", "国外");
    }

    /**
     * 身份证的有效验证
     *
     * @param idcard 身份证号
     * @return 有效返回null; 无效返回错误信息
     */
    public static String validateIdcard(String idcard) {
        /*---------------------------------------- 身份证验证开始 ----------------------------------------*/
        /*
         * 身份证号码验证
         * 1、号码的结构：公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
         * 2、地址码（前六位数）：表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。
         * 3、出生日期码（第七位至十四位）：表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。
         * 4、顺序码（第十五位至十七位）：表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配给女性。
         * 5、校验码（第十八位数）：
         * (1)十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
         * -- Ai:表示第i位置上的身份证号码数字值
         * -- Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
         * (2)计算模 Y = mod(S, 11)
         * (3)通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10
         * -- 校验码: 1 0 X 9 8 7 6 5 4 3 2
         */
        String[] valCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        String Ai;
        // ================ 号码的长度: 15位或18位 ================
        if (idcard.length() != 15 && idcard.length() != 18) {
            return "身份证号码长度应该为15位或18位";
        }
        // =======================(end)========================

        // ================ 数字: 除最后一位都为数字 ================
        if (idcard.length() == 18) {
            Ai = idcard.substring(0, 17);
        } else {
            Ai = idcard.substring(0, 6) + "19" + idcard.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
            return "身份证15位号码都应为数字; 18位号码除最后一位外，都应为数字";
        }
        // =======================(end)========================

        // ================ 地区码是否有效 ================
        if (AREA_CODE.get(Ai.substring(0, 2)) == null) {
            return "身份证地区编码错误";
        }
        // =======================(end)=======================

        // ================ 出生年月是否有效 ================
        String year = Ai.substring(6, 10);// 年份
        String month = Ai.substring(10, 12);// 月份
        String day = Ai.substring(12, 14);// 日期
        String date = year + "-" + month + "-" + day;
        if (!isDate(date)) {
            return "身份证生日无效";
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(year)) > 150
                || (gc.getTime().getTime() - sdf.parse(date).getTime()) < 0) {
                return "身份证生日不在有效范围";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (Integer.parseInt(month) > 12 || Integer.parseInt(month) == 0) {
            return "身份证月份无效";
        }
        if (Integer.parseInt(day) > 31 || Integer.parseInt(day) == 0) {
            return "身份证日期无效";
        }
        // =====================(end)=====================

        // ================ 判断最后一位的值 ================
        int totalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            totalmulAiWi = totalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = totalmulAiWi % 11;
        String verifyCode = valCodeArr[modValue];
        Ai = Ai + verifyCode;

        if (idcard.length() == 18 && !Ai.equals(idcard)) {
            return "身份证无效，不是合法的身份证号码";
        }
        // =====================(end)=====================

        return null;
    }

    /**
     * 验证手机号
     */
    public static boolean isMobile(String mobile) {
        return match(MOBILE, mobile);
    }

    /**
     * 验证是否为数字
     */
    public static boolean isNumeric(String num) {
        return match(NUM, num);
    }

    /**
     * 验证是否为日期格式 yyyy-MM-dd
     */
    public static boolean isDate(String date) {
        return match(DATE, date);
    }

    /**
     * @param pattern 编译后的正则表达式
     * @param str     要匹配的字符串
     * @return 匹配返回true, 否则返回false
     */
    private static boolean match(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


}
