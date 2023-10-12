package com.sntech.auth.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0

    @Column
    var name = ""

    @Column(unique = true)
    var email = ""

    @Column
    var password = ""
        @JsonIgnore
        get() = field
        set(value){
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }
    fun comparePassword(password: String): Boolean{
        return BCryptPasswordEncoder().matches(password, this.password)
    }


}
