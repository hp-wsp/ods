package com.ts.server.ods;

import com.ts.server.ods.common.utils.ZhCnUtils;
import org.junit.Assert;
import org.junit.Test;

public class ZhCnUtilsTest {

    @Test
    public void testZhCn(){
        String a = ZhCnUtils.toChinese(156);
        Assert.assertEquals(a, "一百五十六");
        a = ZhCnUtils.toChinese(10);
        Assert.assertEquals(a, "十");
        a = ZhCnUtils.toChinese(0);
        Assert.assertEquals(a, "零");
        a = ZhCnUtils.toChinese(101);
        Assert.assertEquals(a, "一百零一");
    }
}
