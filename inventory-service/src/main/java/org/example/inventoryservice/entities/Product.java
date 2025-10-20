package org.example.inventoryservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data @NoArgsConstructor
@AllArgsConstructor @Builder
@Getter
@Setter
@ToString
public class Product {
    @Id
    private String id;
    private String name;
    private double price;
    private int quantity;
}
