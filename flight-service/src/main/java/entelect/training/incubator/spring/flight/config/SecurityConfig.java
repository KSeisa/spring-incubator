package entelect.training.incubator.spring.flight.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Disclaimer! In a production system you will never store your credentials in either clear text or in the code.
     * It is done here so that development is both easy to understand and change.
     * The commented code below shows you how to connect to a DB. You will also want to use some kind of password encoding/hashing.
     */

    //    @Autowired
    //    private DataSource securityDataSource;
    //
    //    @Override
    //    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //        auth.jdbcAuthentication().dataSource(securityDataSource);
    //    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("{noop}the_cake").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}is_a_lie").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("loyalty").password("{noop}because_its_not_sweet").roles("LOYALTY_USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // !!! Disclaimer: NEVER DISABLE CSRF IN PRODUCTION !!!
                .cors().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/flights/**").permitAll()
                .antMatchers(HttpMethod.POST, "/flights/**").hasAnyRole("SYSTEM", "ADMIN")
                .antMatchers(HttpMethod.GET, "/flights/specials").hasAnyRole("LOYALTY_USER", "ADMIN")
//                .anyRequest().denyAll()
                .and()
                .httpBasic();
    }

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
