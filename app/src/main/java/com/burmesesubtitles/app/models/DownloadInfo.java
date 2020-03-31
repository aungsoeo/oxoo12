package com.burmesesubtitles.app.models;

public class DownloadInfo {

    int downloadId;
    int notificationId;
    String fileName;

    public DownloadInfo(int downloadId, int notificationId, String fileName) {
        this.downloadId = downloadId;
        this.notificationId = notificationId;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
