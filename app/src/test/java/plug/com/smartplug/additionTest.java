package plug.com.smartplug;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import static org.junit.Assert.*;
/**
 * Created by andres on 10/18/17.
 */

public class additionTest {
    @Test
    public void additionTest() throws Exception {
        //Fail this on purpose
        int expected = 4;
        int actual = MainActivity.addition(2, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void multiplicationTest() throws Exception {
        //Fail this on purpose
        int expected = 4;
        int actual = MainActivity.multiplication(2, 2);
        assertEquals(expected, actual);
    }
}