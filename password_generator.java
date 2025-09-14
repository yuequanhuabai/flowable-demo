import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        System.out.println("生成正确的BCrypt密码哈希:");
        System.out.println("user/password: " + encoder.encode("password"));
        System.out.println("admin/admin: " + encoder.encode("admin"));  
        System.out.println("demo/demo: " + encoder.encode("demo"));
        
        // 验证现有哈希
        String existingAdminHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        System.out.println("\n验证现有哈希:");
        System.out.println("admin密码验证: " + encoder.matches("admin", existingAdminHash));
        System.out.println("password密码验证: " + encoder.matches("password", existingAdminHash)); 
    }
}