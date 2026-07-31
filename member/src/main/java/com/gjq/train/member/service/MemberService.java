package com.gjq.train.member.service;

import com.gjq.train.member.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.member.req.MemberRegisterReq;

/**
 * <p>
 * 会员 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-07-30
 */
public interface MemberService extends IService<Member> {
    long register(MemberRegisterReq memberRegisterReq);
}
