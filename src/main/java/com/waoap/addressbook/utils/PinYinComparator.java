package com.waoap.addressbook.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class PinYinComparator implements Comparator<String> {
    Collator collator = Collator.getInstance(Locale.CHINA);

    @Override
    public int compare(String o1, String o2) {
        if (o1.charAt(0) <= 'Z' && o1.charAt(0) >= 'A') {
            if (o1.equals(getFirstCharacterSpell(o2))) return -1;
        }
        if (o2.charAt(0) <= 'Z' && o2.charAt(0) >= 'A') {
            if (o2.equals(getFirstCharacterSpell(o1))) return 1;
        }
        CollationKey key1 = collator.getCollationKey(getFirstCharacterSpell(o1));
        CollationKey key2 = collator.getCollationKey(getFirstCharacterSpell(o2));
        return key1.compareTo(key2);
    }

    public static String getFirstCharacterSpell(String word) {
        if (word.length() == 0) return null;
        if (word.charAt(0) <= 255) {
            return word.substring(0, 1);
        }
        try {
            StringBuilder builder = new StringBuilder();
            char[] chars = word.toCharArray();
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            String[] temp = PinyinHelper.toHanyuPinyinStringArray(chars[0], format);
            if (temp != null) {
                builder.append(temp[0].charAt(0));
            }
            return builder.toString().replaceAll("\\W", "").trim();
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return null;
    }
}
