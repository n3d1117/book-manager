package it.ned.bookmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import it.ned.bookmanager.controller.BookManagerController;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

@RunWith(GUITestRunner.class)
public class BookManagerSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private BookManagerSwingView view;

	@Mock
	private BookManagerController controller;

	private static final int MOCKITO_TIMEOUT = 3000;

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

		robot().waitForIdle();

		GuiActionRunner.execute(() -> {
			view.requestFocusInWindow();
			view.toFront();
		});
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {

		window.requireTitle("Book Manager");

		// Authors panel
		window.label(JLabelMatcher.withName("authorsLabel"));
		window.button(JButtonMatcher.withName("deleteAuthorButton")).requireDisabled();
		window.list("authorsList").requireItemCount(0).requireNoSelection();
		window.label("authorErrorLabel").requireText(" ");
		window.label(JLabelMatcher.withName("authorIdLabel"));
		window.textBox("authorIdTextField").requireEnabled().requireEmpty();
		window.label(JLabelMatcher.withName("authorNameLabel"));
		window.textBox("authorNameTextField").requireEnabled().requireEmpty();
		window.button(JButtonMatcher.withName("addAuthorButton")).requireDisabled();

		// Books panel
		window.label(JLabelMatcher.withName("booksLabel"));
		window.button(JButtonMatcher.withName("deleteBookButton")).requireDisabled();
		window.table("booksTable").requireRowCount(0).requireColumnCount(3).requireNoSelection();
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

	@Test
	@GUITest
	public void testAddAuthorButtonShouldBeEnabledWhenIdAndNameAreNotEmpty() {
		window.textBox("authorIdTextField").enterText("1");
		window.textBox("authorNameTextField").enterText("George Orwell");
		window.button(JButtonMatcher.withName("addAuthorButton")).requireEnabled();
	}

	@Test
	@GUITest
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

	@Test
	@GUITest
	public void testDeleteAuthorShouldOnlyBeEnabledWhenAnAuthorIsSelected() {
		GuiActionRunner.execute(() -> view.getAuthorListModel().addElement(new Author("1", "George Orwell")));
		window.list("authorsList").selectItem(0);
		window.button(JButtonMatcher.withName("deleteAuthorButton")).requireEnabled();
	}

	@Test
	@GUITest
	public void testDeleteAuthorShouldBeDisabledWhenSelectionIsCleared() {
		GuiActionRunner.execute(() -> view.getAuthorListModel().addElement(new Author("1", "George Orwell")));
		window.list("authorsList").selectItem(0);
		window.list("authorsList").clearSelection();
		window.button(JButtonMatcher.withName("deleteAuthorButton")).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddBookButtonShouldBeEnabledWhenIdAndNameAndAuthorAndLengthAreNotEmpty() {
		Author author = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorComboBoxModel().addElement(author);
			view.getAuthorComboBoxModel().setSelectedItem(author);
		});
		window.textBox("bookIdTextField").enterText("1");
		window.textBox("bookTitleTextField").enterText("Animal Farm");
		window.textBox("bookLengthTextField").enterText("93");

		window.button(JButtonMatcher.withName("addBookButton")).requireEnabled();
	}

	@Test
	@GUITest
	public void testAddBookButtonShouldBeDisabledWhenAnyFieldIsBlank() {
		JButtonFixture addBookButton = window.button(JButtonMatcher.withName("addBookButton"));

		JTextComponentFixture idTextBox = window.textBox("bookIdTextField");
		JTextComponentFixture titleTextBox = window.textBox("bookTitleTextField");
		JTextComponentFixture lengthTextBox = window.textBox("bookLengthTextField");
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> {
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

		GuiActionRunner.execute(() -> view.getAuthorComboBoxModel().setSelectedItem(null));
		idTextBox.enterText("1");
		titleTextBox.enterText("Animal Farm");
		lengthTextBox.enterText("93");
		addBookButton.requireDisabled();
	}

	@Test
	@GUITest
	public void testBookLengthTextFieldShouldNotAcceptNonIntegerStrings() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorComboBoxModel().addElement(georgeOrwell);
			view.getAuthorComboBoxModel().setSelectedItem(georgeOrwell);
		});

		window.textBox("bookIdTextField").enterText("1");
		window.textBox("bookTitleTextField").enterText("Animal Farm");
		window.textBox("bookLengthTextField").enterText("not an integer");
		window.button(JButtonMatcher.withName("addBookButton")).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteBookShouldOnlyBeEnabledWhenABookIsSelected() {
		GuiActionRunner.execute(() -> view.getBookTableModel().addElement(new Book("1", "Animal Farm", 93, "1"),
				new Author("1", "George Orwell")));
		window.table("booksTable").selectRows(0);
		window.button(JButtonMatcher.withName("deleteBookButton")).requireEnabled();
	}

	@Test
	@GUITest
	public void testDeleteBookShouldBeDisabledWhenSelectionIsCleared() {
		GuiActionRunner.execute(() -> view.getBookTableModel().addElement(new Book("1", "Animal Farm", 93, "1"),
				new Author("1", "George Orwell")));
		window.table("booksTable").selectRows(0);
		window.table("booksTable").unselectRows(0);
		window.button(JButtonMatcher.withName("deleteBookButton")).requireDisabled();
	}

	@Test
	@GUITest
	public void testShowAllAuthorsShouldAddAuthorsToList() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> view.showAllAuthors(Arrays.asList(danBrown, georgeOrwell)));
		String[] authorsListContent = window.list("authorsList").contents();
		assertThat(authorsListContent).containsExactly(danBrown.getName(), georgeOrwell.getName());
	}

	@Test
	@GUITest
	public void testShowAllAuthorsShouldAddAuthorsToListInAlphabeticalOrder() {
		Author danBrown = new Author("1", "Dan Brown");
		Author jamesJoyce = new Author("2", "James Joyce");
		Author georgeOrwell = new Author("3", "George Orwell");
		GuiActionRunner.execute(() -> view.showAllAuthors(Arrays.asList(jamesJoyce, danBrown, georgeOrwell)));
		String[] authorsListContent = window.list("authorsList").contents();
		assertThat(authorsListContent).containsExactly(danBrown.getName(), georgeOrwell.getName(),
				jamesJoyce.getName());
	}

	@Test
	@GUITest
	public void testShowAllAuthorsShouldAddAuthorsToCombobox() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> view.showAllAuthors(Arrays.asList(danBrown, georgeOrwell)));
		String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();
		assertThat(authorsListComboboxContent).containsExactly(danBrown.getName(), georgeOrwell.getName());
	}

	@Test
	@GUITest
	public void testShowAllAuthorsShouldAddAuthorsToComboBoxInAlphabeticalOrder() {
		Author danBrown = new Author("1", "Dan Brown");
		Author jamesJoyce = new Author("2", "James Joyce");
		Author georgeOrwell = new Author("3", "George Orwell");
		GuiActionRunner.execute(() -> view.showAllAuthors(Arrays.asList(jamesJoyce, danBrown, georgeOrwell)));
		String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();
		assertThat(authorsListComboboxContent).containsExactly(danBrown.getName(), georgeOrwell.getName(),
				jamesJoyce.getName());
	}

	@Test
	@GUITest
	public void testShowAllBooksShouldAddBooksToTable() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> view.getAuthorListModel().addElement(georgeOrwell));
		Book nineteenEightyFour = new Book("1", "1984", 293, georgeOrwell.getId());
		Book animalFarm = new Book("2", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(nineteenEightyFour, animalFarm)));
		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireRowCount(2);
		assertThat(booksTable.contents()[0]).containsExactly(nineteenEightyFour.getTitle(), georgeOrwell.getName(),
				nineteenEightyFour.getNumberOfPages().toString());
		assertThat(booksTable.contents()[1]).containsExactly(animalFarm.getTitle(), georgeOrwell.getName(),
				animalFarm.getNumberOfPages().toString());
	}

	@Test
	@GUITest
	public void testShowAllBooksShouldAddBooksToTableInAlphabeticalOrder() {
		Author georgeOrwell = new Author("1", "George Orwell");
		Author danBrown = new Author("2", "Dan Brown");
		Author jamesJoyce = new Author("3", "James Joyce");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(georgeOrwell);
			view.getAuthorListModel().addElement(danBrown);
			view.getAuthorListModel().addElement(jamesJoyce);
		});

		Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
		Book theDaVinciCode = new Book("2", "The Da Vinci Code", 402, danBrown.getId());
		Book ulysses = new Book("3", "Ulysses", 1341, jamesJoyce.getId());
		GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(theDaVinciCode, ulysses, animalFarm)));

		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireRowCount(3);
		assertThat(booksTable.contents()[0]).containsExactly(animalFarm.getTitle(), georgeOrwell.getName(),
				animalFarm.getNumberOfPages().toString());
		assertThat(booksTable.contents()[1]).containsExactly(theDaVinciCode.getTitle(), danBrown.getName(),
				theDaVinciCode.getNumberOfPages().toString());
		assertThat(booksTable.contents()[2]).containsExactly(ulysses.getTitle(), jamesJoyce.getName(),
				ulysses.getNumberOfPages().toString());
	}

	@Test
	@GUITest
	public void testShowAllBooksShouldThrowWhenAtLeastOneOneCorrespondingAuthorIsMissing() {
		Author jamesJoyce = new Author("2", "James Joyce");
		GuiActionRunner.execute(() -> view.getAuthorListModel().addElement(jamesJoyce));
		Book animalFarm = new Book("1", "Animal Farm", 93, "1");
		Book ulysses = new Book("2", "Ulysses", 1341, jamesJoyce.getId());
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(animalFarm, ulysses))));
		assertTrue(e.getMessage().contains(animalFarm.getAuthorId()));
	}

	@Test
	@GUITest
	public void testAuthorAddedShouldAddAuthorToListAndComboboxAndResetErrorLabel() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> view.authorAdded(georgeOrwell));
		String[] authorsListContent = window.list("authorsList").contents();
		String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();

		assertThat(authorsListContent).containsExactly(georgeOrwell.getName());
		assertThat(authorsListComboboxContent).containsExactly(georgeOrwell.getName());

		window.label("authorErrorLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testAuthorAddedShouldAddAuthorToListInAlphabeticalOrder() {
		Author jamesJoyce = new Author("1", "James Joyce");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.authorAdded(jamesJoyce);
			view.authorAdded(georgeOrwell);
		});
		String[] authorsListContent = window.list("authorsList").contents();
		assertThat(authorsListContent).containsExactly(georgeOrwell.getName(), jamesJoyce.getName());
	}

	@Test
	@GUITest
	public void testAuthorAddedShouldAddAuthorToComboBoxInAlphabeticalOrder() {
		Author jamesJoyce = new Author("2", "James Joyce");
		Author georgeOrwell = new Author("3", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.authorAdded(jamesJoyce);
			view.authorAdded(georgeOrwell);
		});
		String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();
		assertThat(authorsListComboboxContent).containsExactly(georgeOrwell.getName(), jamesJoyce.getName());
	}

	@Test
	@GUITest
	public void testAuthorAddedShouldAlsoClearSelection() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> view.authorAdded(danBrown));
		window.list("authorsList").selectItem(0);
		GuiActionRunner.execute(() -> view.authorAdded(georgeOrwell));
		window.list("authorsList").requireNoSelection();
	}

	@Test
	@GUITest
	public void testAuthorAddedShouldAlsoClearAuthorTextFields() {
		Author georgeOrwell = new Author("1", "George Orwell");
		window.textBox("authorIdTextField").enterText(georgeOrwell.getId());
		window.textBox("authorNameTextField").enterText(georgeOrwell.getName());
		GuiActionRunner.execute(() -> view.authorAdded(georgeOrwell));
		window.textBox("authorIdTextField").requireEmpty();
		window.textBox("authorNameTextField").requireEmpty();
	}

	@Test
	@GUITest
	public void testAuthorDeletedShouldRemoveAuthorFromListAndComboboxAndResetErrorLabel() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(danBrown);
			view.getAuthorComboBoxModel().addElement(danBrown);
			view.getAuthorListModel().addElement(georgeOrwell);
			view.getAuthorComboBoxModel().addElement(georgeOrwell);

			view.authorDeleted(new Author("1", "Dan Brown"));
		});

		String expected = georgeOrwell.getName();
		String[] authorsListContent = window.list("authorsList").contents();
		String[] authorsListComboboxContent = window.comboBox("authorsCombobox").contents();

		assertThat(authorsListContent).containsExactly(expected);
		assertThat(authorsListComboboxContent).containsExactly(expected);
		window.label("authorErrorLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testAuthorDeletedShouldAlsoClearSelection() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(danBrown);
			view.getAuthorListModel().addElement(georgeOrwell);
		});
		window.list("authorsList").selectItem(0);
		GuiActionRunner.execute(() -> view.authorDeleted(new Author("1", "Dan Brown")));
		window.list("authorsList").requireNoSelection();
	}

	@Test
	@GUITest
	public void testDeleteAllBooksForAuthorShouldRemoveAllAuthorsBooksFromBooksTable() {
		Author georgeOrwell = new Author("1", "George Orwell");
		Author danBrown = new Author("2", "Dan Brown");

		Book nineteenEightyFour = new Book("1", "1984", 293, georgeOrwell.getId());
		Book animalFarm = new Book("2", "Animal Farm", 93, georgeOrwell.getId());
		Book theDaVinciCode = new Book("3", "The Da Vinci Code", 402, danBrown.getId());

		GuiActionRunner.execute(() -> {
			view.getBookTableModel().addElement(nineteenEightyFour, georgeOrwell);
			view.getBookTableModel().addElement(animalFarm, georgeOrwell);
			view.getBookTableModel().addElement(theDaVinciCode, danBrown);

			view.deletedAllBooksForAuthor(georgeOrwell);
		});

		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireRowCount(1);
		assertThat(booksTable.contents()[0]).containsExactly(theDaVinciCode.getTitle(), danBrown.getName(),
				theDaVinciCode.getNumberOfPages().toString());
	}

	@Test
	@GUITest
	public void testBookAddedShouldAddBookToTableAndResetErrorLabel() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> view.getAuthorListModel().addElement(georgeOrwell));
		Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> view.bookAdded(animalFarm));
		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireRowCount(1);
		assertThat(booksTable.contents()[0]).containsExactly(animalFarm.getTitle(), georgeOrwell.getName(),
				animalFarm.getNumberOfPages().toString());
		window.label("bookErrorLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testBookAddedShouldAlsoClearBookTextFields() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(georgeOrwell);
			view.getAuthorComboBoxModel().addElement(georgeOrwell);
		});
		Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
		window.textBox("bookIdTextField").enterText(animalFarm.getId());
		window.textBox("bookTitleTextField").enterText(animalFarm.getTitle());
		window.textBox("bookLengthTextField").enterText(animalFarm.getNumberOfPages().toString());
		window.comboBox("authorsCombobox").selectItem(0);
		GuiActionRunner.execute(() -> view.bookAdded(animalFarm));
		window.textBox("bookIdTextField").requireEmpty();
		window.textBox("bookTitleTextField").requireEmpty();
		window.textBox("bookLengthTextField").requireEmpty();
		window.comboBox("authorsCombobox").requireNoSelection();
	}

	@Test
	@GUITest
	public void testBookAddedWithNoCorrespondingAuthorShouldThrow() {
		Book animalFarm = new Book("1", "Animal Farm", 93, "1");
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> GuiActionRunner.execute(() -> view.bookAdded(animalFarm)));
		assertTrue(e.getMessage().contains(animalFarm.getAuthorId()));
	}

	@Test
	@GUITest
	public void testBookDeletedShouldRemoveBookFromTableAndResetErrorLabel() {
		Author georgeOrwell = new Author("1", "George Orwell");
		Book nineteenEightyFour = new Book("2", "1984", 293, georgeOrwell.getId());
		Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> {
			view.getBookTableModel().addElement(nineteenEightyFour, georgeOrwell);
			view.getBookTableModel().addElement(animalFarm, georgeOrwell);
		});
		GuiActionRunner.execute(() -> view.bookDeleted(new Book("2", "1984", 293, georgeOrwell.getId())));

		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireRowCount(1);
		assertThat(booksTable.contents()[0]).containsExactly(animalFarm.getTitle(), georgeOrwell.getName(),
				animalFarm.getNumberOfPages().toString());

		window.label("bookErrorLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testAuthorNotAddedBecauseAlreadyExistsErrorShouldDisplayErrorMessage() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> view.authorNotAddedBecauseAlreadyExistsError(georgeOrwell));
		window.label("authorErrorLabel")
				.requireText(String.format("Error: Author with id %s already exists!", georgeOrwell.getId()));
	}

	@Test
	@GUITest
	public void testAuthorNotAddedBecauseAlreadyExistsErrorShouldAddExistingOneToTheListIfNotPresent() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(danBrown);
			view.getAuthorComboBoxModel().addElement(danBrown);
		});
		GuiActionRunner.execute(() -> view.authorNotAddedBecauseAlreadyExistsError(georgeOrwell));
		assertThat(window.list("authorsList").contents()).containsExactly(danBrown.getName(), georgeOrwell.getName());
		assertThat(window.comboBox("authorsCombobox").contents()).containsExactly(danBrown.getName(),
				georgeOrwell.getName());
	}

	@Test
	@GUITest
	public void testAuthorNotAddedBecauseAlreadyExistsErrorShouldNotAddExistingOneToTheListIfAlreadyPresent() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(danBrown);
			view.getAuthorComboBoxModel().addElement(danBrown);
			view.getAuthorListModel().addElement(georgeOrwell);
			view.getAuthorComboBoxModel().addElement(georgeOrwell);
		});
		GuiActionRunner.execute(() -> view.authorNotAddedBecauseAlreadyExistsError(georgeOrwell));
		// Make sure duplicates are avoided
		assertThat(window.list("authorsList").contents()).containsExactly(danBrown.getName(), georgeOrwell.getName());
		assertThat(window.comboBox("authorsCombobox").contents()).containsExactly(danBrown.getName(),
				georgeOrwell.getName());
	}

	@Test
	@GUITest
	public void testAuthorNotDeletedBecauseNotFoundErrorShouldDisplayErrorMessage() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> view.authorNotDeletedBecauseNotFoundError(georgeOrwell));
		window.label("authorErrorLabel")
				.requireText(String.format("Error: Author with id %s not found!", georgeOrwell.getId()));
	}

	@Test
	@GUITest
	public void testAuthorNotDeletedBecauseNotFoundErrorShouldAlsoRemoveAuthorFromListAndClearSelection() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(danBrown);
			view.getAuthorComboBoxModel().addElement(danBrown);
			view.getAuthorListModel().addElement(georgeOrwell);
			view.getAuthorComboBoxModel().addElement(georgeOrwell);
		});
		window.list("authorsList").selectItem(0);
		GuiActionRunner.execute(() -> view.authorNotDeletedBecauseNotFoundError(danBrown));
		window.list("authorsList").requireNoSelection();
		String expected = georgeOrwell.getName();
		assertThat(window.list("authorsList").contents()).containsExactly(expected);
		assertThat(window.comboBox("authorsCombobox").contents()).containsExactly(expected);
	}

	@Test
	@GUITest
	public void testBookNotAddedBecauseAlreadyExistsErrorShouldDisplayErrorMessage() {
		Author georgeOrwell = new Author("1", "George Orwell");
		GuiActionRunner.execute(() -> view.getAuthorListModel().addElement(georgeOrwell));
		Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> view.bookNotAddedBecauseAlreadyExistsError(animalFarm));
		window.label("bookErrorLabel")
				.requireText(String.format("Error: Book with id %s already exists!", animalFarm.getId()));
	}

	@Test
	@GUITest
	public void testBookNotAddedBecauseAlreadyExistsErrorShouldAddExistingOneToTheTableIfNotPresent() {
		Author georgeOrwell = new Author("1", "George Orwell");
		Book nineteenEightyFour = new Book("1", "1984", 293, georgeOrwell.getId());
		Book animalFarm = new Book("2", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(georgeOrwell);
			view.getBookTableModel().addElement(nineteenEightyFour, georgeOrwell);
		});
		GuiActionRunner.execute(() -> view.bookNotAddedBecauseAlreadyExistsError(animalFarm));
		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireRowCount(2);
		assertThat(booksTable.contents()[0]).containsExactly(nineteenEightyFour.getTitle(), georgeOrwell.getName(),
				nineteenEightyFour.getNumberOfPages().toString());
		assertThat(booksTable.contents()[1]).containsExactly(animalFarm.getTitle(), georgeOrwell.getName(),
				animalFarm.getNumberOfPages().toString());
	}

	@Test
	@GUITest
	public void testBookNotAddedBecauseAlreadyExistsErrorShouldNotAddExistingOneToTheTableIfAlreadyPresent() {
		Author georgeOrwell = new Author("1", "George Orwell");
		Book nineteenEightyFour = new Book("1", "1984", 293, georgeOrwell.getId());
		Book animalFarm = new Book("2", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(georgeOrwell);
			view.getBookTableModel().addElement(nineteenEightyFour, georgeOrwell);
			view.getBookTableModel().addElement(animalFarm, georgeOrwell);
		});
		GuiActionRunner.execute(() -> view.bookNotAddedBecauseAlreadyExistsError(animalFarm));
		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireRowCount(2);
		assertThat(booksTable.contents()[0]).containsExactly(nineteenEightyFour.getTitle(), georgeOrwell.getName(),
				nineteenEightyFour.getNumberOfPages().toString());
		assertThat(booksTable.contents()[1]).containsExactly(animalFarm.getTitle(), georgeOrwell.getName(),
				animalFarm.getNumberOfPages().toString());
	}

	@Test
	@GUITest
	public void testBookNotAddedShouldThrowWhenThereIsNoCorrespondingAuthor() {
		Book animalFarm = new Book("1", "Animal Farm", 93, "1");
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> GuiActionRunner.execute(() -> view.bookNotAddedBecauseAlreadyExistsError(animalFarm)));
		assertTrue(e.getMessage().contains(animalFarm.getAuthorId()));
	}

	@Test
	@GUITest
	public void testBookNotDeletedBecauseNotFoundErrorShouldDisplayErrorMessage() {
		Book animalFarm = new Book("1", "Animal Farm", 93, "1");
		GuiActionRunner.execute(() -> view.bookNotDeletedBecauseNotFoundError(animalFarm));
		window.label("bookErrorLabel")
				.requireText(String.format("Error: Book with id %s not found!", animalFarm.getId()));
	}

	@Test
	@GUITest
	public void testBookNotDeletedBecauseNotFoundErrorShouldAlsoRemoveBookFromTableAndClearSelection() {
		Author georgeOrwell = new Author("1", "George Orwell");
		Book nineteenEightyFour = new Book("1", "1984", 293, georgeOrwell.getId());
		Book animalFarm = new Book("2", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> {
			view.getBookTableModel().addElement(nineteenEightyFour, georgeOrwell);
			view.getBookTableModel().addElement(animalFarm, georgeOrwell);
		});
		GuiActionRunner.execute(() -> view.bookNotDeletedBecauseNotFoundError(animalFarm));
		JTableFixture booksTable = window.table("booksTable");
		booksTable.requireNoSelection().requireRowCount(1);
		assertThat(booksTable.contents()[0]).containsExactly(nineteenEightyFour.getTitle(), georgeOrwell.getName(),
				nineteenEightyFour.getNumberOfPages().toString());
	}

	@Test
	@GUITest
	public void testAddAuthorButtonShouldDelegateToControllerAddAuthor() {
		window.textBox("authorIdTextField").enterText("1");
		window.textBox("authorNameTextField").enterText("George Orwell");
		window.button(JButtonMatcher.withName("addAuthorButton")).click();
		verify(controller, Mockito.timeout(MOCKITO_TIMEOUT)).addAuthor(new Author("1", "George Orwell"));
	}

	@Test
	@GUITest
	public void testDeleteAuthorButtonShouldDelegateToControllerDeleteAuthor() {
		Author danBrown = new Author("1", "Dan Brown");
		Author georgeOrwell = new Author("2", "George Orwell");
		GuiActionRunner.execute(() -> {
			view.getAuthorListModel().addElement(danBrown);
			view.getAuthorListModel().addElement(georgeOrwell);
		});
		window.list("authorsList").selectItem(1);
		window.button(JButtonMatcher.withName("deleteAuthorButton")).click();
		verify(controller, Mockito.timeout(MOCKITO_TIMEOUT)).deleteAuthor(georgeOrwell);
	}

	@Test
	@GUITest
	public void testAddBookButtonShouldDelegateToControllerAddBook() {
		GuiActionRunner.execute(() -> view.getAuthorComboBoxModel().addElement(new Author("1", "George Orwell")));
		window.textBox("bookIdTextField").enterText("1");
		window.textBox("bookTitleTextField").enterText("Animal Farm");
		window.textBox("bookLengthTextField").enterText("93");
		window.comboBox("authorsCombobox").selectItem(0);
		window.button(JButtonMatcher.withName("addBookButton")).click();
		verify(controller, Mockito.timeout(MOCKITO_TIMEOUT)).addBook(new Book("1", "Animal Farm", 93, "1"));
	}

	@Test
	@GUITest
	public void testDeleteBookButtonShouldDelegateToControllerDeleteBook() {
		Author georgeOrwell = new Author("1", "George Orwell");
		Book nineteenEightyFour = new Book("1", "1984", 293, georgeOrwell.getId());
		Book animalFarm = new Book("2", "Animal Farm", 93, georgeOrwell.getId());
		GuiActionRunner.execute(() -> {
			view.getBookTableModel().addElement(nineteenEightyFour, georgeOrwell);
			view.getBookTableModel().addElement(animalFarm, georgeOrwell);
		});
		window.table("booksTable").selectRows(1);
		window.button(JButtonMatcher.withName("deleteBookButton")).click();
		verify(controller, Mockito.timeout(MOCKITO_TIMEOUT)).deleteBook(animalFarm);
	}
}
