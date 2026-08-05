package com.gjq.train.member.member.service;

import com.gjq.train.member.member.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.member.member.req.MemberLoginReq;
import com.gjq.train.member.member.req.MemberRegisterReq;
import com.gjq.train.member.member.resp.MemberLoginResp;

/**
 * <p>
 * 会员 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-07-30
 */
public interface MemberService extends IService<Member> {
    void register(MemberRegisterReq memberRegisterReq);

    MemberLoginResp login(MemberLoginReq memberLoginReq);
}
