package org.example.mindlab.domain.summation.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.mindlab.domain.like.QLike.like;
import static org.example.mindlab.domain.subject.QSubject.subject;
import static org.example.mindlab.domain.summation.QSummation.summation;

@Component
@RequiredArgsConstructor
public class QuerySummationRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Summation> querySummationsWithPaging(Pageable pageable, String tags, String searchTerm) {
        BooleanBuilder builder = new BooleanBuilder();

        // tags 처리
        if (tags != null && !tags.isBlank()) {
            List<String> tagList = Arrays.asList(tags.split(","));
            builder.and(subject.name.in(tagList));
        }

        // searchTerm 처리
        if (searchTerm != null && !searchTerm.isBlank()) {
            builder.and(
                summation.title.like("%" + searchTerm + "%")
                    .or(summation.content.like("%" + searchTerm + "%"))
            );
        }

        List<Summation> summations = queryFactory.selectFrom(summation)
            .leftJoin(subject).on(subject.summation.id.eq(summation.id))
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .distinct()
            .fetch();

        long total = queryFactory.selectFrom(summation)
            .leftJoin(subject).on(subject.summation.id.eq(summation.id))
            .where(builder)
            .fetchCount();

        return new PageImpl<>(summations, pageable, total);
    }


    public Page<Summation> queryLikedSummationsWithPaging(Pageable pageable, String tags,
                                                          String searchTerm, Long userId) {
        BooleanBuilder builder = new BooleanBuilder();

        // tags 처리
        if (tags != null && !tags.isBlank()) {
            List<String> tagList = Arrays.asList(tags.split(","));
            builder.and(subject.name.in(tagList));
        }

        // searchTerm 처리
        if (searchTerm != null && !searchTerm.isBlank()) {
            builder.and(
                summation.title.like("%" + searchTerm + "%")
                    .or(summation.content.like("%" + searchTerm + "%"))
            );
        }

        List<Summation> summations = queryFactory.selectFrom(summation)
            .join(subject).on(subject.summation.id.eq(summation.id))
            .leftJoin(like).on(summation.id.eq(like.summation.id))
            .where(builder.and(like.userId.eq(userId)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .distinct()
            .fetch();

        long total = queryFactory.selectFrom(summation)
            .join(subject).on(subject.summation.id.eq(summation.id))
            .leftJoin(like).on(summation.id.eq(like.summation.id))
            .where(builder.and(like.userId.eq(userId)))
            .fetchCount();

        return new PageImpl<>(summations, pageable, total);
    }

    public Page<Summation> queryMySummationsWithPaging(Pageable pageable, String tags,
                                                       String searchTerm, Long userId) {
        BooleanBuilder builder = new BooleanBuilder();
        // tags 처리
        if (tags != null && !tags.isBlank()) {
            List<String> tagList = Arrays.asList(tags.split(","));
            builder.and(subject.name.in(tagList));
        }

        // searchTerm 처리
        if (searchTerm != null && !searchTerm.isBlank()) {
            builder.and(
                summation.title.like("%" + searchTerm + "%")
                    .or(summation.content.like("%" + searchTerm + "%"))
            );
        }

        List<Summation> summations = queryFactory.selectFrom(summation)
            .leftJoin(subject).on(subject.summation.id.eq(summation.id))
            .where(builder.and(summation.userId.eq(userId))) // todo userId 비교하는거로 변경하기(현재 id로 비교)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .distinct()
            .fetch();

        long total =  queryFactory.selectFrom(summation)
            .leftJoin(subject).on(subject.summation.id.eq(summation.id))
            .where(builder.and(summation.id.eq(userId)))
            .fetchCount();

        return new PageImpl<>(summations, pageable, total);
    }
}