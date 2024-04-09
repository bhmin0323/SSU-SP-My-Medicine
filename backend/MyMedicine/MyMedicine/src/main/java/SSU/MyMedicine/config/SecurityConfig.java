//package SSU.MyMedicine.config;
//
//import SSU.MyMedicine.jwt.JWTFilter;
//import SSU.MyMedicine.jwt.JWTUtil;
//import SSU.MyMedicine.jwt.LoginFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final AuthenticationConfiguration authenticationConfiguration;
//    private final JWTUtil jwtUtil;
//    private static final String[] PERMIT_URL_ARRAY = {
//            /* swagger v2 */
//            "/v2/api-docs",
//            "/swagger-resources",
//            "/swagger-resources/**",
//            "/configuration/ui",
//            "/configuration/security",
//            "/swagger-ui.html",
//            "/webjars/**",
//            /* swagger v3 */
//            "/v3/api-docs/**",
//            "/swagger-ui/**"
//    };
//
//    public  SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil){
//        this.authenticationConfiguration = authenticationConfiguration;
//        this.jwtUtil = jwtUtil;
//    }
//
//    //AuthenticationManager Bean 등록
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        //csrf disable
//        http
//                .csrf((auth) -> auth.disable());
//
//        //From 로그인 방식 disable
//        http
//                .formLogin((auth) -> auth.disable());
//
//        //http basic 인증 방식 disable
//        http
//                .httpBasic((auth) -> auth.disable());
//
//        //경로별 인가 작업
//        http
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/hello", "/signup","/login","/alive","/").permitAll()
//                        .requestMatchers("/manage/**").hasRole("ADMIN")
//                        .requestMatchers("/todo").hasAnyRole("USER","ADMIN")
//                        .requestMatchers(PERMIT_URL_ARRAY).permitAll()
//                        .anyRequest().authenticated());
//
//        http
//                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
//
//        http
//                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
//
//        //세션 설정
//        http
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//}
