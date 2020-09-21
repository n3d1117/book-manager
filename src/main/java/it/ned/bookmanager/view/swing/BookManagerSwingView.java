package it.ned.bookmanager.view.swing;

import it.ned.bookmanager.controller.BookManagerController;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.view.BookManagerView;
import it.ned.bookmanager.view.swing.components.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static java.awt.Font.BOLD;

public class BookManagerSwingView extends JFrame implements BookManagerView {

    private transient BookManagerController controller;

    private final JList<Author> authorList;
    private final SortedListModel<Author> authorListModel;

    private final AuthorComboBox<Author> authorComboBox;
    private final SortedComboBoxModel<Author> authorComboBoxModel;

    private final JTextField bookIdTextField;
    private final JTextField bookTitleTextField;
    private final JTextField bookLengthTextField;
    private final JButton addBookButton;

    private final JTable booksTable;
    private final BookTableModel bookTableModel;
    private final JLabel authorErrorLabel;
    private final JLabel bookErrorLabel;

    private static final String AUTHOR_ALREADY_EXISTS_ERROR = "Error: Author with id %s already exists!";
    private static final String AUTHOR_NOT_FOUND_ERROR = "Error: Author with id %s not found!";
    private static final String BOOK_ALREADY_EXISTS_ERROR = "Error: Book with id %s already exists!";
    private static final String BOOK_NOT_FOUND_ERROR = "Error: Book with id %s not found!";

    public BookManagerSwingView() {
        setPreferredSize(new Dimension(700, 500));
        setTitle("Book Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        GridBagConstraints c = new GridBagConstraints();

        // Left panel
        JPanel leftSplitPanel = new JPanel();
        leftSplitPanel.setBackground(new Color(159, 181, 199));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 480;
        c.weightx = 0.4;
        c.gridx = 0;
        c.gridy = 0;
        leftSplitPanel.setLayout(null);
        mainPanel.add(leftSplitPanel, c);

        // Vertical separator
        JSeparator vSeparator = new JSeparator(SwingConstants.VERTICAL);
        vSeparator.setForeground(Color.BLACK);
        vSeparator.setBackground(Color.BLACK);
        vSeparator.setBounds(275, 0, 5, 480);
        leftSplitPanel.add(vSeparator);

        // Authors title
        JLabel authorsLabel = new JLabel("AUTHORS");
        authorsLabel.setName("authorsLabel");
        authorsLabel.setForeground(new Color(0x38424E));
        authorsLabel.setFont(new Font(new JLabel().getFont().getFontName(), BOLD, 14));
        authorsLabel.setBounds(15, 10, 100, 30);
        leftSplitPanel.add(authorsLabel);

        // Delete selected author button
        JButton deleteAuthorButton = new JButton("🗑 Delete selected");
        deleteAuthorButton.setName("deleteAuthorButton");
        deleteAuthorButton.setEnabled(false);
        deleteAuthorButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteAuthorButton.setBounds(120, 10, 150, 30);
        leftSplitPanel.add(deleteAuthorButton);

        // Authors Scroll Panel
        JScrollPane scrollPaneAuthors = new JScrollPane();
        scrollPaneAuthors.setBorder(new LineBorder(new Color(0x8594AC)));
        scrollPaneAuthors.setBounds(15, 43, 250, 300);
        leftSplitPanel.add(scrollPaneAuthors);

        // Authors List
        authorListModel = new SortedListModel<>();
        authorList = new JList<>(authorListModel);
        authorList.setName("authorsList");
        authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        authorList.setBackground(Color.WHITE);
        authorList.setFixedCellHeight(25);
        authorList.setCellRenderer(new AuthorCellRenderer());
        scrollPaneAuthors.setViewportView(authorList);

        // Author error label
        authorErrorLabel = new JLabel(" ");
        authorErrorLabel.setName("authorErrorLabel");
        authorErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorErrorLabel.setFont(new Font(new JLabel().getFont().getFontName(), BOLD, 12));
        authorErrorLabel.setForeground(new Color(0xFA4343));
        authorErrorLabel.setBounds(15, 340, 250, 40);
        leftSplitPanel.add(authorErrorLabel);

        // Horizontal author separator
        JSeparator hAuthorSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        hAuthorSeparator.setForeground(Color.BLACK);
        hAuthorSeparator.setBackground(Color.BLACK);
        hAuthorSeparator.setBounds(0, 377, 281, 5);
        leftSplitPanel.add(hAuthorSeparator);

        // Add author panel
        JPanel addAuthorPanel = new JPanel();
        addAuthorPanel.setBounds(0, 381, 280, 100);
        addAuthorPanel.setBackground(new Color(-9929840));
        addAuthorPanel.setLayout(null);
        leftSplitPanel.add(addAuthorPanel);

        // Author id label
        JLabel authorIdLabel = new JLabel("Id");
        authorIdLabel.setName("authorIdLabel");
        authorIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorIdLabel.setForeground(Color.WHITE);
        authorIdLabel.setBounds(0, 10, 80, 20);
        addAuthorPanel.add(authorIdLabel);

        // Author id textfield
        JTextField authorIdTextField = new JTextField();
        authorIdTextField.setName("authorIdTextField");
        authorIdTextField.setHorizontalAlignment(SwingConstants.CENTER);
        authorIdTextField.setBounds(80, 10, 180, 20);
        authorIdTextField.setCaretColor(Color.WHITE);
        addAuthorPanel.add(authorIdTextField);

        // Author name label
        JLabel authorNameLabel = new JLabel("Name");
        authorNameLabel.setName("authorNameLabel");
        authorNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorNameLabel.setForeground(Color.WHITE);
        authorNameLabel.setBounds(0, 35, 80, 20);
        addAuthorPanel.add(authorNameLabel);

        // Author name textfield
        JTextField authorNameTextField = new JTextField();
        authorNameTextField.setName("authorNameTextField");
        authorNameTextField.setHorizontalAlignment(SwingConstants.CENTER);
        authorNameTextField.setBounds(80, 35, 180, 20);
        authorNameTextField.setCaretColor(Color.WHITE);
        addAuthorPanel.add(authorNameTextField);

        // Add author button
        JButton addAuthorButton = new JButton("👤 Add Author");
        addAuthorButton.setName("addAuthorButton");
        addAuthorButton.setEnabled(false);
        addAuthorButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addAuthorButton.setBounds(50, 65, 180, 30);
        addAuthorPanel.add(addAuthorButton);

        // Right panel
        JPanel rightSplitPanel = new JPanel();
        rightSplitPanel.setBackground(new Color(229, 175, 175));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 480;
        c.weightx = 0.6;
        c.gridx = 1;
        c.gridy = 0;
        rightSplitPanel.setLayout(null);
        mainPanel.add(rightSplitPanel, c);

        // Books title
        JLabel booksLabel = new JLabel("BOOKS");
        booksLabel.setName("booksLabel");
        booksLabel.setForeground(new Color(0xFF573131, true));
        booksLabel.setFont(new Font(new JLabel().getFont().getFontName(), BOLD, 14));
        booksLabel.setBounds(15, 10, 100, 30);
        rightSplitPanel.add(booksLabel);

        // Delete selected book button
        JButton deleteBookButton = new JButton("🗑 Delete selected");
        deleteBookButton.setName("deleteBookButton");
        deleteBookButton.setEnabled(false);
        deleteBookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBookButton.setBounds(260, 10, 150, 30);
        rightSplitPanel.add(deleteBookButton);

        // Books Scroll Panel
        JScrollPane scrollPaneBooks = new JScrollPane();
        scrollPaneBooks.setBorder(new LineBorder(new Color(0xE38484)));
        scrollPaneBooks.setBounds(15, 43, 390, 300);
        rightSplitPanel.add(scrollPaneBooks);

        // Books table
        bookTableModel = new BookTableModel();
        booksTable = new JTable(bookTableModel);
        booksTable.setName("booksTable");
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setBackground(Color.WHITE);
        booksTable.setRowHeight(25);
        booksTable.setDefaultRenderer(String.class, new BookTableCellRenderer());
        scrollPaneBooks.setViewportView(booksTable);

        // Book error label
        bookErrorLabel = new JLabel(" ");
        bookErrorLabel.setName("bookErrorLabel");
        bookErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookErrorLabel.setFont(new Font(new JLabel().getFont().getFontName(), BOLD, 12));
        bookErrorLabel.setForeground(new Color(0xFA4343));
        bookErrorLabel.setBounds(15, 340, 390, 40);
        rightSplitPanel.add(bookErrorLabel);

        // Horizontal book separator
        JSeparator hBooksSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        hBooksSeparator.setForeground(Color.BLACK);
        hBooksSeparator.setBackground(Color.BLACK);
        hBooksSeparator.setBounds(0, 377, 420, 5);
        rightSplitPanel.add(hBooksSeparator);

        // Add book panel
        JPanel addBookPanel = new JPanel();
        addBookPanel.setBounds(0, 381, 420, 100);
        addBookPanel.setBackground(new Color(0xE38484));
        addBookPanel.setLayout(null);
        rightSplitPanel.add(addBookPanel);

        // Book id label
        JLabel bookIdLabel = new JLabel("Id");
        bookIdLabel.setName("bookIdLabel");
        bookIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookIdLabel.setForeground(Color.WHITE);
        bookIdLabel.setBounds(0, 10, 60, 20);
        addBookPanel.add(bookIdLabel);

        // Book id textfield
        bookIdTextField = new JTextField();
        bookIdTextField.setName("bookIdTextField");
        bookIdTextField.setHorizontalAlignment(SwingConstants.CENTER);
        bookIdTextField.setBounds(60, 10, 140, 20);
        bookIdTextField.setCaretColor(Color.WHITE);
        addBookPanel.add(bookIdTextField);

        // Book title label
        JLabel bookTitleLabel = new JLabel("Title");
        bookTitleLabel.setName("bookTitleLabel");
        bookTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookTitleLabel.setForeground(Color.WHITE);
        bookTitleLabel.setBounds(0, 35, 60, 20);
        addBookPanel.add(bookTitleLabel);

        // Book title textfield
        bookTitleTextField = new JTextField();
        bookTitleTextField.setName("bookTitleTextField");
        bookTitleTextField.setHorizontalAlignment(SwingConstants.CENTER);
        bookTitleTextField.setBounds(60, 35, 140, 20);
        bookTitleTextField.setCaretColor(Color.WHITE);
        addBookPanel.add(bookTitleTextField);

        // Book author label
        JLabel bookAuthorLabel = new JLabel("Author");
        bookAuthorLabel.setName("bookAuthorLabel");
        bookAuthorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookAuthorLabel.setForeground(Color.WHITE);
        bookAuthorLabel.setBounds(210, 10, 60, 20);
        addBookPanel.add(bookAuthorLabel);

        // Book author combobox
        authorComboBoxModel = new SortedComboBoxModel<>();
        authorComboBox = new AuthorComboBox<>(authorComboBoxModel);
        authorComboBox.setName("authorsCombobox");
        authorComboBox.setBounds(270, 10, 140, 20);
        authorComboBox.setSelectedItem(null); // show placeholder
        addBookPanel.add(authorComboBox);

        // Book length label
        JLabel bookLengthLabel = new JLabel("Length");
        bookLengthLabel.setName("bookLengthLabel");
        bookLengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookLengthLabel.setForeground(Color.WHITE);
        bookLengthLabel.setBounds(210, 35, 60, 20);
        addBookPanel.add(bookLengthLabel);

        // Book length textfield
        bookLengthTextField = new JTextField();
        bookLengthTextField.setName("bookLengthTextField");
        bookLengthTextField.setHorizontalAlignment(SwingConstants.CENTER);
        bookLengthTextField.setBounds(270, 35, 140, 20);
        bookLengthTextField.setCaretColor(Color.WHITE);
        addBookPanel.add(bookLengthTextField);

        // Add book button
        addBookButton = new JButton("📖 Add Book");
        addBookButton.setName("addBookButton");
        addBookButton.setEnabled(false);
        addBookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBookButton.setBounds(120, 65, 180, 30);
        addBookPanel.add(addBookButton);

        /* Listeners */

        // 'Add Author' button enabled key listener
        KeyAdapter addAuthorButtonEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                addAuthorButton.setEnabled(
                        !authorIdTextField.getText().trim().isEmpty() &&
                                !authorNameTextField.getText().trim().isEmpty()
                );
            }
        };
        authorIdTextField.addKeyListener(addAuthorButtonEnabler);
        authorNameTextField.addKeyListener(addAuthorButtonEnabler);

        // 'Delete Author' button selection listener
        authorList.addListSelectionListener(e ->
                deleteAuthorButton.setEnabled(authorList.getSelectedIndex() != -1)
        );

        // 'Add Book' button enabled key listener
        KeyAdapter addBookButtonEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                enableAddBookButton();
            }
        };
        bookIdTextField.addKeyListener(addBookButtonEnabler);
        bookTitleTextField.addKeyListener(addBookButtonEnabler);
        bookLengthTextField.addKeyListener(addBookButtonEnabler);
        authorComboBox.addActionListener(e -> enableAddBookButton());

        // 'Delete Book' button selection listener
        booksTable.getSelectionModel().addListSelectionListener(e ->
                deleteBookButton.setEnabled(booksTable.getSelectedRow() != -1)
        );

        // 'Add Author' button action
        addAuthorButton.addActionListener(e ->
                controller.addAuthor(new Author(authorIdTextField.getText(), authorNameTextField.getText()))
        );

        // 'Delete Author' button action
        deleteAuthorButton.addActionListener(e ->
                controller.deleteAuthor(authorList.getSelectedValue())
        );

        // 'Add Book' button action
        addBookButton.addActionListener(e -> {
            if (authorComboBox.getSelectedItem() instanceof Author) {
                Author selectedAuthor = (Author)authorComboBox.getSelectedItem();
                controller.addBook(
                        new Book(bookIdTextField.getText(),
                                bookTitleTextField.getText(),
                                Integer.parseInt(bookLengthTextField.getText()),
                                selectedAuthor.getId())
                );
            }
        });

        // 'Delete Book' button action
        deleteBookButton.addActionListener(e ->
                controller.deleteBook(bookTableModel.getBookAt(booksTable.getSelectedRow()))
        );
    }

    private void enableAddBookButton() {
        addBookButton.setEnabled(
                !bookIdTextField.getText().trim().isEmpty() &&
                        !bookTitleTextField.getText().trim().isEmpty() &&
                        !bookLengthTextField.getText().trim().isEmpty() &&
                        isInteger(bookLengthTextField.getText().trim()) &&
                        authorComboBox.getSelectedIndex() != -1
        );
    }

    /* Getters */

    public SortedListModel<Author> getAuthorListModel() {
        return authorListModel;
    }

    public SortedComboBoxModel<Author> getAuthorComboBoxModel() {
        return authorComboBoxModel;
    }

    public BookTableModel getBookTableModel() {
        return bookTableModel;
    }

    /* Setters */

    public void setController(BookManagerController controller) {
        this.controller = controller;
    }

    /* Overrides */

    @Override
    public void showAllAuthors(List<Author> allAuthors) {
        for (Author author : allAuthors) {
            authorListModel.addElement(author);
            authorComboBoxModel.addElement(author);
        }
    }

    @Override
    public void showAllBooks(List<Book> allBooks) {
        allBooks.forEach(bookTableModel::addElement);
    }

    @Override
    public void authorAdded(Author author) {
        authorListModel.addElement(author);
        authorComboBoxModel.addElement(author);
        resetAuthorErrorLabel();
        authorList.clearSelection();
    }

    @Override
    public void authorDeleted(Author author) {
        authorListModel.removeElement(author);
        authorComboBoxModel.removeElement(author);
        resetAuthorErrorLabel();
        authorList.clearSelection();
    }

    @Override
    public void bookAdded(Book book) {
        bookTableModel.addElement(book);
        resetBookErrorLabel();
    }

    @Override
    public void bookDeleted(Book book) {
        bookTableModel.removeElement(book);
        resetBookErrorLabel();
    }

    @Override
    public void deletedAllBooksForAuthor(Author author) {
        bookTableModel.removeAllBooksFromAuthorId(author.getId());
    }

    @Override
    public void authorNotAddedBecauseAlreadyExistsError(Author existingAuthor) {
        authorErrorLabel.setText(String.format(AUTHOR_ALREADY_EXISTS_ERROR, existingAuthor.getId()));
        authorListModel.addElement(existingAuthor);
        authorComboBoxModel.addElement(existingAuthor);
    }

    @Override
    public void authorNotDeletedBecauseNotFoundError(Author author) {
        authorErrorLabel.setText(String.format(AUTHOR_NOT_FOUND_ERROR, author.getId()));
        authorListModel.removeElement(author);
        authorComboBoxModel.removeElement(author);
    }

    @Override
    public void bookNotAddedBecauseAlreadyExistsError(Book book) {
        bookErrorLabel.setText(String.format(BOOK_ALREADY_EXISTS_ERROR, book.getId()));
        bookTableModel.addElement(book);
    }

    @Override
    public void bookNotDeletedBecauseNotFoundError(Book book) {
        bookErrorLabel.setText(String.format(BOOK_NOT_FOUND_ERROR, book.getId()));
        bookTableModel.removeElement(book);
    }

    /* Utils */

    private void resetAuthorErrorLabel() {
        authorErrorLabel.setText(" ");
    }

    private void resetBookErrorLabel() {
        bookErrorLabel.setText(" ");
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
