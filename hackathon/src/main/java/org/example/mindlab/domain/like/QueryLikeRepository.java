package org.example.mindlab.domain.like;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

import static org.example.mindlab.domain.like.QLike.like;

@RequiredArgsConstructor
@Component
public class QueryLikeRepository {

    private final JPAQueryFactory queryFactory;

    public List<Long> getLikeCountsByPostsId(List<Long> postsId) {
        return queryFactory
            .select(like.count())
            .from(like)
            .where(like.summation.id.in(postsId))
            .fetch();
    }
}
