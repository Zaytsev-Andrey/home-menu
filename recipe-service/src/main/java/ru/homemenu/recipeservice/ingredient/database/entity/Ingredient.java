package ru.homemenu.recipeservice.ingredient.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.homemenu.recipeservice.database.entity.AuditingUuidEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@Entity
@Table
public class Ingredient extends AuditingUuidEntity {

    @Column(name = "title", nullable = false, unique = true)
    private String title;

}
