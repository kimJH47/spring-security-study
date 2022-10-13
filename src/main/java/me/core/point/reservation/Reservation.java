package me.core.point.reservation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.core.point.IdEntity;
import me.core.point.wallet.PointWallet;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Reservation extends IdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_wallet_id", nullable = false)
    private PointWallet pointWallet;

    @Column(nullable = false, columnDefinition = "BIGINT")
    private BigInteger amount;
    @Column(nullable = false)
    private LocalDate earnedDate;
    @Column(nullable = false)
    private int availableDays;
    @Column(name = "is_executed", columnDefinition = "TINYINT", length = 1)
    private boolean executed;

    public Reservation(PointWallet pointWallet, BigInteger amount, LocalDate earnedDate, int availableDays) {
        this.pointWallet = pointWallet;
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.availableDays = availableDays;
        this.executed = false;
    }

    public void execute() {
        this.executed = true;
    }

    public LocalDate getExpireDate() {
        return this.earnedDate.plusDays(this.availableDays);
    }

}
