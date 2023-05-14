package com.example.emotrak.dto.comment;

public class CommentDetailDtoImpl implements CommentDetailDto {
    private Long id;
    private String comment;
    private String createdAt;
    private boolean hasAuth;
    private String nickname;
    private int likesCnt;
    private boolean hasLike;
    private boolean hasReport;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean getHasAuth() {
        return hasAuth;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public int getLikesCnt() {
        return likesCnt;
    }

    @Override
    public boolean getHasLike() {
        return hasLike;
    }

    @Override
    public boolean getHasReport() {
        return hasReport;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setHasAuth(boolean hasAuth) {
        this.hasAuth = hasAuth;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setLikesCnt(int likesCnt) {
        this.likesCnt = likesCnt;
    }

    @Override
    public void setHasLike(boolean hasLike) {
        this.hasLike = hasLike;
    }

    @Override
    public void setHasReport(boolean hasReport) {
        this.hasReport = hasReport;
    }
}
