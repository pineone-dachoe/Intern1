package com.example.mypage;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

public class DownloadManager {

    private DownloadStatusListener downloadStatusListener = null;

    private ArrayList<DownloadDto> dataList = new ArrayList<>();
    private LinkedList<DownloadDto> downloadQueue = new LinkedList<>();

    DownloadDto recentDownload = null;

    public static final String ADDED = "ADDED"; // 다운로드 상태 (다운로드 대기중) 코드
    public static final String PAUSED = "PAUSED"; // 다운로드 상태 (다운로드 일시중지) 코드
    public static final String PROGRESS = "PROGRESS"; // 다운로드 상태 (다운로드 중) 코드
    public static final String COMPLETED = "COMPLETED"; // 다운로드 상태 (다운로드 완료) 코드
    public static final String DELETED = "DELETED"; // 다운로드 상태 (다운로드 삭제) 코드

    private static class DownloadManagerHolder {
        public static final DownloadManager INSTANCE = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return DownloadManager.DownloadManagerHolder.INSTANCE;
    }

    public void activeDownloadThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Log.d("DownloadThread", "isActive");

                        if (recentDownload != null) {
                            download(recentDownload);
                        } else {
                            for (DownloadDto downloadDto : downloadQueue) {
                                if (TextUtils.equals(ADDED, downloadDto.getStatus())
                                        || TextUtils.equals(PROGRESS, downloadDto.getStatus())) {
                                    download(downloadDto);
                                    break;
                                }
                            }
                        }

                        Thread.sleep(500);
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    private void download(DownloadDto downloadDto) {

        int progress = downloadDto.getProgress();

        if (progress < 100) {
            downloadDto.setProgress(progress + 5);
            downloadDto.setStatus(PROGRESS);
            recentDownload = downloadDto;
            onCallBack(downloadDto, PROGRESS);
        } else {
            downloadDto.setStatus(COMPLETED);
            recentDownload = null;
            onCallBack(downloadDto, COMPLETED);
        }

        Log.d("===Thread | ", downloadDto.toString());
    }

    public ArrayList<DownloadDto> getList() {
        ArrayList<DownloadDto> result = new ArrayList<>();
        for (DownloadDto downloadDto : downloadQueue) {
            result.add(new DownloadDto(
                    downloadDto.getContId(),
                    downloadDto.getTy1Code(),
                    downloadDto.getContNm(),
                    downloadDto.getIsAdultCont(),
                    downloadDto.getPosterUrl(),
                    downloadDto.getStatus(),
                    downloadDto.getProgress()
            ));
        }

        return result;
    }

    public boolean addDownload() {
        if (dataList.size() == 0)
            return false;

        DownloadDto dto = dataList.remove(0);
        downloadQueue.add(dto);
        onCallBack(dto, ADDED);

        Log.d("DownloadManager", "addDownload " + downloadQueue.size() + ", " + dataList.size());
        Log.d("DownloadManager", "addDTO | " + dto.toString());

        return true;
    }

    public boolean removeDownload(DownloadDto downloadDto) {

        boolean result;

        if (recentDownload != null && recentDownload.getContId() != null)
            if (TextUtils.equals(recentDownload.getContId(), downloadDto.getContId()))
                recentDownload = null;

        result = downloadQueue.remove(downloadDto);

        if (result) {
            dataList.add(new DownloadDto(
                    downloadDto.getContId(),
                    downloadDto.getTy1Code(),
                    downloadDto.getContNm(),
                    downloadDto.getIsAdultCont(),
                    downloadDto.getPosterUrl()
            ));
            downloadDto.setStatus(DELETED);
            onCallBack(downloadDto, DELETED);
        }

        return result;
    }

    public boolean removeAll() {
        recentDownload = null;
        downloadQueue.clear();
        initDataList();
        return true;
    }

    public void startDownload(DownloadDto downloadDto) {

        int index = downloadQueue.indexOf(downloadDto);

        if (index == -1)
            return;

        DownloadDto downloadingDto = downloadQueue.get(index);

        if (!TextUtils.equals(PAUSED, downloadingDto.getStatus()))
            return;

        downloadingDto.setStatus(ADDED);

        downloadQueue.set(index, downloadingDto);

        onCallBack(downloadingDto, ADDED);
    }

    public void pauseDownload(DownloadDto downloadDto) {

        int index = downloadQueue.indexOf(downloadDto);

        if (index == -1)
            return;

        DownloadDto downloadingDto = downloadQueue.get(index);

        if (TextUtils.equals(PAUSED, downloadingDto.getStatus())
                || TextUtils.equals(COMPLETED, downloadingDto.getStatus())
                || TextUtils.equals(DELETED, downloadingDto.getStatus())) {
            return;
        }

        if (recentDownload != null && recentDownload.getContId() != null)
            if (TextUtils.equals(recentDownload.getContId(), downloadDto.getContId()))
                recentDownload = null;

        downloadingDto.setStatus(PAUSED);

        downloadQueue.set(index, downloadingDto);

        onCallBack(downloadingDto, PAUSED);
    }

    public void addDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        this.downloadStatusListener = downloadStatusListener;
    }

    public void removeDownloadStatusListener() {
        this.downloadStatusListener = null;
    }

    private void onCallBack(DownloadDto downloadDto, String status) {

        if (downloadStatusListener == null)
            return;

        switch (status) {
            case ADDED:
                downloadStatusListener.onAdded(downloadDto);
                break;

            case PAUSED:
                downloadStatusListener.onPaused(downloadDto);
                break;

            case PROGRESS:
                downloadStatusListener.onProgress(downloadDto);
                break;

            case COMPLETED:
                downloadStatusListener.onCompleted(downloadDto);
                break;

            case DELETED:
                downloadStatusListener.onDeleted(downloadDto);
                break;

            default:
        }
    }

    public void initDataList() {
        dataList.clear();
        dataList.add(new DownloadDto("CO20225000005559", "VR", "비투비영상테스트", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/VR/202203/2022031510241220058_7.png"));
        dataList.add(new DownloadDto("PR2019030400020001", "ETC", "3대 폭포 제목길게~제목길게~제목길게~제목길게~제목길게~제목길게~제목길", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/ETC/202111/20211115174747802_0.jpg"));
        dataList.add(new DownloadDto("CO20215000001791", "ETC", "AR Sniper", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/ETC/202112/20211213135158867_0.png"));
        dataList.add(new DownloadDto("CG20215000000064", "ETC", "손나은스타데이트시리즈", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT_GRP/202111/20211116202348913_0.jpg"));
        dataList.add(new DownloadDto("CO20225000008982", "VR", "IVE - LOVE DIVE (DRM X)", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/VR/202205/2022053009245744759_0.png"));
        dataList.add(new DownloadDto("M0120AQ259PPV00", "VR", "SUPER 3 Lamborghini Huracan 1화", true, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M0120AQ259PPV00MD130.png"));
        dataList.add(new DownloadDto("CO20215000000230", "ETC", "WEB링크형테스트이거제목길게~길게~길게~길게~길게~길게~길게~길게~길게~", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/ETC/202108/20210819204113201_8.png"));
        dataList.add(new DownloadDto("CO20225000003465", "ETC", "[여행이온다]맛이온다1편", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/VRVP/202201/2022012016243857955_2.png"));
        dataList.add(new DownloadDto("PR2019030400050001", "ETC", "이집트", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/ETC/202203/20220323111013140_0.jpeg"));
        dataList.add(new DownloadDto("CG20225000000233", "ETC", "영상종류별로시리즈물", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT_GRP/202203/20220317142003703_0.png"));
        dataList.add(new DownloadDto("CO20215000002079", "LB", "인앱과쿠폰", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/LBVR/202112/20211221175532231_7.jpg"));
        dataList.add(new DownloadDto("CO20215000000944", "ETC", "극한캐치볼", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/ETC/202111/20211115110548827_0.png"));
        dataList.add(new DownloadDto("M01194B540PPV00", "VR", "모모랜드 - I`m so hot  (더쇼 182회)", true, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M01194B540PPV00M6230.png"));
        dataList.add(new DownloadDto("CO13063623128242", "AR", "뽀로로와공룡여행3", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/AR/202203/20220329131551406_0.png"));
        dataList.add(new DownloadDto("M01217N165PPV00", "VR", "브룩에버스의촬영장비하인드", false, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M01217N165PPV00MD130.png"));
        dataList.add(new DownloadDto("CG20225000000213", "ETC", "파노라마 묶음", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT_GRP/202201/20220116150548998_0.jpg"));
        dataList.add(new DownloadDto("M012032275PPV00", "VR", "청하, 청하를 말하다 2화", false, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M012032275PPV00MD130.png"));
        dataList.add(new DownloadDto("PR2019030400090001", "ETC", "네팔 에베레스트", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/ETC/202111/20211115180617540_0.jpg"));
        dataList.add(new DownloadDto("CO13076626312634", "AR", "돌아온 홀맨 감정티콘 5", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/AR/202203/20220317163840653_0.png"));
        dataList.add(new DownloadDto("M0120AU156PPV00", "VR", "EXO 카이의 Vacation", false, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M0120AU156PPV00MD130.png"));
        dataList.add(new DownloadDto("CO20225000003762", "VR", "IVE - ELEVEN (더쇼 284회)_언어테스트", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/VR/202201/2022012514243254705_1.jpeg"));
        dataList.add(new DownloadDto("WT2020031200020009", "ETC", "[유미의세포들(완결)]신순록편6", false, "https://d1szs72hycmioq.cloudfront.net/service/MIG/VR/service/WT/WT2020031200020009.IMG.1.jpg"));
        dataList.add(new DownloadDto("CO20215000000911", "VR", "NCT127 버킷리스트 - 태용/마크", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/VR/202111/2021110513244329018_0.jpg"));
        dataList.add(new DownloadDto("CO20215000002197", "LB", "유료4 (복합) 제목변경", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/LBVR/202112/20211224112900665_4.jpg"));
        dataList.add(new DownloadDto("WT2019030400040001", "ETC", "옥수역 귀신 제목 길게제목 길게제목 길게제목 길게제목 길게제목 길게제", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/ETC/202112/20211230103830902_0.jpeg"));
        dataList.add(new DownloadDto("M01193L183PPV00", "VR", "사랑스런신재은1화", false, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M01193L183PPV00MD230.png"));
        dataList.add(new DownloadDto("M01213J224PPV00", "VR", "서울스프링실내악콘서트3화", false, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M01213J224PPV00MD130.png"));
        dataList.add(new DownloadDto("CO20215000002193", "LB", "유료1 (인앱)", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/LBVR/202112/20211224112322812_3.jpg"));
        dataList.add(new DownloadDto("CG20215000000066", "AR", "AR 묶음 컨텐츠 그룹", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT_GRP/202111/20211118105328973_0.png"));
        dataList.add(new DownloadDto("CG20225000000223", "AR", "AR 묶음 혼재", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT_GRP/202202/20220223175043078_0.jpeg"));
        dataList.add(new DownloadDto("CO20215000000361", "VR", "유료1~제목길게~제목길게~제목길게~제목길게~제목길게~제목길게(인앱 only)~~", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/VR/202109/2021090315241901112_2.jpg"));
        dataList.add(new DownloadDto("WT201908190001", "ETC", "타인은 지옥이다(완결) 2줄로 넘어가게 이름 길게길게 하겠습니다. 타인은", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT_GRP/202111/20211119181000716_0.jpg"));
        dataList.add(new DownloadDto("CO20215000001265", "AR", "통통이 댄스댄스댄스", false, "https://d1szs72hycmioq.cloudfront.net/service/CONT/AR/202112/2021120615245040805_5.png"));
        dataList.add(new DownloadDto("CO20215000002073", "LB", "방송시작 전_무료(유료로 잠시만)", true, "https://d1szs72hycmioq.cloudfront.net/service/CONT/LBVR/202112/20211221163029779_5.jpeg"));
        dataList.add(new DownloadDto("M012163584PPV00", "VR", "브레이브걸스-운전만해(더쇼259회)", false, "https://d1szs72hycmioq.cloudfront.net/service/MIG/BC/poster/M012163584PPV00MD130.png"));
    }
}
