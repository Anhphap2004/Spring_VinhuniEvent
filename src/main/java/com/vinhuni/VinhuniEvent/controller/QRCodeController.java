package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.*;
import com.vinhuni.VinhuniEvent.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/qr")
public class QRCodeController {

    private final QRCodeService qrCodeService;
    private final UserService userService;
    private final EventService eventService;
    private final AttendanceService attendanceService;
    private final CertificateService certificateService;
    private final EventRegistrationService registrationService;

    public QRCodeController(QRCodeService qrCodeService,
                           UserService userService,
                           EventService eventService,
                           AttendanceService attendanceService,
                           CertificateService certificateService,
                           EventRegistrationService registrationService) {
        this.qrCodeService = qrCodeService;
        this.userService = userService;
        this.eventService = eventService;
        this.attendanceService = attendanceService;
        this.certificateService = certificateService;
        this.registrationService = registrationService;
    }

    /**
     * API: Lấy mã QR của user hiện tại
     */
    @GetMapping("/my-qr")
    public ResponseEntity<Map<String, Object>> getMyQRCode(HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Vui lòng đăng nhập"));
        }

        // Tạo nội dung QR với userId và studentCode
        String qrContent = qrCodeService.createUserQRContent(
            currentUser.getUserId(),
            currentUser.getStudentCode()
        );

        // Tạo mã QR base64
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrContent, 300, 300);

        Map<String, Object> response = new HashMap<>();
        response.put("qrCode", "data:image/png;base64," + qrCodeBase64);
        response.put("userId", currentUser.getUserId());
        response.put("fullName", currentUser.getFullName());
        response.put("studentCode", currentUser.getStudentCode());
        response.put("qrContent", qrContent);

        return ResponseEntity.ok(response);
    }

    /**
     * API: Lấy mã QR cho sự kiện cụ thể (user đã đăng ký)
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getEventQRCode(
            @PathVariable Long eventId,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Vui lòng đăng nhập"));
        }

        // Kiểm tra user đã đăng ký event chưa
        Optional<EventRegistration> registration = registrationService
            .findByEventIdAndUserId(eventId, currentUser.getUserId());

        if (registration.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Bạn chưa đăng ký sự kiện này"));
        }

        // Tạo nội dung QR cho điểm danh
        String qrContent = qrCodeService.createAttendanceQRContent(currentUser.getUserId(), eventId);
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrContent, 300, 300);

        Map<String, Object> response = new HashMap<>();
        response.put("qrCode", "data:image/png;base64," + qrCodeBase64);
        response.put("eventId", eventId);
        response.put("userId", currentUser.getUserId());
        response.put("fullName", currentUser.getFullName());

        return ResponseEntity.ok(response);
    }

    /**
     * API: Admin quét QR để điểm danh
     */
    @PostMapping("/admin/scan-attendance")
    public ResponseEntity<Map<String, Object>> scanAttendance(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || admin.getRole() == null || admin.getRole().getRoleId() > 2) {
            return ResponseEntity.status(403).body(Map.of("error", "Không có quyền truy cập"));
        }

        String qrContent = request.get("qrContent");
        String eventIdStr = request.get("eventId");

        // Kiểm tra null
        if (qrContent == null || qrContent.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nội dung mã QR trống"));
        }
        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chưa chọn sự kiện"));
        }

        Long eventId;
        try {
            eventId = Long.parseLong(eventIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "ID sự kiện không hợp lệ"));
        }

        Map<String, Object> response = new HashMap<>();

        try {
            // Parse QR content
            Map<String, String> qrData = qrCodeService.parseQRContent(qrContent);
            String type = qrData.get("type");

            if ("INVALID".equals(type)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã QR không hợp lệ hoặc không đúng định dạng"));
            }

            String userIdStr = qrData.get("userId");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin người dùng trong mã QR"));
            }

            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID người dùng trong mã QR không hợp lệ: " + userIdStr));
            }

            // Lấy thông tin user
            Optional<User> userOpt = userService.findUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy người dùng với ID: " + userId));
            }
            User user = userOpt.get();

            // Lấy thông tin event
            Optional<Event> eventOpt = eventService.getEventById(eventId);
            if (eventOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy sự kiện"));
            }
            Event event = eventOpt.get();

            // Kiểm tra đã đăng ký sự kiện chưa
            Optional<EventRegistration> registration = registrationService
                .findByEventIdAndUserId(eventId, userId);
            if (registration.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Người dùng chưa đăng ký sự kiện này",
                    "userName", user.getFullName()
                ));
            }

            // Kiểm tra đã điểm danh chưa
            Optional<Attendance> existingAttendance = attendanceService
                .getAttendanceByEventAndUser(eventId, userId);

            if (existingAttendance.isPresent() && existingAttendance.get().getIsPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Người dùng đã được điểm danh trước đó",
                    "userName", user.getFullName(),
                    "attendanceTime", existingAttendance.get().getAttendanceTime().toString()
                ));
            }

            // Thực hiện điểm danh
            Attendance attendance = new Attendance();
            attendance.setUser(user);
            attendance.setEvent(event);
            attendance.setIsPresent(true);
            attendance.setAttendanceTime(LocalDateTime.now());
            attendanceService.saveAttendance(attendance);

            response.put("success", true);
            response.put("message", "Điểm danh thành công!");
            response.put("userName", user.getFullName());
            response.put("studentCode", user.getStudentCode());
            response.put("eventTitle", event.getTitle());
            response.put("attendanceTime", LocalDateTime.now().toString());
            response.put("userId", userId);
            response.put("eventId", eventId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi xử lý: " + e.getMessage()));
        }
    }

    /**
     * API: Cấp giấy chứng nhận sau điểm danh
     */
    @PostMapping("/admin/issue-certificate")
    public ResponseEntity<Map<String, Object>> issueCertificate(
            @RequestBody Map<String, Long> request,
            HttpSession session) {

        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || admin.getRole() == null || admin.getRole().getRoleId() > 2) {
            return ResponseEntity.status(403).body(Map.of("error", "Không có quyền truy cập"));
        }

        Long userId = request.get("userId");
        Long eventId = request.get("eventId");

        try {
            // Kiểm tra đã điểm danh chưa
            Optional<Attendance> attendance = attendanceService.getAttendanceByEventAndUser(eventId, userId);
            if (attendance.isEmpty() || !attendance.get().getIsPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Người dùng chưa điểm danh sự kiện này"));
            }

            // Kiểm tra đã có giấy chứng nhận chưa
            if (certificateService.hasCertificate(userId, eventId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Đã cấp giấy chứng nhận trước đó"));
            }

            // Lấy user và event
            User user = userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

            // Cấp giấy chứng nhận
            Certificate certificate = certificateService.issueCertificate(user, event, admin.getFullName());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cấp giấy chứng nhận thành công!");
            response.put("certificateCode", certificate.getCertificateCode());
            response.put("userName", user.getFullName());
            response.put("eventTitle", event.getTitle());
            response.put("issuedDate", certificate.getIssuedDate().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API: Cấp giấy chứng nhận hàng loạt cho tất cả người đã điểm danh
     */
    @PostMapping("/admin/issue-certificates-batch")
    public ResponseEntity<Map<String, Object>> issueCertificatesBatch(
            @RequestBody Map<String, Long> request,
            HttpSession session) {

        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || admin.getRole() == null || admin.getRole().getRoleId() > 2) {
            return ResponseEntity.status(403).body(Map.of("error", "Không có quyền truy cập"));
        }

        Long eventId = request.get("eventId");

        try {
            Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

            // Lấy tất cả attendance có mặt của event
            var attendances = attendanceService.getAttendancesByEventId(eventId);

            int issuedCount = 0;
            int skippedCount = 0;

            for (Attendance att : attendances) {
                if (att.getIsPresent() && !certificateService.hasCertificate(att.getUser().getUserId(), eventId)) {
                    certificateService.issueCertificate(att.getUser(), event, admin.getFullName());
                    issuedCount++;
                } else {
                    skippedCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Đã cấp %d giấy chứng nhận, bỏ qua %d (đã có hoặc chưa điểm danh)",
                issuedCount, skippedCount));
            response.put("issuedCount", issuedCount);
            response.put("skippedCount", skippedCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API: Lấy thông tin chi tiết giấy chứng nhận
     */
    @GetMapping("/certificate/{certId}")
    public ResponseEntity<Map<String, Object>> getCertificateDetails(
            @PathVariable Long certId,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Vui lòng đăng nhập"));
        }

        try {
            var certificates = certificateService.getCertificatesByUserId(currentUser.getUserId());
            var cert = certificates.stream()
                .filter(c -> c.getCertificateId().equals(certId))
                .findFirst();

            if (cert.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy giấy chứng nhận"));
            }

            Certificate certificate = cert.get();
            Map<String, Object> response = new HashMap<>();
            response.put("certificateId", certificate.getCertificateId());
            response.put("certificateCode", certificate.getCertificateCode());
            response.put("userName", currentUser.getFullName());
            response.put("studentCode", currentUser.getStudentCode());
            response.put("eventTitle", certificate.getEvent().getTitle());
            response.put("eventDate", certificate.getEvent().getStartTime());
            response.put("issuedDate", certificate.getIssuedDate());
            response.put("issuedBy", certificate.getIssuedBy());
            response.put("description", certificate.getDescription());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

