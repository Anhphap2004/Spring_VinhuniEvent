package com.vinhuni.VinhuniEvent.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    /**
     * Tạo mã QR dạng Base64 để hiển thị trên web
     * @param content Nội dung mã QR (userId_eventId hoặc userId)
     * @param width Chiều rộng
     * @param height Chiều cao
     * @return Base64 encoded image
     */
    public String generateQRCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            byte[] qrCodeBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(qrCodeBytes);

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Không thể tạo mã QR: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo nội dung QR cho điểm danh sự kiện
     * Format: ATTENDANCE:{userId}:{eventId}:{timestamp}
     */
    public String createAttendanceQRContent(Long userId, Long eventId) {
        long timestamp = System.currentTimeMillis();
        return String.format("ATTENDANCE:%d:%d:%d", userId, eventId, timestamp);
    }

    /**
     * Tạo nội dung QR cho user (dùng để điểm danh nhiều sự kiện)
     * Format: USER:{userId}:{studentCode}
     */
    public String createUserQRContent(Long userId, String studentCode) {
        return String.format("USER:%d:%s", userId, studentCode != null ? studentCode : "N/A");
    }

    /**
     * Parse nội dung QR để lấy thông tin
     */
    public Map<String, String> parseQRContent(String qrContent) {
        Map<String, String> result = new HashMap<>();

        if (qrContent == null || qrContent.trim().isEmpty()) {
            result.put("type", "INVALID");
            result.put("error", "Nội dung QR trống");
            return result;
        }

        // Trim whitespace
        qrContent = qrContent.trim();

        String[] parts = qrContent.split(":");

        if (parts.length >= 2) {
            String type = parts[0].toUpperCase();
            result.put("type", type);

            if ("ATTENDANCE".equals(type) && parts.length >= 3) {
                result.put("userId", parts[1]);
                result.put("eventId", parts[2]);
                if (parts.length >= 4) {
                    result.put("timestamp", parts[3]);
                }
            } else if ("USER".equals(type) && parts.length >= 2) {
                result.put("userId", parts[1]);
                if (parts.length >= 3) {
                    result.put("studentCode", parts[2]);
                }
            } else {
                // Type không được hỗ trợ, thử parse userId từ parts[1]
                result.put("userId", parts[1]);
            }
        } else {
            // Có thể là userId đơn giản (chỉ số)
            try {
                Long.parseLong(qrContent);
                result.put("type", "SIMPLE_USER");
                result.put("userId", qrContent);
            } catch (NumberFormatException e) {
                result.put("type", "INVALID");
                result.put("error", "Định dạng QR không hợp lệ: " + qrContent);
            }
        }

        return result;
    }
}

