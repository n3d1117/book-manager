package it.ned.bookmanager.view.swing;

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

@RunWith(GUITestRunner.class)
public class BookManagerSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private BookManagerSwingView view;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            view = new BookManagerSwingView();
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

        idTextBox.enterText("");
        nameTextBox.enterText("");

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
}
