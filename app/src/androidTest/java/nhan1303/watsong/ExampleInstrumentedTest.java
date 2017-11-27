package nhan1303.watsong;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.skyfishjy.library.RippleBackground;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nhan1303.watsong.activity.MainActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(
            MainActivity.class
    );

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("nhan1303.watsong", appContext.getPackageName());
        RippleBackground anima = (RippleBackground) mainActivityActivityTestRule.getActivity().findViewById(R.id.content);
        RippleBackground rbutton = (RippleBackground) mainActivityActivityTestRule.getActivity().findViewById(R.id.btnRecord);

        boolean status = anima.isRippleAnimationRunning();
        boolean rButton = rbutton.isRippleAnimationRunning();

        Espresso.onView(ViewMatchers.withId(R.id.btnRecord)).perform(ViewActions.click());
        assertTrue(status != anima.isRippleAnimationRunning());

        Espresso.onView(ViewMatchers.withId(R.id.btnRecord)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.btnRecord)).perform(ViewActions.click());
        assertFalse(status != anima.isRippleAnimationRunning());

    }
}
