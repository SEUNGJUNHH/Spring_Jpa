package jpabook.jpashop.ExamClass;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTestMember is a Querydsl query type for TestMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestMember extends EntityPathBase<TestMember> {

    private static final long serialVersionUID = 814504808L;

    public static final QTestMember testMember = new QTestMember("testMember");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath username = createString("username");

    public QTestMember(String variable) {
        super(TestMember.class, forVariable(variable));
    }

    public QTestMember(Path<? extends TestMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestMember(PathMetadata metadata) {
        super(TestMember.class, metadata);
    }

}

