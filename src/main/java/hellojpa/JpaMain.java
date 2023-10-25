package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * <br> TODO : JPA 구동 방식
 * <br>     1. Persistence 클래스에서 시작
 * <br>     2. persistence.xml 설정 정보를 읽어드림
 * <br>     3. 읽어드린 정보를 바탕으로 EntityManagerFactory라는 클래스를 만듬
 * <br>         3-1. EntityManagerFactory는 어플리케이션 로딩 시점에 하나만 생성
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
 */
public class JpaMain {
    public static void main(String[] args) {

        // Persistence 클래스에서 persistence.xml 설정 정보를 읽어드림
        // persistence.xml 설정정보에서 전달인자로 보내준 persistence-unit 네임을 조회
        // 조회된 설정을 바탕으로 EntityManagerFactory를 생성
        // EntityManagerFactory 만드는 순간 데이터베이스와 연결되 다 되고 웬만한것들이 전부 가능함
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 트랜잭션(db 커넥션 얻고 쿼리를 날리고 종료되는 일관적인 단위) 단위마다 EntityManager를 만들어야함
        // JPA에서는 트랜잭션 단위가 많이 중요
        // JPA에서는 데이터를 변경하는 모든 작업은 트랜잭션안에서 작업을 해야함
        // EntityManager는 정말 쉽게 DB 커넥션 하나 받았다고 생각하면 편함
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션 시작

        // 트랜잭션 단위
        Member member = new Member();
        member.setId(2L);
        member.setName("HelloA");
        // 실행 시 콘솔(persistence.xml 옵션)
        //      hibernate.showsql : 콘솔에 쿼리가 보임
        //      hibernate.format_sql : 쿼리가 포맷되어 콘솔에 보임
        //      hibernate.use_sql_comments : 쿼리의 주석 부분
        em.persist(member);
        
        tx.commit(); // 트랜잭션 종료

        // 문제가 생기면 RollBack 사용
        // tx.rollback();

        em.close();
        // 어플리케이션이 끝나면 EntityManagerFactory를 닫아줘야함
        emf.close();
    }
}
