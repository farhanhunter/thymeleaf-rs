package org.example.thymeleaf.thymeleafrs.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "mst_phone_info")
public class PhoneInfo extends BaseEntity {
    @Id
    private String phone;
    private String name;
    private String source;

    @ElementCollection
    @CollectionTable(
            name = "mst_phone_info_tags",
            joinColumns = @JoinColumn(name = "phone", referencedColumnName = "phone")
    )
    @Column(name = "tag")
    private List<String> tags;

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
