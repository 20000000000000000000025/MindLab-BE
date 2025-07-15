package org.example.mindlab.domain.subject;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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
}