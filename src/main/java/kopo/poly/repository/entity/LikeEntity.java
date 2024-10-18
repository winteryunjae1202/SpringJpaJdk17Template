package kopo.poly.repository.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTICE_LIKES")
@DynamicInsert
@DynamicUpdate
@Builder
@Cacheable
@Entity
@IdClass(LikePK.class)
public class LikeEntity {

    @Id
    @Column(name = "notice_seq")
    private Long noticeSeq;

    @Id
    @Column(name = "user_id")
    private String userId;
}