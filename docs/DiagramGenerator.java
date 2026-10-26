// Tác giả: Nguyễn Mạnh Thắng (QA/Tester & Báo cáo)
package docs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DiagramGenerator {

    public static void main(String[] args) {
        File dir = new File("docs/img");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            generateErdImage(new File("docs/img/erd.png"));
            generateUseCaseImage(new File("docs/img/usecase.png"));
            generateFlowImage(new File("docs/img/flow.png"));
            generateStateImage(new File("docs/img/state.png"));
            generateSitemapImage(new File("docs/img/sitemap.png"));
            System.out.println("All 5 diagrams generated successfully into docs/img/!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. ERD DIAGRAM
    private static void generateErdImage(File file) throws IOException {
        int width = 1200;
        int height = 850;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        setupGraphics(g);

        // Nền tối thanh lịch / hiện đại
        g.setColor(new Color(245, 247, 250));
        g.fillRect(0, 0, width, height);

        // Tiêu đề
        drawHeader(g, "SƠ ĐỒ QUAN HỆ THỰC THỂ (ERD) - HỆ THỐNG QUẢN LÝ THU CHI CÁ NHÂN & GIA ĐÌNH", 1200);

        // Vẽ các khối bảng thực thể CSDL
        // Phân hệ User & Auth
        drawTableCard(g, 50, 90, 220, 150, "users", new Color(59, 130, 246),
                new String[]{"PK: id (bigint)", "username (varchar)", "email (varchar)", "password_hash (varchar)", "full_name (varchar)", "role (enum)", "is_active (bool)"});

        drawTableCard(g, 50, 280, 220, 110, "refresh_tokens", new Color(107, 114, 128),
                new String[]{"PK: id (bigint)", "FK: user_id (bigint)", "token (varchar)", "expiry_date (timestamp)"});

        drawTableCard(g, 50, 430, 220, 130, "audit_logs", new Color(107, 114, 128),
                new String[]{"PK: id (bigint)", "FK: admin_id (bigint)", "action (varchar)", "target_type (varchar)", "target_id (bigint)", "created_at (timestamp)"});

        // Phân hệ Gia đình
        drawTableCard(g, 330, 90, 240, 150, "financial_spaces / families", new Color(16, 185, 129),
                new String[]{"PK: id (bigint)", "name (varchar)", "type (PERSONAL/FAMILY)", "currency (varchar)", "FK: owner_id (bigint)", "invite_code (varchar)", "is_active (bool)"});

        drawTableCard(g, 330, 280, 240, 130, "family_members", new Color(16, 185, 129),
                new String[]{"PK: id (bigint)", "FK: space_id (bigint)", "FK: user_id (bigint)", "role (OWNER/ADMIN/MEMBER)", "is_active (bool)"});

        // Phân hệ Ví & Danh mục
        drawTableCard(g, 630, 90, 230, 150, "wallets", new Color(245, 158, 11),
                new String[]{"PK: id (bigint)", "FK: user_id (bigint)", "FK: space_id (bigint)", "name (varchar)", "wallet_type (enum)", "balance (decimal)", "is_shared (bool)"});

        drawTableCard(g, 630, 280, 230, 150, "categories", new Color(139, 92, 246),
                new String[]{"PK: id (bigint)", "name (varchar)", "name_vi (varchar)", "type (INCOME/EXPENSE)", "icon / color", "is_system (bool)", "FK: user_id (bigint)"});

        // Phân hệ Giao dịch & Hóa đơn
        drawTableCard(g, 920, 90, 230, 190, "transactions", new Color(239, 68, 68),
                new String[]{"PK: id (bigint)", "FK: space_id (bigint)", "FK: user_id (bigint)", "FK: category_id (bigint)", "FK: wallet_id (bigint)", "FK: destination_wallet_id", "type (INCOME/EXPENSE/TRANSFER)", "amount (decimal)", "transaction_date (date)", "note (text)"});

        drawTableCard(g, 920, 320, 230, 110, "transaction_receipts", new Color(239, 68, 68),
                new String[]{"PK: id (bigint)", "FK: transaction_id (bigint)", "file_url (varchar)", "file_name (varchar)", "created_at (timestamp)"});

        // Phân hệ Ngân sách & Mục tiêu tiết kiệm
        drawTableCard(g, 330, 460, 240, 150, "budgets", new Color(14, 165, 233),
                new String[]{"PK: id (bigint)", "FK: space_id (bigint)", "FK: user_id (bigint)", "FK: category_id (bigint)", "amount (decimal)", "month / year (int)", "warning_threshold (decimal)"});

        drawTableCard(g, 630, 470, 230, 150, "savings_goals", new Color(236, 72, 153),
                new String[]{"PK: id (bigint)", "FK: space_id (bigint)", "FK: user_id (bigint)", "name (varchar)", "target_amount (decimal)", "current_amount (decimal)", "status (enum)"});

        drawTableCard(g, 920, 470, 230, 130, "notifications", new Color(249, 115, 22),
                new String[]{"PK: id (bigint)", "FK: user_id (bigint)", "title (varchar)", "message (text)", "type (enum)", "is_read (bool)"});

        // Vẽ các đường nối quan hệ thực thể (Connector Lines)
        g.setColor(new Color(156, 163, 175));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4f, 4f}, 0f));

        // users -> spaces
        drawArrow(g, 270, 140, 330, 140, "1:N");
        // spaces -> members
        drawArrow(g, 450, 240, 450, 280, "1:N");
        // users -> wallets
        drawArrow(g, 270, 170, 630, 150, "1:N");
        // wallets -> transactions
        drawArrow(g, 860, 150, 920, 150, "1:N");
        // categories -> transactions
        drawArrow(g, 860, 320, 920, 200, "1:N");
        // transactions -> receipts
        drawArrow(g, 1030, 280, 1030, 320, "1:N");
        // categories -> budgets
        drawArrow(g, 630, 380, 570, 490, "1:N");

        // Footer ghi chú
        drawFooter(g, "Mô hình CSDL chuẩn hóa quan hệ thực thể (3NF) - 12 bảng chính tối ưu cho nghiệp vụ thu chi cá nhân & gia đình", 1200, height);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // 2. USE CASE DIAGRAM
    private static void generateUseCaseImage(File file) throws IOException {
        int width = 1200;
        int height = 850;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        setupGraphics(g);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
        drawHeader(g, "SƠ ĐỒ USE CASE TỔNG QUAN (UC01 - UC16)", 1200);

        // System Boundary Box
        g.setColor(Color.WHITE);
        g.fillRoundRect(280, 75, 640, 700, 16, 16);
        g.setColor(new Color(203, 213, 225));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(280, 75, 640, 700, 16, 16);

        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g.setColor(new Color(30, 41, 59));
        g.drawString("HỆ THỐNG QUẢN LÝ THU CHI CÁ NHÂN & GIA ĐÌNH", 420, 105);

        // Actors
        drawActor(g, 100, 220, "Người Dùng Cá Nhân", "(Personal User)", new Color(37, 99, 235));
        drawActor(g, 100, 520, "Chủ Hộ / Quản Lý Nhóm", "(Family Owner)", new Color(5, 150, 105));
        drawActor(g, 1050, 370, "Quản Trị Viên", "(System Admin)", new Color(220, 38, 38));

        // Use Case Ellipses bên trong Boundary
        int startY = 130;
        int stepY = 38;

        String[] personalUCs = {
                "UC01: Đăng ký, Đăng nhập & JWT",
                "UC02: Quản lý Hồ sơ cá nhân",
                "UC03: Quản lý Ví tiền & Số dư",
                "UC04: Chuyển tiền nội bộ giữa các ví",
                "UC05: Ghi chép Thu / Chi hằng ngày",
                "UC06: Quản lý Danh mục cá nhân",
                "UC07: Ngân sách & Cảnh báo chi tiêu",
                "UC12: Xem Báo cáo dòng tiền & Biểu đồ",
                "UC13: Xuất Báo cáo Excel / PDF"
        };

        String[] familyUCs = {
                "UC08: Khởi tạo Nhóm & Mã mời (Invite Code)",
                "UC09: Nhập mã mời để Tham gia Gia đình",
                "UC10: Quản lý Ví chung & Thu chi gia đình",
                "UC11: Phân quyền thành viên Gia đình"
        };

        String[] adminUCs = {
                "UC14: Dashboard Quản trị hệ thống",
                "UC15: Quản lý Người dùng (Khóa/Mở)",
                "UC16: Danh mục chuẩn toàn hệ thống"
        };

        // Vẽ các UC cá nhân
        for (int i = 0; i < personalUCs.length; i++) {
            drawUseCaseBubble(g, 330, startY + (i * stepY), 250, 32, personalUCs[i], new Color(239, 246, 255), new Color(37, 99, 235));
            // Nối actor cá nhân tới UC
            g.setColor(new Color(191, 219, 254));
            g.drawLine(180, 240, 330, startY + (i * stepY) + 16);
        }

        // Vẽ các UC gia đình
        for (int i = 0; i < familyUCs.length; i++) {
            drawUseCaseBubble(g, 330, startY + (personalUCs.length * stepY) + (i * stepY) + 10, 260, 32, familyUCs[i], new Color(236, 253, 245), new Color(5, 150, 105));
            g.setColor(new Color(167, 243, 208));
            g.drawLine(180, 540, 330, startY + (personalUCs.length * stepY) + (i * stepY) + 26);
        }

        // Vẽ các UC Admin
        for (int i = 0; i < adminUCs.length; i++) {
            drawUseCaseBubble(g, 630, startY + (i * 50) + 100, 260, 36, adminUCs[i], new Color(254, 242, 242), new Color(220, 38, 38));
            g.setColor(new Color(254, 202, 202));
            g.drawLine(990, 390, 890, startY + (i * 50) + 118);
        }

        drawFooter(g, "Phân định rõ 3 tác nhân chính với 16 ca sử dụng tiêu chuẩn đáp ứng phân hệ cá nhân, gia đình và quản trị", 1200, height);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // 3. CORE FLOW DIAGRAM
    private static void generateFlowImage(File file) throws IOException {
        int width = 1200;
        int height = 800;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        setupGraphics(g);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
        drawHeader(g, "SƠ ĐỒ LUỒNG NGHIỆP VỤ CỐT LÕI (CORE BUSINESS FLOW)", 1200);

        // Vẽ các bước luồng giao dịch
        drawFlowNode(g, 50, 180, 200, 90, "1. Nhập Form Giao Dịch", "Người dùng nhập số tiền,\nloại thu/chi, ví, danh mục", new Color(59, 130, 246));
        drawArrow(g, 250, 225, 290, 225, "Submit");

        drawFlowNode(g, 290, 180, 200, 90, "2. Validate & JWT Auth", "Kiểm tra token hợp lệ,\nvalidate ràng buộc DTO", new Color(99, 102, 241));
        drawArrow(g, 490, 225, 530, 225, "Hợp lệ");

        drawFlowNode(g, 530, 180, 210, 90, "3. Mở DB Transaction", "@Transactional (ACID)\nKhóa dòng bản ghi ví", new Color(168, 85, 247));
        drawArrow(g, 740, 225, 780, 225, "Bảo toàn số dư");

        drawFlowNode(g, 780, 180, 210, 90, "4. Cập Nhật Số Dư Ví", "EXPENSE: Balance -= Tiền\nINCOME: Balance += Tiền", new Color(245, 158, 11));

        // Rẽ nhánh xuống bước kiểm tra ngân sách
        drawArrowDown(g, 885, 270, 885, 360, "Khoản chi (EXPENSE)");

        drawDecisionNode(g, 785, 360, 200, 100, "5. Kiểm Tra Ngân Sách?", "Quét hạn mức tháng theo category", new Color(236, 72, 153));

        // Nhánh không vượt
        drawArrow(g, 785, 410, 650, 410, "< 80% Ngân sách");
        drawFlowNode(g, 450, 365, 200, 90, "Trạng Thái NORMAL", "Cập nhật spent_amount\nKhông phát cảnh báo", new Color(16, 185, 129));

        // Nhánh cảnh báo 80%
        drawArrowDown(g, 885, 460, 885, 520, "80% - 100%");
        drawFlowNode(g, 780, 520, 210, 90, "Cảnh Báo WARNING", "Mã FIN-NB05\nHiển thị thanh màu vàng cam", new Color(249, 115, 22));

        // Nhánh vượt 100%
        drawArrow(g, 985, 410, 1040, 410, "> 100% (Bội chi)");
        drawFlowNode(g, 1010, 520, 170, 90, "Bội Chi EXCEEDED", "Mã FIN-NB06\nCảnh báo thanh màu đỏ", new Color(239, 68, 68));

        // Hoàn tất giao dịch
        drawArrow(g, 450, 410, 270, 410, "Commit");
        drawFlowNode(g, 70, 365, 200, 90, "6. Commit & Hoàn Tất", "Lưu Audit Log, trả về\nHTTP 201 Created kèm dữ liệu", new Color(5, 150, 105));

        drawFooter(g, "Cơ chế cập nhật nguyên tử (@Transactional) ngăn ngừa race condition, tự động đối soát và kích hoạt cảnh báo ngân sách", 1200, height);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // 4. STATE LIFECYCLE DIAGRAM
    private static void generateStateImage(File file) throws IOException {
        int width = 1200;
        int height = 800;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        setupGraphics(g);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
        drawHeader(g, "VÒNG ĐỜI VÀ MÁY TRẠNG THÁI (STATE MACHINE & LIFECYCLE)", 1200);

        // Section A: Giao dịch
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g.setColor(new Color(30, 41, 59));
        g.drawString("A. Vòng đời Trạng thái Giao Dịch (Transaction Lifecycle)", 80, 120);

        drawStateBox(g, 100, 160, 180, 80, "CREATED", "Khởi tạo thông tin", new Color(148, 163, 184));
        drawArrow(g, 280, 200, 380, 200, "Xác thực & Trừ ví");

        drawStateBox(g, 380, 160, 200, 80, "ACTIVE", "Giao dịch có hiệu lực\nẢnh hưởng trực tiếp số dư", new Color(34, 197, 94));
        drawArrow(g, 580, 200, 700, 200, "Người dùng nhấn Xóa");

        drawStateBox(g, 700, 160, 220, 80, "DELETED / ROLLBACK", "Hoàn tác chính xác số dư ví\nSoft Delete lưu trữ đối soát", new Color(239, 68, 68));

        // Section B: Ngân sách
        g.drawString("B. Vòng đời Trạng thái Ngân Sách (Budget State Machine)", 80, 360);

        drawStateBox(g, 100, 420, 200, 90, "NORMAL (< 80%)", "Mức chi an toàn\nTiến trình hiển thị màu xanh", new Color(16, 185, 129));
        drawArrow(g, 300, 465, 430, 465, "Tổng chi >= 80%");

        drawStateBox(g, 430, 420, 220, 90, "WARNING (80% - 100%)", "Ngưỡng cảnh báo chi tiêu\nTiến trình hiển thị màu vàng cam", new Color(245, 158, 11));
        drawArrow(g, 650, 465, 780, 465, "Tổng chi > 100%");

        drawStateBox(g, 780, 420, 220, 90, "EXCEEDED (> 100%)", "Bội chi ngân sách\nCảnh báo đỏ & gửi thông báo", new Color(239, 68, 68));

        // Vòng cung hoàn tiền
        g.setColor(new Color(107, 114, 128));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{4f, 4f}, 0f));
        g.drawArc(450, 520, 400, 100, 0, -180);
        g.drawString("Hoàn tác/Hủy giao dịch chi (Chi tiêu giảm dưới 80%)", 450, 650);

        drawFooter(g, "Kiểm soát trạng thái dữ liệu kế toán chặt chẽ, hỗ trợ kiểm toán (Audit Trail) và bảo toàn tính toàn vẹn số liệu", 1200, height);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // 5. SITEMAP DIAGRAM
    private static void generateSitemapImage(File file) throws IOException {
        int width = 1200;
        int height = 850;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        setupGraphics(g);

        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
        drawHeader(g, "SƠ ĐỒ CẤU TRÚC TRANG WEB UI (SITE MAP ARCHITECTURE)", 1200);

        // Gốc: Landing Page & Auth
        drawSiteNode(g, 480, 80, 240, 50, "Cổng Thông Tin & Đăng Nhập", "/login | /register", new Color(30, 41, 59));

        // Dashboard trung tâm
        drawArrowDown(g, 600, 130, 600, 180, "Đăng nhập thành công");
        drawSiteNode(g, 460, 180, 280, 60, "BẢNG ĐIỀU KHIỂN CHÍNH (DASHBOARD)", "Tổng quan tài chính, số dư & giao dịch gần nhất", new Color(37, 99, 235));

        // Các phân hệ con
        int subY = 320;
        drawSiteNode(g, 50, subY, 170, 70, "1. Sổ Giao Dịch", "/transactions\nThêm, sửa, lọc thu chi", new Color(16, 185, 129));
        drawSiteNode(g, 240, subY, 170, 70, "2. Quản Lý Ví", "/wallets\nVí tiền mặt, thẻ, ví điện tử", new Color(245, 158, 11));
        drawSiteNode(g, 430, subY, 170, 70, "3. Ngân Sách", "/budgets\nĐịnh mức chi & cảnh báo", new Color(139, 92, 246));
        drawSiteNode(g, 620, subY, 170, 70, "4. Không Gian Gia Đình", "/family\nMã mời, quỹ chung, thành viên", new Color(236, 72, 153));
        drawSiteNode(g, 810, subY, 170, 70, "5. Báo Cáo Tài Chính", "/reports\nBiểu đồ thu/chi, xuất Excel", new Color(14, 165, 233));
        drawSiteNode(g, 1000, subY, 160, 70, "6. Quản Trị Admin", "/admin\nUser, danh mục chuẩn", new Color(220, 38, 38));

        // Nối từ Dashboard xuống 6 phân hệ
        g.setColor(new Color(148, 163, 184));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(135, 280, 1080, 280);
        g.drawLine(600, 240, 600, 280);

        int[] xs = {135, 325, 515, 705, 895, 1080};
        for (int x : xs) {
            g.drawLine(x, 280, x, 320);
        }

        // Các trang chi tiết bên dưới
        int detailY = 470;
        drawSubCard(g, 50, detailY, 170, 180, new String[]{
                "• Danh sách giao dịch",
                "• Modal tạo giao dịch",
                "• Đính kèm hóa đơn ảnh",
                "• Lọc theo ngày, danh mục"
        });

        drawSubCard(g, 240, detailY, 170, 180, new String[]{
                "• Thẻ số dư từng ví",
                "• Thêm ví tiền mới",
                "• Chuyển tiền nội bộ",
                "• Lịch sử chuyển ví"
        });

        drawSubCard(g, 430, detailY, 170, 180, new String[]{
                "• Đặt hạn mức tháng",
                "• Thanh tiến độ chi tiêu",
                "• Cảnh báo vượt 80%",
                "• Cảnh báo bội chi 100%"
        });

        drawSubCard(g, 620, detailY, 170, 180, new String[]{
                "• Mã mời FAM-XXXX",
                "• Tham gia nhóm gia đình",
                "• Phân quyền thành viên",
                "• Ví quỹ chi tiêu chung"
        });

        drawSubCard(g, 810, detailY, 170, 180, new String[]{
                "• Biểu đồ tròn cơ cấu chi",
                "• Biểu đồ cột dòng tiền",
                "• Xuất tệp Excel .xlsx",
                "• Đối soát chênh lệch"
        });

        drawSubCard(g, 1000, detailY, 160, 180, new String[]{
                "• Thống kê người dùng",
                "• Khóa / Mở tài khoản",
                "• Quản lý danh mục mẫu",
                "• Xem lịch sử thao tác"
        });

        drawFooter(g, "Kiến trúc trang web Single Page Application (SPA) - Thiết kế responsive tương thích Mobile và Desktop", 1200, height);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // Helper functions
    private static void setupGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static void drawHeader(Graphics2D g, String title, int width) {
        g.setColor(new Color(30, 41, 59));
        g.fillRect(0, 0, width, 55);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(title)) / 2;
        g.drawString(title, x, 34);
    }

    private static void drawFooter(Graphics2D g, String text, int width, int height) {
        g.setColor(new Color(241, 245, 249));
        g.fillRect(0, height - 35, width, 35);
        g.setColor(new Color(100, 116, 139));
        g.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, height - 12);
    }

    private static void drawTableCard(Graphics2D g, int x, int y, int w, int h, String name, Color headerColor, String[] cols) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(226, 232, 240));
        g.drawRoundRect(x, y, w, h, 8, 8);

        g.setColor(headerColor);
        g.fillRoundRect(x, y, w, 28, 8, 8);
        g.fillRect(x, y + 20, w, 8);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString(name, x + 10, y + 19);

        g.setColor(new Color(51, 65, 85));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int py = y + 44;
        for (String col : cols) {
            g.drawString(col, x + 10, py);
            py += 15;
        }
    }

    private static void drawActor(Graphics2D g, int x, int y, String title, String sub, Color c) {
        g.setColor(c);
        g.fillOval(x + 25, y, 30, 30);
        g.setStroke(new BasicStroke(3f));
        g.drawLine(x + 40, y + 30, x + 40, y + 65);
        g.drawLine(x + 20, y + 45, x + 60, y + 45);
        g.drawLine(x + 40, y + 65, x + 20, y + 90);
        g.drawLine(x + 40, y + 65, x + 60, y + 90);

        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, x + 40 - (fm.stringWidth(title) / 2), y + 110);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(new Color(100, 116, 139));
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(sub, x + 40 - (fm2.stringWidth(sub) / 2), y + 125);
    }

    private static void drawUseCaseBubble(Graphics2D g, int x, int y, int w, int h, String text, Color bg, Color border) {
        g.setColor(bg);
        g.fillOval(x, y, w, h);
        g.setColor(border);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x, y, w, h);

        g.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2;
        g.drawString(text, tx, y + (h / 2) + 4);
    }

    private static void drawFlowNode(Graphics2D g, int x, int y, int w, int h, String title, String desc, Color c) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(c);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, 12, 12);

        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.drawString(title, x + 12, y + 25);

        g.setColor(new Color(71, 85, 105));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        String[] lines = desc.split("\n");
        int py = y + 45;
        for (String line : lines) {
            g.drawString(line, x + 12, py);
            py += 15;
        }
    }

    private static void drawDecisionNode(Graphics2D g, int x, int y, int w, int h, String title, String desc, Color c) {
        Polygon poly = new Polygon();
        poly.addPoint(x + w / 2, y);
        poly.addPoint(x + w, y + h / 2);
        poly.addPoint(x + w / 2, y + h);
        poly.addPoint(x, y + h / 2);

        g.setColor(new Color(253, 242, 248));
        g.fillPolygon(poly);
        g.setColor(c);
        g.setStroke(new BasicStroke(2f));
        g.drawPolygon(poly);

        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, x + (w - fm.stringWidth(title)) / 2, y + 45);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.setColor(new Color(100, 116, 139));
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(desc, x + (w - fm2.stringWidth(desc)) / 2, y + 62);
    }

    private static void drawStateBox(Graphics2D g, int x, int y, int w, int h, String name, String desc, Color border) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 20, 20);
        g.setColor(border);
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(x, y, w, h, 20, 20);

        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString(name, x + 16, y + 28);

        g.setColor(new Color(71, 85, 105));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        String[] lines = desc.split("\n");
        int py = y + 48;
        for (String line : lines) {
            g.drawString(line, x + 16, py);
            py += 15;
        }
    }

    private static void drawSiteNode(Graphics2D g, int x, int y, int w, int h, String title, String url, Color c) {
        g.setColor(c);
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString(title, x + 12, y + 25);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.drawString(url, x + 12, y + 42);
    }

    private static void drawSubCard(Graphics2D g, int x, int y, int w, int h, String[] items) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(226, 232, 240));
        g.drawRoundRect(x, y, w, h, 8, 8);

        g.setColor(new Color(51, 65, 85));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int py = y + 25;
        for (String item : items) {
            g.drawString(item, x + 10, py);
            py += 22;
        }
    }

    private static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, String label) {
        g.setColor(new Color(100, 116, 139));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(new int[]{x2, x2 - 8, x2 - 8}, new int[]{y2, y2 - 4, y2 + 4}, 3);
        if (label != null && !label.isEmpty()) {
            g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g.drawString(label, x1 + 6, y1 - 6);
        }
    }

    private static void drawArrowDown(Graphics2D g, int x1, int y1, int x2, int y2, String label) {
        g.setColor(new Color(100, 116, 139));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(new int[]{x2, x2 - 4, x2 + 4}, new int[]{y2, y2 - 8, y2 - 8}, 3);
        if (label != null && !label.isEmpty()) {
            g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g.drawString(label, x1 + 8, (y1 + y2) / 2);
        }
    }
}
