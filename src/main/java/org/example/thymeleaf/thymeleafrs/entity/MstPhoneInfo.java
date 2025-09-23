package org.example.thymeleaf.thymeleafrs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.thymeleaf.thymeleafrs.constant.SourceType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mst_phone_info")
@Data
@Getter
@Setter
public class MstPhoneInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SourceType source;


    @ElementCollection
    @CollectionTable(
            name = "mst_phone_info_tags",
            joinColumns = @JoinColumn(name = "phone_info_id", referencedColumnName = "id")
    )
    @Column(name = "tag")
    private List<String> tags;
}
