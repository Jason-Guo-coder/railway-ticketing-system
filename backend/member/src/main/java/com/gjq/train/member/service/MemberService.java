package com.gjq.train.member.service;

import com.gjq.train.member.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.member.req.MemberLoginReq;
import com.gjq.train.member.req.MemberSendCodeReq;
import com.gjq.train.member.resp.MemberLoginResp;

/**
 * <p>
 * 会员 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-07-30
 */
public interface MemberService extends IService<Member> {
    void sendCode(MemberSendCodeReq memberSendCodeReq);

    MemberLoginResp login(MemberLoginReq memberLoginReq);
}
