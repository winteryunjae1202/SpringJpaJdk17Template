package kopo.poly.repository.entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
@Builder
@NoArgsConstructor
@Getter
public class LikePK implements Serializable {

    private long noticeSeq;
    private String userId;

    public LikePK(long noticeSeq, String userId) {
        this.noticeSeq = noticeSeq;
        this.userId = userId;
    }
}