package com.project.springbootlibrary.service;

import com.project.springbootlibrary.dao.BookRepository;
import com.project.springbootlibrary.dao.CheckoutRepository;
import com.project.springbootlibrary.entity.Book;
import com.project.springbootlibrary.entity.Checkout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;



@Service
@Transactional
public class BookService {

    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
    }

    public Book checkoutBook (String userEmail, Long bookId) throws Exception{

        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId); // We want the user to checkout only one book at a time. We do not want any user to checkout same book more then once and hence this validation.

        if(!book.isPresent() || validateCheckout != null || book.get().getCopiesAvailable() <=0){
            throw new Exception("Book doesn't exist or already checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1); // Decrease book copies available and save
        bookRepository.save(book.get());

        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                book.get().getId()
        );

        checkoutRepository.save(checkout); //Save checkout details to database

        return book.get();
    }

    public boolean checkoutBookByUser(String userEmail, Long bookId){
        Checkout validatecheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validatecheckout != null){
            return true;
        } else {
            return false;
        }
    }

    public int currentLoansCount(String userEmail){
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

}
