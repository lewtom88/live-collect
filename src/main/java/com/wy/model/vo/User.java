package com.wy.model.vo;

public class User {
    private Integer id;

    private String principalId;

    private String name;

    private String contactId;

    private String contactNick;

    private String contactType;

    //亲密程度
    private Integer intimacyLevel;

    //粉丝群亲密程度？
    private Integer fansGroupIntimacyLevel;

    //??
    private Integer wealthGrade;

    //粉丝灯牌
    private String badgeKey;

    private Long createTime;

    private Long updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactNick() {
        return contactNick;
    }

    public void setContactNick(String contactNick) {
        this.contactNick = contactNick;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public Integer getIntimacyLevel() {
        return intimacyLevel;
    }

    public void setIntimacyLevel(Integer intimacyLevel) {
        this.intimacyLevel = intimacyLevel;
    }

    public Integer getFansGroupIntimacyLevel() {
        return fansGroupIntimacyLevel;
    }

    public void setFansGroupIntimacyLevel(Integer fansGroupIntimacyLevel) {
        this.fansGroupIntimacyLevel = fansGroupIntimacyLevel;
    }

    public Integer getWealthGrade() {
        return wealthGrade;
    }

    public void setWealthGrade(Integer wealthGrade) {
        this.wealthGrade = wealthGrade;
    }

    public String getBadgeKey() {
        return badgeKey;
    }

    public void setBadgeKey(String badgeKey) {
        this.badgeKey = badgeKey;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
