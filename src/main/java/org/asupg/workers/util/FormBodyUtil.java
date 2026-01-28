package org.asupg.workers.util;

import org.asupg.workers.config.BankClientConfig;
import org.asupg.workers.model.SessionDTO;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormBodyUtil {

    private FormBodyUtil() {}

    public static Map<String, String> authFormBody(SessionDTO session, BankClientConfig bankClientConfig) {
        Map<String, String> formBody = new LinkedHashMap<>();
        formBody.put("dtid", session.getDtUuid());

        formBody.put("cmd_0", ConstantsUtil.CMD_ON_CHANGE);
        formBody.put("uuid_0", session.getPasswordUuid());
        formBody.put("data_0", buildOnChangeData(bankClientConfig.getPassword()));

        formBody.put("cmd_1", ConstantsUtil.CMD_ON_CHANGE);
        formBody.put("uuid_1", session.getUsernameUuid());
        formBody.put("data_1", buildOnChangeData(bankClientConfig.getUsername()));

        formBody.put("cmd_2", ConstantsUtil.CMD_ON_CLICK);
        formBody.put("uuid_2", session.getLoginBtnUuid());
        formBody.put("data_2", ConstantsUtil.AUTH_BTN_DATA);

        return formBody;
    }

    public static Map<String, String> openReportFormBody(String dtUuid, String statementBtnUuid) {
        Map<String, String> formBody = new LinkedHashMap<>();

        formBody.put("dtid", dtUuid);

        formBody.put("cmd_0", ConstantsUtil.CMD_ON_CLICK);
        formBody.put("uuid_0", statementBtnUuid);
        formBody.put("data_0", ConstantsUtil.REQUEST_REPORT_DATA);

        return formBody;
    }

    public static Map<String, String> setFromFormBody(String dtUuid, String reportWindowUuid, String startDateUuid) {
        Map<String, String> setFromFormBody = new LinkedHashMap<>();

        setFromFormBody.put("dtid", dtUuid);

        setFromFormBody.put("cmd_0", ConstantsUtil.CMD_ON_MOVE);
        setFromFormBody.put("opt_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        setFromFormBody.put("uuid_0", reportWindowUuid);
        setFromFormBody.put("data_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        setFromFormBody.put("cmd_1", ConstantsUtil.CMD_ON_ZINDEX);
        setFromFormBody.put("opt_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        setFromFormBody.put("uuid_1", reportWindowUuid);
        setFromFormBody.put("data_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_ZINDEX_DATA);

        setFromFormBody.put("cmd_2", ConstantsUtil.CMD_ON_MOVE);
        setFromFormBody.put("opt_2", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        setFromFormBody.put("uuid_2", reportWindowUuid);
        setFromFormBody.put("data_2", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        setFromFormBody.put("cmd_3", ConstantsUtil.CMD_ON_CHANGE);
        setFromFormBody.put("uuid_3", startDateUuid);
        setFromFormBody.put("data_3", buildDateChangeData(LocalDate.now().minusDays(ConstantsUtil.DAY_DIFFERENCE)));

        setFromFormBody.put("cmd_4", ConstantsUtil.CMD_ON_BLUR);
        setFromFormBody.put("uuid_4", startDateUuid);

        return setFromFormBody;
    }

    public static Map<String, String> setToFormBody(String dtUuid, String endDateUuid) {
        Map<String, String> setToFormBody = new LinkedHashMap<>();

        setToFormBody.put("dtid", dtUuid);

        setToFormBody.put("cmd_0", ConstantsUtil.CMD_ON_CHANGE);
        setToFormBody.put("uuid_0", endDateUuid);
        setToFormBody.put("data_0", buildDateChangeData(LocalDate.now()));

        setToFormBody.put("cmd_1", ConstantsUtil.CMD_ON_BLUR);
        setToFormBody.put("uuid_1", endDateUuid);

        return setToFormBody;
    }

    public static Map<String, String> requestReportFormBody(String dtUuid, String reportWindowUuid, String excelBtnUuid) {
        Map<String, String> requestReportFormBody = new LinkedHashMap<>();

        requestReportFormBody.put("dtid", dtUuid);

        requestReportFormBody.put("cmd_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        requestReportFormBody.put("uuid_0", reportWindowUuid);
        requestReportFormBody.put("data_0", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        requestReportFormBody.put("cmd_1", ConstantsUtil.CMD_ON_ZINDEX);
        requestReportFormBody.put("opt_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_OPT);
        requestReportFormBody.put("uuid_1", reportWindowUuid);
        requestReportFormBody.put("data_1", ConstantsUtil.REQUEST_REPORT_WINDOWS_ZINDEX_DATA);

        requestReportFormBody.put("cmd_2", ConstantsUtil.CMD_ON_MOVE);
        requestReportFormBody.put("uuid_2", reportWindowUuid);
        requestReportFormBody.put("data_2", ConstantsUtil.REQUEST_REPORT_WINDOWS_MOVE_DATA);

        requestReportFormBody.put("cmd_3", ConstantsUtil.CMD_ON_CLICK);
        requestReportFormBody.put("uuid_3", excelBtnUuid);
        requestReportFormBody.put("data_3", ConstantsUtil.REQUEST_REPORT_EXCEL_BTN_DATA);

        return requestReportFormBody;
    }

    private static String buildOnChangeData(String value) {
        return String.format(ConstantsUtil.AUTH_DATA, value, value.length());
    }

    private static String buildDateChangeData(LocalDate date) {
        String zkDate = date.getYear() + "." +
                date.getMonthValue() + "." +
                date.getDayOfMonth() + ".0.0.0.0";

        return String.format(
                ConstantsUtil.DATE_CHANGE_DATA,
                zkDate
        );
    }

}
