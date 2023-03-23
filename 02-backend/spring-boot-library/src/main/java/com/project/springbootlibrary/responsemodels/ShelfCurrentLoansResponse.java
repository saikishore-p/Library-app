package com.project.springbootlibrary.responsemodels;

import com.project.springbootlibrary.entity.Book;
import lombok.Data;

// Things in responsemodels package is special response from the springboot application to our React frontend

@Data
public class ShelfCurrentLoansResponse {

    public ShelfCurrentLoansResponse(Book book, int daysLeft){
        this.book = book;
        this.daysLeft = daysLeft;
    }

    private Book book;

    private int daysLeft;
}
