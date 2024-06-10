package com.example.node.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_data")
@Builder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Update event;

}
