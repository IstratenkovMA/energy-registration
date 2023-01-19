package com.istratenkov.energyregistration.model.entity;

import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(schema = "energy")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Fraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(precision = 2)
    private float value;
    private Month month;
    private Integer year;

    @ManyToOne/*(cascade = CascadeType.ALL)*/
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    public Fraction(float value, Month month, Integer year, Profile profile) {
        this.value = value;
        this.month = month;
        this.year = year;
        this.profile = profile;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        return Float.compare(fraction.value, value) == 0
                && Objects.equals(id, fraction.id)
                && month == fraction.month
                && Objects.equals(year, fraction.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, month, year);
    }
}
