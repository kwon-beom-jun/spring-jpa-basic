package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaFldColMain {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            MemberFldCol member = new MemberFldCol();
            member.setId(1L);
            member.setUsername("A");
            member.setRoleType(RoleType.USER);

            MemberFldCol member2 = new MemberFldCol();
            member2.setId(2L);
            member2.setUsername("B");
            member2.setRoleType(RoleType.ADMIN);

            em.persist(member);
            em.persist(member2);

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            System.out.println("ERROR : " + e);
        } finally {
            em.close();
            emf.close();

        }
    }
}
