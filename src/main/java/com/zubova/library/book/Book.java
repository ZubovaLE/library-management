package com.zubova.library.book;

import com.zubova.library.author.Author;
import com.zubova.library.common.persistence.BaseEntity;
import com.zubova.library.exception.ResourceConflictException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(nullable = false)
    private int publicationYear;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private int totalCopies;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private int availableCopies;

    public void borrowCopy() {
        if (availableCopies <= 0) {
            throw new ResourceConflictException("No copies of this book are available");
        }
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies >= totalCopies) {
            throw new ResourceConflictException("Cannot return a copy beyond total inventory");
        }
        availableCopies++;
    }

    void initializeCopies(int totalCopies) {
        validateTotalCopies(totalCopies);

        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    void changeTotalCopies(int newTotalCopies) {
        validateTotalCopies(newTotalCopies);

        int borrowedCopies = totalCopies - availableCopies;

        if (newTotalCopies < borrowedCopies) {
            throw new ResourceConflictException("Total copies cannot be less than borrowed copies");
        }

        this.totalCopies = newTotalCopies;
        this.availableCopies = newTotalCopies - borrowedCopies;
    }

    private static void validateTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
    }

}