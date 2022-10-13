package me.core.point.wallet;

import lombok.*;
import me.core.point.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PointWallet extends IdEntity {

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    @Column(name = "amount",columnDefinition = "BIGINT")
    @Setter
    private BigInteger amount;

    public void minusAmount(BigInteger amount) {
        this.amount=this.amount.subtract(amount);
    }
    public void plusAmount(BigInteger amount) {
        this.amount = this.amount.add(amount);
        System.out.println("now amount" + amount + " wallet amount" + this.amount);

    }

}
