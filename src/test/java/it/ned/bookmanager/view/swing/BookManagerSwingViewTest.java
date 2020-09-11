package it.ned.bookmanager.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
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
    public void testCorrectWindowTitle() {
        window.requireTitle("Book Manager");
    }
}