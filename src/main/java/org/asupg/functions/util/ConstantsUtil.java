package org.asupg.functions.util;

import org.apache.poi.ss.util.CellReference;

import java.util.regex.Pattern;

public class ConstantsUtil {

    public static final int DAY_DIFFERENCE = 2;
    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    public static final int SAFE_SLICE_LENGTH = 5000;
    public static final String ZKAU_ENDPOINT = "/zkau";

    public static final String CMD_ON_CLICK = "onClick";
    public static final String CMD_ON_MOVE = "onMove";
    public static final String CMD_ON_ZINDEX = "onZIndex";
    public static final String CMD_ON_CHANGE = "onChange";
    public static final String CMD_ON_BLUR = "onBlur";

    public static final Pattern DT_COMPONENT_PATTERN = Pattern.compile("dt\\s*:\\s*'([^']+)'");
    public static final Pattern USERNAME_COMPONENT_PATTERN = Pattern.compile("\\['[^']+','([^']+)',\\{[^}]*id\\s*:\\s*'" + "tb_username" + "'", Pattern.MULTILINE);
    public static final Pattern PASSWORD_COMPONENT_PATTERN = Pattern.compile("\\['[^']+','([^']+)',\\{[^}]*id\\s*:\\s*'" + "tb_password" + "'", Pattern.MULTILINE);
    public static final Pattern LOGIN_BUTTON_PATTERN = Pattern.compile("\\['zul\\.wgt\\.Button','([^']+)',\\{[^}]*id\\s*:\\s*['\"]btn_login['\"]", Pattern.MULTILINE);
    public static final Pattern STATEMENT_BUTTON_PATTERN = Pattern.compile("\\['zul\\.wgt\\.Toolbarbutton','([^']+)',\\{[^}]*tooltiptext:'Выписка за период'", Pattern.MULTILINE);

    public static final String AUTH_ENDPOINT = "/zkau;jsessionid=";
    public static final String AUTH_DATA = "{\"value\":\"%s\",\"start\":%d}";
    public static final String AUTH_BTN_DATA = "{\"pageX\":278,\"pageY\":262,\"which\":1,\"x\":85.1875,\"y\":9}";

    public static final String REQUEST_REPORT_DATA = "{\"pageX\":1188,\"pageY\":284,\"which\":1,\"x\":8.265625,\"y\":11}";
    public static final Pattern REPORT_WINDOW_PATTERN = Pattern.compile("\\['zul\\.wnd\\.Window','([^']+)',\\{[^}]*id:'reportMain'", Pattern.MULTILINE);
    public static final Pattern START_DATE_PATTERN = Pattern.compile("\\['zul\\.db\\.Datebox','([^']+)',\\{[^}]*id:'P_START_DATE'", Pattern.MULTILINE);
    public static final Pattern END_DATE_PATTERN = Pattern.compile("\\['zul\\.db\\.Datebox','([^']+)',\\{[^}]*id:'P_END_DATE'", Pattern.MULTILINE);
    public static final Pattern EXCEL_BUTTON_PATTERN = Pattern.compile("\\['zul\\.wgt\\.Toolbarbutton','([^']+)',\\{[^}]*id:'btn_excel'", Pattern.MULTILINE);


    public static final String REQUEST_REPORT_WINDOWS_OPT = "i";
    public static final String REQUEST_REPORT_WINDOWS_MOVE_DATA = "{\"left\":\"267px\",\"top\":\"314px\"}";
    public static final String REQUEST_REPORT_WINDOWS_ZINDEX_DATA = "{\"\":1800}";
    public static final String REQUEST_REPORT_EXCEL_BTN_DATA = "{\"pageX\":421,\"pageY\":605,\"which\":1,\"x\":129.59375,\"y\":17.5}";
    public static final String DATE_CHANGE_DATA = "{\"value\":\"%s\",\"start\":10,\"z_type_value\":\"Date\"}";

    public static final int START_ROW = 17;
    public static final int DATE_COLUMN = column("F");
    public static final int DOC_NUM_COLUMN = column("J");
    public static final int ACCOUNT_NAME_COLUMN = column("P");
    public static final int ACCOUNT_NUM_COLUMN = column("Y");
    public static final int MFO_COLUMN = column("AF");
    public static final int DEBIT_COLUMN = column("AG");
    public static final int CREDIT_COLUMN = column("AK");
    public static final int DESCRIPTION_COLUMN = column("AM");

    public static int column(String columnName) {
        return CellReference.convertColStringToIndex(columnName);
    }
}
