package me.core.point;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
public class ExpiredPointSummary {
    private String userId;
    private BigInteger amount;
    private LocalDate expiredDate;

    @QueryProjection
    public ExpiredPointSummary(String userId, BigInteger amount, LocalDate expiredDate) {
        this.userId = userId;
        this.amount = amount;
        this.expiredDate = expiredDate;
    }
}
