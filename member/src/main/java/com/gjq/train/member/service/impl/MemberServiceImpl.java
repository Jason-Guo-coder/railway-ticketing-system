package com.gjq.train.member.service.impl;

import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.member.entity.Member;
import com.gjq.train.member.mapper.MemberMapper;
import com.gjq.train.member.req.MemberRegisterReq;
import com.gjq.train.member.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员 服务实现类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-07-30
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public long register(MemberRegisterReq memberRegisterReq) {
        String mobile = memberRegisterReq.getMobile();
        long count = lambdaQuery().eq(Member::getMobile, mobile).count();
        if (count > 0) {
            throw new BusinessException(
                    BusinessExceptionEnum.MEMBER_MOBILE_EXIST
            );
        }

        Member member = new Member();
        member.setMobile(mobile);

        memberMapper.insert(member);

        return member.getId();
    }
}
