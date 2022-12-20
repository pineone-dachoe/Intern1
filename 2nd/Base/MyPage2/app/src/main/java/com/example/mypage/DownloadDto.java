package com.example.mypage;

public class DownloadDto {

    private String contId; // 콘텐츠 ID
    private String ty1Code; // 콘텐츠 타입 코드 (AR : AR 콘텐츠, VR : VR 콘텐츠, LB : 라이브 콘텐츠)
    private String contNm; // 콘텐츠 이름
    private boolean isAdultCont; // 성인 콘텐츠 여부 (true : 성인콘텐츠, false : 일반콘텐츠)
    private String posterUrl; // 썸네일 url
    private String status; // 다운로드 상태 (ADDED : 다운로드 대기중, PAUSED : 다운로드 일시중지, PROGRESS : 다운로드 중, COMPLETED : 다운로드 완료, DELETED : 다운로드 삭제됨)
    private int progress; // 다운로드 진행률

    public DownloadDto(String contId, String ty1Code, String contNm, boolean isAdultCont, String posterUrl) {
        this.contId = contId;
        this.ty1Code = ty1Code;
        this.contNm = contNm;
        this.isAdultCont = isAdultCont;
        this.posterUrl = posterUrl;
        this.status = DownloadManager.ADDED;
        this.progress = 0;
    }

    public DownloadDto(String contId, String ty1Code, String contNm, boolean isAdultCont, String posterUrl, String status, int progress) {
        this.contId = contId;
        this.ty1Code = ty1Code;
        this.contNm = contNm;
        this.isAdultCont = isAdultCont;
        this.posterUrl = posterUrl;
        this.status = status;
        this.progress = progress;
    }

    public String getContId() {
        return contId;
    }

    public String getContNm() {
        return contNm;
    }

    public boolean getIsAdultCont() {
        return isAdultCont;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTy1Code() {
        return ty1Code;
    }

    public String getStatus() {
        return status;
    }

    public int getProgress() {
        return progress;
    }

    public void setContId(String contId) {
        this.contId = contId;
    }

    public void setContNm(String contNm) {
        this.contNm = contNm;
    }

    public void setIsAdultCont(boolean isAdultCont) {
        this.isAdultCont = isAdultCont;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setTy1Code(String ty1Code) {
        this.ty1Code = ty1Code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "DownloadDto{" +
                "contId='" + contId + '\'' +
                ", ty1Code='" + ty1Code + '\'' +
                ", contNm='" + contNm + '\'' +
                ", isAdultCont='" + isAdultCont + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", status='" + status + '\'' +
                ", progress=" + progress +
                '}';
    }
}
