package com.airtel.helper.report;

/**
 * HTML/CSS templates for the custom automation report (light + Airtel orange theme).
 */
public interface ReportConstants {

	String BRAND_ORANGE = "#E63D00";
	String BRAND_ORANGE_DARK = "#C43400";
	String COLOR_PASS = "#12B76A";
	String COLOR_FAIL = "#F04438";
	String COLOR_WARN = "#F79009";
	String COLOR_TEXT = "#1F2937";
	String COLOR_MUTED = "#6B7280";
	String COLOR_BORDER = "#E5E7EB";
	String COLOR_BG = "#F3F4F6";
	String COLOR_CARD = "#FFFFFF";

	String REPORT_CSS = ""
			+ "<style>"
			+ ":root{--orange:#E63D00;--orange-dark:#C43400;--pass:#12B76A;--fail:#F04438;--warn:#F79009;"
			+ "--text:#1F2937;--muted:#6B7280;--border:#E5E7EB;--bg:#F3F4F6;--card:#FFFFFF;}"
			+ "*{box-sizing:border-box;}"
			+ "body{margin:0;font-family:Inter,Segoe UI,Roboto,Helvetica,Arial,sans-serif;background:var(--bg);color:var(--text);line-height:1.45;}"
			+ ".page{max-width:1200px;margin:0 auto;padding:24px;}"
			+ ".hero{background:linear-gradient(135deg,var(--orange) 0%,var(--orange-dark) 100%);color:#fff;border-radius:16px;padding:28px 32px;margin-bottom:20px;box-shadow:0 10px 30px rgba(230,61,0,.25);}"
			+ ".hero h1{margin:0 0 8px;font-size:1.75rem;font-weight:700;}"
			+ ".hero p{margin:0;opacity:.92;font-size:.95rem;}"
			+ ".meta-grid,.kpi-grid,.chart-grid{display:grid;gap:16px;margin-bottom:20px;}"
			+ ".meta-grid{grid-template-columns:repeat(auto-fit,minmax(200px,1fr));}"
			+ ".kpi-grid{grid-template-columns:repeat(auto-fit,minmax(160px,1fr));}"
			+ ".chart-grid{grid-template-columns:repeat(auto-fit,minmax(280px,1fr));}"
			+ ".card{background:var(--card);border:1px solid var(--border);border-radius:14px;padding:16px 18px;box-shadow:0 1px 2px rgba(16,24,40,.04);}"
			+ ".card .label{font-size:.75rem;text-transform:uppercase;letter-spacing:.04em;color:var(--muted);margin-bottom:6px;}"
			+ ".card .value{font-size:1.05rem;font-weight:600;word-break:break-word;}"
			+ ".kpi .value{font-size:1.8rem;font-weight:700;}"
			+ ".kpi.pass .value{color:var(--pass);} .kpi.fail .value{color:var(--fail);} .kpi.warn .value{color:var(--warn);} .kpi.total .value{color:var(--orange);}"
			+ ".section-title{font-size:1.15rem;font-weight:700;margin:8px 0 12px;}"
			+ "table.data{width:100%;border-collapse:separate;border-spacing:0;background:var(--card);border:1px solid var(--border);border-radius:14px;overflow:hidden;}"
			+ "table.data th{background:#FFF4EF;color:var(--orange-dark);text-align:left;padding:12px 14px;font-size:.8rem;text-transform:uppercase;letter-spacing:.03em;border-bottom:1px solid var(--border);}"
			+ "table.data td{padding:12px 14px;border-bottom:1px solid var(--border);font-size:.9rem;vertical-align:top;word-break:break-word;}"
			+ "table.data tr:last-child td{border-bottom:none;}"
			+ "table.data tr:hover td{background:#FAFAFA;}"
			+ "table.data a{color:var(--orange);text-decoration:none;font-weight:600;} table.data a:hover{text-decoration:underline;}"
			+ ".badge{display:inline-block;padding:4px 10px;border-radius:999px;font-size:.75rem;font-weight:700;}"
			+ ".badge.pass{background:#DCFAE6;color:#027A48;} .badge.fail{background:#FEE4E2;color:#B42318;} .badge.warn{background:#FEF0C7;color:#B54708;}"
			+ ".chart-wrap{height:260px;position:relative;}"
			+ ".footer-note{color:var(--muted);font-size:.8rem;margin-top:18px;text-align:center;}"
			+ ".method-hero{background:linear-gradient(135deg,var(--orange) 0%,var(--orange-dark) 100%);color:#fff;border-radius:14px;padding:20px 24px;margin-bottom:16px;}"
			+ ".method-hero h1{margin:0;font-size:1.35rem;}"
			+ "table.api th:nth-child(1),table.api td:nth-child(1){width:22%;}"
			+ "table.api th:nth-child(5),table.api td:nth-child(5){width:22%;}"
			+ "</style>";

	String SUITE_HEAD = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"/>"
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
			+ "<title>API Automation Test Report</title>"
			+ "<script src=\"https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js\"></script>"
			+ REPORT_CSS
			+ "</head><body><div class=\"page\">";

	String METHOD_HEAD = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"/>"
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>"
			+ "<title>API Test Report</title>"
			+ REPORT_CSS
			+ "</head><body><div class=\"page\">";

	/** Kept for any legacy references */
	String REPORT_START_TEMPLATE = SUITE_HEAD;
	String DETAILED_REPORT_TITLE = "<div class=\"section-title\">Detailed Report</div>";
	String DETAILED_REPORT_BODY_START = "<th>";
	String REPORT_COLUMN_END = "</th>";

	String SUMMARY_REPORT_TEMPLATE = "<div class=\"section-title\">Summary</div>";
	String SUMMARY_REPORT_ROW_START = "";
	String SUMMARY_REPORT_VALUE_TEMPLATE = "";

	String REQ_RESP_LOG_TEMPLATE = METHOD_HEAD
			+ "<div class=\"method-hero\"><h1>API Test Report</h1>"
			+ "<p>Request / response log for this test method</p></div>"
			+ "<div class=\"card\" style=\"padding:0;overflow:hidden;\">"
			+ "<table class=\"data api\"><thead><tr>"
			+ "<th>API URL</th><th>Headers</th><th>Request</th><th>Response</th>"
			+ "<th>Message</th><th>Status</th><th>Calling Method</th>"
			+ "</tr></thead><tbody>";

	String TEST_PASS_TAG = "<td><span class=\"badge pass\">";
	String TEST_FAIL_TAG = "<td><span class=\"badge fail\">";
	String TEST_WARN_TAG = "<td><span class=\"badge warn\">";
	String TEST_BADGE_END = "</span></td>";

	String COMPARATOR_REPORT_TITLE = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/><title>Compare Engine Response</title>"
			+ REPORT_CSS + "</head><body><div class=\"page\"><div class=\"section-title\">Compare Engine Response</div>"
			+ "<div class=\"card\" style=\"padding:0;overflow:hidden;\"><table class=\"data\"><thead><tr>"
			+ "<th>Operation Type</th><th>Field Name</th><th>Expected value</th><th>Test build value</th>"
			+ "</tr></thead><tbody>";

	String TESTRESULT_WARNING = "Warning";
	String TESTRESULT_HTMLREPORT = "HtmlReporting";
}
