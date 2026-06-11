package com.nimbleways.springboilerplate.entities;

import com.nimbleways.springboilerplate.constants.ErrorMessages;
import com.nimbleways.springboilerplate.enums.ProductType;
import lombok.*;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lead_time")
    private Integer leadTime;

    @Column(name = "available")
    private Integer available;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ProductType type;

    @Column(name = "name")
    private String name;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;

    public boolean isInSeason() {
        LocalDate today = LocalDate.now();
        return today.isAfter(seasonStartDate) && today.isBefore(seasonEndDate);
    }

    public boolean isExpired() {
        return expiryDate != null && !expiryDate.isAfter(LocalDate.now());
    }

    public boolean hasStock() {
        return available > 0;
    }

    public void decrementStock() {
        if (available <= 0) throw new IllegalStateException(ErrorMessages.productOutOfStock(id));
        available--;
    }

    public boolean willSeasonalLeadTimeExceedSeason() {
        return LocalDate.now().plusDays(leadTime).isAfter(seasonEndDate);
    }

    public boolean hasSeasonStarted() {
        return seasonStartDate != null && LocalDate.now().isAfter(seasonStartDate);
    }
}
