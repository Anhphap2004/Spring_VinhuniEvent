package com.vinhuni.VinhuniEvent.config;

import com.vinhuni.VinhuniEvent.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Nếu không phải là request vào Controller (ví dụ file ảnh, css) thì bỏ qua
        if (!(handler instanceof HandlerMethod)) return true;

        HandlerMethod method = (HandlerMethod) handler;

        // 2. Tìm xem hàm này hoặc Class này có dán nhãn @RequiredRole không
        RequiredRole roleAnnotation = method.getMethodAnnotation(RequiredRole.class);
        if (roleAnnotation == null) {
            // Nếu hàm không có, tìm thử ở trên đầu Class
            roleAnnotation = method.getBeanType().getAnnotation(RequiredRole.class);
        }

        // 3. Nếu không có nhãn -> Ai vào cũng được -> Cho qua
        if (roleAnnotation == null) return true;

        // 4. Có nhãn -> Kiểm tra Session xem đã đăng nhập chưa
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("loggedInUser");

        if (currentUser == null) {
            // Chưa đăng nhập -> Đuổi về trang Login
            response.sendRedirect("/login");
            return false;
        }

        // 5. Kiểm tra quyền (So sánh role của User với role ghi trên nhãn)
        int userRoleId = currentUser.getRole().getRole_id();
        int[] allowedRoles = roleAnnotation.value();

        boolean isAllowed = false;
        for (int role : allowedRoles) {
            if (role == userRoleId) {
                isAllowed = true; // Tìm thấy quyền hợp lệ
                break;
            }
        }
        if (!isAllowed) {
            // Đăng nhập rồi nhưng không đủ quyền -> Chuyển về trang chủ hoặc trang lỗi
            // Bạn có thể tạo file 403.html sau, giờ tạm thời đẩy về trang chủ kèm thông báo
            response.sendRedirect("/403");
            return false; // Chặn lại
        }
        return true; // OK -> Cho phép vào Controller
    }
}
