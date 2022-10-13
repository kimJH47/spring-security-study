package me.core.point;


import lombok.*;
import me.core.point.reservation.Reservation;
import me.core.point.wallet.PointWallet;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;


@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Point extends IdEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_wallet_id", nullable = false)
    private PointWallet pointWallet;

    @Column(name = "amount", nullable = false, columnDefinition = "BIGINT")
    private BigInteger amount;

    @Column(name = "earned_date", nullable = false)
    private LocalDate earnedDate;

    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    @Column(name = "is_used", nullable = false, columnDefinition = "TINYINT", length = 1)
    private boolean used;

    @Column(name = "is_expired", nullable = false, columnDefinition = "TINYINT", length = 1)
    @Setter
    private boolean expired;

    public Point(PointWallet pointWallet, BigInteger amount, LocalDate earnedDate, LocalDate expireDate) {
        this.pointWallet = pointWallet;
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.expireDate = expireDate;
        this.used = false;
        this.expired = false;
    }

    public void expire() {
        if (!this.used) {
            this.expired = true;
        }
    }

    public void plusAmountInWallet() {
        this.pointWallet.plusAmount(this.getAmount());
    }

    public static Point ReservationToPoint(Reservation reservation) {
        return new Point(reservation.getPointWallet(),
                reservation.getAmount(),
                reservation.getEarnedDate(),
                reservation.getExpireDate());
    }
}

