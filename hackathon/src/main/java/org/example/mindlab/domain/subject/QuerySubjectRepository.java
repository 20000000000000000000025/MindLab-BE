package org.example.mindlab.domain.subject;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.mindlab.domain.subject.QSubject.subject;

@Component
@RequiredArgsConstructor
public class QuerySubjectRepository {

    private final JPAQueryFactory queryFactory;

    public List<Subject> querySubjectBySummationId(Long id) {
        return queryFactory.selectFrom(subject)
                .where(subject.summation.id.eq(id))
                .fetch();
    }

    public Page<Subject> querySubjectsBySummationIdWithPaging(Long id, Pageable pageable) {
        List<Subject> subjects = queryFactory.selectFrom(subject)
                .where(subject.summation.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(subject)
                .where(subject.summation.id.eq(id))
                .fetchCount();

        return new org.springframework.data.domain.PageImpl<>(subjects, pageable, total);
    }
}