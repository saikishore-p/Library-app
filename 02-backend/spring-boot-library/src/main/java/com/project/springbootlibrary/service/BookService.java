package com.project.springbootlibrary.service;

import com.project.springbootlibrary.dao.BookRepository;
import com.project.springbootlibrary.dao.CheckoutRepository;
import com.project.springbootlibrary.entity.Book;
import com.project.springbootlibrary.entity.Checkout;
import com.project.springbootlibrary.responsemodels.ShelfCurrentLoansResponse;
import org.hibernate.annotations.Check;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


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

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {

        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();

        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail); // Getting list of all the books that the user currently has checked out. It gives only book id's
        List<Long> bookIdList = new ArrayList<>();

        for (Checkout i : checkoutList){
            bookIdList.add(i.getBookId());
        }

        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Book book: books){
            // Optional because we are not sure if this is actually going to be real item so, we are wrapping it in optional
            // We are using Java stream which returns a sequential stream with this collection as its source and we're going to be following it with a filter which returns a stream consisting of elements of the stream that matches that given predicate
            // We are passing x and x is going to be each item in our checkoutList. then we are grabbing bookid for x and then matching it with our books that has that Id and end search if we find one (findFirst)
            // Now we will have the checkout and book both with the same matching book ID
            Optional<Checkout> checkout = checkoutList.stream()
                    .filter(x -> x.getBookId() == book.getId()).findFirst();

            if(checkout.isPresent()){

                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());

                TimeUnit time = TimeUnit.DAYS;

                long difference_In_Time = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, (int) difference_In_Time));
            }
        }
        return shelfCurrentLoansResponses;
    }

    public void returnBook(String userEmail, Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId); //checking if we do have a record that has our user email with that specific bookID

        if(!book.isPresent() || validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);

        bookRepository.save(book.get());
        checkoutRepository.deleteById(validateCheckout.getId());
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = sdFormat.parse(validateCheckout.getReturnDate());
        Date d2 = sdFormat.parse(LocalDate.now().toString());

        // we need to make sure that checkout date is later than current date
        if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }

}
