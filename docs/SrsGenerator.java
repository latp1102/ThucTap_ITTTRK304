// Tác giả: Nguyễn Mạnh Thắng (QA/Tester & Báo cáo)
package docs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Bộ sinh tài liệu đặc tả SRS (100% Java thuần)
 * Sinh tự động 3 định dạng: .md, .html, .docx
 */
public class SrsGenerator {

    public static void main(String[] args) {
        System.out.println("Bắt đầu sinh tài liệu SRS...");
        File docsDir = new File("docs");
        if (!docsDir.exists()) docsDir.mkdirs();

        try {
            generateMarkdown(new File("docs/SRS_Finance_v1.0.md"));
            generateHtml(new File("docs/SRS_Finance_v1.0.html"));
            generateDocx(new File("docs/SRS_Finance_v1.0.docx"));
            System.out.println("=> Đã sinh thành công: docs/SRS_Finance_v1.0.md");
            System.out.println("=> Đã sinh thành công: docs/SRS_Finance_v1.0.html");
            System.out.println("=> Đã sinh thành công: docs/SRS_Finance_v1.0.docx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateMarkdown(File file) throws IOException {
        String md = """
# TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM (SRS)
## HỆ THỐNG QUẢN LÝ THU CHI CÁ NHÂN VÀ GIA ĐÌNH
*(Personal & Family Financial Management System - Spring Boot & React / Web UI)*

---

### THÔNG TIN DỰ ÁN & PHÂN CÔNG KỸ THUẬT
- **Học phần:** Phát triển Dự án Phần mềm / Đồ án Chuyên ngành
- **Lớp học phần:** HN-KS24-CNTT1
- **Nhóm thực hiện:** Nhóm 04
- **Giảng viên hướng dẫn:** ThS. Giảng viên Bùi Thanh Hải
- **Mã tài liệu:** SRS-FINANCE-SB-2026-001
- **Phiên bản:** 1.0 (Trạng thái: Hoàn thiện phục vụ nghiệm thu đồ án)
- **Hạn nộp & Ngày nộp:** 13/11/2026

#### Danh sách thành viên & Trách nhiệm chính:
1. **Trần Hữu Nhật** - *Backend & DevOps Lead*: Kiến trúc hệ thống Spring Boot, JWT Security, Service cốt lõi, Docker & Nginx.
2. **Nguyễn Hoàng Phúc** - *Frontend Lead & UI/UX*: Giao diện Web SPA, trực quan hóa dòng tiền, tích hợp RESTful API.
3. **La Thế Phong** - *Database & Data Lead*: Lược đồ quan hệ CSDL (ERD), kịch bản init.sql, tối ưu hóa truy vấn đối soát số dư.
4. **Nguyễn Mạnh Thắng** - *QA / Tester & Viết báo cáo*: Thiết kế Test Cases (UC01 - UC16), kiểm thử toàn diện E2E, quản trị lỗi và biên soạn tài liệu đồ án.

---

## PHẦN 1: TỔNG QUAN HỆ THỐNG

### 1.1 Mục đích tài liệu
Tài liệu này đặc tả chi tiết toàn bộ yêu cầu chức năng và phi chức năng của **"Hệ thống Quản lý Thu chi Cá nhân và Gia đình"**. Tài liệu đóng vai trò là căn cứ kỹ thuật chính xác phục vụ thiết kế cơ sở dữ liệu, xây dựng RESTful API, triển khai giao diện người dùng, lập kế hoạch kiểm thử (Test Cases) và nghiệm thu dự án.

### 1.2 Bối cảnh & Lý do chọn đề tài
Quản lý tài chính cá nhân và phân bổ ngân sách gia đình là một nhu cầu thiết yếu đối với mỗi cá nhân và hộ gia đình trong xã hội hiện đại. Tuy nhiên, phần lớn mọi người vẫn duy trì thói quen ghi chép thủ công trên sổ tay hoặc ghi chú rải rác trên điện thoại, dẫn đến việc dễ thất lạc số liệu, khó nắm bắt bức tranh toàn cảnh về dòng tiền, và thường xuyên rơi vào tình trạng chi tiêu vượt quá hạn mức. Đặc biệt trong phạm vi gia đình, việc minh bạch các khoản chi tiêu chung, chia sẻ trách nhiệm tài chính và đóng góp vào các quỹ mục tiêu chung (mua sắm tài sản, du lịch, quỹ khẩn cấp) thường gặp nhiều bất cập.

Dự án cung cấp giải pháp chuyển đổi số toàn diện trong quản lý tài chính: hỗ trợ cá nhân ghi chép các giao dịch thu chi nhanh chóng, lập kế hoạch ngân sách và phân tích dòng tiền; đồng thời cung cấp tính năng "Không gian gia đình" cho phép các thành viên cùng quản lý các ví chung một cách minh bạch, nhận cảnh báo khi vượt ngân sách và hướng tới các mục tiêu tài chính bền vững.

---

## PHẦN 2: THIẾT KẾ KIẾN TRÚC & SƠ ĐỒ HỆ THỐNG

### 2.1 Sơ đồ Quan hệ Thực thể (ERD)
Hệ thống chuẩn hóa gồm 12 bảng thực thể chính: `users`, `refresh_tokens`, `financial_spaces` (gia đình), `family_members`, `wallets`, `categories`, `transactions`, `transaction_receipts`, `budgets`, `savings_goals`, `notifications`, `audit_logs`.

![Sơ đồ ERD](img/erd.png)

### 2.2 Sơ đồ Use Case Tổng quan (UC01 – UC16)
Bao gồm 3 tác nhân chính: **Người dùng cá nhân (Personal User)**, **Chủ hộ gia đình (Family Owner)**, và **Quản trị viên (System Admin)**.

![Sơ đồ Use Case](img/usecase.png)

### 2.3 Sơ đồ Luồng Nghiệp vụ Cốt lõi (Core Business Flow)
Quy trình ghi nhận giao dịch thu chi, đảm bảo tính nguyên tử ACID (@Transactional), cập nhật biến động số dư tức thì và tự động kiểm tra cảnh báo hạn mức ngân sách tháng.

![Sơ đồ Luồng Nghiệp Vụ](img/flow.png)

### 2.4 Vòng đời và Máy trạng thái (State Machine & Lifecycle)
- **Giao dịch:** `CREATED` -> `ACTIVE` -> `DELETED` (Soft Delete hoàn trả chính xác số dư ví).
- **Ngân sách:** `NORMAL` (< 80%) -> `WARNING` (80% - 100%) -> `EXCEEDED` (> 100% Bội chi).

![Sơ đồ Trạng Thái](img/state.png)

### 2.5 Sơ đồ Cấu trúc Trang Web UI (Site Map)
Cấu trúc điều hướng giao diện Single Page Application đáp ứng đầy đủ phân hệ người dùng cá nhân, gia đình và quản trị hệ thống.

![Sơ đồ Cấu Trúc Trang](img/sitemap.png)

---

## PHẦN 3: ĐẶC TẢ CHI TIẾT 16 USE CASE (UC01 – UC16)

| Mã UC | Tên Ca Sử Dụng | Phân Hệ | Tác Nhân Chính | Trạng Thái |
|---|---|---|---|---|
| **UC01** | Đăng ký, Đăng nhập, JWT & Refresh Token | Tài khoản | Mọi đối tượng | Đã hoàn thành |
| **UC02** | Xem & Cập nhật Hồ sơ cá nhân | Tài khoản | Người dùng | Đã hoàn thành |
| **UC03** | Khởi tạo & Quản lý danh sách Ví tiền cá nhân | Nguồn tiền | Người dùng cá nhân | Đã hoàn thành |
| **UC04** | Chuyển tiền qua lại giữa các ví nội bộ | Nguồn tiền | Người dùng cá nhân | Đã hoàn thành |
| **UC05** | Thêm, sửa, xóa Giao dịch Thu/Chi (Transaction) | Sổ giao dịch | Người dùng, Thành viên | Đã hoàn thành |
| **UC06** | Quản lý Danh mục Thu Chi cá nhân (Category) | Phân loại | Người dùng | Đã hoàn thành |
| **UC07** | Thiết lập Hạn mức Ngân sách & Cảnh báo vượt chi | Ngân sách | Người dùng, Chủ hộ | Đã hoàn thành |
| **UC08** | Khởi tạo Nhóm Gia đình & Sinh mã mời (Invite Code) | Gia đình | Chủ hộ (Family Owner) | Đã hoàn thành |
| **UC09** | Nhập mã mời để Tham gia Gia đình | Gia đình | Người dùng | Đã hoàn thành |
| **UC10** | Quản lý Ví chung & Ghi chép chi tiêu Gia đình | Gia đình | Thành viên gia đình | Đã hoàn thành |
| **UC11** | Phân quyền & Quản lý thành viên trong Gia đình | Gia đình | Chủ hộ (Family Owner) | Đã hoàn thành |
| **UC12** | Xem Báo cáo Dòng tiền & Biểu đồ Cơ cấu chi tiêu | Báo cáo | Người dùng, Thành viên | Đã hoàn thành |
| **UC13** | Xuất Báo cáo Thu Chi sang tệp Excel (.xlsx) | Báo cáo | Người dùng, Chủ hộ | Đã hoàn thành |
| **UC14** | Bảng điều khiển Quản trị hệ thống (Admin Dashboard) | Quản trị | Quản trị viên (Admin) | Đã hoàn thành |
| **UC15** | Quản lý Người dùng hệ thống (Khóa / Mở khóa) | Quản trị | Quản trị viên (Admin) | Đã hoàn thành |
| **UC16** | Quản lý Danh mục thu chi mặc định toàn hệ thống | Quản trị | Quản trị viên (Admin) | Đã hoàn thành |

---

## PHẦN 4: HỆ THỐNG THÔNG BÁO & MÃ CẢNH BÁO

- **FIN-NB01:** Đăng ký tài khoản thành công.
- **FIN-NB02:** Đã ghi nhận giao dịch vào ví tiền.
- **FIN-NB03:** Chuyển tiền nội bộ giữa các ví thành công.
- **FIN-NB04:** Tạo nhóm gia đình thành công kèm mã mời duy nhất.
- **FIN-NB05:** Cảnh báo: Danh mục chi tiêu đã chạm ngưỡng 80% ngân sách tháng.
- **FIN-NB06:** Bội chi: Bạn đã chi tiêu vượt quá 100% hạn mức ngân sách đã đề ra.
- **FIN-NB07:** Cảnh báo: Số dư khả dụng trong ví không đủ để chi tiêu.
- **FIN-NB08:** Lỗi: Mã mời tham gia gia đình không hợp lệ hoặc đã hết hạn.
- **FIN-NB09:** Lỗi: Không thể xóa ví tiền đã phát sinh dữ liệu kế toán.
- **FIN-NB10:** Lỗi: Email đã được sử dụng bởi một tài khoản khác.
- **FIN-NB11:** Lỗi: Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.
- **FIN-NB12:** Xác nhận: Xác nhận trước khi rời khỏi nhóm gia đình.

---

## PHẦN 5: CẤU HÌNH VẬN HÀNH & DEVOPS
- **Backend:** Java 17, Spring Boot 3.3.5, Spring Security 6 (JWT Stateless), Spring Data JPA, Apache POI 5.2.5.
- **Database:** MySQL 8.0 chuẩn InnoDB utf8mb4. File kịch bản khởi tạo: `database/init.sql`.
- **Reverse Proxy:** Nginx cấu hình tại `nginx/default.conf`.
- **Containerization:** Docker multi-stage build kết hợp `docker-compose.yml`.
""";
        Files.writeString(file.toPath(), md, StandardCharsets.UTF_8);
    }

    private static void generateHtml(File file) throws IOException {
        String html = """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TÀI LIỆU ĐẶC TẢ SRS - HỆ THỐNG QUẢN LÝ THU CHI CÁ NHÂN VÀ GIA ĐÌNH</title>
    <style>
        :root {
            --primary: #2563eb;
            --primary-dark: #1d4ed8;
            --text-main: #1e293b;
            --text-muted: #64748b;
            --bg-card: #ffffff;
            --border: #e2e8f0;
        }
        body {
            font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, Helvetica, Arial, sans-serif;
            line-height: 1.6;
            color: var(--text-main);
            background: #f8fafc;
            margin: 0;
            padding: 40px 20px;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: var(--bg-card);
            padding: 50px 60px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
            border: 1px solid var(--border);
        }
        .doc-header {
            text-align: center;
            border-bottom: 2px solid var(--primary);
            padding-bottom: 25px;
            margin-bottom: 35px;
        }
        .doc-header h1 {
            color: var(--primary-dark);
            margin: 0 0 10px 0;
            font-size: 26px;
            text-transform: uppercase;
        }
        .doc-header h2 {
            color: var(--text-main);
            font-size: 20px;
            margin: 0 0 15px 0;
            font-weight: 600;
        }
        .meta-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
            background: #f1f5f9;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 35px;
        }
        .meta-item {
            font-size: 14px;
        }
        .meta-item strong {
            color: var(--primary-dark);
        }
        h2.section-title {
            color: var(--primary-dark);
            border-left: 4px solid var(--primary);
            padding-left: 12px;
            margin-top: 40px;
            margin-bottom: 20px;
            font-size: 20px;
        }
        h3 {
            color: #334155;
            font-size: 16px;
            margin-top: 25px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
            font-size: 14px;
        }
        table, th, td {
            border: 1px solid var(--border);
        }
        th {
            background-color: #f8fafc;
            color: #334155;
            font-weight: 600;
            text-align: left;
            padding: 12px 14px;
        }
        td {
            padding: 10px 14px;
        }
        tr:nth-child(even) {
            background-color: #fdfdfe;
        }
        .img-container {
            text-align: center;
            margin: 25px 0;
            background: #f8fafc;
            padding: 15px;
            border: 1px solid var(--border);
            border-radius: 8px;
        }
        .img-container img {
            max-width: 100%;
            height: auto;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        .img-caption {
            margin-top: 10px;
            font-size: 13px;
            font-style: italic;
            color: var(--text-muted);
        }
        .badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
        }
        .badge-success { background: #dcfce7; color: #166534; }
        .badge-warning { background: #fef3c7; color: #92400e; }
        .badge-danger { background: #fee2e2; color: #991b1b; }
        ul, ol {
            padding-left: 20px;
        }
        li {
            margin-bottom: 8px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="doc-header">
            <h1>Tài Liệu Đặc Tả Yêu Cầu Phần Mềm (SRS)</h1>
            <h2>Hệ Thống Quản Lý Thu Chi Cá Nhân Và Gia Đình</h2>
            <div style="font-size: 14px; color: var(--text-muted);">Mã tài liệu: SRS-FINANCE-SB-2026-001 | Phiên bản: 1.0</div>
        </div>

        <div class="meta-grid">
            <div class="meta-item"><strong>Học phần:</strong> Phát triển Dự án Phần mềm / Đồ án Chuyên ngành</div>
            <div class="meta-item"><strong>Lớp học phần:</strong> HN-KS24-CNTT1</div>
            <div class="meta-item"><strong>Nhóm thực hiện:</strong> Nhóm 04 (4 thành viên)</div>
            <div class="meta-item"><strong>Giảng viên hướng dẫn:</strong> ThS. Giảng viên Bùi Thanh Hải</div>
            <div class="meta-item"><strong>Hạn nộp & Ngày nộp:</strong> 13/11/2026</div>
            <div class="meta-item"><strong>Trạng thái:</strong> Hoàn thiện nghiệm thu đồ án</div>
        </div>

        <h2 class="section-title">1. Phân Chia Nhân Sự & Trách Nhiệm</h2>
        <table>
            <tr>
                <th>STT</th>
                <th>Thành Viên</th>
                <th>Vai Trò Chính</th>
                <th>Phạm Vi Trách Nhiệm Chi Tiết</th>
            </tr>
            <tr>
                <td>1</td>
                <td><strong>Trần Hữu Nhật</strong></td>
                <td>Backend & DevOps Lead</td>
                <td>Kiến trúc hệ thống Spring Boot, JWT Security, TransactionService, Docker & Nginx Reverse Proxy.</td>
            </tr>
            <tr>
                <td>2</td>
                <td><strong>Nguyễn Hoàng Phúc</strong></td>
                <td>Frontend Lead & UI/UX</td>
                <td>Giao diện Responsive React SPA, trực quan hóa dòng tiền (Chart.js / ApexCharts), form thu chi.</td>
            </tr>
            <tr>
                <td>3</td>
                <td><strong>La Thế Phong</strong></td>
                <td>Database & Data Lead</td>
                <td>Thiết kế lược đồ ERD, viết init.sql chuẩn hóa, tối ưu hóa Index truy vấn báo cáo đối soát số dư.</td>
            </tr>
            <tr>
                <td>4</td>
                <td><strong>Nguyễn Mạnh Thắng</strong></td>
                <td>QA / Tester & Viết Báo Cáo</td>
                <td>Thiết kế kịch bản kiểm thử UC01 – UC16, kiểm thử E2E số dư, theo dõi Bug Tracking và biên soạn báo cáo.</td>
            </tr>
        </table>

        <h2 class="section-title">2. Kiến Trúc Sơ Đồ Hệ Thống</h2>

        <h3>2.1 Sơ đồ Quan hệ Thực thể (ERD) - 12 Bảng</h3>
        <div class="img-container">
            <img src="img/erd.png" alt="ERD Diagram">
            <div class="img-caption">Hình 1: Sơ đồ lược đồ quan hệ thực thể (ERD) chuẩn hóa 12 bảng chính</div>
        </div>

        <h3>2.2 Sơ đồ Use Case Tổng quan (UC01 - UC16)</h3>
        <div class="img-container">
            <img src="img/usecase.png" alt="Use Case Diagram">
            <div class="img-caption">Hình 2: Sơ đồ Use Case tổng quan phân bổ theo 3 tác nhân chính</div>
        </div>

        <h3>2.3 Sơ đồ Luồng Nghiệp vụ Cốt lõi (Core Business Flow)</h3>
        <div class="img-container">
            <img src="img/flow.png" alt="Core Flow Diagram">
            <div class="img-caption">Hình 3: Quy trình ghi nhận giao dịch, đối soát số dư ACID và cảnh báo ngân sách</div>
        </div>

        <h3>2.4 Vòng đời và Máy trạng thái (State Machine & Lifecycle)</h3>
        <div class="img-container">
            <img src="img/state.png" alt="State Diagram">
            <div class="img-caption">Hình 4: Vòng đời trạng thái Giao dịch và Máy trạng thái Ngân sách</div>
        </div>

        <h3>2.5 Sơ đồ Cấu trúc Trang Web UI (Site Map)</h3>
        <div class="img-container">
            <img src="img/sitemap.png" alt="Site Map Diagram">
            <div class="img-caption">Hình 5: Sơ đồ cấu trúc điều hướng các trang trong hệ thống Single Page Application</div>
        </div>

        <h2 class="section-title">3. Danh Mục 16 Use Case Tiêu Chuẩn</h2>
        <table>
            <tr>
                <th>Mã UC</th>
                <th>Tên Use Case</th>
                <th>Phân Hệ</th>
                <th>Tác Nhân</th>
                <th>Độ Ưu Tiên</th>
            </tr>
            <tr><td>UC01</td><td>Đăng ký, Đăng nhập & Xác thực Stateless JWT</td><td>Tài khoản</td><td>Tất cả</td><td><span class="badge badge-danger">Cao nhất (P1)</span></td></tr>
            <tr><td>UC02</td><td>Quản lý Hồ sơ cá nhân</td><td>Tài khoản</td><td>Người dùng</td><td><span class="badge badge-warning">Trung bình (P2)</span></td></tr>
            <tr><td>UC03</td><td>Khởi tạo & Quản lý danh sách Ví tiền, Số dư</td><td>Nguồn tiền</td><td>Người dùng</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC04</td><td>Chuyển tiền nội bộ giữa các ví</td><td>Nguồn tiền</td><td>Người dùng</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC05</td><td>Ghi chép Giao dịch Thu/Chi (Transaction)</td><td>Sổ giao dịch</td><td>Người dùng</td><td><span class="badge badge-danger">Cao nhất (P1)</span></td></tr>
            <tr><td>UC06</td><td>Quản lý Danh mục Thu Chi cá nhân</td><td>Phân loại</td><td>Người dùng</td><td><span class="badge badge-warning">Trung bình (P2)</span></td></tr>
            <tr><td>UC07</td><td>Thiết lập Hạn mức Ngân sách & Cảnh báo vượt chi</td><td>Ngân sách</td><td>Người dùng, Chủ hộ</td><td><span class="badge badge-warning">Trung bình (P2)</span></td></tr>
            <tr><td>UC08</td><td>Khởi tạo Nhóm Gia đình & Sinh mã mời (Invite Code)</td><td>Gia đình</td><td>Chủ hộ (Owner)</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC09</td><td>Nhập mã mời để Tham gia Gia đình</td><td>Gia đình</td><td>Người dùng</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC10</td><td>Quản lý Ví chung & Thu chi Gia đình</td><td>Gia đình</td><td>Thành viên</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC11</td><td>Phân quyền & Quản lý thành viên Gia đình</td><td>Gia đình</td><td>Chủ hộ (Owner)</td><td><span class="badge badge-warning">Trung bình (P2)</span></td></tr>
            <tr><td>UC12</td><td>Báo cáo Dòng tiền & Biểu đồ Cơ cấu Chi</td><td>Báo cáo</td><td>Người dùng, Thành viên</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC13</td><td>Xuất Báo cáo Thu Chi sang tệp Excel (.xlsx)</td><td>Báo cáo</td><td>Người dùng, Chủ hộ</td><td><span class="badge badge-warning">Trung bình (P2)</span></td></tr>
            <tr><td>UC14</td><td>Bảng điều khiển Quản trị hệ thống (Admin Dashboard)</td><td>Quản trị</td><td>Quản trị viên</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC15</td><td>Quản lý Người dùng hệ thống (Khóa / Mở)</td><td>Quản trị</td><td>Quản trị viên</td><td><span class="badge badge-danger">Cao (P1)</span></td></tr>
            <tr><td>UC16</td><td>Quản lý Danh mục Thu Chi mặc định toàn hệ thống</td><td>Quản trị</td><td>Quản trị viên</td><td><span class="badge badge-warning">Trung bình (P2)</span></td></tr>
        </table>

        <h2 class="section-title">4. Hệ Thống Thông Báo & Cảnh Báo (FIN-NB)</h2>
        <table>
            <tr>
                <th>Mã Thông Báo</th>
                <th>Phân Loại</th>
                <th>Nội Dung Hiển Thị Mẫu</th>
                <th>Ngữ Cảnh Phát Sinh</th>
            </tr>
            <tr><td>FIN-NB01</td><td><span class="badge badge-success">Thành công</span></td><td>Đăng ký tài khoản thành công. Chào mừng bạn đến với hệ thống!</td><td>Sau khi hoàn tất UC01</td></tr>
            <tr><td>FIN-NB02</td><td><span class="badge badge-success">Thành công</span></td><td>Đã ghi nhận giao dịch vào ví tiền thành công.</td><td>Sau khi lưu UC05</td></tr>
            <tr><td>FIN-NB03</td><td><span class="badge badge-success">Thành công</span></td><td>Chuyển tiền nội bộ giữa các ví thành công.</td><td>Sau khi hoàn thành UC04</td></tr>
            <tr><td>FIN-NB04</td><td><span class="badge badge-success">Thành công</span></td><td>Tạo nhóm gia đình thành công. Mã mời: {invite_code}</td><td>Sau khi hoàn thành UC08</td></tr>
            <tr><td>FIN-NB05</td><td><span class="badge badge-warning">Cảnh báo</span></td><td>Chú ý: Danh mục đã chi tiêu đạt 80% hạn mức ngân sách tháng!</td><td>Khi chi tiêu chạm 80% (UC07)</td></tr>
            <tr><td>FIN-NB06</td><td><span class="badge badge-danger">Bội chi</span></td><td>Cảnh báo bội chi: Bạn đã vượt quá 100% ngân sách đề ra!</td><td>Khi chi tiêu vượt 100% (UC07)</td></tr>
            <tr><td>FIN-NB07</td><td><span class="badge badge-warning">Cảnh báo</span></td><td>Số dư khả dụng trong ví không đủ để thực hiện khoản chi này.</td><td>Chi tiêu vượt số dư ví tiền mặt</td></tr>
            <tr><td>FIN-NB08</td><td><span class="badge badge-danger">Lỗi</span></td><td>Mã mời gia đình không hợp lệ hoặc đã hết hạn sử dụng.</td><td>Tham gia nhóm thất bại (UC09)</td></tr>
            <tr><td>FIN-NB09</td><td><span class="badge badge-danger">Lỗi</span></td><td>Không thể xóa ví đã phát sinh dữ liệu giao dịch kế toán.</td><td>Ràng buộc toàn vẹn số liệu (UC03)</td></tr>
            <tr><td>FIN-NB10</td><td><span class="badge badge-danger">Lỗi</span></td><td>Email này đã được sử dụng. Vui lòng chọn email khác.</td><td>Đăng ký trùng email (UC01)</td></tr>
            <tr><td>FIN-NB11</td><td><span class="badge badge-danger">Lỗi</span></td><td>Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.</td><td>Token JWT không hợp lệ</td></tr>
            <tr><td>FIN-NB12</td><td><span class="badge badge-warning">Xác nhận</span></td><td>Bạn có chắc chắn muốn rời khỏi nhóm gia đình này không?</td><td>Xác nhận hủy tham gia (UC11)</td></tr>
        </table>

        <div style="text-align: center; margin-top: 40px; font-size: 13px; color: var(--text-muted); border-top: 1px solid var(--border); padding-top: 20px;">
            Đồ án Chuyên ngành / Phát triển Dự án Phần mềm - Nhóm 04 (Lớp HN-KS24-CNTT1) - GVHD: ThS. Bùi Thanh Hải
        </div>
    </div>
</body>
</html>
""";
        Files.writeString(file.toPath(), html, StandardCharsets.UTF_8);
    }

    private static void generateDocx(File file) throws IOException {
        // Đóng gói file Word .docx chuẩn Office Open XML (chứa document.xml, [Content_Types].xml, _rels/.rels)
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            addZipEntry(zos, "[Content_Types].xml", """
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>
""");

            addZipEntry(zos, "_rels/.rels", """
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>
""");

            addZipEntry(zos, "word/document.xml", """
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    <w:p>
      <w:pPr><w:jc w:val="center"/></w:pPr>
      <w:r><w:rPr><w:b/><w:sz w:val="36"/><w:color w:val="1E3A8A"/></w:rPr><w:t>BÁO CÁO ĐẶC TẢ YÊU CẦU PHẦN MỀM (SRS)</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:jc w:val="center"/></w:pPr>
      <w:r><w:rPr><w:b/><w:sz w:val="28"/><w:color w:val="2563EB"/></w:rPr><w:t>HỆ THỐNG QUẢN LÝ THU CHI CÁ NHÂN VÀ GIA ĐÌNH</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:jc w:val="center"/></w:pPr>
      <w:r><w:rPr><w:i/><w:sz w:val="22"/><w:color w:val="64748B"/></w:rPr><w:t>(Personal and Family Financial Management System - Spring Boot and React)</w:t></w:r>
    </w:p>
    <w:p><w:r><w:t></w:t></w:r></w:p>
    <w:p>
      <w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Học phần: Phát triển Dự án Phần mềm / Đồ án Chuyên ngành</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Lớp học phần: HN-KS24-CNTT1</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Giảng viên hướng dẫn: ThS. Giảng viên Bùi Thanh Hải</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Nhóm thực hiện: Nhóm 04 (Trần Hữu Nhật, Nguyễn Hoàng Phúc, La Thế Phong, Nguyễn Mạnh Thắng)</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Hạn nộp &amp; Ngày nộp: 13/11/2026</w:t></w:r>
    </w:p>
    <w:p><w:r><w:t></w:t></w:r></w:p>
    <w:p>
      <w:r><w:rPr><w:b/><w:sz w:val="26"/><w:color w:val="1E3A8A"/></w:rPr><w:t>1. DANH SÁCH 16 USE CASE TIÊU CHUẨN (UC01 - UC16)</w:t></w:r>
    </w:p>
    <w:p><w:r><w:t>• UC01: Đăng ký, Đăng nhập, Cấp phát JWT &amp; Refresh Token</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC02: Xem &amp; Cập nhật Hồ sơ cá nhân</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC03: Khởi tạo &amp; Quản lý danh sách Ví tiền cá nhân</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC04: Chuyển tiền qua lại giữa các ví nội bộ</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC05: Thêm, sửa, xóa Giao dịch Thu/Chi (Transaction)</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC06: Quản lý Danh mục Thu Chi cá nhân (Category)</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC07: Thiết lập Hạn mức Ngân sách &amp; Cảnh báo vượt chi</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC08: Khởi tạo Nhóm Gia đình &amp; Sinh mã mời (Invite Code)</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC09: Nhập mã mời để Tham gia Gia đình</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC10: Quản lý Ví chung &amp; Ghi chép chi tiêu Gia đình</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC11: Phân quyền &amp; Quản lý thành viên trong Gia đình</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC12: Xem Báo cáo Dòng tiền &amp; Biểu đồ Cơ cấu chi tiêu</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC13: Xuất Báo cáo Thu Chi sang tệp Excel / PDF</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC14: Bảng điều khiển Quản trị hệ thống (Admin Dashboard)</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC15: Quản lý Người dùng hệ thống (Khóa / Mở khóa)</w:t></w:r></w:p>
    <w:p><w:r><w:t>• UC16: Quản lý Danh mục thu chi mặc định toàn hệ thống</w:t></w:r></w:p>
    <w:p><w:r><w:t></w:t></w:r></w:p>
    <w:p>
      <w:r><w:rPr><w:b/><w:sz w:val="26"/><w:color w:val="1E3A8A"/></w:rPr><w:t>2. HỆ THỐNG MÃ THÔNG BÁO VÀ CẢNH BÁO (FIN-NB01 ĐẾN FIN-NB12)</w:t></w:r>
    </w:p>
    <w:p><w:r><w:t>Hệ thống định nghĩa 12 mã thông báo chuẩn hóa từ FIN-NB01 đến FIN-NB12 hỗ trợ trải nghiệm người dùng trực quan và an toàn dữ liệu.</w:t></w:r></w:p>
    <w:sectPr/>
  </w:body>
</w:document>
""");
        }
    }

    private static void addZipEntry(ZipOutputStream zos, String name, String content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }
}
