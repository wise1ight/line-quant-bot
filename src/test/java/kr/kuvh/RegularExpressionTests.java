package kr.kuvh;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class RegularExpressionTests {

    @Test
    public void emailRegularExpressionTest() {
        Pattern emailRegex = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b");

        //Test Case 1
        String testCase1 = "kuvh@live.co.kr";
        boolean isSatisfiedTestCase1 = emailRegex.matcher(testCase1).matches();
        assertThat(isSatisfiedTestCase1).isEqualTo(true);

        //Test Case 2
        String testCase2 = "iamkuvh@gmail.com";
        boolean isSatisfiedTestCase2 = emailRegex.matcher(testCase2).matches();
        assertThat(isSatisfiedTestCase2).isEqualTo(true);

        String testCase3 = "testtest.com";
        boolean isSatisfiedTestCase3 = emailRegex.matcher(testCase3).matches();
        assertThat(isSatisfiedTestCase3).isEqualTo(false);

        String testCase4 = "test@test";
        boolean isSatisfiedTestCase4 = emailRegex.matcher(testCase4).matches();
        assertThat(isSatisfiedTestCase4).isEqualTo(false);
    }

    @Test
    public void pinCodeRegularExpressionTest() {
        Pattern pinCodeRegex = Pattern.compile("^[0-9]{6}$");

        String testCase1 = "123456";
        boolean isSatisfiedTestCase1 = pinCodeRegex.matcher(testCase1).matches();
        assertThat(isSatisfiedTestCase1).isEqualTo(true);

        String testCase2 = "";
        boolean isSatisfiedTestCase2 = pinCodeRegex.matcher(testCase2).matches();
        assertThat(isSatisfiedTestCase2).isEqualTo(false);

        String testCase3 = "일 해라 핫산";
        boolean isSatisfiedTestCase3 = pinCodeRegex.matcher(testCase3).matches();
        assertThat(isSatisfiedTestCase3).isEqualTo(false);

        String testCase4 = "0000";
        boolean isSatisfiedTestCase4 = pinCodeRegex.matcher(testCase4).matches();
        assertThat(isSatisfiedTestCase4).isEqualTo(false);

        String testCase5 = "0000000";
        boolean isSatisfiedTestCase5 = pinCodeRegex.matcher(testCase5).matches();
        assertThat(isSatisfiedTestCase5).isEqualTo(false);
    }
}
