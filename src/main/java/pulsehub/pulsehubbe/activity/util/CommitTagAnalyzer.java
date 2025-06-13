package pulsehub.pulsehubbe.activity.util;

import pulsehub.pulsehubbe.activity.dto.CommitTag;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class CommitTagAnalyzer {

    public static List<CommitTag> analyzeTags(
            Map<LocalDate, Integer> dailyCommitCounts,
            Map<Integer, Integer> hourlyCommitCounts
    ) {
        List<CommitTag> result = new ArrayList<>();

        int peakHour = getPeakHour(hourlyCommitCounts);
        int totalCommits = hourlyCommitCounts.values().stream().mapToInt(i -> i).sum();

        // 야행성 타입
        if (peakHour >= 21 || peakHour <= 2) {
            result.add(new CommitTag("NIGHT_OWL", "야행성 타입", "커밋 최다 시간대가 21시~2시예요."));
        }

        // 아침형 인간
        if (peakHour >= 5 && peakHour <= 10) {
            result.add(new CommitTag("MORNING_TYPE", "아침형 인간", "아침 시간대에 커밋을 많이 했어요!"));
        }

        // 업무 시간 커밋러
        int workCommits = 0;
        for (int h = 9; h <= 18; h++) {
            workCommits += hourlyCommitCounts.getOrDefault(h, 0);
        }
        if (totalCommits > 0 && workCommits >= totalCommits * 0.7) {
            result.add(new CommitTag("WORK_HOUR", "업무 시간 커밋러", "커밋 대부분이 9~18시에 있어요."));
        }

        // 야근 잦음
        int lateNight = 0;
        for (int h = 0; h <= 2; h++) {
            lateNight += hourlyCommitCounts.getOrDefault(h, 0);
        }
        if (totalCommits > 0 && lateNight >= totalCommits * 0.1) {
            result.add(new CommitTag("NIGHT_WORKER", "야근 잦음", "심야 시간(0~2시)에 커밋이 많아요."));
        }

        // 주말 개발자
        int weekendCommits = 0;
        int allCommits = 0;
        for (Map.Entry<LocalDate, Integer> entry : dailyCommitCounts.entrySet()) {
            DayOfWeek day = entry.getKey().getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendCommits += entry.getValue();
            }
            allCommits += entry.getValue();
        }
        if (allCommits > 0 && weekendCommits >= allCommits * 0.4) {
            result.add(new CommitTag("WEEKEND_DEV", "주말 개발자", "주말 커밋 비율이 높아요!"));
        }

        // 꾸준한 커밋러
        long activeDays = dailyCommitCounts.values().stream().filter(i -> i > 0).count();
        if (dailyCommitCounts.size() > 0 && activeDays >= dailyCommitCounts.size() * 0.8) {
            result.add(new CommitTag("CONSISTENT", "꾸준한 커밋러", "거의 매일 커밋했어요."));
        }

        // 폭발형 커밋러
        int max = dailyCommitCounts.values().stream().mapToInt(i -> i).max().orElse(0);
        long burstDays = dailyCommitCounts.values().stream().filter(i -> i == max).count();
        if (dailyCommitCounts.size() > 0 && burstDays >= dailyCommitCounts.size() * 0.2) {
            result.add(new CommitTag("BURSTY", "폭발형 커밋러", "특정 날에 몰아서 커밋했어요."));
        }

        return result;
    }

    private static int getPeakHour(Map<Integer, Integer> hourly) {
        return hourly.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
    }
}
