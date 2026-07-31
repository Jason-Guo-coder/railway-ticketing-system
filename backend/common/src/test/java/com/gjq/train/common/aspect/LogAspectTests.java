package com.gjq.train.common.aspect;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogAspectTests {

    @Test
    void shouldKeepMaskedMobileInRequestArguments() {
        Object[] arguments = {new MobileArgument("13900001234")};

        String json = JSONObject.toJSONString(
                arguments,
                LogAspect.createSensitiveValueFilter()
        );

        assertEquals(
                "[{\"mobile\":\"139****1234\"}]",
                json
        );
    }

    @Test
    void shouldMaskShortMobileCompletely() {
        assertEquals("****", LogAspect.maskMobile("123456"));
    }

    @Test
    void shouldMaskVerificationCode() {
        assertEquals(
                "****",
                LogAspect.createSensitiveValueFilter()
                        .process(null, "code", "8888")
        );
    }

    private static class MobileArgument {

        private final String mobile;

        private MobileArgument(String mobile) {
            this.mobile = mobile;
        }

        public String getMobile() {
            return mobile;
        }
    }
}
