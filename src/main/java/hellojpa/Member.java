package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *  Entity, Id는 javax에 있는 클래스이고 표준
 */
@Entity
public class Member {

    @Id // PK
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
