package it.ned.bookmanager.view.swing;

import it.ned.bookmanager.controller.BookManagerController;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(GUITestRunner.class)
public class BookManagerSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private BookManagerSwingView view;

    @Mock private BookManagerController controller;

    @Override
    protected void onSetUp() {
        MockitoAnnotations.openMocks(this);
        GuiActionRunner.execute(() -> {
            view = new BookManagerSwingView();
            view.setController(controller);
            return view;
        });
        window = new FrameFixture(robot(), view);
        window.show();
    }

    @Test @GUITest
    public void testControlsInitialStates() {

        window.requireTitle("Book Manager");

        // Authors panel
        window.label(JLabelMatcher.withName("authorsLabel"));
        window.button(JButtonMatcher.withName("deleteAuthorButton")).requireDisabled();
        window.list("authorsList");
        window.label("authorErrorLabel").requireText(" ");
        window.label(JLabelMatcher.withName("authorIdLabel"));
        window.textBox("authorIdTextField").requireEnabled().requireEmpty();
        window.label(JLabelMatcher.withName("authorNameLabel"));
        window.textBox("authorNameTextField").requireEnabled().requireEmpty();
        window.button(JButtonMatcher.withName("addAuthorButton")).requireDisabled();

        // Books panel
        window.label(JLabelMatcher.withName("booksLabel"));
        window.button(JButtonMatcher.withName("deleteBookButton")).requireDisabled();
        window.table("booksTable").requireColumnCount(3);
        window.label("bookErrorLabel").requireText(" ");
        window.label(JLabelMatcher.withName("bookIdLabel"));
        window.textBox("bookIdTextField").requireEnabled().requireEmpty();
        window.label(JLabelMatcher.withName("bookTitleLabel"));
        window.textBox("bookTitleTextField").requireEnabled().requireEmpty();
        window.label(JLabelMatcher.withName("bookAuthorLabel"));
        window.comboBox("authorsCombobox").requireNoSelection();
        window.label(JLabelMatcher.withName("bookLengthLabel"));
        window.textBox("bookLengthTextField").requireEnabled().requireEmpty();
        window.button(JButtonMatcher.withName("addBookButton")).requireDisabled();
    }

    @Test @GUITest
    public void testAddAuthorButtonShouldBeEnabledWhenIdAndNameAreNotEmpty() {
        window.textBox("authorIdTextField").enterText("1");
        window.textBox("authorNameTextField").enterText("George Orwell");
        window.button(JButtonMatcher.withName("addAuthorButton")).requireEnabled();
    }

    @Test @GUITest
    public void testAddAuthorButtonDisabledWhenIdOrNameAreBlank() {
        JButtonFixture addButton = window.button(JButtonMatcher.withName("addAuthorButton"));

        JTextComponentFixture idTextBox = window.textBox("authorIdTextField");
        JTextComponentFixture nameTextBox = window.textBox("authorNameTextField");

        idTextBox.enterText("1");
        nameTextBox.enterText(" ");
        addButton.requireDisabled();

        idTextBox.setText("");
        nameTextBox.setText("");

        idTextBox.enterText(" ");
        nameTextBox.enterText("George Orwell");
        addButton.requireDisabled();
    }

    @Test @GUITest
    public void testDeleteAuthorShouldOnlyBeEnabledWhenAnAuthorIsSelected() {
        GuiActionRunner.execute(() ->
            view.getAuthorListModel().addElement(new Author("1", "George Orwell"))
        );
        window.list("authorsList").selectItem(0);
        window.button(JButtonMatcher.withName("deleteAuthorButton")).requireEnabled();
    }

    @Test @GUITest
    public void testDeleteAuthorShouldBeDisabledWhenSelectionIsCleared() {
        GuiActionRunner.execute(() ->
            view.getAuthorListModel().addElement(new Author("1", "George Orwell"))
        );
        window.list("authorsList").selectItem(0);
        window.list("authorsList").clearSelection();
        window.button(JButtonMatcher.withName("deleteAuthorButton")).requireDisabled();
    }

    @Test @GUITest
    public void testAddBookButtonShouldBeEnabledWhenIdAndNameAndAuthorAndLengthAreNotEmpty() {
        GuiActionRunner.execute(() -> {
            view.getAuthorComboBoxModel().addElement(new Author("1", "G. Orwell"));
        });
        window.textBox("bookIdTextField").enterText("1");
        window.textBox("bookTitleTextField").enterText("Animal Farm");
        window.textBox("bookLengthTextField").enterText("93");

        window.button(JButtonMatcher.withName("addBookButton")).requireEnabled();
    }

    @Test @GUITest
    public void testAddBookButtonShouldBeDisabledWhenAnyFieldIsBlank() {
        JButtonFixture addBookButton = window.button(JButtonMatcher.withName("addBookButton"));

        JTextComponentFixture idTextBox = window.textBox("bookIdTextField");
        JTextComponentFixture titleTextBox = window.textBox("bookTitleTextField");
        JTextComponentFixture lengthTextBox = window.textBox("bookLengthTextField");
        GuiActionRunner.execute(() -> {
            Author georgeOrwell = new Author("1", "G. Orwell");
            view.getAuthorComboBoxModel().addElement(georgeOrwell);
            view.getAuthorComboBoxModel().setSelectedItem(georgeOrwell);
        });

        idTextBox.enterText("1");
        titleTextBox.enterText("Animal Farm");
        lengthTextBox.enterText(" ");
        addBookButton.requireDisabled();

        // Reset
        idTextBox.setText("");
        titleTextBox.setText("");
        lengthTextBox.setText("");

        idTextBox.enterText("1");
        titleTextBox.enterText(" ");
        lengthTextBox.enterText("93");
        addBookButton.requireDisabled();

        // Reset
        idTextBox.setText("");
        titleTextBox.setText("");
        lengthTextBox.setText("");

        idTextBox.enterText(" ");
        titleTextBox.enterText("Animal Farm");
        lengthTextBox.enterText("93");
        addBookButton.requireDisabled();

        // Reset
        idTextBox.setText("");
        titleTextBox.setText("");
        lengthTextBox.setText("");

        GuiActionRunner.execute(() -> {
            view.getAuthorComboBoxModel().setSelectedItem(null);
        });
        idTextBox.enterText("1");
        titleTextBox.enterText("Animal Farm");
        lengthTextBox.enterText("93");
        addBookButton.requireDisabled();
    }

    @Test @GUITest
    public void testDeleteBookShouldOnlyBeEnabledWhenABookIsSelected() {
        GuiActionRunner.execute(() ->
                view.getBookTableModel().addElement(new Book("1", "Animal Farm", 93, "1"))
        );
        window.table("booksTable").selectRows(0);
        window.button(JButtonMatcher.withName("deleteBookButton")).requireEnabled();
    }

    @Test @GUITest
    public void testDeleteBookShouldBeDisabledWhenSelectionIsCleared() {
        GuiActionRunner.execute(() ->
                view.getBookTableModel().addElement(new Book("1", "Animal Farm", 93, "1"))
        );
        window.table("booksTable").selectRows(0);
        window.table("booksTable").unselectRows(0);
        window.button(JButtonMatcher.withName("deleteBookButton")).requireDisabled();
    }

    @Test @GUITest
    public void testShowAllAuthorsShouldAddAuthorsToList() {
        Author georgeOrwell = new Author("1", "George Orwell");
        Author danBrown = new Author("2", "Dan Brown");
        GuiActionRunner.execute(() ->
                view.showAllAuthors(Arrays.asList(georgeOrwell, danBrown))
        );
        String[] authorsListContent = window.list("authorsList").contents();
        assertThat(authorsListContent).containsExactly(
                "👤 " + georgeOrwell.getName(), "👤 " + danBrown.getName()
        );
    }

    @Test @GUITest
    public void testShowAllAuthorsShouldAddAuthorsToCombobox() {
        Author georgeOrwell = new Author("1", "George Orwell");
        Author danBrown = new Author("2", "Dan Brown");
        GuiActionRunner.execute(() ->
                view.showAllAuthors(Arrays.asList(georgeOrwell, danBrown))
        );
        String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();
        assertThat(authorsListComboboxContent).containsExactly(
                "👤 " + georgeOrwell.getName(), "👤 " + danBrown.getName()
        );
    }

    @Test @GUITest
    public void testShowAllBooksShouldAddBooksToTable() {
        Book nineteenEightyFour = new Book("1", "1984", 293, "1");
        Book animalFarm = new Book("2", "Animal Farm", 93, "1");
        GuiActionRunner.execute(() ->
                view.showAllBooks(Arrays.asList(nineteenEightyFour, animalFarm))
        );
        String[][] booksTableContent = window.table("booksTable").contents();
        assertThat(booksTableContent[0]).containsExactly(
                nineteenEightyFour.getTitle(),
                nineteenEightyFour.getAuthorId(),
                nineteenEightyFour.getNumberOfPages().toString()
        );
        assertThat(booksTableContent[1]).containsExactly(
                animalFarm.getTitle(),
                animalFarm.getAuthorId(),
                animalFarm.getNumberOfPages().toString()
        );
    }

    @Test @GUITest
    public void testAuthorAddedShouldAddAuthorToListAndComboboxAndResetErrorLabel() {
        Author georgeOrwell = new Author("1", "George Orwell");
        GuiActionRunner.execute(() ->
                view.authorAdded(georgeOrwell)
        );
        String[] authorsListContent = window.list("authorsList").contents();
        assertThat(authorsListContent).containsExactly("👤 " + georgeOrwell.getName());

        String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();
        assertThat(authorsListComboboxContent).containsExactly("👤 " + georgeOrwell.getName());

        window.label("authorErrorLabel").requireText(" ");
    }

    @Test @GUITest
    public void testAuthorDeletedShouldRemoveAuthorFromListAndComboboxAndResetErrorLabel() {
        Author georgeOrwell = new Author("1", "George Orwell");
        Author danBrown = new Author("2", "Dan Brown");
        GuiActionRunner.execute(() -> {
            view.getAuthorListModel().addElement(georgeOrwell);
            view.getAuthorComboBoxModel().addElement(georgeOrwell);
            view.getAuthorListModel().addElement(danBrown);
            view.getAuthorComboBoxModel().addElement(danBrown);
        });

        GuiActionRunner.execute(() -> {
            view.authorDeleted(new Author("1", "George Orwell"));
        });

        String expected = "👤 " + danBrown.getName();

        String[] authorsListContent = window.list("authorsList").contents();
        assertThat(authorsListContent).containsExactly(expected);
        String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();
        assertThat(authorsListComboboxContent).containsExactly(expected);

        window.label("authorErrorLabel").requireText(" ");
    }

    @Test @GUITest
    public void testDeleteAllBooksForAuthorShouldRemoveAllAuthorsBooksFromBooksTable() {
        Author georgeOrwell = new Author("1", "George Orwell");

        Book nineteenEightyFour = new Book("2", "1984", 293, georgeOrwell.getId());
        Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
        Book theDaVinciCode = new Book("3", "The Da Vinci Code", 402, "3");

        GuiActionRunner.execute(() -> {
            view.getBookTableModel().addElement(nineteenEightyFour);
            view.getBookTableModel().addElement(animalFarm);
            view.getBookTableModel().addElement(theDaVinciCode);
        });

        GuiActionRunner.execute(() -> {
            view.deletedAllBooksForAuthor(georgeOrwell);
        });

        String[][] booksTableContent = window.table("booksTable").contents();
        assertThat(booksTableContent[0]).containsExactly(
                theDaVinciCode.getTitle(),
                theDaVinciCode.getAuthorId(),
                theDaVinciCode.getNumberOfPages().toString()
        );
    }

    @Test @GUITest
    public void testBookAddedShouldAddBookToTableAndResetErrorLabel() {
        Book animalFarm = new Book("1", "Animal Farm", 93, "1");
        GuiActionRunner.execute(() ->
                view.bookAdded(animalFarm)
        );
        String[][] booksTableContent = window.table("booksTable").contents();
        assertThat(booksTableContent[0]).containsExactly(
                animalFarm.getTitle(),
                animalFarm.getAuthorId(),
                animalFarm.getNumberOfPages().toString()
        );

        window.label("bookErrorLabel").requireText(" ");
    }

    @Test @GUITest
    public void testBookDeletedShouldRemoveBookFromTableAndResetErrorLabel() {
        Book nineteenEightyFour = new Book("2", "1984", 293, "1");
        Book animalFarm = new Book("1", "Animal Farm", 93, "1");
        GuiActionRunner.execute(() -> {
            view.getBookTableModel().addElement(nineteenEightyFour);
            view.getBookTableModel().addElement(animalFarm);
        });

        GuiActionRunner.execute(() -> {
            view.bookDeleted(new Book("2", "1984", 293, "1"));
        });

        String[][] booksTableContent = window.table("booksTable").contents();
        assertThat(booksTableContent[0]).containsExactly(
                animalFarm.getTitle(),
                animalFarm.getAuthorId(),
                animalFarm.getNumberOfPages().toString()
        );

        window.label("bookErrorLabel").requireText(" ");
    }

    @Test @GUITest
    public void testAuthorNotAddedBecauseAlreadyExistsErrorShouldDisplayErrorMessage() {
        Author georgeOrwell = new Author("1", "George Orwell");
        GuiActionRunner.execute(() ->
                view.authorNotAddedBecauseAlreadyExistsError(georgeOrwell)
        );
        window.label("authorErrorLabel").requireText(
                String.format("Error: Author with id %s already exists!", georgeOrwell.getId())
        );
    }

    @Test @GUITest
    public void testAuthorNotDeletedBecauseNotFoundErrorShouldDisplayErrorMessage() {
        Author georgeOrwell = new Author("1", "George Orwell");
        GuiActionRunner.execute(() ->
                view.authorNotDeletedBecauseNotFoundError(georgeOrwell)
        );
        window.label("authorErrorLabel").requireText(
                String.format("Error: Author with id %s not found!", georgeOrwell.getId())
        );
    }

    @Test @GUITest
    public void testBookNotAddedBecauseAlreadyExistsErrorShouldDisplayErrorMessage() {
        Book animalFarm = new Book("1", "Animal Farm", 93, "1");
        GuiActionRunner.execute(() ->
                view.bookNotAddedBecauseAlreadyExistsError(animalFarm)
        );
        window.label("bookErrorLabel").requireText(
                String.format("Error: Book with id %s already exists!", animalFarm.getId())
        );
    }

    @Test @GUITest
    public void testBookNotDeletedBecauseNotFoundErrorShouldDisplayErrorMessage() {
        Book animalFarm = new Book("1", "Animal Farm", 93, "1");
        GuiActionRunner.execute(() ->
                view.bookNotDeletedBecauseNotFoundError(animalFarm)
        );
        window.label("bookErrorLabel").requireText(
                String.format("Error: Book with id %s not found!", animalFarm.getId())
        );
    }

    @Test @GUITest
    public void testAddAuthorButtonShouldDelegateToControllerAddAuthor() {
        window.textBox("authorIdTextField").enterText("1");
        window.textBox("authorNameTextField").enterText("George Orwell");
        window.button(JButtonMatcher.withName("addAuthorButton")).click();
        verify(controller).addAuthor(new Author("1", "George Orwell"));
    }

    @Test @GUITest
    public void testDeleteAuthorButtonShouldDelegateToControllerDeleteAuthor() {
        Author georgeOrwell = new Author("1", "George Orwell");
        Author danBrown = new Author("2", "Dan Brown");
        GuiActionRunner.execute(() -> {
            view.getAuthorListModel().addElement(georgeOrwell);
            view.getAuthorListModel().addElement(danBrown);
        });
        window.list("authorsList").selectItem(1);
        window.button(JButtonMatcher.withName("deleteAuthorButton")).click();
        verify(controller).deleteAuthor(danBrown);
    }

    @Test @GUITest
    public void testAddBookButtonShouldDelegateToControllerAddBook() {
        GuiActionRunner.execute(() -> {
            view.getAuthorComboBoxModel().addElement(new Author("1", "George Orwell"));
        });
        window.textBox("bookIdTextField").enterText("1");
        window.textBox("bookTitleTextField").enterText("Animal Farm");
        window.textBox("bookLengthTextField").enterText("93");
        window.comboBox("authorsCombobox").selectItem(0);
        window.button(JButtonMatcher.withName("addBookButton")).click();
        verify(controller).addBook(new Book("1", "Animal Farm", 93, "1"));
    }

    @Test @GUITest
    public void testDeleteBookButtonShouldDelegateToControllerDeleteBook() {
        Book nineteenEightyFour = new Book("2", "1984", 293, "1");
        Book animalFarm = new Book("1", "Animal Farm", 93, "1");
        GuiActionRunner.execute(() -> {
            view.getBookTableModel().addElement(nineteenEightyFour);
            view.getBookTableModel().addElement(animalFarm);
        });
        window.table("booksTable").selectRows(1);
        window.button(JButtonMatcher.withName("deleteBookButton")).click();
        verify(controller).deleteBook(animalFarm);
    }
}
