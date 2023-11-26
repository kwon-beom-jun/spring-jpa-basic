package hellojpa;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *  필드와 컬럼 매핑
 */
@Entity
public class MemberFldCol {

    // PK
    @Id
    private Long id;

    // 컬럼 "name"과 매핑
    @Column(name = "name")
    private String username;

    private Integer age;

    // enum 타입 사용
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    // DATE, TIME, TIMESTAMP
    // DB는 날짜, 시간, 날짜+시간 을 구분해서 사용하므로 매핑 정보를 제공해야 함
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    // 최근 하이버네이트 지원 기능
    private LocalDateTime lastModifiedDate;

    // 큰 데이터 값
    // @Lob에 String이면 기본적으로 CLOB으로 생성
    @Lob
    private String description;

    // DB와 매핑하지 않음
    @Transient
    private int temp;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
