/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author taigai
 * @version : HashcodeUtil.java, v 0.1 2021年05月23日 21:45 taigai Exp $
 */
class HashcodeUtil {

    /**
     * 计算哈希值
     */
    static int hashcode(String key) {
        return Math.abs(DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8)).hashCode());
    }

}