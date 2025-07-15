package org.example.mindlab.domain.summation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.mindlab.domain.summation.Summation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.mindlab.domain.summation.QSummation.summation;

@Component
@RequiredArgsConstructor
public class QuerySummationRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Summation> querySummationsWithPaging(Pageable pageable) {
        List<Summation> summations = queryFactory.selectFrom(summation)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(summation)
                .fetchCount();

        return new PageImpl<>(summations, pageable, total);
    }
}
