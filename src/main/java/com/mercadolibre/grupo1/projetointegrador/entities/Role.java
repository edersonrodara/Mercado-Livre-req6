package com.mercadolibre.grupo1.projetointegrador.entities;

import lombok.*;

import javax.persistence.*;

/**
 * Entidade que mapeia a tabela de cargos para autorizacao (roles).
 * @author Weverton Bruno
 */

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
