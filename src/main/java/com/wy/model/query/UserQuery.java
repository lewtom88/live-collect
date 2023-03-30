package com.wy.model.query;

public class UserQuery extends SortPaging {
    private Integer id;
    private String principalId;
    private String name;
    private String contactType;
    private String contactId;
    private Integer intimacyLevel;
    private Integer fansGroupIntimacyLevel;
    private Integer wealthGrade;
    private String badgeKey;
    private String keyword;

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

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
