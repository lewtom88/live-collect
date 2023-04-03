package com.wy.model.vo;

public class GameRound {

    private Integer gameId;
    private Integer userCount;
    private String gameResult;
    private String player;
    private Integer kill;
    private Integer death;
    private Integer assist;
    private String playMode;
    private String bonusMode;
    private Integer bonusAmount;
    private Long createTime;
    private Long updateTime;
    private String gameUsers;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public String getGameResult() {
        return gameResult;
    }

    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Integer getKill() {
        return kill;
    }

    public void setKill(Integer kill) {
        this.kill = kill;
    }

    public Integer getDeath() {
        return death;
    }

    public void setDeath(Integer death) {
        this.death = death;
    }

    public Integer getAssist() {
        return assist;
    }

    public void setAssist(Integer assist) {
        this.assist = assist;
    }

    public String getPlayMode() {
        return playMode;
    }

    public void setPlayMode(String playMode) {
        this.playMode = playMode;
    }

    public String getBonusMode() {
        return bonusMode;
    }

    public void setBonusMode(String bonusMode) {
        this.bonusMode = bonusMode;
    }

    public Integer getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(Integer bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getGameUsers() {
        return gameUsers;
    }

    public void setGameUsers(String gameUsers) {
        this.gameUsers = gameUsers;
    }
}
