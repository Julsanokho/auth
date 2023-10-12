package com.sntech.auth.services

import com.sntech.auth.models.User
import com.sntech.auth.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServices(private val userRepository: UserRepository) {

    fun save(user: User): User{
        return this.userRepository.save(user)
    }

    fun finByEmail(email: String): User?{
        return this.userRepository.findByEmail(email)
    }

    fun getById(id: Int): User{
        return this.userRepository.getById(id)
    }
}
