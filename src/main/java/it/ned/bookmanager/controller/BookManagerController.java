package it.ned.bookmanager.controller;

import it.ned.bookmanager.service.BookManagerService;
import it.ned.bookmanager.view.BookManagerView;

public class BookManagerController {
    private BookManagerService service;
    private BookManagerView view;

    public void getAllBooks() {
        view.showAllBooks(service.getAllBooks());
    }

    public void getAllAuthors() {
        view.showAllAuthors(service.getAllAuthors());
    }
}
