package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * <br> TODO : JPA 구동 방식
 * <br>     1. Persistence 클래스에서 시작
 * <br>     2. persistence.xml 설정 정보를 읽어드림
 * <br>     3. 읽어드린 정보를 바탕으로 EntityManagerFactory라는 클래스를 만듬
 * <br>         3-1. EntityManagerFactory는 어플리케이션 로딩 시점에 하나만 생성 (DB당 하나씩 생성)
 * <br>     4. EntityManagerFactory에서 무언가 필요할때마다 EntityManager를 찍어내서 구동
 * <br>
 * <br> TODO : H2 데이터베이스
 * <br>     H2 데이터베이스는 "임베디드" 모드와 "서버" 모드 두 가지 주요 작동 모드가 있음
 * <br>     임베디드 모드로 실행되고 있을 때, 데이터베이스 파일은 보통 한 번에 하나의 JVM 프로세스만이 접근할 수 있음
 * <br>     ("file locking" 메커니즘)
 * <br>     (데이터의 동시 수정을 방지하고 데이터 무결성을 유지하기 위한 것)
 * <br>
 * <br>     H2 데이터베이스의 임베디드 모드에서는 데이터베이스 파일(.mv.db 확장자)을 통해
 * <br>     (예시 : test(데이터베이스이름).mv.db)
 * <br>     실제 데이터 저장 및 관리가 이루어짐. 주로 사용자 홈 디렉토리에 생성
 * <br>     실제 데이터베이스의 데이터와 구조를 저장하는 물리적 파일
 * <br>
 * <br>     임베디드(Default)
 * <br>         - 애플리케이션의 일부로 실행되며, 별도의 서버 프로세스 없이도 애플리케이션 내에서
 * <br>           직접 데이터베이스 파일에 접근
 * <br>         - 별도의 H2 데이터베이스 리스너(서버)가 실행되고 있을 필요가 없음.
 * <br>           데이터베이스는 애플리케이션 코드 내에서 직접 관리되며,
 * <br>           애플리케이션이 시작될 때 데이터베이스도 함께 시작
 * <br>         - 임베디드 모드는 주로 개발 중이거나 단일 사용자 애플리케이션에서 사용
 * <br>
 * <br>     서버 모드(원격 모드)
 * <br>         - H2 데이터베이스는 별도의 서버 프로세스로 실행
 * <br>           애플리케이션은 네트워크를 통해 데이터베이스에 연결
 * <br>         - JPA를 사용하는 애플리케이션이 H2 데이터베이스 서버 모드와 함께 작동하려면,
 * <br>           H2 서버가 실행 중이어야 하며, 애플리케이션은 이 서버에 접속하기 위한 적절한 JDBC URL을 가지고 있어야 함
 * <br>         - 이 모드는 여러 클라이언트가 동일한 데이터베이스 서버에 접근해야 하는 경우,
 * <br>           또는 실제 서버 환경에서 애플리케이션을 배포하는 경우에 적합
 * <br>
 * <br>     ※ 주의 ※
 * <br>         org.h2.mvstore.MVStoreException: The file is locked:  에러 발생 시
 * <br>         H2-DB.bat 사용하여 H2 콘솔을 사용 중 JPA를 사용하면 에러발생
 * <br>             → H2는 기본적으로 한번에 하나의 프로세스에서만 데이터베이스 파일에 접근 할 수 있도록 파일을 잠금
 * <br>             → 동시에 여러 프로세스가 데이터베이스 파일에 변경을 가할 때 데이터 손상을 방지하기 위함
 * <br>         해결
 * <br>             h2 > bin > h2.bat 실행 시 TCP 서버 모드로 실행하여 동시 사용
 * <br>             h2.bat 수정
 * <br>                 @java -cp "h2-2.2.224.jar;%H2DRIVERS%;%CLASSPATH%" org.h2.tools.Server -web -tcp -tcpAllowOthers
 * <br>                     • -web는 H2 웹 콘솔을 시작, 웹 서버 모드에서 실행
 * <br>                     • -tcp는 TCP 서버 모드를 활성화
 * <br>                     • -tcpAllowOthers는 같은 네트워크 내의 다른 컴퓨터들이 서버에 접근할 수 있도록 허용,
 * <br>                        로컬 PC에서만 사용할 경우 이 옵션은 선택
 * <br>                     • org.h2.tools.Server는 H2 서버 도구를 실행
 * <br>             persistence.xml 수정
 * <br>                 "jdbc:h2:~/test" → "jdbc:h2:tcp://192.168.1.101:9092/~/test"
 * <br>                 <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://192.168.1.101:9092/~/test"/>
 * <br>
 * <br> TODO : 실행 시 콘솔(persistence.xml 옵션)
 * <br>     hibernate.showsql : 콘솔에 쿼리가 보임
 * <br>     hibernate.format_sql : 쿼리가 포맷되어 콘솔에 보임
 * <br>     hibernate.use_sql_comments : 쿼리의 주석 부분
 * <br>
 */
public class JpaMain {
    public static void main(String[] args) {

        // Persistence 클래스에서 persistence.xml 설정 정보를 읽어드림
        // persistence.xml 설정정보에서 전달인자로 보내준 persistence-unit 네임을 조회
        // 조회된 설정을 바탕으로 EntityManagerFactory를 생성
        // EntityManagerFactory 만드는 순간 데이터베이스와 연결되고 웬만한것들이 전부 가능함
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        /**
         * <br> TODO : EntityManager
         * <br>     JPA에서는 트랜잭션 단위가 많이 중요함
         * <br>     트랜잭션(db 커넥션 얻고 쿼리를 날리고 종료되는 일관적인 단위) 단위마다 EntityManager를 만들어야함
         * <br>     JPA에서는 데이터를 변경하는 모든 작업은 트랜잭션안에서 작업을 해야함
         * <br>     EntityManager는 정말 쉽게 DB 커넥션 하나 받았다고 생각하면 편함
         * <br>
         * <br> TODO : ※ EntityManager 중요! ※
         * <br>     쓰레드간에 공유는 절대 하면 안됌 (사용 후 폐기)
         * <br>         → ex) JPA Update : 커밋 시점에 엔터티 체크하여 자동 업데이트 처리
         * <br>         →     (한 스레드에서의 변경이 다른 스레드의 작업에 영향을 줄 수 있음)
         * <br>         → ex) 스레드들이 동시에 commit을 시도할 때 트랜잭션 충돌이 발생할 수 있음
         * <br>     JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
         * <br>         → 원래 DB는 데이터 변경 자체를 트랜잭션 안에서 실행하도록 설계
         * <br>         → DB는 트랜잭션이라는 개념을 단권 커리가 올 때마다 내부적으로 처리
         * <br>         → DB는 내부적으로 트랜잭션 개념을 가지고 있음
         * <br>
         */
        EntityManager em = emf.createEntityManager();

        // 트랜잭션 단위
        // 단순 데이터 조회 같은 경우에는 트랜잭션 선언하지 않고 사용이 가능함
        EntityTransaction tx = em.getTransaction();

        // 트랜잭션 시작
        tx.begin();

        try {

            // ======================== Create Ex =========================

            /**
             * <br> TODO : JPA persist
             * <br>     persist()는 DB에 쿼리를 보내는것이 아닌 영속성 상태로 만드는것
             * <br>     쿼리를 보내는 시점은 트랙잭션(EntityTransaction)이 커밋되는 시점
             */
            Member member = new Member(1L, "HelloA");
            em.persist(member);

            // ========================= Read Ex ==========================

            /**
             * <br> TODO : JPA Read
             * <br>     위의 em.persist(member)로 인해서 1L(키) Member(값) 객체가 1차 캐시에 들어감 
             * <br>     → find 사용하여 조회시 1차 캐시에 있으므로 DB에서 조회하지 않고 1차 캐시에서 가져옴
             * <br>     ( select가 console에 출력되지 않는 이유 )
             * <br>
             * <br>     em.persist(member)를 하지 않고 em.find()시에
             * <br>     DB에서 조회해서 엔티티를 생성하고, 이를 1차 캐시에 저장
             */
            Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getUserName());
            Member findMember2 = em.find(Member.class, 1L);

//            Member findMember1 = em.find(Member.class, 2L);
//            Member findMember2 = em.find(Member.class, 2L);

            /**
             * <br> TODO : JPA Read (영속 엔티티의 동일성 보장)
             * <br>     1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭션 격리 수준을
             * <br>     데이터베이스가 아닌 어플리케이션에서 제공
             * <br>     ( JPA가 영속 엔티티의 동일성을 보장 )
             * <br>
             */
            System.out.println("findMember result == " + (findMember == findMember2));

            // ======================== Update Ex =========================

            /**
             * <br> TODO : JPA Update
             * <br>     em.persistence(저장)을 하지 않아도 됨
             * <br>     JPA 목적 중 하나인 Java Collection 처럼 이용 할 수 있게 설계되어 있어서 가능함
             * <br>     JPA를 통해서 엔티티를 가져오면 JPA가 관리를 함
             * <br>     JPA가 트랜잭션을 커밋하는 시점에 체크를 해서 바뀌었으면 업데이트 쿼리를 보냄
             * <br>     ( Delete가 있으면 실행이 되지 않는 이유 )
             */
            /*findMember.setUserName("HelloJPA");

            findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getUserName());*/

            // ========================= Jpql Ex ==========================

            /**
             * <br> TODO : Jpql이란? (Jpql이란, 예시, 등장 배경 ... 등)
             * <br>     객체를 대상으로 하는 객체지향 SQL
             * <br>     SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어
             * <br>     SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
             * <br>     JPQL은 엔티티 객체를 대상으로 쿼리 ↔ SQL은 데이터베이스 테이블을 대상으로 쿼리
             * <br>
             * <br>     장점 : 방언을 바꾸더라도 Jqpl을 변경 할 필요가 없음
             * <br>
             * <br>     Jpql 등장 배경
             * <br>         → JPA를 사용하면 엔티티 객체를 중심으로 개발
             * <br>         → 검색 쿼리
             * <br>         → 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
             * <br>           (테이블이 아닌 객체인 이유는 JPA 사상이 깨짐)
             * <br>         → 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
             * <br>         → 어플리케이션이 필요한 데이터만 DB에서 불러오면 결국 검색 조건이 포함된 SQL이 필요
             * <br>         → 테이블 대상으로 쿼리를 날리면 해당 DB에 종속적인 설계가 되어버리므로 entity 객체를
             * <br>           대상으로 쿼리를 할 수 있는 Jpql이 등장
             * <br>
             * <br>     JPA는 테이블 대상으로 코드를 작성하지 않고 객체 대상으로 코드를 작성함
             * <br>     select m from Member as m : Member 객체를 전부 가져오라는 쿼리 (콘솔(showsql)의 로그 확인)
             */
            /*List<Member> findMembers =
                    em.createQuery("select m from Member as m", Member.class)
                            // 페이징 처리
                            .setFirstResult(1) // 1번부터
                            .setMaxResults(10) // 10번까지
                            .getResultList();

            for (Member m : findMembers) {
                System.out.println("member.name ===== " + member.getUserName());
            }*/

            // ======================== Delete Ex =========================

            /*em.remove(findMember);*/

            // ============================================================

            // 트랜잭션 종료
            tx.commit();

        } catch (Exception e) {

            // 문제가 생기면 RollBack 사용
            tx.rollback();
            System.out.println("ERROR : " + e);

        } finally {

            // EntityManager가 내부적으로 데이터베이스 커넥션을 물고 동작하므로 항상 끊어줘야함
            em.close();

            // 어플리케이션이 끝나면 EntityManagerFactory를 닫아줘야함
            // 커넥션 풀링 등 내부적으로 리소스가 릴리즈됨
            emf.close();

        }
    }
}
