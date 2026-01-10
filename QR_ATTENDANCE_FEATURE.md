# Tính năng QR Code Điểm danh & Giấy Chứng Nhận

## 📋 Tổng quan
Đã triển khai hoàn chỉnh hệ thống:
1. **User:** Hiển thị mã QR cá nhân để điểm danh
2. **Admin:** Quét QR để điểm danh + Cấp giấy chứng nhận
3. **Profile:** Hiển thị giấy chứng nhận đã nhận

---

## 📁 Các File Đã Tạo/Sửa

### 1. Model - Certificate.java (MỚI)
**Đường dẫn:** `src/main/java/com/vinhuni/VinhuniEvent/model/Certificate.java`

```java
@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
    
    private String certificateCode;  // CERT-{eventId}-{userId}-{random}
    private LocalDateTime issuedDate;
    private String issuedBy;
    private String description;
    private Boolean isValid;
}
```

### 2. Repository - CertificateRepository.java (MỚI)
**Đường dẫn:** `src/main/java/com/vinhuni/VinhuniEvent/repository/CertificateRepository.java`

- `findByUserId(Long userId)` - Lấy GCN theo user
- `findByEventId(Long eventId)` - Lấy GCN theo event
- `findByUserIdAndEventId(Long userId, Long eventId)` - Kiểm tra đã có GCN chưa
- `existsByUserUserIdAndEventEventId(Long userId, Long eventId)`

### 3. Service - QRCodeService.java (MỚI)
**Đường dẫn:** `src/main/java/com/vinhuni/VinhuniEvent/service/QRCodeService.java`

- `generateQRCodeBase64(String content, int width, int height)` - Tạo QR dạng Base64
- `createUserQRContent(Long userId, String studentCode)` - Format: `USER:{userId}:{studentCode}`
- `createAttendanceQRContent(Long userId, Long eventId)` - Format: `ATTENDANCE:{userId}:{eventId}:{timestamp}`
- `parseQRContent(String qrContent)` - Parse nội dung QR

### 4. Service - CertificateService.java (MỚI)
**Đường dẫn:** `src/main/java/com/vinhuni/VinhuniEvent/service/CertificateService.java`

- `issueCertificate(User user, Event event, String issuedBy)` - Cấp GCN
- `getCertificatesByUserId(Long userId)` - Lấy danh sách GCN của user
- `hasCertificate(Long userId, Long eventId)` - Kiểm tra đã có GCN
- `getCertificate(Long userId, Long eventId)` - Lấy GCN cụ thể

### 5. Controller - QRCodeController.java (MỚI)
**Đường dẫn:** `src/main/java/com/vinhuni/VinhuniEvent/controller/QRCodeController.java`

**API Endpoints:**
- `GET /api/qr/my-qr` - Lấy mã QR của user hiện tại
- `GET /api/qr/event/{eventId}` - Lấy mã QR cho sự kiện cụ thể
- `POST /api/qr/admin/scan-attendance` - Admin quét QR điểm danh
- `POST /api/qr/admin/issue-certificate` - Cấp GCN cho 1 người
- `POST /api/qr/admin/issue-certificates-batch` - Cấp GCN hàng loạt

### 6. Cập nhật - UserProfileController.java
**Thêm:**
- Inject `QRCodeService` và `CertificateService`
- Tạo QR code và truyền vào model
- Lấy danh sách certificates và truyền vào model

### 7. Cập nhật - AttendanceService.java
**Thêm methods:**
- `saveAttendance(Attendance attendance)`
- `getAttendanceByEventAndUser(Long eventId, Long userId)`

### 8. Template - client/profile/index.html (CẬP NHẬT)
**Tính năng mới:**
- Hiển thị mã QR cá nhân với nút phóng to
- Tab "Giấy chứng nhận" hiển thị danh sách GCN đã nhận
- Cột "Chứng nhận" trong bảng sự kiện
- Modal xem chi tiết GCN

### 9. Template - admin/event_register/attendance.html (CẬP NHẬT)
**Tính năng mới:**
- Nút "Quét mã QR" mở section scanner
- Tích hợp html5-qrcode library
- Hiển thị kết quả quét real-time
- Nút cấp GCN sau khi điểm danh
- Nút cấp GCN hàng loạt

---

## 🗄️ Database Schema

### Bảng certificates (TỰ ĐỘNG TẠO)
```sql
CREATE TABLE certificates (
    certificate_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    event_id BIGINT NOT NULL REFERENCES events(event_id),
    certificate_code VARCHAR(50) UNIQUE,
    issued_date TIMESTAMP,
    issued_by VARCHAR(255),
    description TEXT,
    is_valid BOOLEAN DEFAULT true
);
```

---

## 📦 Dependencies (pom.xml)
```xml
<!-- ZXing QR Code Library -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.2</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.2</version>
</dependency>
```

---

## 🎯 Cách Sử Dụng

### Phía User (Trang cá nhân /profile)
1. Đăng nhập vào hệ thống
2. Vào trang "Hồ sơ cá nhân"
3. Xem mã QR ở cột bên trái
4. Click "Phóng to" để hiển thị modal QR lớn hơn
5. Đưa QR cho admin quét để điểm danh
6. Sau khi điểm danh, xem GCN ở tab "Giấy chứng nhận"

### Phía Admin (Điểm danh /admin/event_register)
1. Chọn sự kiện → Click "Điểm danh"
2. Click nút "Quét mã QR" ở góc phải
3. Click "Bắt đầu quét" → Cho phép camera
4. Hướng camera vào QR của user
5. Hệ thống tự động điểm danh
6. Popup hỏi có muốn cấp GCN ngay không
7. Hoặc click "Cấp GCN Hàng Loạt" để cấp cho tất cả

---

## 🔒 Validation & Logic

### Điểm danh qua QR
- ✅ Kiểm tra user có tồn tại không
- ✅ Kiểm tra user đã đăng ký event chưa
- ✅ Kiểm tra đã điểm danh chưa (chỉ 1 lần)
- ✅ Ghi nhận thời gian điểm danh

### Cấp giấy chứng nhận
- ✅ Chỉ cấp cho người đã điểm danh có mặt
- ✅ Không cấp trùng (1 user 1 event = 1 GCN)
- ✅ Tự động tạo mã GCN unique: `CERT-{eventId}-{userId}-{random}`
- ✅ Ghi nhận người cấp và thời gian

---

## 📱 QR Code Format

### User QR (dùng chung)
```
USER:{userId}:{studentCode}
```
Ví dụ: `USER:123:2021001234`

### Event-specific QR
```
ATTENDANCE:{userId}:{eventId}:{timestamp}
```
Ví dụ: `ATTENDANCE:123:5:1704844800000`

---

## 🧪 Testing

### Test QR Generation
```
GET http://localhost:8090/api/qr/my-qr
```
→ Trả về Base64 QR code

### Test Scan Attendance
```
POST http://localhost:8090/api/qr/admin/scan-attendance
Content-Type: application/json

{
    "qrContent": "USER:123:2021001234",
    "eventId": "1"
}
```

### Test Issue Certificate
```
POST http://localhost:8090/api/qr/admin/issue-certificate
Content-Type: application/json

{
    "userId": 123,
    "eventId": 1
}
```

---

## ✅ Checklist Hoàn Thành

- [x] Model Certificate
- [x] CertificateRepository
- [x] QRCodeService (tạo QR)
- [x] CertificateService
- [x] QRCodeController (API endpoints)
- [x] Cập nhật UserProfileController
- [x] Cập nhật AttendanceService
- [x] Template profile với QR + certificates
- [x] Template attendance với QR scanner
- [x] Thêm ZXing dependency
- [x] JavaScript QR scanner

---

**Ngày hoàn thành:** 2026-01-10
**Trạng thái:** ✅ HOÀN TẤT

---

## 🚀 Hướng dẫn chạy ứng dụng

### 1. Download dependencies (lần đầu)
```bash
.\mvnw.cmd dependency:resolve
```

### 2. Compile project
```bash
.\mvnw.cmd clean compile -DskipTests
```

### 3. Chạy ứng dụng
```bash
.\mvnw.cmd spring-boot:run
```

### 4. Truy cập
- **User profile với QR:** http://localhost:8090/profile
- **Admin điểm danh:** http://localhost:8090/admin/event_register → chọn sự kiện → Điểm danh

---

## 📝 Lưu ý Database

Hibernate sẽ tự động tạo bảng `certificates` khi chạy lần đầu nếu `spring.jpa.hibernate.ddl-auto=update`.

Nếu cần tạo thủ công:
```sql
CREATE TABLE certificates (
    certificate_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    event_id BIGINT NOT NULL REFERENCES events(event_id),
    certificate_code VARCHAR(50) UNIQUE,
    issued_date TIMESTAMP,
    issued_by VARCHAR(255),
    description TEXT,
    is_valid BOOLEAN DEFAULT true
);
```

