package me.core.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.core.point.ExpiredPointSummary;
import me.core.point.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.format.DateTimeFormatter;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Message extends IdEntity {

    String userId;
    String title;
    @Column(name = "content",columnDefinition = "text",nullable = false)
    String content;

    public static Message expiredPointMessageInstance(ExpiredPointSummary pointSummary) {
        return new Message(
                pointSummary.getUserId(),
                String.format("%s 포인트 만료", pointSummary.getAmount()
                                                       .toString()),
                String.format("%s 기준 %s 포인트가 만료되었습니다.", pointSummary.getExpiredDate()
                                                                    .format(DateTimeFormatter.ISO_DATE), pointSummary.getAmount())
        );
    }
}
