package com.ts.server.ods.common.utils;

import org.junit.Test;

public class AESUtilsTest {

    @Test
    public void testDecrypt(){
        String c = "U2FsdGVkX1/R94Qys7XLvqs8m5F/qZPAxPaT8rpO6fPOXyPZnkJ+fnwqSkUgmOzN3TIEqnymBgJrLae0u4RqXMvAv+O1V+PkdFAqPP4Kcjv5MbHuOS+Aa8JbvzaD5zoOxjNDn99939YNSNWPr2MKudsQgKtc093q2zVcyGD+1kU4gBMXLHO3tMXdAfFt9PZURtGLL4KSt7yn/uqGpB86+ScgkWJ22n38vTtZsocmc681CxEHNXyEsia3mtN7XXglrp/1ovxk24WlfKRXdGxAsA==";
        String key = "68da34b968da34b9";

        String content = AESUtils.decrypt(c, key);

        System.out.println(content);
    }
}
