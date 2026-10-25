# 🔍 Debug Login Issue

## Các bước kiểm tra khi login không hoạt động:

### 1️⃣ Kiểm tra Backend đã chạy chưa

```bash
# Kiểm tra backend có đang chạy không
curl http://localhost:8080/api/v1/auth/login
# Hoặc mở browser: http://localhost:8080/swagger-ui.html
```

**Nếu backend chưa chạy:**
```bash
cd d:/DevOps/BTON
./mvnw spring-boot:run
```

---

### 2️⃣ Kiểm tra Console của Browser

Mở Developer Tools (F12) và xem tab **Console** và **Network**:

1. Nhấn F12 trong browser
2. Vào tab **Network**
3. Thử login lại
4. Xem request `/api/v1/auth/login`:
   - **Status**: 200? 401? 500? CORS error?
   - **Response**: Có data không?
   - **Headers**: Có đúng không?

---

### 3️⃣ Test Login trực tiếp bằng cURL

```bash
# Test với tài khoản admin
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin@1234"
  }'

# Test với tài khoản user
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "nguyen.van.a",
    "password": "User@1234"
  }'
```

**Response mong đợi:**
```json
{
  "token": "eyJhbGc...",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@bton.app",
    "fullName": "System Administrator",
    "role": "ADMIN",
    "isActive": true
  }
}
```

---

### 4️⃣ Kiểm tra Database có dữ liệu không

```sql
-- Kết nối MySQL
mysql -u root -p

-- Chọn database
USE bton_db;

-- Kiểm tra users
SELECT id, username, email, full_name, role, is_active 
FROM users;

-- Kiểm tra password hash của admin
SELECT username, password_hash 
FROM users 
WHERE username = 'admin';
```

**Nếu không có data:**
- Flyway migration chưa chạy
- Cần check log backend xem có lỗi migration không

---

### 5️⃣ Kiểm tra CORS Configuration

Đảm bảo file `SecurityConfig.java` có cấu hình CORS đúng:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

### 6️⃣ Thêm Debug Logging vào Login.tsx

Thêm console.log để debug:

```typescript
const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  
  console.log('🔐 Login attempt:', formData);
  
  if (!validateForm()) {
    console.log('❌ Validation failed');
    return;
  }

  setLoading(true);

  try {
    console.log('📡 Sending request to:', '/auth/login');
    const response = await api.post<AuthResponse>('/auth/login', formData);
    console.log('✅ Response:', response.data);
    
    login(response.data.user, response.data.token);
    console.log('✅ Login successful, navigating to /');
    
    toast.success('Đăng nhập thành công!');
    navigate('/');
  } catch (error: any) {
    console.error('❌ Login error:', error);
    console.error('❌ Error response:', error.response);
    toast.error(error.response?.data?.message || 'Đăng nhập thất bại');
  } finally {
    setLoading(false);
  }
};
```

---

### 7️⃣ Kiểm tra LocalStorage

Mở Console (F12) và chạy:

```javascript
// Xem auth state
console.log(localStorage.getItem('auth-storage'));

// Clear nếu bị lỗi
localStorage.clear();
```

---

### 8️⃣ Các lỗi thường gặp:

#### ❌ Error: Network Error / CORS
**Nguyên nhân:** Backend chưa chạy hoặc CORS chưa config đúng

**Giải pháp:**
1. Chạy backend: `./mvnw spring-boot:run`
2. Check CORS config trong SecurityConfig.java

---

#### ❌ Error: 401 Unauthorized
**Nguyên nhân:** Username/password sai hoặc user không tồn tại

**Giải pháp:**
1. Check database có user không
2. Kiểm tra password hash đúng không
3. Run lại migration nếu cần

---

#### ❌ Error: 500 Internal Server Error
**Nguyên nhân:** Lỗi trong backend (JWT secret, database connection, etc.)

**Giải pháp:**
1. Xem backend logs
2. Check application.yml có đúng không
3. Check database connection

---

#### ❌ Login thành công nhưng redirect về login
**Nguyên nhân:** 
- `isAuthenticated` không được set đúng
- Store không persist
- Navigation timing issue

**Giải pháp:**
```typescript
// Trong Login.tsx, sau khi login thành công, thêm:
console.log('Auth state after login:', useAuthStore.getState());

// Check isAuthenticated
console.log('Is authenticated:', useAuthStore.getState().isAuthenticated);
```

---

### 9️⃣ Quick Fix: Restart Everything

```bash
# Stop tất cả
# Ctrl+C trong terminal backend
# Ctrl+C trong terminal frontend

# Xóa cache
rm -rf node_modules/.vite
rm -rf frontend/node_modules/.vite

# Restart backend
./mvnw spring-boot:run

# Restart frontend (terminal mới)
cd frontend
npm run dev

# Clear browser cache
# Trong browser: Ctrl+Shift+Delete → Clear cache
# Hoặc: F12 → Network → Disable cache (checkbox)
```

---

## ✅ Checklist

Để đăng nhập thành công, cần:

- [ ] Backend đang chạy tại `http://localhost:8080`
- [ ] Frontend đang chạy tại `http://localhost:3000`
- [ ] Database MySQL đang chạy
- [ ] Database `bton_db` đã được tạo
- [ ] Flyway migrations đã chạy (có tables và data)
- [ ] CORS đã được config trong SecurityConfig
- [ ] JWT secret đã được set trong application.yml
- [ ] Browser console không có error
- [ ] Network tab shows request thành công (Status 200)

---

## 🆘 Nếu vẫn không được

Chụp màn hình và gửi cho tôi:

1. **Backend logs** khi chạy `./mvnw spring-boot:run`
2. **Browser Console** (F12 → Console tab)
3. **Network tab** (F12 → Network → Login request)
4. **Database query** result: `SELECT * FROM users;`

---

## 📞 Quick Commands

```bash
# Check backend
curl http://localhost:8080/actuator/health

# Check database
mysql -u root -p -e "USE bton_db; SELECT COUNT(*) FROM users;"

# Check frontend
curl http://localhost:3000

# Clear all and restart
rm -rf node_modules/.vite && cd frontend && npm run dev
```

---

**Ghi chú:** Mật khẩu mặc định:
- Admin: `Admin@1234`
- User: `User@1234`

Nếu không đúng, check lại file migration `V3__seed_demo_data.sql`
