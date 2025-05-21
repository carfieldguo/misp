package com.groqdata.provider.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.groqdata.common.annotation.Excel;
import com.groqdata.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 服务提供方-账号信息对象 provider_user_info
 * 
 * @author carfield
 * @date 2025-05-21
 */
@ApiModel("服务提供方-账号信息对象")
public class ProviderUserInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @ApiModelProperty("主键ID")
    private Long id;

    /** 开放账号 */
    @Excel(name = "开放账号")
    @ApiModelProperty("开放账号")
    private String account;

    /** 密码 */
    @Excel(name = "密码")
    @ApiModelProperty("密码")
    private String password;

    /** 账号类型：个人账号还是企业账号，或者政府事业单位账号，决定了子表使用哪个 */
    @Excel(name = "账号类型：个人账号还是企业账号，或者政府事业单位账号，决定了子表使用哪个")
    @ApiModelProperty("账号类型：个人账号还是企业账号，或者政府事业单位账号，决定了子表使用哪个")
    private String accountType;

    /** 认证审核状态，只有认证了的账号才能开展业务 */
    @Excel(name = "认证审核状态，只有认证了的账号才能开展业务")
    @ApiModelProperty("认证审核状态，只有认证了的账号才能开展业务")
    private String certifyStatus;

    /** 昵称 */
    @Excel(name = "昵称")
    @ApiModelProperty("昵称")
    private String nickname;

    /** 头像地址 */
    @Excel(name = "头像地址")
    @ApiModelProperty("头像地址")
    private String avator;

    /** 手机号码，即微信号码 */
    @Excel(name = "手机号码，即微信号码")
    @ApiModelProperty("手机号码，即微信号码")
    private String mobile;

    /** 邮箱地址 */
    @Excel(name = "邮箱地址")
    @ApiModelProperty("邮箱地址")
    private String email;

    /** 账号状态 */
    @Excel(name = "账号状态")
    @ApiModelProperty("账号状态")
    private String accountStatus;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAccount(String account) 
    {
        this.account = account;
    }

    public String getAccount() 
    {
        return account;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setAccountType(String accountType) 
    {
        this.accountType = accountType;
    }

    public String getAccountType() 
    {
        return accountType;
    }
    public void setCertifyStatus(String certifyStatus) 
    {
        this.certifyStatus = certifyStatus;
    }

    public String getCertifyStatus() 
    {
        return certifyStatus;
    }
    public void setNickname(String nickname) 
    {
        this.nickname = nickname;
    }

    public String getNickname() 
    {
        return nickname;
    }
    public void setAvator(String avator) 
    {
        this.avator = avator;
    }

    public String getAvator() 
    {
        return avator;
    }
    public void setMobile(String mobile) 
    {
        this.mobile = mobile;
    }

    public String getMobile() 
    {
        return mobile;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setAccountStatus(String accountStatus) 
    {
        this.accountStatus = accountStatus;
    }

    public String getAccountStatus() 
    {
        return accountStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("account", getAccount())
            .append("password", getPassword())
            .append("accountType", getAccountType())
            .append("certifyStatus", getCertifyStatus())
            .append("nickname", getNickname())
            .append("avator", getAvator())
            .append("mobile", getMobile())
            .append("email", getEmail())
            .append("accountStatus", getAccountStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
