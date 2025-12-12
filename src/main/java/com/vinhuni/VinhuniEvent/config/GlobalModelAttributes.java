package com.vinhuni.VinhuniEvent.config; // Đặt trong gói config hoặc util

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    /**
     * Phương thức này sẽ chạy trước mỗi Controller method
     * và tự động thêm thuộc tính "loggedInUser" vào Model.
     */
    @ModelAttribute("loggedInUser")
    public Object addLoggedInUserToModel(HttpSession session) {
        // Trả về giá trị của session attribute. Nếu session không có, nó sẽ trả về null.
        return session.getAttribute("loggedInUser");
    }
}