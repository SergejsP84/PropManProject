package lv.emendatus.Destiny_PropMan.util;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public class JwtUtil {

//    private String secretKey = "your-secret-key"; // Replace with your secret key
//
//    public String generateToken(UserDetails userDetails) {
//        Object GrantedAuthority;
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .claim("authorities", userDetails.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.toList()))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration
//                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
//                .compact();
//    }
}