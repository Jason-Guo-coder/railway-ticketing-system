package com.gjq.train.common.context;

import com.gjq.train.common.resp.MemberLoginResp;

public final class LoginMemberContext {

    private static final ThreadLocal<MemberLoginResp> MEMBER = new ThreadLocal<>();

    private LoginMemberContext() {
    }

    public static void setMember(MemberLoginResp member) {
        MEMBER.set(member);
    }

    public static MemberLoginResp getMember() {
        return MEMBER.get();
    }

    public static Long getId() {
        MemberLoginResp member = getMember();
        return member == null ? null : member.getId();
    }

    public static void remove() {
        MEMBER.remove();
    }
}
