package me.core.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PointCustomRepository{
    Page<ExpiredPointSummary> sumByExpiredDate(LocalDate alarmCriteriaDate, Pageable pageable);
    Page<ExpiredPointSummary> sumByExpiredSoonDate(LocalDate alarmCriteriaDate, Pageable pageable);

}
