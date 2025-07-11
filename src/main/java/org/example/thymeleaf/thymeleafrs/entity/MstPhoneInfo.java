package org.example.thymeleaf.thymeleafrs.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "mst_phone_info")
public class MstPhoneInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;
    private String name;
    private String source;

    @ElementCollection
    @CollectionTable(
            name = "mst_phone_info_tags",
            joinColumns = @JoinColumn(name = "phone_info_id", referencedColumnName = "id")
    )
    @Column(name = "tag")
    private List<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
