package com.gjq.train.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.util.JwtUtil;
import com.gjq.train.member.entity.Member;
import com.gjq.train.member.mapper.MemberMapper;
import com.gjq.train.member.req.MemberLoginReq;
import com.gjq.train.member.req.MemberRegisterReq;
import com.gjq.train.member.resp.MemberLoginResp;
import com.gjq.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private static final Logger LOG =
            LoggerFactory.getLogger(MemberServiceImpl.class);

    private static final String LOGIN_CODE = "8888";

    @Resource
    private MemberMapper memberMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void register(MemberRegisterReq memberRegisterReq) {
        String mobile = memberRegisterReq.getMobile();
        long count = memberMapper.selectCount(
                new LambdaQueryWrapper<Member>().eq(Member::getMobile, mobile)
        );

        // 未查询到手机号count=0 直接注册
        if (count == 0) {
            Member member = new Member();
            member.setMobile(mobile);
            memberMapper.insert(member);
            LOG.info("手机号不存在，自动注册会员");
        } else {
            LOG.info("手机号已经注册");
        }

        // 生成验证码
        String code = LOGIN_CODE;
        LOG.info("生成短信验证码：{}", code);

        // 保存短信记录表：手机号 短信验证码 有效期 是否已经使用 业务类型 发送时间 使用时间
        //TODO 真实项目中需要开发，本项目不开发。
        LOG.info("保存短信记录表");

        // 对接短信通道 发动短信
        //TODO 真实项目中需要开发，本项目不开发。
        LOG.info("对接短信通道");
    }

    @Override
    public MemberLoginResp login(MemberLoginReq memberLoginReq) {
        // 根据手机号查询会员
        List<Member> members = memberMapper.selectList(
                new LambdaQueryWrapper<Member>()
                        .eq(Member::getMobile, memberLoginReq.getMobile())
        );
        if (members == null || members.isEmpty()) {
            throw new BusinessException(
                    BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST
            );
        }
        Member member = members.get(0);

        // 校验登录验证码
        if (!LOGIN_CODE.equals(memberLoginReq.getCode())) {
            throw new BusinessException(
                    BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR
            );
        }

        MemberLoginResp response = new MemberLoginResp();
        response.setId(member.getId());
        response.setMobile(member.getMobile());

        // 生成包含会员信息且1小时有效的JWT
        response.setToken(JwtUtil.createToken(
                response.getId(),
                response.getMobile(),
                jwtSecret
        ));
        return response;
    }
}
