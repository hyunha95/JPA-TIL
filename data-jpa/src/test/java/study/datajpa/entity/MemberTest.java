package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA); // 영속성 컨텍스트에 저장
        em.persist(teamB); // 영속성 컨텍스트에 저장

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1); // 영속성 컨텍스트에 저장
        em.persist(member2); // 영속성 컨텍스트에 저장
        em.persist(member3); // 영속성 컨텍스트에 저장
        em.persist(member4); // 영속성 컨텍스트에 저장

        // 초기화
        em.flush(); // 영속성 컨텍스트에 있는 내용을 DB에 반영
        em.clear(); // 영속성 컨텍스트 초기화

        // 확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

}