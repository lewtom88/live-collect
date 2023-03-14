package com.wy.model.vo;

public class Comment {
    private Integer id;
    private String principalId;
    private String name;
    private String comment;
    private Integer type;
    private Integer giftId;
    private Integer comboCount;
    private Integer rank;
    private Long createTime;

    public static final int TYPE_COMMENT = 0;

    public static final int TYPE_GUESSING = 1;

    public static final int TYPE_GIFT = 2;

    public static final int TYPE_LIKE = 3;

    public static final int TYPE_CONTACT_UPDATE = 4;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public Integer getComboCount() {
        return comboCount;
    }

    public void setComboCount(Integer comboCount) {
        this.comboCount = comboCount;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
