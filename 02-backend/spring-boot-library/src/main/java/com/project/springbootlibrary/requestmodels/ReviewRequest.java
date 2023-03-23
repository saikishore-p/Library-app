package com.project.springbootlibrary.requestmodels;

import lombok.Data;

import java.util.Optional;

// Things in requestmodels package is a special entity that's from the client to us.

@Data
public class ReviewRequest {

    private double rating;

    private Long bookId;

    private Optional<String> reviewDescription;
}
