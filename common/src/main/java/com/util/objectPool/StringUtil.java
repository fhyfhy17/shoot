package com.util.objectPool;

public class StringUtil {

    public static String cutByRemovePostfix(String complete, String postfix) {
        return complete.replace(postfix, "");
    }
}
