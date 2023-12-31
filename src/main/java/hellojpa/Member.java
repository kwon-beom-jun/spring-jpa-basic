package hellojpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *  @Entity, @Id는 javax에 있는 클래스이고 표준
 */
@Entity
// @Table(name = "") 테이블 이름 지정 가능
public class Member {

    @Id // PK
    private Long id;
    @Column(name = "name") // 컬럼 이름 지정 가능
    private String userName;

    // JPA는 기본적으로 내부에서 리플렉션 등 동적으로 객체를 생성해야 하기 때문에 기본 생성자가 있어야함
    public Member() {}

    public Member(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
