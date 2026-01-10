# Sửa Lỗi Không Sửa Được User trong Admin

## 🐛 Vấn đề

Khi cập nhật thông tin user trong admin panel, hệ thống không lưu được thay đổi, đặc biệt là vai trò (Role).

## 🔍 Nguyên nhân

1. **Form binding sai:** Template `admin/user/form.html` đang bind `th:field="*{role}"` và `th:value="${r}"` - cố gắng gửi toàn bộ object Role thay vì chỉ ID
2. **Service không xử lý đúng:** `UserServiceImpl.saveUser()` đang set trực tiếp `existingUser.setRole(user.getRole())` nhưng `user.getRole()` là một Role object chỉ có roleId, không có đầy đủ thông tin
3. **Thiếu dependency:** Service không inject `RoleService` để fetch Role entity đầy đủ

## ✅ Giải pháp đã áp dụng

### 1. Sửa Template Form (admin/user/form.html)

**Trước:**
```html
<select th:field="*{role}" class="form-select" required>
    <option value="">-- Chọn quyền --</option>
    <option th:each="r : ${roles}"
            th:value="${r}"
            th:text="${r.roleName}">
    </option>
</select>
```

**Sau:**
```html
<select th:field="*{role.roleId}" class="form-select" required>
    <option value="">-- Chọn quyền --</option>
    <option th:each="r : ${roles}"
            th:value="${r.roleId}"
            th:text="${r.roleName}">
    </option>
</select>
```

**Thay đổi:**
- `th:field="*{role}"` → `th:field="*{role.roleId}"`
- `th:value="${r}"` → `th:value="${r.roleId}"`

### 2. Cập nhật UserServiceImpl

#### A. Thêm RoleService dependency

**Trước:**
```java
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;

public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}
```

**Sau:**
```java
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final RoleService roleService;

public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.roleService = roleService;
}
```

#### B. Sửa saveUser() method

**Thêm vào phần CREATE (thêm mới):**
```java
// Xử lý Role: nếu có roleId, fetch Role entity
if (user.getRole() != null && user.getRole().getRoleId() != null) {
    Role role = roleService.getRoleById(user.getRole().getRoleId());
    user.setRole(role);
}
```

**Thay đổi phần UPDATE:**

**Trước:**
```java
existingUser.setRole(user.getRole());
```

**Sau:**
```java
// Xử lý Role: fetch Role entity từ roleId
if (user.getRole() != null && user.getRole().getRoleId() != null) {
    Role role = roleService.getRoleById(user.getRole().getRoleId());
    existingUser.setRole(role);
}
```

## 📊 Các thay đổi

- ✅ **1 Template:** `admin/user/form.html` - Fix role binding
- ✅ **1 Service:** `UserServiceImpl.java` - Add RoleService dependency và logic xử lý Role
- ✅ **0 Controllers:** UserController không cần thay đổi

## 🎯 Kết quả

Sau khi áp dụng các thay đổi này:

1. ✅ Form giờ sẽ gửi `role.roleId` (Integer) thay vì toàn bộ Role object
2. ✅ Service sẽ fetch đầy đủ Role entity từ database bằng roleId
3. ✅ Cập nhật user với Role đầy đủ thông tin
4. ✅ Tránh lỗi conversion và binding
5. ✅ Tránh lỗi LazyInitializationException khi truy cập Role

## 🔧 Cách test

1. Truy cập: `http://localhost:8090/admin/users`
2. Click "Sửa" trên bất kỳ user nào
3. Thay đổi thông tin (tên, email, role, v.v.)
4. Click "Lưu lại"
5. Kiểm tra: User được cập nhật với đầy đủ thông tin mới

## 💡 Lưu ý quan trọng

### Về Password
- Khi edit user, password field để trống → Giữ nguyên password cũ
- Nếu nhập password mới → Mã hóa và cập nhật

### Về Role binding
- **Luôn bind bằng ID** (`role.roleId`) chứ không phải object
- **Service phải fetch entity** từ database để có đầy đủ thông tin
- Tránh lỗi: `TransientObjectException`, `LazyInitializationException`

### Best Practice
```java
// ❌ SAI - Set object không đầy đủ
existingUser.setRole(user.getRole());

// ✅ ĐÚNG - Fetch entity từ DB
Role role = roleService.getRoleById(user.getRole().getRoleId());
existingUser.setRole(role);
```

## 📅 Thông tin

- **Ngày fix:** 2026-01-10
- **Trạng thái:** ✅ HOÀN TẤT
- **Lỗi gốc:** Role không được cập nhật khi edit user
- **Nguyên nhân:** Object binding thay vì ID binding
- **Giải pháp:** Bind role.roleId và fetch Role entity trong service

