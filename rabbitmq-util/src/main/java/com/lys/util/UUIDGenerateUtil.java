package com.lys.util;

import cn.hutool.core.date.DateUtil;
import com.lys.constant.CommonConsts;
import com.lys.constant.KeywordConsts;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * UUIDGenerateUtil
 *
 * @author lys
 * @date 2022-05-18
 */
public class UUIDGenerateUtil {

    private UUIDGenerateUtil() {

    }

    /**
     * 指定分隔符的UUID
     *
     * @param spliter 指定分隔符
     * @return UUID
     */
    public static String randomUuid(String spliter) {

        String randomUuid = UUID.randomUUID().toString();
        if (KeywordConsts.DASH.equals(spliter)) {
            return randomUuid;
        }
        StringBuffer strBuf = new StringBuffer(randomUuid.substring(0, 8));
        strBuf.append(randomUuid.substring(9, 13)).append(spliter).append(randomUuid.substring(14, 18)).append(spliter)
            .append(randomUuid.substring(19, 23)).append(spliter).append(randomUuid.substring(24));
        return strBuf.toString();

    }

    /**
     * 不带分隔符的UUID
     *
     * @return
     */
    public static String randomUuid() {

        return randomUuid(StringUtils.EMPTY);

    }

    /**
     * 生成32位唯一uniqueId: idValue + 年月日时分秒 + uuid 截断32位，idValue + 年月日时分秒不超过24位，超过截断
     * @param idValue id值
     * @return 32位唯一uniqueId
     */
    public static String buildUniqueId(Object idValue) {
        String uuid = randomUuid();
        String timeStr = DateUtil.date().toString(CommonConsts.SHORT_DATETIME_PATTERN);
        String uniqueId = idValue + KeywordConsts.UNDERLINE + timeStr;
        uniqueId = uniqueId.substring(0, Math.min(uniqueId.length(), 24));
        uniqueId = uniqueId + KeywordConsts.UNDERLINE + uuid;
        uniqueId = uniqueId.substring(0, Math.min(uniqueId.length(), 32));
        return uniqueId;
    }

}
