/**
 * Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved.
 */
package cc.lzy.file.utils;

import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author taigai
 * @version : ConsistanceHash.java, v 0.1 2021年05月23日 21:41 taigai Exp $
 */
public class ConsistanceHash {

    private static Set<String> serverSet = new HashSet<>();

    private static HashItem[]    items;

    private static final int     BUCKET    = 128;

    private static AtomicBoolean isInited = new AtomicBoolean(false);

    private static final String  PRE_FIX   = "_COUNT_";

    private static class HashItem implements Comparable<Object> {

        private int    hashcode;

        private String server;

        public HashItem(int hashcode, String server) {
            this.hashcode = hashcode;
            this.server = server;
        }

        @Override
        public int compareTo(Object o) {
            int anotherHash = 0;
            if (o instanceof HashItem) {
                anotherHash = ((HashItem) o).hashcode;
            } else if (o instanceof Integer) {
                anotherHash = ((Integer) o).intValue();
            }
            if (this.hashcode < anotherHash) {
                return -1;
            } else if (this.hashcode > anotherHash) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    public static void init(Set<String> serverSet) {
        synchronized (ConsistanceHash.class) {
            ConsistanceHash.serverSet = serverSet;
            int curPos = 0;
            items = new HashItem[serverSet.size() * BUCKET];
            for (String server : serverSet) {
                for (int count = 0; count < BUCKET; count++) {
                    int hashcode = HashcodeUtil
                            .hashcode(server + PRE_FIX + count);
                    items[curPos] = new HashItem(hashcode, server);
                    curPos++;
                }
            }
            // 重新排序
            Arrays.sort(items);
            isInited.set(true);
        }
    }

    public static String getServer(int hashcode) {
        synchronized (ConsistanceHash.class) {
            if (!isInited.get() || CollectionUtils.isEmpty(serverSet)) {
                return null;
            }
            int index = Arrays.binarySearch(items, hashcode);
            if (index < 0) {
                index = Math.abs(index) - 1;
            }
            if (index >= items.length) {
                index = items.length - 1;
            }
            return items[index].server;
        }
    }

    public static void addServer(String server) throws UnsupportedEncodingException {
        synchronized (ConsistanceHash.class) {
            if (!isInited.get()) {
                throw new IllegalStateException("consistant hash is not init");
            }
            if (!serverSet.contains(server)) {
                serverSet.add(server);
                HashItem[] tempItems = new HashItem[serverSet.size() * BUCKET];
                int length = items.length;
                for (int count = 0; count < length; count++) {
                    tempItems[count] = items[count];
                }
                for (int count = 0; count < BUCKET; count++) {
                    int hashcode = HashcodeUtil
                            .hashcode(server + PRE_FIX + Integer.toString(count));
                    tempItems[length] = new HashItem(hashcode, server);
                    length++;
                }
                // 重新排序
                Arrays.sort(tempItems);
                items = tempItems;
            }
        }
    }

    public static void removeServer(String server) {
        synchronized (ConsistanceHash.class) {
            if (!isInited.get()) {
                throw new IllegalStateException("consistant hash is not init");
            }
            if (serverSet.contains(server)) {
                HashItem[] tempItems = new HashItem[(serverSet.size() - 1) * BUCKET];
                int index = 0;
                for (int count = 0; count < items.length; count++) {
                    if (!server.equals(items[count].server)) {
                        tempItems[index] = items[count];
                        index++;
                    }
                }
                items = tempItems;
                serverSet.remove(server);
            }
        }
    }

    public static void clear() {
        synchronized (ConsistanceHash.class) {
            items = new HashItem[0];
            Iterator<String> iterator = serverSet.iterator();
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            isInited.set(false);
        }
    }

    public static Set<String> getServerSet() {
        synchronized (ConsistanceHash.class) {
            return serverSet;
        }
    }
}