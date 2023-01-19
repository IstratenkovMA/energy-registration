package com.istratenkov.energyregistration.model.entity;

import com.istratenkov.energyregistration.model.entity.enumeration.Month;
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

/**
 * Represents data for specific meter for one year. For that task we assume that one profile only have one meter.
 * By using this entity can be calculated consumption for a given period. For example OCT value minus FEB value
 * will give a consumption for the period from FEB to OCT.
 * By the start of the year every meter value reset to 0.
 */
@Entity
@Table(schema = "energy")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeterMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer value;
    private Month month;
    private Integer year;

    @ManyToOne
    @JoinColumn(name="profile_id", nullable=false)
    private Profile profile;

    public MeterMeasurement(Integer value, Month month, Integer year) {
        this.value = value;
        this.month = month;
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterMeasurement that = (MeterMeasurement) o;
        return Objects.equals(id, that.id) && Objects.equals(value, that.value) && month == that.month && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, month, year);
    }
}
