package kopo.poly.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@Getter
public class CommentPK implements Serializable {

    private long commentSeq;
    private long noticeSeq;

    public CommentPK(long commentSeq, long noticeSeq) {

        this.commentSeq = commentSeq;
        this.noticeSeq = noticeSeq;
    }

}