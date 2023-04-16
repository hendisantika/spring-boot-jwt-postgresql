package com.hendisantika.springbootjwtpostgresql.repository;

import com.hendisantika.springbootjwtpostgresql.model.ERole;
import com.hendisantika.springbootjwtpostgresql.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-jwt-postgresql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 4/16/23
 * Time: 09:40
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
