package ru.enikeev.stolplusapp.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.enikeev.stolplusapp.model.User;
import ru.enikeev.stolplusapp.repository.UserRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Попытка загрузить пользователя по имени: {}", username);
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {

                    log.warn("Пользователь с именем пользователя не найден: {}", username);
                    return new UsernameNotFoundException("Пользователь с именем пользователя: " + username + " не найден");
                });

        log.info("Пользователь найден: {} с ролью {}", user.getUserName(), user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}