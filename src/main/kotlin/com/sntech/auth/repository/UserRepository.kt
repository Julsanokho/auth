package com.sntech.auth.repository

import com.sntech.auth.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun findByEmail(email:String): User?
}
