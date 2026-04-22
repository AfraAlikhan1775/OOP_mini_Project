package com.model;

public class Notice {

    private int noticeId;
    private String title;
    private String content;
    private String category;
    private String targetRole;
    private String postedBy;
    private String postedDate;

    public Notice() {
    }

    public Notice(String title, String content, String category,
                  String targetRole, String postedBy) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.targetRole = targetRole;
        this.postedBy = postedBy;
    }

    public Notice(int noticeId, String title, String content, String category,
                  String targetRole, String postedBy, String postedDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.targetRole = targetRole;
        this.postedBy = postedBy;
        this.postedDate = postedDate;
    }

    public int getNoticeId() { return noticeId; }
    public void setNoticeId(int noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    @Override
    public String toString() {
        return "Notice{" +
                "noticeId=" + noticeId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", targetRole='" + targetRole + '\'' +
                ", postedBy='" + postedBy + '\'' +
                ", postedDate='" + postedDate + '\'' +
                '}';
    }
}
